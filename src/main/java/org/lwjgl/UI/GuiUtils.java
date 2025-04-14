package org.lwjgl.UI;

import imgui.ImGui;
import org.lwjgl.Scene;
import org.lwjgl.Texture;

public class GuiUtils {

    /*
    Inputs: cols, rows - Grid dimensions,
            tileNames - list of file names to be searched for in the textures folder
            scene - to get the texture cache
            selectedTexture - to maintain existing selection between render calls

    Output: selectedTexture - selected texture based on chosen tile
            several imageButtons laid out in a grid
     */
    public static Texture createTerrainGrid(int cols, int rows, String[] tileNames, Scene scene, Texture selectedTexture) {
        int tileNameIncrement = 0;
        for (int y = 0; y < cols; y++) {
            for (int x = 0; x < rows; x++) {
                if (x > 0) {
                    ImGui.sameLine();
                }
                ImGui.pushID(y * 4 + x);
                if (ImGui.imageButton(scene.getTextureCache().getTexture(tileNames[tileNameIncrement]).getTextureId(),
                        75.0f, 75.0f)) {
                    selectedTexture = scene.getTextureCache().getTexture(tileNames[tileNameIncrement]);
                }
                ImGui.popID();
                tileNameIncrement++;
            }
        }
        ImGui.treePop();

        return selectedTexture;
    }
}
