package org.lwjgl;

import org.lwjgl.data.ImageGeneration;
import org.lwjgl.data.MapSaveLoad;
import org.lwjgl.continentMap.ContinentHexagon;
import org.lwjgl.input.InputHandler;
import org.lwjgl.input.ObjectSelection;
import org.lwjgl.objects.Grid;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.objects.Trap;
import org.lwjgl.shaders.ShaderProgramCache;
import org.lwjgl.textures.TextureCache;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

public class Scene extends SceneObject {
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
        camera.setPosition(60f, 50.0f, 40f);
        //camera.setPosition(0, 10f, 0);
        camera.setRotation(1.5f, 0.0f);
        camera.resize(width, height);
    }

    public void initContinentScene() {
        this.grid = new Grid(this, 80, 40);
        grid.makeContinentGrid(this);
        addObject(grid);
    }

    public void initCityScene() {
        this.grid = new Grid(this, 80, 40);
        grid.makeCityGrid(this);
        addObject(grid);

        Trap trap = new Trap(2, grid.getHexagonAt(20, 40).getOffsetCoords());
        trap.setShaderProgram(this.getShaderCache().getShader("trap"));
        trap.setId("trap");
        trap.setParent(grid.getHexagonAt(20, 40));
        trap.setPosition(0.0f, 0.2f, 0.0f);
        trap.setTexture(this.getTextureCache().getTexture("sandvich"));
        trap.setIsHidden(false);
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
        if (grid != null) {
            grid.clearSelectedHexagons();
        }

        ObjectSelection.selectObject(this, inputHandler, rootObjects);
        if (selectedObject != null) {
            selectedObject.update(this, deltaTime, inputHandler);
        }
    }

    public void render() {
        for (SceneObject root : rootObjects) {
            root.render();
        }
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler inputHandler) {

    }

    public void cleanup() {
        for (SceneObject root : rootObjects) {
            if (root instanceof ContinentHexagon) {
                ((ContinentHexagon) root).cleanup();
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

    //Recursively get all objects
    public List<SceneObject> getAllObjects() {
        List<SceneObject> objects = new ArrayList<>();

        for (SceneObject object : rootObjects) {
            objects.add(search(object, objects));
        }
        return objects;
    }

    public SceneObject search(SceneObject object, List<SceneObject> objects) {
        if (object.children.isEmpty()) {
            objects.add(object);
            return object;
        }

        for (SceneObject child : object.children) {
            search(child, objects);
        }
        return object;
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