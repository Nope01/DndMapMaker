package org.lwjgl.objects.entities;

import org.joml.Vector2i;
import org.lwjgl.Scene;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.Hexagon;

public class Player extends Creature {

    public Player(Vector2i offsetPos) {
        super(offsetPos);

        this.name = "Creature";
        this.type = BARD;
        this.moveSpeed = moveSpeedToHexSpeed(20);
        this.offsetPos = offsetPos;
        this.cubePos = Hexagon.offsetToCubeCoords(offsetPos);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler inputHandler) {
    }

    @Override
    public void cleanup() {

    }
}
