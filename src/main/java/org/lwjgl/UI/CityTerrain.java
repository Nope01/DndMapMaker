package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector3i;
import org.lwjgl.Scene;
import org.lwjgl.cityMap.CityHexagon;
import org.lwjgl.continentMap.ContinentHexagon;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.objects.entities.Player;
import org.lwjgl.textures.Texture;

public class CityTerrain extends ImGuiWindow {

    private String[] tileNames = new String[]{
            "table",
            "barrel",
            "jungle_01",
            "sand_07",
    };

    private Texture selectedTerrain;
    private SceneObject selectedObject;
    private Vector3i[] neighbours = new Vector3i[6];

    public CityTerrain(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "City terrain");
        uiWidth = 400;
        uiHeight = 600;
        uiXPos = 0;
        uiYPos = 20+250;
    }

    @Override
    protected void init(Scene scene) {
        ImGui.setNextWindowPos(uiXPos, uiYPos);
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        renderContent();
    }

    //TODO: create obstacles and walls
    @Override
    protected void update() {
        selectedObject = scene.getHoveredObject();

        if (inputHandler.isLeftClickedAndHeld() && selectedObject != null) {
            if (selectedObject instanceof CityHexagon && selectedObject.children.isEmpty()) {
                ((CityHexagon) selectedObject).setIconTexture(selectedTerrain);
                ((CityHexagon) selectedObject).isHalfCover = true;
            }

            //Neighbours
            if (selectedObject instanceof Player) {
                if (selectedObject.parent instanceof CityHexagon) {
                    CityHexagon hexUnderPlayer = (CityHexagon) selectedObject.parent;
                    neighbours = hexUnderPlayer.getAllNeighbours();
                }
            }
        }

        //Erasing
        if (inputHandler.isRightClicked() && selectedObject != null) {
            ((CityHexagon) selectedObject).setIconTexture(scene.getTextureCache().getTexture("empty"));
            ((CityHexagon) selectedObject).isHalfCover = false;
        }

    }

    @Override
    protected void renderContent() {
        ImGui.begin("City Terrain", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);

        ImGui.separator();
        ImGui.setNextItemOpen(true);
        if (ImGui.treeNode("Grid", "Terrain")) {
            if (GuiUtils.createTerrainGrid(4, 1, tileNames, scene, this)) {
            }
        }
        ImGui.separator();
        if (selectedObject instanceof CityHexagon) {
            ImGui.text("Cover: " + ((CityHexagon) selectedObject).isHalfCover);
        }

        for (int i = 0; i < neighbours.length; i++) {
            if (neighbours[i] != null) {
                ImGui.text("Neighbour " + i + ": " + neighbours[i].x + ", " + neighbours[i].y + ", " + neighbours[i].z);
            }
        }

        ImGui.end();
    }

    public void setSelectedTerrain(Texture selectedTerrain) {
        this.selectedTerrain = selectedTerrain;
    }
}
