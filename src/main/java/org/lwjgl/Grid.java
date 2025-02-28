package org.lwjgl;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL20.*;

public class Grid extends SceneObject{

    private int columns;
    private int rows;
    private Hexagon[][] grid;

    public Grid(Scene scene, int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        makeGrid(scene);
    }

    public Grid(Scene scene) {
        this.columns = 2;
        this.rows = 2;
        makeGrid(scene);
    }

    public Hexagon[][] makeGrid(Scene scene) {
        Hexagon[][] grid = new Hexagon[rows][columns];
        float width = 2*Hexagon.SIZE;
        float height = (float) (width * Math.sqrt(3) / 2);

        float horizSpacing = 0.75f * width;
        float vertSpacing = height;

        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                float x, y, z;

                if (col % 2 == 0) {
                    y = row * vertSpacing;
                }
                else {
                    y = row * vertSpacing + (height/2);
                }
                x = col * horizSpacing;

                Hexagon hexagon = new Hexagon();
                hexagon.setPosition(x, y, 0.0f);
                hexagon.setParent(this);
                grid[row][col] = hexagon;
                scene.addObject(hexagon);
            }
        }
        this.grid = grid;
        return grid;
    }

    public void cleanup() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid[row][col].cleanup();
            }
        }
    }

    @Override
    public void update(Scene scene, long deltaTime) {

    }

    @Override
    public void render(int shaderProgram) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Hexagon hexagon = grid[row][col];
                hexagon.render(shaderProgram);
            }
        }
    }
}
