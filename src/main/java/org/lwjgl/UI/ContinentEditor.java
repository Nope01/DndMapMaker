package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.*;
import org.lwjgl.continentMap.ContinentHexagon;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.Grid;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.textures.Texture;

public class ContinentEditor extends ImGuiWindow{

    private SceneObject selectedObject;
    private int selectedType;
    private Texture selectedTerrainTexture;
    private Texture selectedIconTexture;
    private boolean isTerrainSelected = true;


    //Grid uses this to populate tile selection
    private String[] tileNames = new String[]{
            "dead_forest_01",
            "grass_05",
            "jungle_01",
            "sand_07",
            "snow_01",
            "water_01",
    };

    private int[] tileTypes = new int[]{
            0,
            1,
            0,
            2,
            3,
            4
    };

    private String[] iconNames = new String[] {
            "tavern",
            "caravel",
            "castle",
            "anchor",
            "anvil",
            "coffer",
    };


    public ContinentEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "Continent Editor");
        uiWidth = 400;
        uiHeight = 600;
        uiXPos = 0;
        uiYPos = 20;

        this.selectedTerrainTexture = scene.getTextureCache().getTexture("default_tile");
        this.selectedIconTexture = scene.getTextureCache().getTexture("empty");

        Grid grid = (Grid) scene.getObject("grid");
        selectedObject = scene.getHoveredObject();
        selectedType = -1;
    }

    @Override
    protected void init(Scene scene) {
        ImGui.setNextWindowPos(uiXPos, uiYPos);
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        renderContent();
    }

    @Override
    protected void update() {
        selectedObject = scene.getHoveredObject();

        if (inputHandler.isLeftClicked() && selectedObject != null) {
            ((ContinentHexagon) selectedObject).setType(selectedType);
            if (isTerrainSelected) {
                selectedObject.setTexture(selectedTerrainTexture);
            }
            else {
                ((ContinentHexagon) selectedObject).setIconTexture(selectedIconTexture);
            }
        }

        //Erase the tile based on whether a tile or icon was last selected
        if (inputHandler.isRightClicked() && selectedObject != null) {
            if (isTerrainSelected) {
                ((ContinentHexagon) selectedObject).clearType();
                selectedObject.setTexture(scene.getTextureCache().getTexture("default_tile"));
            }
            else {
                ((ContinentHexagon) selectedObject).setIconTexture(scene.getTextureCache().getTexture("empty"));
            }
        }
    }

    @Override
    protected void renderContent() {
        ImGui.begin("Hex Editor", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);

        ImGui.text("Selected Type: " + ContinentHexagon.getTypeAsString(selectedType));
        ImGui.text("Selected Texture: " + selectedTerrainTexture.getTextureName());
        ImGui.text("Selected Icon: " + selectedIconTexture.getTextureName());

        //Tiles
        ImGui.separator();
        ImGui.setNextItemOpen(true);
        if (ImGui.treeNode("Grid", "Terrain")) {
            if (GuiUtils.createTerrainGrid(2, 3, tileNames, tileTypes, scene, this)) {
                //selectedIconTexture = scene.getTextureCache().getTexture("default_texture");
                isTerrainSelected = true;
            }
        }

        //Icons
        ImGui.separator();
        ImGui.setNextItemOpen(true);
        if (ImGui.treeNode("Grid", "Icons")) {
            if(GuiUtils.creatIconGrid(2, 3, iconNames, scene, this)) {
                //selectedTerrainTexture = scene.getTextureCache().getTexture("default_texture");
                isTerrainSelected = false;
            }
        }
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
    public void setSelectedType(int type) {
        selectedType = type;
    }
    public int getSelectedType() {
        return selectedType;
    }
}
