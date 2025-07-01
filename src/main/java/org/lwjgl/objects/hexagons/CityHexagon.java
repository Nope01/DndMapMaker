package org.lwjgl.objects.hexagons;

import org.joml.Vector2i;
import org.lwjgl.Scene;
import org.lwjgl.engine.input.InputHandler;

import java.io.Serializable;

import static org.lwjgl.opengl.GL20.*;


public class CityHexagon extends Hexagon implements Serializable {
    public static final float DIFFICULT_TERRAIN = 0.5f;

    public static final float BRIGHT_LIGHT = 1.0f;
    public static final float DIM_LIGHT = 0.5f;
    public static final float DARKNESS = 0.1f;
    private int movementModifier;
    private int visibilityModifier;

    //TODO: destructible walls and cover, with HP?
    public boolean isHalfCover;
    public boolean isFullCover;
    public boolean isWall;
    public CityHexagon(Vector2i offsetPos) {

        super(offsetPos);
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler input) {

    }

    @Override
    public void render() {
        glUseProgram(shaderProgram);
        int selected = glGetUniformLocation(shaderProgram, "selected");
        glUniform1i(selected, this.getSelected() ? 1 : 0);
        int highlighted = glGetUniformLocation(shaderProgram, "highlighted");
        glUniform1i(highlighted, this.highlighted ? 1 : 0);
        super.render();
    }

    public int getMovementModifier() {
        return movementModifier;
    }
    public void setMovementModifier(int movementModifier) {
        this.movementModifier = movementModifier;
    }
    public int getVisibilityModifier() {
        return visibilityModifier;
    }
    public void setVisibilityModifier(int visibilityModifier) {
        this.visibilityModifier = visibilityModifier;
    }


}
