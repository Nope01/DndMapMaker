package org.lwjgl;

import org.joml.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Scene {
    private Camera camera;
    private List<SceneObject> rootObjects;
    private int screenWidth;
    private int screenHeight;
    private InputHandler inputHandler;
    private SceneObject selectedObject;
    private TextureCache textureCache;

    public Scene(int width, int height, InputHandler inputHandler) {
        rootObjects = new ArrayList<>();
        this.screenWidth = width;
        this.screenHeight = height;
        this.inputHandler = inputHandler;
        this.selectedObject = null;
        this.textureCache = new TextureCache();

        setupScene(width, height);
    }

    private void setupScene(int width, int height) {
        camera = new Camera(width, height);
        camera.setPosition(50f, 80.0f, 50f);
        camera.setRotation(1.5f, 0.0f);
        camera.resize(width, height);
        Grid grid = new Grid(this, 70, 50);
        addObject(grid);

        Hexagon background = new Hexagon(new Vector2i(0, 0));
        background.setPosition(0.0f, -1.0f, 0.0f);
        background.setScale(100f);
        background.setColour(0.5f, 0.5f, 0.0f);
        addObject(background);
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
        Grid grid = (Grid) getObject("grid");
        grid.lineDraw(this);
    }

    public void render(int shaderProgram) {
        for (SceneObject root : rootObjects) {
            root.render(shaderProgram);
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

    public SceneObject getSelectedObject() {
        return selectedObject;
    }
    public void setSelectedObject(SceneObject selectedObject) {
        this.selectedObject = selectedObject;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }
}