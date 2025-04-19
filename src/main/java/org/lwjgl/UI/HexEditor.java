package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
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
    private Texture selectedTerrainTexture;
    private Texture selectedIconTexture;
    private boolean isTerrainSelected = true;
    private int[] gridColumns;
    private int[] gridRows;
    private int oldCols;
    private int oldRows;
    private Grid grid;
    private Vector3i distance;
    //Grid uses this to populate tile selection

    private String[] grassTileNames = new String[] {
        "grass_05",
        "grass_10",
        "grass_11",
        "grass_12",
        "grass_13",
    };

    private String[] desertTileNames = new String[] {
        "sand_07",
        "sand_12",
        "sand_13",
        "sand_14",
        "sand_15",
    };

    private String[] iconNames = new String[] {
            "tavern",
            "soda",
            "tavern (2)",
            "caravel",
            "castle",
            "anchor",
            "anvil",
            "coffer",
    };


    public HexEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super("Hex Editor");
        this.imGuiManager = imGuiManager;
        this.scene = scene;
        this.inputHandler = inputHandler;
        this.selectedTerrainTexture = scene.getTextureCache().getTexture("default_texture");
        this.selectedIconTexture = scene.getTextureCache().getTexture("default_texture");

        Grid grid = (Grid) scene.getObject("grid");
        selectedObject = scene.getSelectedObject();
        selectedType = 99;
        gridColumns = new int[]{grid.columns};
        gridRows = new int[]{grid.rows};
        oldCols = gridColumns[0];
        oldRows = gridRows[0];
        this.grid = scene.getObject("grid") instanceof Grid ? (Grid) scene.getObject("grid") : null;
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
        ImGui.begin("Hex Editor", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);

        ImGui.text("Selected Type: " + Hexagon.getTypeAsString(selectedType));
        ImGui.text("Selected Texture: " + selectedTerrainTexture.getTextureName());
        ImGui.text("Selected Icon: " + selectedIconTexture.getTextureName());

        //Tiles
        ImGui.separator();
        ImGui.setNextItemOpen(true);
        if (ImGui.treeNode("Grid", "Forest")) {
            if (GuiUtils.createTerrainGrid(1, 5, grassTileNames, scene, this)) {
                //selectedIconTexture = scene.getTextureCache().getTexture("default_texture");
                isTerrainSelected = true;
            }
        }

        ImGui.separator();
        ImGui.setNextItemOpen(true);
        if (ImGui.treeNode("Grid", "Desert")) {
            if (GuiUtils.createTerrainGrid(1, 5, desertTileNames, scene, this)) {
                //selectedIconTexture = scene.getTextureCache().getTexture("default_texture");
                isTerrainSelected = true;
            }
        }

        //Icons
        ImGui.separator();
        ImGui.setNextItemOpen(true);
        if (ImGui.treeNode("Grid", "Icons")) {
            if(GuiUtils.creatIconGrid(2, 4, iconNames, scene, this)) {
                //selectedTerrainTexture = scene.getTextureCache().getTexture("default_texture");
                isTerrainSelected = false;
            }
        }

        if (inputHandler.isLeftClicked() && selectedObject != null) {
            ((Hexagon) selectedObject).setType(selectedType);
            ((Hexagon) selectedObject).setType(Hexagon.FOREST);
            if (isTerrainSelected) {
                selectedObject.setTexture(selectedTerrainTexture);
            }
            else {
                ((Hexagon) selectedObject).setIconTexture(selectedIconTexture);
            }
        }


//        if (ImGui.sliderInt("Grid columns", gridColumns, 0, 100)) {
//            if (oldCols > gridColumns[0]) {
//                grid.removeColumn(oldCols, gridColumns[0]);
//            }
//            if (oldCols < gridColumns[0]) {
//                grid.addColumn(oldCols, gridColumns[0]);
//            }
//            oldCols = gridColumns[0];
//        }
//
//        if (ImGui.sliderInt("Grid rows", gridRows, 0, 100)) {
//            if (oldRows > gridRows[0]) {
//                grid.removeRow(oldRows, gridRows[0]);
//            }
//            else {
//                if (oldRows < gridRows[0]) {
//                    grid.addRow(oldRows, gridRows[0]);
//                }
//            }
//            oldRows = gridRows[0];
//        }
        ImGui.end();
    }

    public void setSelectedTerrainTexture(Texture texture) {
        selectedTerrainTexture = texture;
    }
    public Texture getSelectedTerrainTexture() {
        return selectedTerrainTexture;
    }
    public void setSelectedIconTexture(Texture texture) {
        selectedIconTexture = texture;
    }
    public Texture getSelectedIconTexture() {
        return selectedIconTexture;
    }
}
