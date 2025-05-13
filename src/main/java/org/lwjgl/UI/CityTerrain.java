package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.lwjgl.Scene;
import org.lwjgl.input.InputHandler;
import org.lwjgl.textures.Texture;

public class CityTerrain extends ImGuiWindow {

    private String[] tileNames = new String[]{
            "dead_forest_01",
            "grass_05",
            "jungle_01",
            "sand_07",
            "snow_01",
            "water_01",
    };

    private Texture selectedTerrain;
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

    @Override
    protected void update() {

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
        ImGui.end();
    }

    public void setSelectedTerrain(Texture selectedTerrain) {
        this.selectedTerrain = selectedTerrain;
    }
}
