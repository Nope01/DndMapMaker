package org.lwjgl.engine;

import org.lwjgl.glfw.GLFWVidMode;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.glViewport;

public class Engine {
    public int width;
    public int height;
    private List<Window> windows;


    public Engine() {
        // Initialize GLFW
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Monitor size
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        width = vidmode.width();
        height = vidmode.height();

        //Windows
        windows = new ArrayList<>();
        Window mainWindow = new Window(width, height, "Main");
        //Window secondaryWindow = new Window(width, height, "Secondary");

        mainWindow.initMainWindow(this);
        //secondaryWindow.initSecondaryWindow(this);

        windows.add(mainWindow);
        //windows.add(secondaryWindow);

        initCallbacks();
    }

    private void initCallbacks() {
        for (Window window: windows) {
            glfwSetFramebufferSizeCallback(window.handle, (windowHandle, width, height) -> {
                if (width > 0 && height > 0) {
                    if (window.scene != null) {
                        glViewport(0, 0, width, height);
                        window.scene.getCamera().updateProjection(width, height);
                        if (window.imGuiManager != null) {
                            window.imGuiManager.resize(width, height);
                        }
                    }
                }
            });
        }
    }

    public void loop() {
        while (!glfwWindowShouldClose(windows.get(0).handle)) {
            for (Window window : windows) {
                window.loop();
            }
        }
    }

    public void cleanup() {

        for (Window window: windows) {
            if (window.scene != null) {
                window.scene.cleanup();
                glfwDestroyWindow(window.handle);
            }
        }
        glfwTerminate();
    }

    public Window getMainWindow() {
        return windows.get(0);
    }

    public Window getSecondaryWindow() {
        if (windows.size() < 2) {
            return null; // No secondary window available
        }
        return windows.get(1);
    }
}
