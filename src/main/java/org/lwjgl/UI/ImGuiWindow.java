package org.lwjgl.UI;

import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;

public abstract class ImGuiWindow {
    protected String title;
    protected ImGuiManager imGuiManager;
    protected Scene scene;
    protected InputHandler inputHandler;

    protected int uiWidth;
    protected int uiHeight;
    protected int uiXPos;
    protected int uiYPos;

    public ImGuiWindow(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler, String title) {
        this.title = title;
        this.imGuiManager = imGuiManager;
        this.scene = scene;
        this.inputHandler = inputHandler;
    }

    public void render() {
        //begin(title);
        renderContent();
        //end();
    }

    protected abstract void init(Scene scene);
    protected abstract void update();

    protected abstract void renderContent();

}