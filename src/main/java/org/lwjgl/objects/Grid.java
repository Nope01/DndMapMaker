package org.lwjgl.objects;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.Scene;
import org.lwjgl.cityMap.CityHexagon;
import org.lwjgl.combatMap.CombatHexagon;
import org.lwjgl.continentMap.ContinentHexagon;
import org.lwjgl.input.InputHandler;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.glUseProgram;


public class Grid extends SceneObject {

    public int columns;
    public int rows;
    private Hexagon[][] grid;

    public Grid(Scene scene, int columns, int rows) {
        this.id = "grid";
        this.columns = columns;
        this.rows = rows;
    }

    public Grid (Scene scene) {
        this(scene, 2, 2);
    }

    public void makeGridFromLoadedGrid(Grid loadedGrid) {
        CombatHexagon[][] grid = new CombatHexagon[loadedGrid.rows][loadedGrid.columns];

        for (int col = 0; col < loadedGrid.columns; col++) {
            for (int row = 0; row < loadedGrid.rows; row++) {
                CombatHexagon combatHexagon = createCombatHexagon(row, col);
                combatHexagon.setShaderProgram(loadedGrid.getHexagonAt(row, col).getShaderProgram());
                grid[row][col] = combatHexagon;
                combatHexagon.paintTerrainTexture(loadedGrid.getHexagonAt(row, col).getTexture());
                combatHexagon.paintIconTexture(loadedGrid.getHexagonAt(row, col).getIconTexture());
            }
        }
        this.grid = grid;
    }

    public void makeContinentGrid(Scene scene) {
        ContinentHexagon[][] grid = new ContinentHexagon[rows][columns];

        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                ContinentHexagon continentHexagon = createContinentHexagon(row, col);
                continentHexagon.setShaderProgram(scene.getShaderCache().getShader("continentHex"));
                grid[row][col] = continentHexagon;
                continentHexagon.setTexture(scene.getTextureCache().getTexture("default_tile"));
                continentHexagon.setIconTexture(scene.getTextureCache().getTexture("empty"));
                this.addChild(continentHexagon);
            }
        }
        this.grid = grid;
    }

    public void makeCityGrid(Scene scene) {
        CityHexagon[][] grid = new CityHexagon[rows][columns];

        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                CityHexagon cityHexagon = createCityHexagon(row, col);
                cityHexagon.setShaderProgram(scene.getShaderCache().getShader("cityHex"));
                grid[row][col] = cityHexagon;
                cityHexagon.setTexture(scene.getTextureCache().getTexture("default_tile"));
                cityHexagon.setIconTexture(scene.getTextureCache().getTexture("empty"));
                this.addChild(cityHexagon);
            }
        }
        this.grid = grid;
    }

    public void makeCombatGrid(Scene scene) {
        CombatHexagon[][] grid = new CombatHexagon[rows][columns];

        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                CombatHexagon combatHexagon = createCombatHexagon(row, col);
                combatHexagon.setShaderProgram(scene.getShaderCache().getShader("combatHex"));
                grid[row][col] = combatHexagon;
                combatHexagon.setTexture(scene.getTextureCache().getTexture("default_tile"));
                combatHexagon.setIconTexture(scene.getTextureCache().getTexture("empty"));
                this.addChild(combatHexagon);
            }
        }
        this.grid = grid;
    }

    public ContinentHexagon createContinentHexagon(int row, int col) {
        Vector3f pos = calculatePosition(row, col);
        ContinentHexagon continentHexagon = new ContinentHexagon(new Vector2i(col, row));
        continentHexagon.setId("hex-" + row + "-" + col);
        continentHexagon.setPosition(pos.x, pos.y, pos.z);
        continentHexagon.setParent(this);
        return continentHexagon;
    }

    public CityHexagon createCityHexagon(int row, int col) {
        Vector3f pos = calculatePosition(row, col);
        CityHexagon cityHexagon = new CityHexagon(new Vector2i(col, row));
        cityHexagon.setId("city-" + row + "-" + col);
        cityHexagon.setPosition(pos.x, pos.y, pos.z);
        cityHexagon.setParent(this);
        return cityHexagon;
    }

    public CombatHexagon createCombatHexagon(int row, int col) {
        Vector3f pos = calculatePosition(row, col);
        CombatHexagon combatHexagon = new CombatHexagon(new Vector2i(col, row));
        combatHexagon.setId("combat-" + row + "-" + col);
        combatHexagon.setPosition(pos.x, pos.y, pos.z);
        combatHexagon.setVisible(true);
        combatHexagon.setParent(this);
        return combatHexagon;
    }

    public Vector3f calculatePosition(int row, int col) {
        float width = 2* ContinentHexagon.SIZE;
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
                grid[row][col].update(scene, deltaTime, input);
            }
        }
    }

    @Override
    public void render() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid[row][col].render();
            }
        }
    }

    public Hexagon[][] getGrid() {
        return grid;
    }
    public void setGrid(Hexagon[][] grid) {
        this.grid = grid;
    }

    //Pretty bad when vbos and vaos are involved, avoid for now
    public void setGridFromLoad(Hexagon[][] newGrid, int rows, int cols) {
        //This logic might help with resizing the grid bug?
        this.grid = newGrid;
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                this.addChild(grid[row][col]);
            }
        }
    }

    public Hexagon getHexagonAt(int row, int col) {
        return grid[row][col];
    }

    public Hexagon getHexagonAt(Vector2i offsetPos) {
        return grid[offsetPos.x][offsetPos.y];
    }

    public Hexagon getHexagonAt(Vector3i cubeCoords) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (grid[row][col].getCubePos().equals(cubeCoords)) {
                    return grid[row][col];
                }
            }
        }
        return null;
    }

    public void clearHoveredHexagons() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid[row][col].setHovered(false);
            }
        }
    }

    public void applyFogOfWar(boolean fogOfWar) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                grid[row][col].setVisible(!fogOfWar);
            }
        }
    }

    public Set<Hexagon> getAllHexagons() {
        Set<Hexagon> hexes = new HashSet<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                hexes.add(grid[row][col]);
            }
        }
        return hexes;
    }


