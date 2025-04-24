package org.lwjgl.UI;

import org.lwjgl.Scene;

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

    protected abstract void init(Scene scene);
    protected abstract void update();

    protected abstract void renderContent();

}