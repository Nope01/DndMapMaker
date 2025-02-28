package org.lwjgl;

import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    private long window;
    private boolean mouseCaptured;
    private Vector2f lastMousePos;
    private Vector2f mouseDelta;
    private Vector2f mousePos;

    public InputHandler(long window) {
        this.window = window;
        this.mouseCaptured = false;
        this.lastMousePos = new Vector2f();
        this.mousePos = new Vector2f();
        this.mouseDelta = new Vector2f();

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
}