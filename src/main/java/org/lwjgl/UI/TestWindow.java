package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.continentMap.ContinentHexagon;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.SceneObject;

import java.text.DecimalFormat;

public class TestWindow extends ImGuiWindow {
    private Camera camera;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private Vector3f worldPos;
    private Vector3f cameraPos;
    private SceneObject selectedObject;
    private String type;


    public TestWindow(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "Test Window");
        uiWidth = 400;
        uiHeight = 250;
        uiXPos = 0;
        uiYPos = 20;

        worldPos = inputHandler.getWorldPos(scene);
        camera = scene.getCamera();
        cameraPos = camera.getPosition();
        selectedObject = scene.getSelectedObject();
        if (selectedObject instanceof ContinentHexagon) {
            type = ((ContinentHexagon) selectedObject).getTypeAsString();
        }
    }

    @Override
    protected void init(Scene scene) {
        ImGui.setNextWindowPos(uiXPos, uiYPos);
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        renderContent();
    }

    @Override
    protected void update() {
        worldPos = inputHandler.getWorldPos(scene);
        cameraPos = camera.getPosition();
        selectedObject = scene.getSelectedObject();
        if (selectedObject instanceof ContinentHexagon) {
            type = ((ContinentHexagon) selectedObject).getTypeAsString();
        }
    }

    @Override
    protected void renderContent() {
        ImGui.begin("Test UI Window", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);
        ImGui.textUnformatted("Mouse pos (world): " + df.format(worldPos.x) + ", "
                + df.format(worldPos.y) + ", "
                + df.format(worldPos.z));

        ImGui.textUnformatted("Camera pos: " + df.format(cameraPos.x)
                + ", " + df.format(cameraPos.y)
                + ", " + df.format(cameraPos.z));


        ImGui.separator();
        ImGui.text("Hexagon details");
        if (selectedObject == null) {
            ImGui.textUnformatted("Null");
        }
        else {
            if (selectedObject instanceof ContinentHexagon)
            ImGui.textUnformatted("Cube coords: " + ((ContinentHexagon) selectedObject).getCubeCoords().x + ", "
                    + ((ContinentHexagon) selectedObject).getCubeCoords().y + " ,"
                    + ((ContinentHexagon) selectedObject).getCubeCoords().z);
            ImGui.textUnformatted("Offset coords: " + ((ContinentHexagon) selectedObject).getOffsetCoords().x + ", "
                    + ((ContinentHexagon) selectedObject).getOffsetCoords().y);
            ImGui.textUnformatted("Tile type: " + type);
            ImGui.textUnformatted("ID: " + selectedObject.getId());
            ImGui.textUnformatted("Colour: " + selectedObject.getColour().x + ", "
                    + selectedObject.getColour().y + ", "
                    + selectedObject.getColour().z);
        }


        ImGui.end();
    }

}