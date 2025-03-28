package org.lwjgl;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL20.*;

public class Grid extends SceneObject{

    public int columns;
    public int rows;
    private Hexagon[][] grid;

    public Grid(Scene scene, int columns, int rows) {
        this.id = "grid";
        this.columns = columns;
        this.rows = rows;
        makeGrid(scene);
    }

    public Grid (Scene scene) {
        this(scene, 2, 2);
    }

    public Hexagon[][] makeGrid(Scene scene) {
        Hexagon[][] grid = new Hexagon[rows][columns];

        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                Hexagon hexagon = createHexagon(row, col);
                grid[row][col] = hexagon;
                hexagon.setTexture(scene.getTextureCache().getTexture(TextureCache.SANDVICH));
                this.addChild(hexagon);
                //scene.addObject(hexagon);
            }
        }
        this.grid = grid;
        return grid;
    }

    public Hexagon createHexagon(int row, int col) {
        Vector3f pos = calculatePosition(row, col);
        Hexagon hexagon = new Hexagon(new Vector2i(col, row));
        hexagon.setId("hex-" + row + "-" + col);
        hexagon.setPosition(pos.x, pos.y, pos.z);
        hexagon.setParent(this);
        return hexagon;
    }
    public Vector3f calculatePosition(int row, int col) {
        float width = 2*Hexagon.SIZE;
        float height = (float) (width * Math.sqrt(3) / 2);

        float horizSpacing = 0.75f * width;
        float vertSpacing = height;
        float x, y, z;

        if (col % 2 == 0) {
            z = row * vertSpacing;
        }
        else {
            z = row * vertSpacing + (height/2);
        }
        x = col * horizSpacing;

        return new Vector3f(x, 0.0f, z);
    }

    public void cleanup() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid[row][col].cleanup();
            }
        }
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler input) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid[row][col].inLine = false;
            }
        }
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

    public Hexagon[][] getGrid() {
        return grid;
    }

    public Hexagon getHexagonAt(int row, int col) {
        return grid[row][col];
    }

    public void removeColumn(int old, int current) {
//        for (int row = 0; row < rows; row++) {
//            this.removeChild(grid[row][old-1]);
//            grid[row][old-1].cleanup();
//        }
        for (int row = 0; row < rows; row++) {
            for (int col = current; col < old; col++) {
                this.removeChild(grid[row][old-1]);
            }
        }
        columns = current;
    }

    public void removeRow(int old, int current) {
//        for (int col = 0; col < columns; col++) {
//            this.removeChild(grid[old-1][col]);
//        }
        for (int row = current; row < old; row++) {
            for (int col = 0; col < columns; col++) {
                this.removeChild(grid[old-1][col]);
            }
        }
        rows = current;
    }

    public void addColumn(int old, int current) {
        Hexagon[][] oldGrid = grid;
        this.grid = new Hexagon[rows][current];
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid[row][col] = oldGrid[row][col];
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = old; col < current; col++) {
                Hexagon hexagon = createHexagon(row, col);
                grid[row][col] = hexagon;
                this.addChild(hexagon);
            }
        }
        columns = current;
    }

    public void addRow(int old, int current) {
        Hexagon[][] oldGrid = grid;
        this.grid = new Hexagon[current][columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid[row][col] = oldGrid[row][col];
            }
        }

        for (int row = old; row < current; row++) {
            for (int col = 0; col < columns; col++) {
                Hexagon hexagon = createHexagon(row, col);
                grid[row][col] = hexagon;
                this.addChild(hexagon);
            }
        }
        rows = current;
    }
}
