package org.lwjgl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public abstract class SceneObject {
    protected Vector3f position;    // Local position
    protected Vector3f rotation;    // Local rotation (Euler angles in degrees)
    protected Vector3f scale;       // Local scale
    protected Matrix4f localMatrix; // Local transformation matrix
    protected Matrix4f worldMatrix; // World transformation matrix
    protected float[] vertices;

    protected SceneObject parent;   // Reference to parent
    protected List<SceneObject> children; // List of children

    public SceneObject() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
        localMatrix = new Matrix4f();
        worldMatrix = new Matrix4f();
        children = new ArrayList<>();
        vertices = new float[16];
    }

    // Update transformation matrices
    public void update() {
        localMatrix.identity()
                .translate(position)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .scale(scale);

        if (parent != null) {
            worldMatrix.set(parent.getWorldMatrix()).mul(localMatrix);
        } else {
            worldMatrix.set(localMatrix);
        }

        for (SceneObject child : children) {
            child.update();
        }
    }

    // Parent-child management
    public void setParent(SceneObject parent) {
        if (this.parent != null) {
            this.parent.children.remove(this);
        }
        this.parent = parent;
        if (parent != null) {
            parent.children.add(this);
        }
    }

    public void addChild(SceneObject child) {
        child.setParent(this);
    }

    // Getters and setters
    public Matrix4f getWorldMatrix() { return worldMatrix; }
    public Vector3f getPosition() { return position; }
    public Vector3f getRotation() { return rotation; }
    public Vector3f getScale() { return scale; }
    public float[] getVertices() { return vertices; }
    public void setPosition(float x, float y, float z) { position.set(x, y, z); }
    public void addPosition(float x, float y, float z) { position.add(x, y, z); }
    public void setRotation(float x, float y, float z) { rotation.set(x, y, z); }
    public void addRotation(float x, float y, float z) { rotation.add(x, y, z); }
    public void setScale(float x, float y, float z) { scale.set(x, y, z); }

    // Abstract render method to be implemented by subclasses
    public abstract void render(int shaderProgram);

    public abstract void update(Scene scene,long deltaTime);

    public abstract void cleanup();


}