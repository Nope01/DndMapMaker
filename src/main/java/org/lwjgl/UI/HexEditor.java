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

    public HexEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super("Hex Editor");
        this.imGuiManager = imGuiManager;
        this.scene = scene;
        this.inputHandler = inputHandler;

        selectedObject = scene.getSelectedObject();
        gridColumns = new int[]{70};
        gridRows = new int[]{50};
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
            scene.removeAllObjects();
            Grid grid = new Grid(scene, gridColumns[0], gridRows[0]);
            scene.addObject(grid);
        }

        if (ImGui.sliderInt("Grid rows", gridRows, 0, 100)) {
            scene.removeAllObjects();
            Grid grid = new Grid(scene, gridColumns[0], gridRows[0]);
            scene.addObject(grid);
        }

        ImGui.end();
    }
}
