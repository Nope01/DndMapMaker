package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.data.MapSaveLoad;

public class MenuBar extends ImGuiWindow {

    public boolean continentOpen = false;
    public boolean cityOpen = false;
    public boolean combatOpen = false;


    public MenuBar(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "Menu Bar");
        uiXPos = 0;
        uiYPos = 0;
        uiWidth = ImGui.getWindowWidth();
        uiHeight = ImGui.getFrameHeight();

        placeUiWindow();
    }


    @Override
    public void placeUiWindow() {
        ImGui.newFrame();
        ImGui.setNextWindowPos(uiXPos, uiYPos);
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        renderContent();
        ImGui.endFrame();
    }

    @Override
    protected void update() {

    }

    @Override
    protected void renderContent() {
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
                    scene.removeAllObjects();
                    imGuiManager.initContinentMap(imGuiManager, scene, inputHandler);
                }
            }
            if (ImGui.menuItem("City editor", cityOpen)) {
                if (!cityOpen) {
                    scene.removeAllObjects();
                    imGuiManager.initCityMap(imGuiManager, scene, inputHandler);
                }

            }
            if (ImGui.menuItem("Combat editor", combatOpen)) {
                if (!combatOpen) {

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
}
