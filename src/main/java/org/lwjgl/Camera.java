package org.lwjgl;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private Vector3f position;
    private Vector3f rotation; // Pitch (x), Yaw (y), Roll (z) in degrees
    private Matrix4f viewMatrix;
    private Matrix4f projectionMatrix;
    private Matrix4f invViewMatrix;
    private Matrix4f invProjMatrix;

    private Vector3f up;
    private Vector3f direction;
    private Vector3f right;
    private float moveSpeed;
    private float rotateSpeed;
    private float fov;
    private float near;
    private float far;
    private float mouseSensitivity;
    private float panSensitivity;

    public Camera(float aspectRatio) {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        viewMatrix = new Matrix4f();
        invViewMatrix = new Matrix4f();
        invProjMatrix = new Matrix4f();

        direction = new Vector3f();
        right = new Vector3f();
        up = new Vector3f();

        moveSpeed = 0.02f;
        rotateSpeed = 1.0f;
        fov = 45.0f;
        near = 0.1f;
        far = 100f;
        mouseSensitivity = 0.05f;
        panSensitivity = 0.01f;

        projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, near, far);

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
            rotation.z -= delta.x * mouseSensitivity; // Roll
            rotation.x -= delta.y * mouseSensitivity; // Pitch
            // Clamp pitch to avoid flipping
            rotation.x = Math.max(-90, Math.min(90, rotation.x));
        }

        //Mousewheel pan
        if (input.isMiddleClicked()) {
            Vector2f delta = input.getMouseDelta();
            position.x -= delta.x * panSensitivity;
            position.y += delta.y * panSensitivity;
        }

        // Update view matrix
        viewMatrix.identity()
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z))
                .translate(-position.x, -position.y, -position.z);
    }

    public void updateProjection(float aspectRatio) {
        projectionMatrix.identity().perspective(fov, aspectRatio, near, far);
        invProjMatrix.set(projectionMatrix).invert();
    }
    public Matrix4f getViewMatrix() { return viewMatrix; }
    public Matrix4f getProjectionMatrix() { return projectionMatrix; }

    public void setPosition(Vector3f position) { this.position = position; }
    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }
    public void setRotation(Vector3f rotation) { this.rotation = rotation; }
    public Vector3f getPosition() { return position; }
    public Vector3f getRotation() { return rotation; }

    private void recalculate() {
        viewMatrix.identity()
                .rotateX(rotation.x)
                .rotateY(rotation.y)
                .translate(-position.x, -position.y, -position.z);
        invViewMatrix.set(viewMatrix).invert();
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