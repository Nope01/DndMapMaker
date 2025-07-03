package org.lwjgl.engine.input;

import imgui.ImGui;
import org.joml.*;
import org.lwjgl.Scene;
import org.lwjgl.engine.Camera;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    private long window;
    private Vector2i windowSize;
    private boolean mouseCaptured;
    private Vector2f lastMousePos;
    private Vector2f mouseDelta;
    private Vector2f mousePos;
    private Vector3f ndcPos;

    public boolean leftMouseJustPressed = false;
    public boolean rightMouseJustPressed = false;
    private int justScrolled = 0;

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

    public void update(int width, int height) {
        if (!ImGui.getIO().getWantCaptureMouse()) {
            double[] x = new double[1];
            double[] y = new double[1];
            glfwGetCursorPos(window, x, y);
            float currentX = (float) x[0];
            float currentY = (float) y[0];
            mouseDelta.set(currentX - lastMousePos.x, currentY - lastMousePos.y);
            mousePos.set((float) x[0], (float) y[0]);
            lastMousePos.set(currentX, currentY);
            windowSize.x = width;
            windowSize.y = height;
        }
    }

    private void setupCallbacks() {
        // Mouse movement callback (optional, handled in update for simplicity)
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                if (!ImGui.getIO().getWantCaptureMouse()) {
                    leftMouseJustPressed = true; // Mark as pressed this frame
                }
            }

            if (button == org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS) {
                if (!ImGui.getIO().getWantCaptureMouse()) {
                    rightMouseJustPressed = true; // Mark as pressed this frame
                }
            }
        });

        glfwSetScrollCallback(window, (window, x, y) -> {
            justScrolled = (int) y;
        });
    }

    /**
     * Checks if a specific key is currently pressed.
     *
     * @param key The key code to check, as defined by GLFW constants (e.g., GLFW_KEY_A).
     * @return true if the key is pressed, false otherwise.
     */
    public boolean isKeyPressed(int key) {
        return glfwGetKey(window, key) == GLFW_PRESS;
    }

    /**
     * Determines if the left mouse button was clicked and released.
     * <p>
     * This method checks if the left mouse button was pressed and then released
     * during the current frame. It consumes the click event by resetting the
     * `leftMouseJustPressed` flag after detecting the release.
     *
     * @return true if the left mouse button was clicked and released, false otherwise.
     */
    public boolean isLeftClicked() {
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_RELEASE && leftMouseJustPressed) {
            leftMouseJustPressed = false; // Consume the click
            System.out.println("Left mouse button clicked");
            return true;
        }
        return false;
    }

    /**
     * Checks if the left mouse button is currently clicked and held down.
     * <p>
     * This method returns true if the left mouse button is currently pressed down.
     * It does not reset any flags, allowing continuous detection while the button is held.
     *
     * @return true if the left mouse button is currently pressed, false otherwise.
     */
    public boolean isLeftClickedAndHeld() {
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            return true;
        }
        return false;
    }

    /**
     * Determines if the right mouse button was clicked and released.
     * <p>
     * This method checks if the right mouse button was pressed and then released
     * during the current frame. It consumes the click event by resetting the
     * `rightMouseJustPressed` flag after detecting the release.
     *
     * @return true if the right mouse button was clicked and released, false otherwise.
     */
    public boolean isRightClicked() {
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_RELEASE && rightMouseJustPressed) {
            rightMouseJustPressed = false; // Consume the click
            return true;
        }
        return false;
    }

    /**
     * Checks if the right mouse button is currently clicked and held down.
     * <p>
     * This method returns true if the right mouse button is currently pressed down.
     * It does not reset any flags, allowing continuous detection while the button is held.
     *
     * @return true if the right mouse button is currently pressed, false otherwise.
     */
    public boolean isRightClickedAndHeld() {
        return glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS;
    }

    /**
     * Checks if the middle mouse button is currently clicked and held down.
     * <p>
     * This method returns true if the middle mouse button is currently pressed down.
     * It does not reset any flags, allowing continuous detection while the button is held.
     *
     * @return true if the middle mouse button is currently pressed, false otherwise.
     */
    public boolean isMiddleClicked() {
        return glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_MIDDLE) == GLFW_PRESS;
    }

    /**
     * Checks if the mouse wheel was moved.
     * <p>
     * This method returns 1 if the mouse wheel was scrolled up, -1 if it was scrolled down,
     * and 0 if there was no movement. It resets the `justScrolled` variable after checking.
     *
     * @return 1 for scroll up, -1 for scroll down, 0 for no movement.
     */
    public int isMouseWheelMoved() {
        if (justScrolled > 0) {
            justScrolled = 0;
            return 1;
        }
        if (justScrolled < 0) {
            justScrolled = 0;
            return -1;
        }
        return 0;
    }


    /**
     * Retrieves the mouse movement delta since the last frame.
     * <p>
     * This method returns a `Vector2f` representing the change in mouse position
     * between the current and previous frames. The delta is calculated during the
     * `update` method and reflects the movement in screen coordinates.
     *
     * @return A `Vector2f` containing the mouse movement delta.
     */
    public Vector2f getMouseDelta() {
        return mouseDelta;
    }

    /**
     * Retrieves the current mouse position in screen coordinates.
     * <p>
     * This method returns a `Vector3f` representing the current mouse position
     * in screen coordinates (pixels). The position is updated during the `update` method.
     *
     * @return A `Vector3f` containing the current mouse position.
     */
    public Vector3f getNdcPos() {
        ndcPos.x = (2*(mousePos.x)/(windowSize.x))-1;
        ndcPos.y = -(2*(mousePos.y)/(windowSize.y))+1;
        ndcPos.z = -1.0f;
        return ndcPos;
    }

    /**
     * Calculates the world position based on the mouse position in the scene.
     * <p>
     * This method computes the intersection of a ray from the camera through the
     * mouse position with a plane at a fixed distance (0.5 units above the ground).
     * It returns the intersection point in world coordinates.
     *
     * @param camera The camera used to calculate the ray direction.
     * @return A `Vector3f` representing the world position where the ray intersects the plane.
     */
    public Vector3f getWorldPos(Camera camera) {
        Vector4f dir = getMouseDir(camera);
        Vector3f rayDir = new Vector3f(
                dir.x, dir.y, dir.z
        );

        Vector3f center = camera.getPosition();
        Vector3f point = new Vector3f(0.0f, 0.0f, 0.0f);
        Vector3f normal = new Vector3f(0.0f, 1.0f, 0.0f);
        float f = Intersectionf.intersectRayPlane(center, rayDir, point, normal, 0.5f);

        //useful math for calculating intersection
        return new Vector3f(rayDir).mul(f).add(center);
    }

    /**
     * Calculates the direction vector from the camera through the mouse position in world space.
     * <p>
     * This method computes the direction vector from the camera through the mouse position
     * in normalized device coordinates (NDC). It transforms this vector using the inverse
     * projection and view matrices to obtain a direction in world space.
     *
     * @param camera The camera used to calculate the direction.
     * @return A `Vector4f` representing the mouse direction in world space.
     */
    public Vector4f getMouseDir(Camera camera) {
        Vector3f ndc = getNdcPos();
        float x = ndc.x;
        float y = ndc.y;
        float z = -1.0f;

        Matrix4f invProjMatrix = camera.getInvProjMatrix();
        Vector4f dir = new Vector4f(x, y, z, 1.0f);
        invProjMatrix.transform(dir); // More precise than mul

        dir.z = -1.0f; dir.w = 0.0f;

        Matrix4f invViewMatrix = camera.getInvViewMatrix();
        invViewMatrix.transform(dir);
        dir.normalize(); // Ensure unit length

        return dir;
    }
}