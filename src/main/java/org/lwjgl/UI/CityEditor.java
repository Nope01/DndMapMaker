package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.cityMap.CityHexagon;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.objects.Trap;

public class CityEditor extends ImGuiWindow {

    private SceneObject selectedObject;
    public CityEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "City Editor");
        uiWidth = 400;
        uiHeight = 250;
        uiXPos = 0;
        uiYPos = 20;

        selectedObject = scene.getSelectedObject();

    }

    @Override
    protected void init(Scene scene) {
        ImGui.setNextWindowPos(uiXPos, uiYPos);
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        renderContent();
    }

    @Override
    protected void update() {
        selectedObject = scene.getSelectedObject();

        if (inputHandler.isLeftClicked() && selectedObject != null) {
            if (selectedObject instanceof CityHexagon) {
                ((CityHexagon) selectedObject).setMovementModifier(69);
            }

        }
    }

    @Override
    protected void renderContent() {
        ImGui.begin("City Editor", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);
        if (selectedObject != null) {
            ImGui.text(selectedObject.getId());
            if (selectedObject instanceof CityHexagon) {
                ImGui.text("Terrain: " + ((CityHexagon) selectedObject).getMovementModifier());
            }
        }

        if (ImGui.button("Show/hide traps")) {
            for (SceneObject obj : scene.getAllObjects()) {
                if (obj instanceof Trap) {
                    ((Trap) obj).swapIsHidden();
                    System.out.println(((Trap) obj).getIsHidden());
                }
            }
        }

        ImGui.end();
    }
}
