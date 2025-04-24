package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.*;
import org.lwjgl.InputHandler;
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
    public boolean queueCleanup = false;
    private int screenWidth;
    private int screenHeight;



    public ImGuiManager(long window, int width, int height) {
        this.window = window;
        this.windows = new ArrayList<>();
        this.screenWidth = width;
        this.screenHeight = height;
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
            for (ImGuiWindow window : windows) {
                window.init(scene);
            }
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

    public void initContinentMap(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        scene.initContinentScene();
        MenuBar menuBar = new MenuBar(imGuiManager, scene, inputHandler);
        TestWindow testWindow = new TestWindow(imGuiManager, scene, inputHandler);
        HexEditor hexEditor = new HexEditor(imGuiManager, scene, inputHandler);

        //Clear the windows before adding new ones because arrays are annoying
        windows = new ArrayList<>();

        imGuiManager.addWindow(menuBar);
        imGuiManager.addWindow(testWindow);
        imGuiManager.addWindow(hexEditor);
    }

    public void initMainMenu(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        MainMenu mainMenu = new MainMenu(imGuiManager, scene, inputHandler);

        imGuiManager.addWindow(mainMenu);
    }
    public void addWindow(ImGuiWindow window) {
        windows.add(window);
    }

    public void removeWindow(ImGuiWindow window) {
        windows.remove(window);
    }


    public ImGuiIO getIO() {
        return io;
    }
}