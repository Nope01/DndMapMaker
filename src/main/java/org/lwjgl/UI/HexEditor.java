package org.lwjgl.UI;

import imgui.ImGui;
import org.lwjgl.*;

public class HexEditor extends ImGuiWindow{
    private ImGuiManager imGuiManager;
    private Scene scene;
    private InputHandler inputHandler;

    private SceneObject selectedObject;
    private int selectedType;
    private int[] gridColumns;
    private int[] gridRows;
    private int oldCols;
    private int oldRows;

    public HexEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super("Hex Editor");
        this.imGuiManager = imGuiManager;
        this.scene = scene;
        this.inputHandler = inputHandler;

        selectedObject = scene.getSelectedObject();
        gridColumns = new int[]{70};
        gridRows = new int[]{50};
        oldCols = gridColumns[0];
        oldRows = gridRows[0];
    }

    @Override
    protected void update() {
        selectedObject = scene.getSelectedObject();
    }

    @Override
    protected void renderContent() {
        ImGui.begin("Hex Editor");

        ImGui.text("Selected Type: " + Hexagon.getTypeAsString(selectedType));
        if (ImGui.button("Forest")) {
            selectedType = Hexagon.FOREST;
        }
        if (ImGui.button("Plains")) {
            selectedType = Hexagon.PLAINS;
        }
        if (ImGui.button("Desert")) {
            selectedType = Hexagon.DESERT;
        }
        if (ImGui.button("Hill")) {
            selectedType = Hexagon.HILL;
        }
        if (ImGui.button("Water")) {
            selectedType = Hexagon.WATER;
        }
        if (inputHandler.isLeftClicked() && selectedObject != null) {
            ((Hexagon) selectedObject).setType(selectedType);
        }

        if (ImGui.sliderInt("Grid columns", gridColumns, 0, 100)) {
            Grid grid = scene.getObject("grid") instanceof Grid ? (Grid) scene.getObject("grid") : null;
            if (oldCols > gridColumns[0]) {
                grid.removeColumn(oldCols, gridColumns[0]);
            }
            if (oldCols < gridColumns[0]) {
                grid.addColumn(oldCols, gridColumns[0]);
            }
            oldCols = gridColumns[0];
        }

        if (ImGui.sliderInt("Grid rows", gridRows, 0, 100)) {
            Grid grid = scene.getObject("grid") instanceof Grid ? (Grid) scene.getObject("grid") : null;
            if (oldRows > gridRows[0]) {
                grid.removeRow(oldRows, gridRows[0]);
            }
            else {
                if (oldRows < gridRows[0]) {
                    grid.addRow(oldRows, gridRows[0]);
                }

            }

            oldRows = gridRows[0];
        }


        ImGui.end();
    }
}
