package org.lwjgl;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private Vector3f position;
    private Vector2f rotation;
    private Matrix4f viewMatrix;
    private Matrix4f projectionMatrix;
    private Matrix4f invViewMatrix;
    private Matrix4f invProjMatrix;

    private Vector3f up;
    private Vector3f direction;
    private Vector3f right;
    private float moveSpeed;
    private float rotateSpeed;
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_FAR = 100.f;
    private static final float Z_NEAR = 1f;
    private float mouseSensitivity;
    private float panSensitivity;

    public Camera(int width, int height) {
        position = new Vector3f();
        rotation = new Vector2f();
        viewMatrix = new Matrix4f();
        invViewMatrix = new Matrix4f();
        invProjMatrix = new Matrix4f();

        direction = new Vector3f();
        right = new Vector3f();
        up = new Vector3f();

        moveSpeed = 0.02f;
        rotateSpeed = 1.0f;
        mouseSensitivity = 0.05f;
        panSensitivity = 0.01f;

        projectionMatrix = new Matrix4f();
        updateProjection(width, height);
    }

    public void update(InputHandler input) {
        // Keyboard movement
        if (input.isKeyPressed(GLFW_KEY_W)) moveForward(moveSpeed);
        if (input.isKeyPressed(GLFW_KEY_S)) moveBackwards(moveSpeed);
        if (input.isKeyPressed(GLFW_KEY_A)) moveLeft(moveSpeed);
        if (input.isKeyPressed(GLFW_KEY_D)) moveRight(moveSpeed);
        if (input.isKeyPressed(GLFW_KEY_Q)) moveUp(moveSpeed);
        if (input.isKeyPressed(GLFW_KEY_E)) moveDown(moveSpeed);

        // Keyboard rotation
        if (input.isKeyPressed(GLFW_KEY_UP)) rotation.x -= rotateSpeed;
        if (input.isKeyPressed(GLFW_KEY_DOWN)) rotation.x += rotateSpeed;
        if (input.isKeyPressed(GLFW_KEY_LEFT)) rotation.y += rotateSpeed;
        if (input.isKeyPressed(GLFW_KEY_RIGHT)) rotation.y -= rotateSpeed;

        // Mouse rotation (only when captured)
        if (input.isRightClicked()) {
            Vector2f delta = input.getMouseDelta();
            this.addRotation((float) Math.toRadians(delta.y * mouseSensitivity),
                    (float) Math.toRadians(delta.x * mouseSensitivity));
        }

        //Middle click pan
        if (input.isMiddleClicked()) {
            Vector2f delta = input.getMouseDelta();
            this.addPosition(delta.y * panSensitivity, delta.x * panSensitivity);
        }
    }

    public void updateProjection(int width, int height) {
        float aspectRatio = (float) width / height;
        projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
        invProjMatrix.set(projectionMatrix).invert();
    }

    private void recalculate() {
        viewMatrix.identity()
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .translate(-position.x, -position.y, -position.z);
        invViewMatrix.set(viewMatrix).invert();
        invProjMatrix.set(projectionMatrix).invert();
    }

    public Matrix4f getViewMatrix() { return viewMatrix; }
    public Matrix4f getProjectionMatrix() { return projectionMatrix; }

    public void setPosition(Vector3f position) {
        position.set(position);
        recalculate();
    }
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        recalculate();
        System.out.println(projectionMatrix);
    }

    public void setRotation(float x, float y) {
        rotation.set(x, y);
        recalculate();
    }

    public Vector3f getPosition() { return position; }
    public Vector2f getRotation() { return rotation; }

    public void addPosition(float x, float y) {
        moveUp(x);
        moveLeft(y);
        recalculate();
    }

    public void addRotation(float x, float y) {
        rotation.add(x, y);
        recalculate();
    }

    public void moveBackwards(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        position.sub(direction);
        recalculate();
    }

    public void moveDown(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.sub(up);
        recalculate();
    }

    public void moveForward(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        position.add(direction);
        recalculate();
    }

    public void moveLeft(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        position.sub(right);
        recalculate();
    }

    public void moveRight(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        position.add(right);
        recalculate();
    }

    public void moveUp(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.add(up);
        recalculate();
    }

    public Matrix4f getInvViewMatrix() {
        return invViewMatrix;
    }

    public Matrix4f getInvProjMatrix() {
        return invProjMatrix;
    }

}