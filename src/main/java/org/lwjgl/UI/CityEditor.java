package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector3f;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.cityMap.CityHexagon;
import org.lwjgl.objects.*;
import org.lwjgl.objects.entities.Player;

import static org.lwjgl.objects.Hexagon.areaSelectClear;
import static org.lwjgl.objects.Hexagon.showMovementRange;

public class CityEditor extends ImGuiWindow {

    private SceneObject hoveredObject;
    private SceneObject selectedObject;
    private Grid gridClass;
    private Hexagon[][] grid;
    private int viewRadius = 4;
    public CityEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "City Editor");
        uiWidth = 400;
        uiHeight = 250;
        uiXPos = 0;
        uiYPos = 20;

        hoveredObject = scene.getHoveredObject();
        grid = scene.getGrid().getGrid();
        gridClass = scene.getGrid();
    }

    @Override
    protected void init(Scene scene) {
        ImGui.setNextWindowPos(uiXPos, uiYPos);
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        renderContent();
    }

    @Override
    protected void update() {
        hoveredObject = scene.getHoveredObject();
        grid = scene.getGrid().getGrid();
        gridClass = scene.getGrid();
        boolean clickInput = inputHandler.isLeftClicked();

        if (hoveredObject != null) {
            if (hoveredObject instanceof Trap) {
                hoveredObject = hoveredObject.parent;
            }
        }

        //Movement logic
        if (clickInput && selectedObject instanceof Player) {
            if (((Player) selectedObject).canMoveCreature(selectedObject, hoveredObject)) {
                selectedObject.setParent(hoveredObject);
                selectedObject.initAabb();
            }
        }

        //Selection logic
        if (clickInput && hoveredObject != null) {
            areaSelectClear(gridClass);
            if (selectedObject != null) {
                selectedObject.selected = false;
            }
            selectedObject = hoveredObject;
            selectedObject.selected = true;

            //Highlight moveable tiles
            if (selectedObject instanceof Player) {
                showMovementRange(gridClass, (Hexagon) selectedObject.parent, ((Player) selectedObject).moveSpeed);
            }
        }

        if (selectedObject != null && inputHandler.isRightClicked()) {
            selectedObject.selected = false;
            selectedObject = null;
            areaSelectClear(gridClass);
        }



    }

    @Override
    protected void renderContent() {
        ImGui.begin("City Editor", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);

        if (ImGui.button("Show/hide traps")) {
            for (SceneObject obj : scene.getAllObjects()) {
                if (obj instanceof TileTrigger) {
                    ((TileTrigger) obj).swapIsHidden();
                }
            }
        }
        if (selectedObject != null) {
            ImGui.text("Selected: " + selectedObject.getId());
        }
        if (hoveredObject != null) {
            ImGui.text("Hovered: " + hoveredObject.getId());
        }

        if (ImGui.button("New character")) {
            ImGui.openPopup("Create a character");
        }
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center, ImGuiCond.Appearing, new ImVec2(0.5f, 0.5f));

        if (ImGui.beginPopupModal("Create a character",
                ImGuiWindowFlags.NoResize
                        | ImGuiWindowFlags.NoMove)) {
            ImGui.text("Bingus");
            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }


        ImGui.end();
    }
}
