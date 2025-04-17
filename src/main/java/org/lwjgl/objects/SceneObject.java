package org.lwjgl.objects;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.Texture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class SceneObject implements Serializable {
    protected String id;
    protected int shaderProgram;
    protected int vaoId, vboId;
    protected Vector3f position;    // Local position
    protected Vector3f rotation;    // Local rotation (Euler angles in degrees)
    protected Vector3f scale;       // Local scale
    protected Matrix4f localMatrix; // Local transformation matrix
    protected Matrix4f worldMatrix; // World transformation matrix
    protected float[] verticesFloats;
    protected Vector3f[] verticesVecs;
    protected int[] indices;
    public boolean selected;
    protected Vector3f colour;
    protected Texture texture;
    protected float[] texCoords;

    //The default bounding box values for a 0,0,0 object
    protected Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    protected Vector3f max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

    //The current bounding box values
    protected Vector3f[] aabbVertices;
    protected Vector3f aabbMin = new Vector3f();
    protected Vector3f aabbMax = new Vector3f();

    public SceneObject parent;   // Reference to parent
    public List<SceneObject> children; // List of children

    public SceneObject(String id, int shaderProgram) {
        this.id = id;
        this.shaderProgram = shaderProgram;
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
        localMatrix = new Matrix4f();
        worldMatrix = new Matrix4f();
        children = new ArrayList<>();
        verticesFloats = new float[16];
        selected = false;
    }

    public SceneObject() {
        this("default", 3);
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

    public void removeChild(SceneObject child) {
        this.children.remove(child);
    }

    // Getters and setters
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setShaderProgram(int shaderProgram) {
        this.shaderProgram = shaderProgram;
    }
    public int getShaderProgram() {
        return shaderProgram;
    }
    public Matrix4f getWorldMatrix() { return worldMatrix; }
    public Vector3f getPosition() { return position; }
    public Vector3f getRotation() { return rotation; }
    public Vector3f getScale() { return scale; }
    public float[] getVerticesFloats() { return verticesFloats; }
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        setAabb(new Vector3f(x, y, z));
    }
    public void setPosition(Vector3f pos) {
        position.set(pos);
        setAabb(pos);
    }
    public void addPosition(float x, float y, float z) {
        position.add(x, y, z);
        translateAabb(new Vector3f(x, y, z));
    }
    public void setRotation(float x, float y, float z) { rotation.set(x, y, z); }
    public void addRotation(float x, float y, float z) { rotation.add(x, y, z); }
    public void setScale(float scale) { this.scale.set(scale, scale, scale); }

    public void setColour(float r, float g, float b) {this.colour = new Vector3f(r, g, b);}
    public Vector3f getColour() { return colour; }
    public void translateAabb(Vector3f translation) {
        aabbMin.add(translation);
        aabbMax.add(translation);
    }

    public void setAabb(Vector3f newPosition) {
        aabbMin = min;
        aabbMax = max;
        aabbMin.add(newPosition);
        aabbMax.add(newPosition);
    }

    public void scaleAabb(Vector3f scale) {
        min.mul(scale);
        max.mul(scale);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() { return texture; }

    // Abstract render method to be implemented by subclasses
    public abstract void render();

    public abstract void update(Scene scene, float deltaTime, InputHandler inputHandler);

    public abstract void cleanup();


}