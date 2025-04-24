package org.lwjgl;

import org.lwjgl.data.ImageGeneration;
import org.lwjgl.data.MapSaveLoad;
import org.lwjgl.objects.Hexagon;
import org.lwjgl.objects.SceneObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private Camera camera;
    private List<SceneObject> rootObjects;
    private Grid grid;
    private int screenWidth;
    private int screenHeight;
    private long window;
    private InputHandler inputHandler;
    private SceneObject selectedObject;
    private TextureCache textureCache;
    private ShaderProgramCache shaderCache;
    private MapSaveLoad mapSaveLoad;

    public Scene(int width, int height, InputHandler inputHandler, ShaderProgramCache shaderCache, long window) {
        rootObjects = new ArrayList<>();
        this.screenWidth = width;
        this.screenHeight = height;
        this.window = window;
        this.inputHandler = inputHandler;
        this.selectedObject = null;
        this.textureCache = new TextureCache();
        this.shaderCache = shaderCache;
        this.mapSaveLoad = new MapSaveLoad();

        setupScene(width, height);
    }

    private void setupScene(int width, int height) {
        camera = new Camera(width, height);
        camera.setPosition(50f, 40.0f, 50f);
        camera.setRotation(1.5f, 0.0f);
        camera.resize(width, height);
        


//        ImageQuad background = new ImageQuad();
//        background.setId("background");
//        background.setPosition(50.0f, 1.0f, 50.0f);
//        background.setScale(100f);
//        background.setColour(0.0f, 0.0f, 0.0f);
//        background.setShaderProgram(shaderCache.getShaderMap().get("transparent"));
//        background.setTexture(textureCache.getTexture("map"));
//        addObject(background);

    }

    public void initContinentScene() {
        this.grid = new Grid(this, 70, 50);
        addObject(grid);
    }

    public void addObject(SceneObject object) {
        if (object.parent == null) {
            rootObjects.add(object);
        }
    }

    public void removeObject(SceneObject object) {
        if (object.parent != null) {
            rootObjects.remove(object);
            cleanupChildren(object);
        }
    }

    public SceneObject getObject(String id) {
        for (SceneObject object : rootObjects) {
            if (object.getId().equals(id)) {
                return object;
            }
        }
        return null;
    }

    public void removeAllObjects() {
        rootObjects.clear();
    }

    public void update(float deltaTime) {
        for (SceneObject root : rootObjects) {
            root.update();
            root.update(this, deltaTime, inputHandler);
        }
        if (selectedObject != null) {
            ObjectSelection.resetSelectedObject(selectedObject);
            selectedObject = null;
        }
        ObjectSelection.selectObject(this, inputHandler, rootObjects);
        if (selectedObject != null) {
            selectedObject.update(this, deltaTime, inputHandler);
        }
        //grid.lineDraw(this);
    }

    public void render() {
        for (SceneObject root : rootObjects) {
            root.render();
        }
    }

    public void cleanup() {
        for (SceneObject root : rootObjects) {
            if (root instanceof Hexagon) {
                ((Hexagon) root).cleanup();
            }
            // Recursively clean up children if needed
            cleanupChildren(root);
        }
    }

    private void cleanupChildren(SceneObject object) {
        for (SceneObject child : object.children) {
            child.cleanup();
            cleanupChildren(child);
        }
    }

    public List<SceneObject> getRootObjects() {
        return rootObjects;
    }

    public Camera getCamera() {
        return camera;
    }

    public int getScreenWidth() {
        return screenWidth;
    }
    public int getScreenHeight() {
        return screenHeight;
    }

    public SceneObject getSelectedObject() {
        return selectedObject;
    }
    public void setSelectedObject(SceneObject selectedObject) {
        this.selectedObject = selectedObject;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }
    public ShaderProgramCache getShaderCache() {
        return shaderCache;
    }
    public Grid getGrid() {
        return grid;
    }

    public void saveMap() {
        mapSaveLoad.saveFile(grid);
    }
    public void loadMap() {
        Grid temp = mapSaveLoad.loadFile();
        if (temp != null) {
            grid.setGridFromLoad(temp.getGrid(), temp.rows, temp.columns);
        }
    }

    public void saveImage() {
        ImageGeneration.saveImageAsFile(window, screenWidth, screenHeight);
    }
}