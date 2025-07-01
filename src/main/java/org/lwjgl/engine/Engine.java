package org.lwjgl.engine;

import org.lwjgl.BufferUtils;
import org.lwjgl.Scene;
import org.lwjgl.UI.ImGuiManager;
import org.lwjgl.engine.input.InputHandler;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.shaders.ShaderProgramCache;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.*;

public class Engine {
    public int width;
    public int height;
    private List<Window> windows;
    private ShaderProgramCache shaderCache;


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
        windows.add(new Window(width, height, "Main"));
        windows.add(new Window(width, height, "Secondary"));

        for (Window window: windows) {
            window.init();
        }

        initCallbacks();
    }

    private void initCallbacks() {
        for (Window window: windows) {
            glfwSetFramebufferSizeCallback(window.handle, (windowHandle, width, height) -> {
                if (width > 0 && height > 0) {
                    glViewport(0, 0, width, height);
                    window.scene.getCamera().updateProjection(width, height);
                    if (window.imGuiManager != null) {
                        window.imGuiManager.resize(width, height);
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
            window.scene.cleanup();
            glfwDestroyWindow(window.handle);
        }
        glfwTerminate();
    }
}
