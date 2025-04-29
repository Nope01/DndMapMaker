package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.Utils;

public class MainMenu extends ImGuiWindow {

    public MainMenu(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "Main Menu");

        uiWidth = 500;
        uiHeight = 500;
    }

    @Override
    protected void init(Scene scene) {
        Vector2f center = Utils.getCenterOfScreen(scene.getScreenWidth(), scene.getScreenHeight(), uiWidth, uiHeight);
        ImGui.setNextWindowPos(center.x, center.y);
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        renderContent();
    }

    @Override
    protected void update() {

    }

    @Override
    protected void renderContent() {
        ImGui.begin("Main Menu time",
                ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoResize);
        GuiUtils.textCentered("DND map maker");
        GuiUtils.setNextCenterOfWindow("Continent map");
        if (ImGui.button("Continent map")) {
            imGuiManager.queueCleanup = true;
            imGuiManager.initContinentMap(imGuiManager, scene, inputHandler);
        }
        GuiUtils.setNextCenterOfWindow("City map");
        if (ImGui.button("City map")) {
            imGuiManager.queueCleanup = true;
            imGuiManager.initCityMap(imGuiManager, scene, inputHandler);
        }

        ImGui.end();
    }
}
