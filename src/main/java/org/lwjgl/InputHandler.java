package org.lwjgl;

import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    private long window;
    private Vector2i windowSize;
    private boolean mouseCaptured;
    private Vector2f lastMousePos;
    private Vector2f mouseDelta;
    private Vector2f mousePos;
    private Vector3f ndcPos;

    public InputHandler(long window, int width, int height) {
        this.window = window;
        this.windowSize = new Vector2i(width, height);
        this.mouseCaptured = false;
        this.lastMousePos = new Vector2f();
        this.mousePos = new Vector2f();
        this.mouseDelta = new Vector2f();
        this.ndcPos = new Vector3f();

        // Initialize mouse position
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
        lastMousePos.set((float) x[0], (float) y[0]);
        mousePos.set((float) x[0], (float) y[0]);

        // Set up callbacks
        setupCallbacks();
    }

    public void update() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(window, x, y);
        float currentX = (float) x[0];
        float currentY = (float) y[0];
        mouseDelta.set(currentX - lastMousePos.x, currentY - lastMousePos.y);
        mousePos.set((float) x[0], (float) y[0]);
        lastMousePos.set(currentX, currentY);

    }

    private void setupCallbacks() {
        // Mouse movement callback (optional, handled in update for simplicity)
        // glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {});
    }

    // Keyboard input queries
    public boolean isKeyPressed(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }
    public boolean isLeftClicked() {
        return glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
    }

    public boolean isRightClicked() {
        return glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2) == GLFW_PRESS;
    }

    public boolean isMiddleClicked() {
        return glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_3) == GLFW_PRESS;
    }

    // Mouse input queries
    public Vector2f getMouseDelta() {
        return mouseDelta;
    }

    public Vector2f getMousePos() {
        return mousePos;
    }

    public boolean isMouseCaptured() {
        return mouseCaptured;
    }

    public Vector3f getNdcPos() {
        ndcPos.x = (2*(mousePos.x)/(windowSize.x))-1;
        ndcPos.y = -(2*(mousePos.y)/(windowSize.y))+1;
        ndcPos.z = -1.0f;
        return ndcPos;
    }

//    public Vector3f getRayIntersection(Scene scene) {
//        Vector4f dir = getMouseDir(scene);
//        Vector3f rayDir = new Vector3f(
//                dir.x, dir.y, dir.z
//        );
//
//        Vector3f center = scene.getCamera().getPosition();
//        Vector3f point = new Vector3f(0.0f, 0.0f, 0.0f);
//        Vector3f normal = new Vector3f(0.0f, 1.0f, 0.0f);
//        float f = Intersectionf.intersectRayPlane(center, rayDir, point, normal, 0.5f);
//
//        //useful math for calculating intersection
//        Vector3f intersectionPoint = new Vector3f(rayDir).mul(f).add(center);
//        return intersectionPoint;
//    }
}