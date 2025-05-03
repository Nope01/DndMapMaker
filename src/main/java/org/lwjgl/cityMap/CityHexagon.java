package org.lwjgl.cityMap;

import org.joml.Vector2i;
import org.lwjgl.Scene;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.Hexagon;

import java.io.Serializable;


public class CityHexagon extends Hexagon implements Serializable {
    public static final float DIFFICULT_TERRAIN = 0.5f;

    public static final float BRIGHT_LIGHT = 1.0f;
    public static final float DIM_LIGHT = 0.5f;
    public static final float DARKNESS = 0.1f;
    private int movementModifier;
    private int visibilityModifier;
    public CityHexagon(Vector2i offsetPos) {
        super(offsetPos);
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler input) {

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