//    public void removeColumn(int old, int current) {
////        for (int row = 0; row < rows; row++) {
////            this.removeChild(grid[row][old-1]);
////            grid[row][old-1].cleanup();
////        }
//        for (int row = 0; row < rows; row++) {
//            for (int col = current; col < old; col++) {
//                this.removeChild(grid[row][old-1]);
//            }
//        }
//        columns = current;
//    }
//
//        public void removeRow(int old, int current) {
////        for (int col = 0; col < columns; col++) {
////            this.removeChild(grid[old-1][col]);
////        }
//        for (int row = current; row < old; row++) {
//            for (int col = 0; col < columns; col++) {
//                this.removeChild(grid[old-1][col]);
//            }
//        }
//        rows = current;
//    }
//
//    public void addColumn(int old, int current) {
//        ContinentHexagon[][] oldGrid = grid;
//        this.grid = new ContinentHexagon[rows][current];
//
//        for (int row = 0; row < rows; row++) {
//            for (int col = 0; col < columns; col++) {
//                grid[row][col] = oldGrid[row][col];
//            }
//        }
//
//        for (int row = 0; row < rows; row++) {
//            for (int col = old; col < current; col++) {
//                ContinentHexagon continentHexagon = createHexagon(row, col);
//                grid[row][col] = continentHexagon;
//                this.addChild(continentHexagon);
//            }
//        }
//        columns = current;
//    }
//
//    public void addRow(int old, int current) {
//        ContinentHexagon[][] oldGrid = grid;
//        this.grid = new ContinentHexagon[current][columns];
//
//        for (int row = 0; row < rows; row++) {
//            for (int col = 0; col < columns; col++) {
//                grid[row][col] = oldGrid[row][col];
//            }
//        }
//
//        for (int row = old; row < current; row++) {
//            for (int col = 0; col < columns; col++) {
//                ContinentHexagon continentHexagon = createHexagon(row, col);
//                grid[row][col] = continentHexagon;
//                this.addChild(continentHexagon);
//            }
//        }
//        rows = current;
//    }
//
//    public void lineDraw(Scene scene) {
//        if (scene.getSelectedObject() != null) {
//            Vector3i[] results = ContinentHexagon.cubeLineDraw(grid[0][0].getCubeCoords(),
//                    ((ContinentHexagon) scene.getSelectedObject()).getCubeCoords());
//
//            for (Vector3i vec : results) {
//                Vector2i offset = ContinentHexagon.cubeToOffsetCoords(vec);
//                if (offset.x < 0 || offset.y < 0) {
//                    continue;
//                }
//                ContinentHexagon lineHex = getHexagonAt(offset.y, offset.x);
//                lineHex.inLine = true;
//            }
//        }
//    }
}
