package org.lwjgl.UI;

import imgui.ImGui;
import imgui.type.ImFloat;
import org.lwjgl.*;

public class TestWindow extends ImGuiWindow {
    private ImGuiManager imGuiManager;
    private Camera camera;
    private Scene scene;
    private ImFloat rotationSpeed; // For controlling scene rotation
    private boolean showDemoWindow; // Toggle for ImGui demo

    public TestWindow(ImGuiManager imGuiManager, Camera camera, Scene scene) {
        super("Test UI Window"); // Window title
        this.imGuiManager = imGuiManager;
        this.camera = camera;
        this.scene = scene;
        this.rotationSpeed = new ImFloat(1.0f); // Default rotation speed
        this.showDemoWindow = false;
        imGuiManager.resize(640, 480);
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

        if (ImGui.button("Rotate")) {
            scene.getRootObjects().get(0).addRotation(0f, 4, 0.0f);
        }
        ImGui.end();
    }

}