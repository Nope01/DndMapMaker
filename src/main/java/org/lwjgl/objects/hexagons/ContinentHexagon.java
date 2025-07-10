package org.lwjgl.objects.hexagons;

import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.engine.input.InputHandler;

import static java.lang.Math.abs;

import java.io.Serializable;

public class ContinentHexagon extends Hexagon implements Serializable {
    public ContinentHexagon(Vector2i offsetPos) {
        super(offsetPos);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler input) {
    }

}
