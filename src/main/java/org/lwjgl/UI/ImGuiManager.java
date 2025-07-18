package org.lwjgl.UI;

import imgui.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.*;
import org.lwjgl.data.CombatFileManager;
import org.lwjgl.engine.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.objects.entities.Creature;
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

    private CombatFileManager combatFileManager;



    public ImGuiManager(long window, int width, int height) {
        this.window = window;
        this.windows = new CopyOnWriteArrayList<>();
        this.screenWidth = width;
        this.screenHeight = height;
        this.fontSize = 16f * scale;
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
        combatFileManager = new CombatFileManager();
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

    public void initContinentMap(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        scene.initContinentScene();

        windows = new CopyOnWriteArrayList<>();

        continentOpen = true;

        ContinentEditor continentEditor = new ContinentEditor(imGuiManager, scene, inputHandler);
        imGuiManager.addWindow(continentEditor);
    }

    public void initCityMap(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        scene.initCityScene();

        windows = new CopyOnWriteArrayList<>();

        cityOpen = true;

        CityEditor cityEditor = new CityEditor(imGuiManager, scene, inputHandler);
        imGuiManager.addWindow(cityEditor);
    }

    public void initCombatMap(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        scene.initCombatScene();

        windows = new CopyOnWriteArrayList<>();

        combatOpen = true;

        CombatEditor combatEditor = new CombatEditor(imGuiManager, scene, inputHandler);
        imGuiManager.addWindow(combatEditor);

        InitiativeTracker initiativeTracker = new InitiativeTracker(imGuiManager, scene, inputHandler);
        imGuiManager.addWindow(initiativeTracker);
        initiativeTracker.placeUiWindow();
    }

    public void initMainMenu(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        MainMenu mainMenu = new MainMenu(imGuiManager, scene, inputHandler);

        imGuiManager.addWindow(mainMenu);
    }

    public void addWindow(ImGuiWindow window) {
        windows.add(window);
    }

    public void removeWindow(ImGuiWindow window) {
        if (window != null) {
            windows.remove(window);
        }
    }

    public ImGuiWindow getWindow(String title) {
        for (ImGuiWindow window : windows) {
            if (Objects.equals(window.title, title)) {
                return window;
            }
        }
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

    //TODO: make the saving items look nicer
    //TODO: create methods for repeated code
    //TODO: use fileManagers within this instead of scene
    //TODO: check for what is likely many more bugs with saving/loading
    public void drawMenuBar(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        ImGui.beginMenuBar();
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save")) {
                if (continentOpen) {
                    scene.saveContinentMap();
                }
            }
            if (ImGui.menuItem("Load")) {
                if (continentOpen) {
                    scene.loadContinentMap();
                }
            }
            if (ImGui.menuItem("Screenshot")) {
                scene.saveImage();
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
                imGuiManager.removeWindow(imGuiManager.getWindow("Initiative Tracker"));
                InitiativeTracker initiativeTracker = new InitiativeTracker(imGuiManager, scene, inputHandler);
                imGuiManager.addWindow(initiativeTracker);
                initiativeTracker.placeUiWindow();
            }

            ImGui.endMenu();
        }

        if (combatOpen) {
            if (ImGui.beginMenu("Map")) {
                if (ImGui.menuItem("Save##map")) {
                    combatFileManager.saveMapFile(scene.getGrid());
                }

                if (ImGui.menuItem("Load##map")) {
                    //TODO: reset walls and obstacle states
                    scene.getGrid().makeGridFromLoadedGrid(combatFileManager.loadMapFile());

                    CombatEditor combatEditor = (CombatEditor) imGuiManager.getWindow("Combat Editor");
                    combatEditor.remakeSameCharacterList();

                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Characters")) {
                if (ImGui.menuItem("Save##characters")) {
                    CombatEditor combatEditor = (CombatEditor) imGuiManager.getWindow("Combat Editor");
                    combatFileManager.saveCharacterFile(combatEditor.getCharacterList());
                }

                if (ImGui.menuItem("Load##characters")) {
                    CombatEditor combatEditor = (CombatEditor) imGuiManager.getWindow("Combat Editor");
                    combatEditor.clearCharacterList();
                    List<Creature> characterList = combatFileManager.loadCharacterFile();

                    combatEditor.remakeLoadedCharacterList(characterList);
                }
                ImGui.endMenu();
            }
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