package org.lwjgl.UI;

import imgui.flag.ImGuiWindowFlags;

import static imgui.ImGui.*;

public abstract class ImGuiWindow {
    protected String title;

    public ImGuiWindow(String title) {
        this.title = title;
    }

    public void render() {
        //begin(title);
        renderContent();
        //end();
    }

    protected abstract void update();

    protected abstract void renderContent();

}