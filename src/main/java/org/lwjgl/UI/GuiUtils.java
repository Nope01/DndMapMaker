package org.lwjgl.UI;

import imgui.ImGui;
import org.lwjgl.Scene;

public class GuiUtils {

    /*
    Inputs: cols, rows - Grid dimensions,
            tileNames - list of file names to be searched for in the textures folder
            scene - to get the texture cache
            selectedTexture - to maintain existing selection between render calls

    Output: selectedTexture - selected texture based on chosen tile
            several imageButtons laid out in a grid
     */
    public static boolean createTerrainGrid(int cols, int rows, String[] tileNames, int[] tileTypes, Scene scene, HexEditor editor) {
        int tileNameIncrement = 0;
        boolean result = false;

        for (int y = 0; y < cols; y++) {
            for (int x = 0; x < rows; x++) {
                if (x > 0) {
                    ImGui.sameLine();
                }
                ImGui.pushID(y * 4 + x);
                if (ImGui.imageButton(scene.getTextureCache().getTexture(tileNames[tileNameIncrement]).getTextureId(),
                        75.0f, 75.0f)) {
                    editor.setSelectedTerrainTexture(scene.getTextureCache().getTexture(tileNames[tileNameIncrement]));
                    editor.setSelectedType(tileTypes[tileNameIncrement]);
                    result = true;
                }
                ImGui.popID();
                tileNameIncrement++;
            }
        }
        ImGui.treePop();

        return result;
    }

    public static boolean creatIconGrid(int cols, int rows, String[] tileNames, Scene scene, HexEditor editor) {
        int tileNameIncrement = 0;
        boolean result = false;

        for (int y = 0; y < cols; y++) {
            for (int x = 0; x < rows; x++) {
                if (x > 0) {
                    ImGui.sameLine();
                }
                ImGui.pushID(y * 4 + x);
                if (ImGui.imageButton(scene.getTextureCache().getTexture(tileNames[tileNameIncrement]).getTextureId(),
                        75.0f, 75.0f)) {
                    editor.setSelectedIconTexture(scene.getTextureCache().getTexture(tileNames[tileNameIncrement]));
                    result = true;
                }
                ImGui.popID();
                tileNameIncrement++;
            }
        }
        ImGui.treePop();

        return result;
    }


    public static void setNextCenterOfWindow(String text) {
        float windowWidth = ImGui.getWindowWidth();
        float textWidth = ImGui.calcTextSizeX(text);

        ImGui.newLine();
        ImGui.sameLine((windowWidth/2) - (textWidth/2));
    }
    public static void textCentered(String text) {
        setNextCenterOfWindow(text);
        ImGui.text(text);
    }
}
