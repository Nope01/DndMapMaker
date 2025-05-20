package org.lwjgl.UI;

import imgui.ImGui;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;

public abstract class ImGuiWindow {
    protected String title;
    protected ImGuiManager imGuiManager;
    protected Scene scene;
    protected InputHandler inputHandler;


    protected float uiWidth;
    protected float uiHeight;
    protected float uiXPos;
    protected float uiYPos;

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

    public void rescaleWindow() {
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        ImGui.begin(title);
        ImGui.end();
    }

    public void resizeWindow() {
        ImGui.setNextWindowPos(uiXPos, uiYPos);
        ImGui.begin(title);
        ImGui.end();
    }

    public void placeUiWindow() {
        ImGui.setNextWindowPos(uiXPos, uiYPos);
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        renderContent();
    }
    protected abstract void update();


    protected abstract void renderContent();

    public float getUiWidth() {
        return uiWidth;
    }

    public float getUiHeight() {
        return uiHeight;
    }

    public float getUiXPos() {
        return uiXPos;
    }

    public float getUiYPos() {
        return uiYPos;
    }

}