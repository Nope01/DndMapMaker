package org.lwjgl.UI;

import imgui.ImGui;
import org.lwjgl.Scene;
import org.lwjgl.textures.Texture;

public class GuiUtils {

    /*
    Inputs: cols, rows - Grid dimensions,
            tileNames - list of file names to be searched for in the textures folder
            scene - to get the texture cache
            selectedTexture - to maintain existing selection between render calls

    Output: selectedTexture - selected texture based on chosen tile
            several imageButtons laid out in a grid
     */
    public static boolean createTerrainGrid(int cols, int rows, String[] tileNames, int[] tileTypes, Scene scene, ContinentEditor editor) {
        int tileNameIncrement = 0;
        boolean result = false;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (x > 0) {
                    ImGui.sameLine();
                }
                ImGui.pushID(y * 4 + x);
                Texture texture = scene.getTextureCache().getTexture(tileNames[tileNameIncrement]);
                if (ImGui.imageButton(texture.getTextureId(),
                        75.0f, 75.0f)) {
                    editor.setSelectedTerrainTexture(texture);
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

    public static boolean creatIconGrid(int cols, int rows, String[] tileNames, Scene scene, ContinentEditor editor) {
        int tileNameIncrement = 0;
        boolean result = false;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (x > 0) {
                    ImGui.sameLine();
                }
                ImGui.pushID(y * 4 + x);
                Texture texture = scene.getTextureCache().getTexture(tileNames[tileNameIncrement]);
                if (ImGui.imageButton(texture.getTextureId(),
                        75.0f, 75.0f)) {
                    editor.setSelectedIconTexture(texture);
                    result = true;
                }
                ImGui.popID();
                tileNameIncrement++;
            }
        }
        ImGui.treePop();

        return result;
    }

    public static boolean createTerrainGrid(int cols, int rows, String[] tileNames, Scene scene, CityEditor editor) {
        int tileNameIncrement = 0;
        boolean result = false;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (x > 0) {
                    ImGui.sameLine();
                }
                ImGui.pushID(y * 4 + x);
                Texture texture = scene.getTextureCache().getTexture(tileNames[tileNameIncrement]);
                if (ImGui.imageButton(texture.getTextureId(),
                        75.0f, 75.0f)) {
                    editor.setSelectedTerrain(texture);
                    result = true;
                }
                ImGui.popID();
                tileNameIncrement++;
            }
        }
        ImGui.treePop();

        return result;
    }

    public static boolean createTerrainGrid(int cols, int rows, String[] tileNames, Scene scene, CombatEditor editor) {
        int tileNameIncrement = 0;
        boolean result = false;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (x > 0) {
                    ImGui.sameLine();
                }
                ImGui.pushID(y * 4 + x);
                Texture texture = scene.getTextureCache().getTexture(tileNames[tileNameIncrement]);
                if (ImGui.imageButton(texture.getTextureId(),
                        75.0f, 75.0f)) {
                    editor.setSelectedTerrain(texture);
                    result = true;
                }
                ImGui.popID();
                tileNameIncrement++;
            }
        }
        ImGui.treePop();

        return result;
    }

    public static boolean createObstacleGrid(int cols, int rows, String[] tileNames, Scene scene, CombatEditor editor) {
        int tileNameIncrement = 0;
        boolean result = false;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (x > 0) {
                    ImGui.sameLine();
                }
                ImGui.pushID(y * 4 + x);
                Texture texture = scene.getTextureCache().getTexture(tileNames[tileNameIncrement]);
                if (ImGui.imageButton(texture.getTextureId(),
                        75.0f, 75.0f)) {
                    editor.setSelectedObstacle(texture);
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

    public static boolean buttonCentered(String text) {
        setNextCenterOfWindow(text);
        if (ImGui.button(text)) {
            return true;
        }
        else {
            return false;
        }
    }
}
