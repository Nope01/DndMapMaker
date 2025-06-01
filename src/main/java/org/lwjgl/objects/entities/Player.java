package org.lwjgl.objects.entities;

import org.joml.Vector2i;
import org.lwjgl.Scene;
import org.lwjgl.UI.DeathSave;
import org.lwjgl.utils.HelperMethods;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.Hexagon;

import static org.lwjgl.objects.entities.Classes.BARD;
import static org.lwjgl.objects.entities.Races.HUMAN;

public class Player extends Creature {

    public Player(Vector2i offsetPos) {
        super(offsetPos);

        new Player("A dude", BARD, HUMAN, 10, 10, 100, offsetPos);
    }

    public Player(String name, int classType, int raceType, int moveSpeed, int AC, int HP, Vector2i offsetPos) {
        super(offsetPos);
        this.setName(name);
        this.setClassType(classType);
        this.setRaceType(raceType);
        this.setMoveSpeed(moveSpeed);
        this.setAC(AC);
        this.setHP(HP);
        this.setMaxHP(HP);
        this.setDungeonVisibleRange(10);

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

    public static Player createCreatureRandomPos(String name, int classType, int raceType, int moveSpeed, int AC, int HP) {
        Vector2i offsetPos = new Vector2i(HelperMethods.randomInt(10, 30), HelperMethods.randomInt(20, 60));
        return new Player(name, classType, raceType, moveSpeed, AC, HP, offsetPos);
    }
}
