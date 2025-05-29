package org.lwjgl.objects;

import org.joml.*;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.textures.Texture;

import java.io.Serializable;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    protected int numFloats;
    protected int[] indices;
    private boolean hovered;
    private boolean selected;
    protected Vector2i offsetPos;
    protected Vector3i cubePos;
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
        children = new CopyOnWriteArrayList<>();
        verticesFloats = new float[16];
        colour = new Vector3f(0, 0, 0);
        hovered = false;
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

        if (parent != null) {
            setAabb(new Vector3f(x + parent.position.x, y + parent.position.y, z + parent.position.z));
        }
        else {
            setAabb(new Vector3f(x, y, z));
        }

        if (children != null) {
            for (SceneObject child : children) {
                child.setAabb(new Vector3f(x, y, z));
            }
        }

    }
    public void setPosition(Vector3f pos) {
        position.set(pos);
        setAabb(pos);
    }
    public void addPosition(float x, float y, float z) {
        position.add(x, y, z);
        translateAabb(new Vector3f(x, y, z));
    }

    public void addPosition(Vector3f pos) {
        position.add(pos);
        translateAabb(pos);
    }
    public void setRotation(float x, float y, float z) { rotation.set(x, y, z); }
    public void addRotation(float x, float y, float z) { rotation.add(x, y, z); }
    public void setScale(float scale) {
        this.scale.set(scale, scale, scale);
    }

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

    public Vector3f getAabbMin() {
        return aabbMin;
    }
    public Vector3f getAabbMax() {
        return aabbMax;
    }

    public Vector2i getOffsetPos() {
        return offsetPos;
    }

    public void setOffsetPos(Vector2i offsetPos) {
        this.offsetPos = offsetPos;
    }

    public void initAabb() {
        for (Vector3f vertex : verticesVecs) {
            min.x = Math.min(min.x, vertex.x);
            min.y = Math.min(min.y, vertex.y);
            min.z = Math.min(min.z, vertex.z);

            max.x = Math.max(max.x, vertex.x);
            max.y = Math.max(max.y, vertex.y);
            max.z = Math.max(max.z, vertex.z);
        }

        Vector3f[] aabbVertices = {
                new Vector3f(min.x, min.y, min.z),  // Bottom-left-back corner
                new Vector3f(max.x, min.y, min.z),  // Bottom-right-back corner
                new Vector3f(max.x, max.y, min.z),  // Top-right-back corner
                new Vector3f(min.x, max.y, min.z),  // Top-left-back corner
                new Vector3f(min.x, min.y, max.z),  // Bottom-left-front corner
                new Vector3f(max.x, min.y, max.z),  // Bottom-right-front corner
                new Vector3f(max.x, max.y, max.z),  // Top-right-front corner
                new Vector3f(min.x, max.y, max.z)   // Top-left-front corner
        };
        this.aabbVertices = aabbVertices;

        aabbMin = min;
        aabbMax = max;
        setPosition(position.x, position.y, position.z);
    }

    //For getting rid of bounding box when a thing is hidden
    protected void clearAabb() {
        aabbVertices = new Vector3f[]{};
        aabbMin = new Vector3f(0.0f, 0.0f, 0.0f);
        aabbMax = new Vector3f(0.0f, 0.0f, 0.0f);
        setPosition(position.x, position.y, position.z);
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() { return texture; }

    //Hit detection
    public boolean rayIntersect(Vector3f worldPos, Vector4f mouseDir, Vector3f cameraPos) {
        float tMin = Float.MIN_VALUE;
        float tMax = Float.MAX_VALUE;
        Vector3f rayDirection = new Vector3f(mouseDir.x, mouseDir.y, mouseDir.z);

        for (int i = 0; i < 3; i++) {  // Iterate over x, y, z axes
            float rayDirComponent = rayDirection.get(i);
            float rayOriginComponent = cameraPos.get(i);
            float aabbMinComponent = aabbMin.get(i);
            float aabbMaxComponent = aabbMax.get(i);

            if (Math.abs(rayDirComponent) < 1E-6) {  // Ray is parallel to the slab
                if (rayOriginComponent < aabbMinComponent || rayOriginComponent > aabbMaxComponent) {
                    return false;  // Ray is outside the slab
                }
            } else {
                float invDir = 1.0f / rayDirComponent;
                float t1 = (aabbMinComponent - rayOriginComponent) * invDir;
                float t2 = (aabbMaxComponent - rayOriginComponent) * invDir;

                if (t1 > t2) {  // Swap t1 and t2 if t1 > t2
                    float temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                tMin = Math.max(tMin, t1);  // Update tMin
                tMax = Math.min(tMax, t2);  // Update tMax

                if (tMin > tMax) {  // No intersection
                    return false;
                }
            }
        }
        return true;  // Intersection found
    }

    // Abstract render method to be implemented by subclasses
    public abstract void render();

    public abstract void update(Scene scene, float deltaTime, InputHandler inputHandler);

    public abstract void cleanup();


    public void setHovered(boolean b) {
        hovered = b;
    }

    public boolean getHovered() {
        return hovered;
    }

    public void setSelected(boolean b) {
        selected = b;
    }

    public boolean getSelected() {
        return selected;
    }
}