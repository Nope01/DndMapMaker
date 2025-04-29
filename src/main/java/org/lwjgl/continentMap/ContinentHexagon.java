package org.lwjgl.continentMap;

import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.objects.Hexagon;
import org.lwjgl.objects.ObjectUtils;
import org.lwjgl.objects.SceneObject;

import static java.lang.Math.abs;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.Serializable;
import java.lang.Math;

public class ContinentHexagon extends Hexagon implements Serializable {
    private int type;




    public static final int FOREST = 0;
    public static final int PLAINS = 1;
    public static final int DESERT = 2;
    public static final int SNOW = 3;
    public static final int WATER = 4;


    public ContinentHexagon(Vector2i offsetPos) {
        super(offsetPos);
        type = -1;
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler input) {
    }

    public String getTypeAsString() {
        return getTypeAsString(type);
    }

    public static String getTypeAsString(int type) {
        return switch (type) {
            case FOREST -> "Forest";
            case PLAINS -> "Plains";
            case DESERT -> "Desert";
            case SNOW -> "Snow";
            case WATER -> "Water";
            default -> "Unknown";
        };
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void clearType() {
        this.type = -1;
    }

}
