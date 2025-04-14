package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.*;
import org.lwjgl.Scene;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.getDrawData;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;

public class ImGuiManager {
    private long window;
    private ImGuiIO io;
    private List<ImGuiWindow> windows;
    private static ImGuiImplGlfw imGuiGlfw;
    private static ImGuiImplGl3 imGuiGl3;
    private boolean firstFrame = true;


    public ImGuiManager(long window) {
        this.window = window;
        this.windows = new ArrayList<>();
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
        init();
    }

    private void init() {
        // Ensure OpenGL context is active
        GL.createCapabilities();

        // Initialize ImGui
        ImGui.createContext();
        io = ImGui.getIO();

        // Initialize GLFW and OpenGL backends
        imGuiGlfw.init(window, true);
        imGuiGl3.init("#version 330 core"); // Match your shader version

        // Set initial display size
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetFramebufferSize(window, width, height);
        io.setDisplaySize(width[0], height[0]);
    }

    public void update(float deltaTime, Scene scene) {
        // Start new ImGui frame
        imGuiGlfw.newFrame();
        imGuiGl3.newFrame();
        ImGui.newFrame();

        // Update and render all registered windows
        for (ImGuiWindow window : windows) {
            window.update();
        }

        if (firstFrame) {
            //Debug
            ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
            ImGui.setNextWindowSize(500, 200);
            windows.get(0).render();

            //Hex
            ImGui.setNextWindowPos(0, 200, ImGuiCond.Always);
            ImGui.setNextWindowSize(500, 900);
            windows.get(1).render();

            firstFrame = false;
        }
        else {
            for (ImGuiWindow window : windows) {
                window.render();
            }
        }

        // End frame and render
        ImGui.render();
        imGuiGl3.renderDrawData(getDrawData());
    }

    public void resize(int width, int height) {
        if (width > 0 && height > 0) {
            io.setDisplaySize(width, height);
        }
    }

    public void cleanup() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }

    public void addWindow(ImGuiWindow window) {
        windows.add(window);
    }

    public ImGuiIO getIO() {
        return io;
    }
}