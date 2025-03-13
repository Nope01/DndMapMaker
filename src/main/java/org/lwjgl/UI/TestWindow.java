package org.lwjgl.UI;

import imgui.ImGui;
import imgui.type.ImFloat;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.*;

import java.text.DecimalFormat;

public class TestWindow extends ImGuiWindow {
    private ImGuiManager imGuiManager;
    private Camera camera;
    private Scene scene;
    private InputHandler inputHandler;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private Vector3f worldPos;
    private Vector3f cameraPos;
    private SceneObject selectedObject;
    private String type;


    public TestWindow(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super("Test UI Window"); // Window title
        this.imGuiManager = imGuiManager;
        this.scene = scene;
        this.inputHandler = inputHandler;

        worldPos = inputHandler.getWorldPos(scene);
        camera = scene.getCamera();
        cameraPos = camera.getPosition();
        selectedObject = scene.getSelectedObject();
        if (selectedObject instanceof Hexagon) {
            type = ((Hexagon) selectedObject).getTypeAsString();
        }

    }

    @Override
    protected void update() {
        worldPos = inputHandler.getWorldPos(scene);
        cameraPos = camera.getPosition();
        selectedObject = scene.getSelectedObject();
        if (selectedObject instanceof Hexagon) {
            type = ((Hexagon) selectedObject).getTypeAsString();
        }
    }

    @Override
    protected void renderContent() {
        ImGui.begin("Test UI Window");
        if (ImGui.button("Press")) {
            scene.removeAllObjects();

            Grid grid = new Grid(scene, 2, 3);
            scene.addObject(grid);
        }

        ImGui.textUnformatted("World position: " + df.format(worldPos.x) + ", "
                + df.format(worldPos.y) + ", "
                + df.format(worldPos.z));

        ImGui.textUnformatted("Camera pos: " + df.format(cameraPos.x)
                + ", " + df.format(cameraPos.y)
                + ", " + df.format(cameraPos.z));


        if (selectedObject == null) {
            ImGui.textUnformatted("Null");
        }
        else {
            if (selectedObject instanceof Hexagon)
            ImGui.textUnformatted("Cube: " + ((Hexagon) selectedObject).getCubeCoords().x + ", "
                    + ((Hexagon) selectedObject).getCubeCoords().y + " ,"
                    + ((Hexagon) selectedObject).getCubeCoords().z);
            ImGui.textUnformatted("Type: " + type);
            ImGui.textUnformatted("ID: " + selectedObject.getId());
            ImGui.textUnformatted("Offset: " + ((Hexagon) selectedObject).getOffsetCoords().x + ", "
                    + ((Hexagon) selectedObject).getOffsetCoords().y);
            ImGui.textUnformatted("Colour: " + selectedObject.getColour().x + ", "
                    + selectedObject.getColour().y + ", "
                    + selectedObject.getColour().z);
        }


        ImGui.end();
    }

}