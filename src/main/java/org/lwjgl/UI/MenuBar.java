package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.data.MapSaveLoad;

public class MenuBar extends ImGuiWindow {

    private ImGuiManager imGuiManager;
    private Scene scene;
    private InputHandler inputHandler;

    public MenuBar(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super("Menu Bar");
        this.imGuiManager = imGuiManager;
        this.scene = scene;
        this.inputHandler = inputHandler;
    }

    @Override
    protected void init(Scene scene) {

    }

    @Override
    protected void update() {

    }

    @Override
    protected void renderContent() {
        ImGui.begin("Menu Bar",
                ImGuiWindowFlags.MenuBar |
                        ImGuiWindowFlags.NoTitleBar |
                        ImGuiWindowFlags.NoMove |
                        ImGuiWindowFlags.NoResize |
                        ImGuiWindowFlags.NoBackground);

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

        ImGui.endMenuBar();
        ImGui.end();
    }
}
