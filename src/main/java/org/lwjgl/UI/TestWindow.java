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
    private SceneObject hoveredObject;
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
        hoveredObject = scene.getHoveredObject();
        if (hoveredObject instanceof ContinentHexagon) {
            type = ((ContinentHexagon) hoveredObject).getTypeAsString();
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
        hoveredObject = scene.getHoveredObject();
        if (hoveredObject instanceof ContinentHexagon) {
            type = ((ContinentHexagon) hoveredObject).getTypeAsString();
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
        if (hoveredObject == null) {
            ImGui.textUnformatted("Null");
        }
        else {
            if (hoveredObject instanceof ContinentHexagon)
            ImGui.textUnformatted("Cube coords: " + ((ContinentHexagon) hoveredObject).getCubeCoords().x + ", "
                    + ((ContinentHexagon) hoveredObject).getCubeCoords().y + " ,"
                    + ((ContinentHexagon) hoveredObject).getCubeCoords().z);
            ImGui.textUnformatted("Offset coords: " + ((ContinentHexagon) hoveredObject).getOffsetCoords().x + ", "
                    + ((ContinentHexagon) hoveredObject).getOffsetCoords().y);
            ImGui.textUnformatted("Tile type: " + type);
            ImGui.textUnformatted("ID: " + hoveredObject.getId());
            ImGui.textUnformatted("Colour: " + hoveredObject.getColour().x + ", "
                    + hoveredObject.getColour().y + ", "
                    + hoveredObject.getColour().z);
        }


        ImGui.end();
    }

}