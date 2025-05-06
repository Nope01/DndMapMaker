package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
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

        if (hoveredObject != null) {
            if (hoveredObject instanceof Trap) {
                hoveredObject = hoveredObject.parent;
            }
        }

        //Selection logic
        if (inputHandler.isLeftClicked() && hoveredObject != null) {
            areaSelectClear(gridClass);
            if (selectedObject != null) {
                selectedObject.selected = false;
            }
            selectedObject = hoveredObject;
            selectedObject.selected = true;
        }

        //Highlight moveable tiles
        if (selectedObject instanceof Player) {
            showMovementRange(gridClass, (Hexagon) selectedObject.parent, ((Player) selectedObject).moveSpeed);
        }
    }

    @Override
    protected void renderContent() {
        ImGui.begin("City Editor", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);
        if (hoveredObject != null) {
            ImGui.text(hoveredObject.getId());
            if (hoveredObject instanceof CityHexagon) {
                ImGui.text("Terrain: " + ((CityHexagon) hoveredObject).getMovementModifier());
            }
        }

        if (ImGui.button("Show/hide traps")) {
            for (SceneObject obj : scene.getAllObjects()) {
                if (obj instanceof TileTrigger) {
                    ((TileTrigger) obj).swapIsHidden();
                }
            }
        }

        //Trap trigger testing
        if (hoveredObject instanceof CityHexagon) {
            Trap trap = (Trap) gridClass.getHexagonAt(20, 40).children.get(0);
            if (trap.isInRange(((CityHexagon) hoveredObject).getCubeCoords())) {
                ImGui.text("Boom");
            }
        }

        if (selectedObject != null) {
            ImGui.text(selectedObject.getId());
        }

        ImGui.end();
    }
}
