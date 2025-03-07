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
    private Vector2f mousePos;
    private Vector3f ndcPos;
    private Vector3f worldPos;
    private Vector4f mouseDir;
    private Vector3f cameraPos;
    private Vector2f cameraRot;
    private SceneObject selectedObject;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public TestWindow(ImGuiManager imGuiManager, Camera camera, Scene scene, InputHandler inputHandler) {
        super("Test UI Window"); // Window title
        this.imGuiManager = imGuiManager;
        this.camera = camera;
        this.scene = scene;
        this.inputHandler = inputHandler;
        mousePos = inputHandler.getMousePos();
        ndcPos = inputHandler.getNdcPos();
        worldPos = inputHandler.getWorldPos(scene);
        selectedObject = scene.getSelectedObject();
        mouseDir = inputHandler.getMouseDir(scene);
        cameraRot = camera.getRotation();
        cameraPos = camera.getPosition();
        imGuiManager.resize(640, 480);
    }

    @Override
    protected void update() {
        mousePos = inputHandler.getMousePos();
        ndcPos = inputHandler.getNdcPos();
        worldPos = inputHandler.getWorldPos(scene);
        selectedObject = scene.getSelectedObject();
        mouseDir = inputHandler.getMouseDir(scene);
        cameraRot = camera.getRotation();
        cameraPos = camera.getPosition();
    }

    @Override
    protected void renderContent() {
        ImGui.begin("Test UI Window");
        if (ImGui.button("Press")) {
            scene.removeAllObjects();

            Grid grid = new Grid(scene, 2, 3);
            scene.addObject(grid);
            grid.makeGrid(scene);
        }

        ImGui.textUnformatted("Mouse position: " + mousePos.x + ", " + mousePos.y);
        ImGui.textUnformatted("NDC position: " + df.format(ndcPos.x) + ", "
                + df.format(ndcPos.y) + ", "
                + df.format(ndcPos.z));

        ImGui.textUnformatted("World position: " + df.format(worldPos.x) + ", "
                + df.format(worldPos.y) + ", "
                + df.format(worldPos.z));

        ImGui.textUnformatted("Mouse dir: " + df.format(mouseDir.x)
                + ", " + df.format(mouseDir.y)
                + ", " + df.format(mouseDir.z));

        ImGui.textUnformatted("Camera rot: " + df.format(cameraRot.x)
                + ", " + df.format(cameraRot.y));

        ImGui.textUnformatted("Camera pos: " + df.format(cameraPos.x)
                + ", " + df.format(cameraPos.y)
                + ", " + df.format(cameraPos.z));


        if (selectedObject == null) {
            ImGui.textUnformatted("Null");
        }
        else {
            if (selectedObject instanceof Hexagon)
            ImGui.textUnformatted("Selected: " + ((Hexagon) selectedObject).getOffset().x + ", "
                    + ((Hexagon) selectedObject).getOffset().y);
            Vector3f pos = selectedObject.getPosition();
            ImGui.textUnformatted("Position: "
                    + df.format(pos.x) + ", "
                    + df.format(pos.y) + ", "
                    + df.format(pos.z));
        }


        ImGui.end();
    }

}