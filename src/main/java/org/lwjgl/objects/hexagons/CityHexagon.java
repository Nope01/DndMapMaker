package org.lwjgl.objects.hexagons;

import org.joml.Vector2i;
import org.lwjgl.Scene;
import org.lwjgl.engine.input.InputHandler;

import java.io.Serializable;

import static org.lwjgl.opengl.GL20.*;

public class CityHexagon extends Hexagon implements Serializable {
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
