package org.lwjgl.UI;

import static imgui.ImGui.*;

public abstract class ImGuiWindow {
    protected String title;

    public ImGuiWindow(String title) {
        this.title = title;
    }

    public void render() {
        begin(title);
        renderContent();
        end();
    }

    protected abstract void renderContent();

}