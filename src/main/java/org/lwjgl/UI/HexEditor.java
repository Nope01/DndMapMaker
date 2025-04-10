package org.lwjgl.UI;

import imgui.ImGui;
import org.joml.Vector3i;
import org.lwjgl.*;
import org.lwjgl.objects.Hexagon;
import org.lwjgl.objects.SceneObject;

public class HexEditor extends ImGuiWindow{
    private ImGuiManager imGuiManager;
    private Scene scene;
    private InputHandler inputHandler;

    private SceneObject selectedObject;
    private int selectedType;
    private Texture selectedTexture;
    private int[] gridColumns;
    private int[] gridRows;
    private int oldCols;
    private int oldRows;
    private Grid grid;
    private Vector3i distance;

    public HexEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super("Hex Editor");
        this.imGuiManager = imGuiManager;
        this.scene = scene;
        this.inputHandler = inputHandler;
        this.selectedTexture = scene.getTextureCache().getTexture("default_texture");

        Grid grid = (Grid) scene.getObject("grid");
        selectedObject = scene.getSelectedObject();
        selectedType = 99;
        gridColumns = new int[]{grid.columns};
        gridRows = new int[]{grid.rows};
        oldCols = gridColumns[0];
        oldRows = gridRows[0];
        grid = scene.getObject("grid") instanceof Grid ? (Grid) scene.getObject("grid") : null;
        distance = new Vector3i();
    }

    @Override
    protected void update() {
        selectedObject = scene.getSelectedObject();
        grid = scene.getObject("grid") instanceof Grid ? (Grid) scene.getObject("grid") : null;
        if (selectedObject instanceof Hexagon) {
            distance =
                    Hexagon.cubeDistance(grid.getGrid()[0][0].getCubeCoords(),
                            ((Hexagon) selectedObject).getCubeCoords());

        }
    }

    @Override
    protected void renderContent() {
        ImGui.begin("Hex Editor");

        ImGui.text("Selected Type: " + Hexagon.getTypeAsString(selectedType));
        if (ImGui.button("Forest")) {
            selectedType = Hexagon.FOREST;
            selectedTexture = scene.getTextureCache().getTexture("grass_13");
        }
        if (ImGui.button("Plains")) {
            selectedType = Hexagon.PLAINS;
            selectedTexture = scene.getTextureCache().getTexture("grass_05");
        }
        if (ImGui.button("Desert")) {
            selectedType = Hexagon.DESERT;
            selectedTexture = scene.getTextureCache().getTexture("sand_07");
        }
        if (ImGui.button("Hill")) {
            selectedType = Hexagon.HILL;
            selectedTexture = scene.getTextureCache().getTexture("stone_07");
        }
        if (ImGui.button("Water")) {
            selectedType = Hexagon.WATER;
        }
        if (ImGui.button("Wall")) {
            selectedType = Hexagon.WALL;
            selectedTexture = scene.getTextureCache().getTexture("modern_campsite");
        }
        if (inputHandler.isLeftClicked() && selectedObject != null) {
            ((Hexagon) selectedObject).setType(selectedType);
            selectedObject.setTexture(selectedTexture);
        }

        if (ImGui.sliderInt("Grid columns", gridColumns, 0, 100)) {
            if (oldCols > gridColumns[0]) {
                grid.removeColumn(oldCols, gridColumns[0]);
            }
            if (oldCols < gridColumns[0]) {
                grid.addColumn(oldCols, gridColumns[0]);
            }
            oldCols = gridColumns[0];
        }

        if (ImGui.sliderInt("Grid rows", gridRows, 0, 100)) {
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

        if (ImGui.button("Save")) {
            grid.save();
        }

        ImGui.textUnformatted(distance.x + ", " + distance.y + ", " + distance.z);



        ImGui.end();
    }
}
