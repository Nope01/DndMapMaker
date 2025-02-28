package org.lwjgl.UI;

import imgui.ImGui;
import imgui.type.ImFloat;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.*;

import java.text.DecimalFormat;

public class TestWindow extends ImGuiWindow {
    private ImGuiManager imGuiManager;
    private Camera camera;
    private Scene scene;

    private InputHandler inputHandler;
    private Vector2f mousePos;
    private Vector3f ndcPos;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public TestWindow(ImGuiManager imGuiManager, Camera camera, Scene scene, InputHandler inputHandler) {
        super("Test UI Window"); // Window title
        this.imGuiManager = imGuiManager;
        this.camera = camera;
        this.scene = scene;
        this.inputHandler = inputHandler;
        mousePos = inputHandler.getMousePos();
        ndcPos = inputHandler.getNdcPos();
        imGuiManager.resize(640, 480);
    }

    @Override
    protected void update() {
        mousePos = inputHandler.getMousePos();
        ndcPos = inputHandler.getNdcPos();
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
        ImGui.end();
    }

}