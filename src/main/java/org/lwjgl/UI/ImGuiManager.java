package org.lwjgl.UI;

import imgui.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.*;
import org.lwjgl.data.MapSaveLoad;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.opengl.GL;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static imgui.ImGui.getDrawData;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;

public class ImGuiManager {
    private long window;
    private ImGuiIO io;
    private List<ImGuiWindow> windows;
    private static ImGuiImplGlfw imGuiGlfw;
    private static ImGuiImplGl3 imGuiGl3;
    private boolean firstFrame = true;
    private int screenWidth;
    private int screenHeight;

    private float fontSize;
    private float scale = 1.5f;
    public boolean scaleFont = false;

    public boolean continentOpen = false;
    public boolean cityOpen = false;
    public boolean combatOpen = false;


    public ImGuiManager(long window, int width, int height) {
        this.window = window;
        this.windows = new CopyOnWriteArrayList<>();
        this.screenWidth = width;
        this.screenHeight = height;
        this.fontSize = 16f * scale;
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

        resizeFont();
    }

    public void update(float deltaTime, Scene scene, InputHandler inputHandler) {
        if (scaleFont) {
            resizeFont();
            scaleFont = false;
        }
        // Start new ImGui frame
        imGuiGlfw.newFrame();
        imGuiGl3.newFrame();
        ImGui.newFrame();

        // Update and render all registered windows
        for (ImGuiWindow window : windows) {
            window.update();
        }

        for (ImGuiWindow window : windows) {
            window.render();
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

    public void rescaleAllWindows() {
        for (ImGuiWindow window : windows) {
            if (!Objects.equals(window.title, "Menu Bar")) {
                window.uiWidth *= scale;
                window.uiHeight *= scale;
                window.rescaleWindow();
                window.resizeWindow();
            }
        }
    }

    public void cleanup() {
        imGuiGl3.shutdown();
        imGuiGlfw.shutdown();
        ImGui.destroyContext();
    }

    //TODO: make menu bar not break everything when changing map modes
    public void initContinentMap(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        scene.initContinentScene();

        windows = new CopyOnWriteArrayList<>();

        continentOpen = true;

        ContinentEditor continentEditor = new ContinentEditor(imGuiManager, scene, inputHandler);
        imGuiManager.addWindow(continentEditor);
        firstFrame = true;
    }

    public void initCityMap(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        scene.initCityScene();

        windows = new CopyOnWriteArrayList<>();

        cityOpen = true;

        CityEditor cityEditor = new CityEditor(imGuiManager, scene, inputHandler);
        imGuiManager.addWindow(cityEditor);

        InitiativeTracker initiativeTracker = new InitiativeTracker(imGuiManager, scene, inputHandler);
        imGuiManager.addWindow(initiativeTracker);

        firstFrame = true;
    }

    public void initCombatMap(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        combatOpen = true;
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

    public ImGuiWindow getWindow(String title) {
        for (ImGuiWindow window : windows) {
            if (Objects.equals(window.title, title)) {
                return window;
            }
        }
        System.out.println("Failed to find window with title: " + title);
        return null;
    }


    private void resizeFont() {
        io.getFonts().clear();

        try {
            InputStream fontStream = getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf");
            if (fontStream != null) {
                Path tempFont = Files.createTempFile("imgui-font", ".ttf");
                Files.copy(fontStream, tempFont, StandardCopyOption.REPLACE_EXISTING);

                io.getFonts().addFontFromFileTTF(tempFont.toString(), fontSize);
                tempFont.toFile().deleteOnExit();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to default font if anything fails
            ImFontConfig fontConfig = new ImFontConfig();
            fontConfig.setSizePixels(fontSize);
            io.getFonts().addFontDefault(fontConfig);
        }

        io.getFonts().build();
        imGuiGl3.destroyFontsTexture();
        imGuiGl3.createFontsTexture();
    }

    public void drawMainMenu(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        ImGui.beginMenuBar();
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save")) {
                scene.saveMap();
            }
            if (ImGui.menuItem("Load")) {
                scene.loadMap();
            }
            if (ImGui.menuItem("Screenshot")) {
                scene.saveImage();
            }
            if (ImGui.menuItem("Test")) {
                MapSaveLoad.fileOverridePopup();
            }
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Window")) {
            if (ImGui.menuItem("Continent editor", continentOpen)) {
                if (!continentOpen) {
                    cityOpen = false;
                    combatOpen = false;
                    scene.removeAllObjects();
                    imGuiManager.initContinentMap(imGuiManager, scene, inputHandler);
                }
            }
            if (ImGui.menuItem("City editor", cityOpen)) {
                if (!cityOpen) {
                    continentOpen = false;
                    combatOpen = false;
                    scene.removeAllObjects();
                    imGuiManager.initCityMap(imGuiManager, scene, inputHandler);
                }
            }
            if (ImGui.menuItem("Combat editor", combatOpen)) {
                if (!combatOpen) {
                    cityOpen = false;
                    continentOpen = false;
                    scene.removeAllObjects();
                    imGuiManager.initCombatMap(imGuiManager, scene, inputHandler);
                }
            }
            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Tools")) {
            if (ImGui.menuItem("Initiative tracker")) {
                InitiativeTracker initiativeTracker = new InitiativeTracker(imGuiManager, scene, inputHandler);
                imGuiManager.addWindow(initiativeTracker);
                initiativeTracker.placeUiWindow();
            }
            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }

    public ImGuiIO getIO() {
        return io;
    }

    public List<ImGuiWindow> getWindows() {
        return windows;
    }

    public float getScale() {
        return scale;
    }
    public void setScale(float scale) {
        this.scale = scale;
        this.fontSize *= scale;

        rescaleAllWindows();
        scaleFont = true;
    }
}