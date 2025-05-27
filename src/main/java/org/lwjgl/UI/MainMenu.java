package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.utils.HelperMethods;

import java.text.NumberFormat;

public class MainMenu extends ImGuiWindow {

    public MainMenu(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "Main Menu");

        uiWidth = 500;
        uiHeight = 500;
        Vector2f center = HelperMethods.getCenterOfScreen(scene.getScreenWidth(), scene.getScreenHeight(), uiWidth, uiHeight);
        uiXPos = center.x;
        uiYPos = center.y;

        placeUiWindow();
    }

    @Override
    public void placeUiWindow() {
        //Main menu needs new frame, as it happens before the main game loop
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
        ImGui.begin("Main Menu time",
                ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoResize);
        GuiUtils.textCentered("DND map maker");

        if (GuiUtils.buttonCentered("Continent map")) {
            imGuiManager.initContinentMap(imGuiManager, scene, inputHandler);
            imGuiManager.removeWindow(this);
        }

        if (GuiUtils.buttonCentered("City map")) {
            imGuiManager.initCityMap(imGuiManager, scene, inputHandler);
            imGuiManager.removeWindow(this);
        }

        if (GuiUtils.buttonCentered("Combat map")) {
            imGuiManager.initCombatMap(imGuiManager, scene, inputHandler);
            imGuiManager.removeWindow(this);
        }

        NumberFormat nf = NumberFormat.getInstance();
        ImGui.separator();
        ImGui.text("Mouse pos: " + nf.format(inputHandler.getMousePos().x) + ", " + nf.format(inputHandler.getMousePos().y));
        ImGui.text("Delta: " + nf.format(inputHandler.getMouseDelta().x) + ", " + nf.format(inputHandler.getMouseDelta().y));
        ImGui.text("NDC pos: " + nf.format(inputHandler.getNdcPos().x) + ", " + nf.format(inputHandler.getNdcPos().y));
        ImGui.text("World pos: " + nf.format(inputHandler.getWorldPos(scene).x) + ", " + nf.format(inputHandler.getWorldPos(scene).z));

        ImGui.end();
    }
}
