package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector3i;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.cityMap.CityHexagon;
import org.lwjgl.objects.*;

public class CityEditor extends ImGuiWindow {

    private SceneObject hoveredObject;
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

        if (inputHandler.isLeftClicked() && hoveredObject != null) {
            if (hoveredObject instanceof CityHexagon) {
                ((CityHexagon) hoveredObject).setMovementModifier(69);
            }
        }

        //Area select
//        for (int i = 0; i < grid.length; i++) {
//            for (int j = 0; j < grid[i].length; j++) {
//                if (selectedObject instanceof Hexagon) {
//                    if (grid[i][j].cubeDistance(((Hexagon) selectedObject).getCubeCoords()) < viewRadius) {
//                        grid[i][j].selected = true;
//                    }
//                }
//            }
//        }
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
            Vector3i target = gridClass.getHexagonAt(20, 40).getCubeCoords();
            Trap trap = (Trap) gridClass.getHexagonAt(20, 40).children.get(0);
            int distance = ((CityHexagon) hoveredObject).cubeDistance(target);
            if (distance < viewRadius) {
                ImGui.text("I can see you");
            }
            if (distance < trap.getTriggerRadius()) {
                ImGui.text("Boom");
            }
        }

        ImGui.end();
    }
}
