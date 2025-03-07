package org.lwjgl;

import org.joml.*;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private Camera camera;
    private List<SceneObject> rootObjects;

    private int screenWidth;
    private int screenHeight;
    private InputHandler inputHandler;
    private SceneObject selectedObject;

    public Scene(int width, int height, InputHandler inputHandler) {
        rootObjects = new ArrayList<>();
        setupScene(width, height);
        this.screenWidth = width;
        this.screenHeight = height;
        this.inputHandler = inputHandler;
        this.selectedObject = null;
    }

    private void setupScene(int width, int height) {
        camera = new Camera(width, height);
        camera.setPosition(3.5f, 10.0f, 5.0f);
        camera.setRotation(1.5f, 0.0f);

        Grid grid = new Grid(this,5, 5);
        addObject(grid);

//        Hexagon plane = new Hexagon(new Vector2i(99, 99));
//        plane.setPosition(0.0f, 0.2f, 0.0f);
//        plane.setColor(0.0f, 0.0f, 0.0f);
//        addObject(plane);
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

    public void removeAllObjects() {
        rootObjects.clear();
    }

    public void update(float deltaTime) {
        for (SceneObject root : rootObjects) {
            root.update();
            root.update(this, deltaTime, inputHandler);
        }
        selectObject();
        if (selectedObject != null) {
            selectedObject.update(this, deltaTime, inputHandler);
        }
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

    public SceneObject selectObject() {
        Vector3f worldPos = inputHandler.getWorldPos(this);
        Vector3f camera = getCamera().getPosition();
        Vector4f mouseDir = inputHandler.getMouseDir(this);
        Vector2f intersect = new Vector2f();
        for (SceneObject object : rootObjects) {
            if (object instanceof Hexagon) {
                if (((Hexagon) object).rayIntersect(worldPos, mouseDir, camera)) {
                    if (selectedObject != null) {
                        selectedObject.selected = false;
                    }
                    selectedObject = object;
                    object.selected = true;
                    return object;
                }
                else {
                    object.selected = false;
                }
            }
            for (SceneObject child : object.children) {
                if (child instanceof Hexagon) {
                    if (((Hexagon) child).rayIntersect(worldPos, mouseDir, camera)) {
                        if (selectedObject != null) {
                            selectedObject.selected = false;
                        }
                        selectedObject = child;
                        child.selected = true;
                        return child;
                    }
                    else {
                        child.selected = false;
                    }
                }
            }
        }


        return null;
    }

    public SceneObject getSelectedObject() {
        return selectedObject;
    }
}