package org.lwjgl.objects.hexagons;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.objects.Grid;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.engine.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.objects.models.opengl.HexagonShape;
import org.lwjgl.textures.Texture;
import org.lwjgl.utils.ObjectUtils;

import java.util.*;

import static java.lang.Math.abs;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public abstract class Hexagon extends SceneObject {
    public boolean inLine;
    protected Texture iconTexture;
    protected int numFloats = 7 * 3;
    protected static Vector3i[] cubeDirectionVectors;

    protected boolean highlighted;
    private boolean movementHighlighted;
    private boolean spellHighlighted;

    private boolean isVisible;

    public static final float SIZE = 1.0f;
    public static final int N = 0;
    public static final int NE = 1;
    public static final int SE = 2;
    public static final int S = 3;
    public static final int SW = 4;
    public static final int NW = 5;



    public Hexagon(Vector2i offsetPos) {
        colour = new Vector3f(0.2f, 0.2f, 0.2f);
        initGeometry();
        initAabb();
        this.setOffsetAndCubePos(offsetPos);
        this.setCubePos(offsetToCubeCoords(offsetPos));
        this.inLine = false;
        cubeDirectionVectors = new Vector3i[]{
                new Vector3i(0, -1, 1), //N
                new Vector3i(1, -1, 0), //NE
                new Vector3i(1, 0, -1), //SE
                new Vector3i(0, 1, -1), //S
                new Vector3i(-1, 1, 0), //SW
                new Vector3i(-1, 0, 1), //NW
                /*
               5  0  1
                ↖ ↑ ↗
                  ·
                ↙ ↓ ↘
               4  3  2
                 */
        };
    }

    @Override
    public void render() {
        // Bind shader and set uniforms
        int e = glGetError();
        if (e != GL_NO_ERROR) {
            System.out.println("Error hex: " + getOffsetPos().x + "," + getOffsetPos().y + "-" + e);
        }
        glUseProgram(shaderProgram);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int modelLoc = glGetUniformLocation(shaderProgram, "model");
        glUniformMatrix4fv(modelLoc, false, worldMatrix.get(new float[16]));
        int colorLoc = glGetUniformLocation(shaderProgram, "color");
        glUniform3f(colorLoc, colour.x, colour.y, colour.z);
        int hovered = glGetUniformLocation(shaderProgram, "hovered");
        glUniform1i(hovered, this.getHovered() ? 1 : 0);
        int inLine = glGetUniformLocation(shaderProgram, "inLine");
        glUniform1i(inLine, this.inLine ? 1 : 0);
        int texCoords = glGetUniformLocation(shaderProgram, "texCoords");
        glUniform2f(texCoords, this.texCoords[0], this.texCoords[1]);

        //Change the texture wrap mode, so icons are clamped and terrain is repeating
        //Doesnt actually work properly because of how the textures are.
        //TODO: fix how hexagon shaped textures are made to allow for texture wrapping
        glActiveTexture(GL_TEXTURE0);
        if (texture != null) {
            texture.bind();
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        }

        glActiveTexture(GL_TEXTURE1);
        if (iconTexture != null) {
            iconTexture.bind();
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        }

        glUniform1i(glGetUniformLocation(shaderProgram, "terrainTexture"), 0);
        glUniform1i(glGetUniformLocation(shaderProgram, "iconTexture"), 1);


        // Render hexagon
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, numFloats, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        // Render children
        for (SceneObject child : children) {
            child.render();
        }
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler inputHandler) {

    }

    @Override
    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }

    protected void initGeometry() {

        verticesFloats = HexagonShape.vertices();
        verticesVecs = HexagonShape.verticesVecs(verticesFloats);
        texCoords = HexagonShape.texCoords();
        indices = HexagonShape.indices();


        //Buffers
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        ObjectUtils.bindVerticesList(verticesFloats);
        ObjectUtils.bindTexCoordList(texCoords);
        ObjectUtils.bindIndicesList(indices);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void setIconTexture(Texture texture) {
        this.iconTexture = texture;
    }

    public Texture getIconTexture() {
        return iconTexture;
    }


    public Vector3f[] getVerticesAsVecs() {
        return verticesVecs;
    }

    public float[] getVerticesAsFloats() {
        return verticesFloats;
    }

    public static String intToDirection(int direction) {
        switch (direction) {
            case N:
                return "N";
            case NE:
                return "NE";
            case SE:
                return "SE";
            case S:
                return "S";
            case SW:
                return "SW";
            case NW:
                return "NW";
            default:
                return "What";
        }
    }

    //Below methods convert between coordinate types, namely cube and offset coords
    //Axial coords is the same as cube coords, but hides the S coord
    //Might as well keep all the data?
    public static Vector2i cubeToAxialCoords(Vector3i cube) {
        int q = cube.x;
        int r = cube.y;
        return new Vector2i(q, r);
    }

    public static Vector3i axialToCubeCoords(Vector2i axial) {
        int q = axial.x;
        int r = axial.y;
        int s = -q-r;
        return new Vector3i(q, r, s);
    }

    public static Vector2i cubeToOffsetCoords(Vector3i cube) {
        int col = cube.x;
        int row = cube.y + (cube.x - (cube.x&1))/2;
        return new Vector2i(col, row);
    }

    public static Vector3i offsetToCubeCoords(Vector2i offset) {
        int q = offset.x;
        int r = offset.y - (offset.x - (offset.x&1))/2;
        int s = -q-r;
        return new Vector3i(q, r, s);
    }

    //Converts direction int value to a vector for the given direction
    public static Vector3i cubeDirection(int direction) {
        return cubeDirectionVectors[direction];
    }

    //Adds the directional vec to a target hexagon, returning the target neighbour hex
    public static Vector3i cubeAddDirection(Vector3i hex, Vector3i vec ) {
        return new Vector3i(hex.x + vec.x, hex.y + vec.y, hex.z + vec.z);
    }

    //Given a hex and directional value, returns the coords for the neighbour in that direction
    public static Vector3i getCubeNeighbour(Vector3i hex, int direction) {
        return cubeAddDirection(hex, cubeDirection(direction));
    }

    public Vector3i[] getAllNeighbours() {
        Vector3i hex = this.getCubePos();
        Vector3i[] neighbours = new Vector3i[6];
        for (int i = 0; i < 6; i++) {
            neighbours[i] = getCubeNeighbour(hex, i);
        }
        return neighbours;
    }

    public Vector3i getCubeNeighbour(int direction) {
        return cubeAddDirection(getCubePos(), cubeDirection(direction));
    }

    //Distances
    public static Vector3i cubeSubtract(Vector3i a, Vector3i b) {
        return new Vector3i(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static int cubeDistance(Vector3i a, Vector3i b) {
        Vector3i vec = cubeSubtract(a, b);
        return new Vector3i(abs(vec.x) + abs(vec.y) + abs(vec.z)).div(2).x;
    }

    public int cubeDistance(Vector3i a) {
        return cubeDistance(a, getCubePos());
    }

    //Line

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
    public static Vector3f cubeLerp(Vector3i a, Vector3i b, float t) {
        return new Vector3f(
                lerp(a.x, b.x, t),
                lerp(a.y, b.y, t),
                lerp(a.z, b.z, t));
    }

    public static Set<Hexagon> cubeLineDraw(Vector3i a, Vector3i b, Grid gridClass) {
        int length = cubeDistance(a, b);
        Set<Hexagon> results = new HashSet<>();
        for (int i = 0; i < length; i++) {
            Hexagon hexagon = gridClass.getHexagonAt(cubeRound(cubeLerp(a, b, (float) (1.0/length * i))));
            results.add(hexagon);
        }
        return results;
    }

    public static Vector3i cubeRound(Vector3f frac) {
        int x = Math.round(frac.x);
        int y = Math.round(frac.y);
        int z = Math.round(frac.z);

        float xDiff = Math.abs(x - frac.x);
        float yDiff = Math.abs(y - frac.y);
        float zDiff = Math.abs(z - frac.z);

        if (xDiff > yDiff && xDiff > zDiff) {
            x = -y - z;
        }
        else if (yDiff > zDiff) {
            y = -x-z;
        }
        else {
            z = -x-y;
        }
        return new Vector3i(x, y, z);
    }

    public static void showMovementRange(Grid gridClass, Hexagon hexagon, int moveRange) {
        Hexagon[][] grid = gridClass.getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].cubeDistance(hexagon.getCubePos()) <= moveRange) {
                    Hexagon hex = grid[i][j];
                    //Basic removal of invalid tiles
                    if (hex instanceof CombatHexagon combatHexagon) {
                        if (!combatHexagon.isWall && !combatHexagon.isFullCover && !combatHexagon.isHalfCover) {
                            hex.highlighted = true;
                        }
                    }
                }
            }
        }
    }

    //TODO: move these to grid class?
    public static void clearReachableTiles(Grid gridClass, boolean fogOfWar) {
        Hexagon[][] grid = gridClass.getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j].setMovementHighlighted(false);
                if (fogOfWar) {
                    grid[i][j].isVisible = false;
                }
                else {
                    grid[i][j].isVisible = true;
                }
            }
        }
    }

    public static void clearReachableTiles(Grid gridClass) {
        clearReachableTiles(gridClass, false);
    }

    public static Set<Hexagon> hexReachable(Hexagon start, int range, Grid gridClass) {
        Set<Hexagon> visited = new HashSet<>();
        visited.add(start);

        List<List<Hexagon>> fringes = new ArrayList<>();
        fringes.add(new ArrayList<>());
        fringes.get(0).add(start);

        for (int k = 1; k <= range; k++) {
            fringes.add(new ArrayList<>());
            for (Hexagon hex : fringes.get(k-1)) {
                for (int dir = 0; dir < 6; dir++) {
                    Hexagon neighbor = gridClass.getHexagonAt(getCubeNeighbour(hex.getCubePos(), dir));
                    if (neighbor instanceof CombatHexagon && !((CombatHexagon) neighbor).isBlocked()) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            fringes.get(k).add(neighbor);
                            neighbor.setMovementHighlighted(true);
                        }
                    }
                }
            }
        }
        return visited;
    }

    public static Set<Hexagon> hexVisible(Hexagon start, int range, Grid gridClass) {
        Set<Hexagon> visible = new HashSet<>();
        visible.add(start); // Always visible to itself

        // Get all hexagons within range first
        Set<Hexagon> inRange = getHexagonsInRange(start, range, gridClass);

        // Check LOS to each hex in range
        for (Hexagon target : inRange) {
            if (hasLineOfSight(start, target, gridClass)) {
                visible.add(target);
                //target.setVisible(true);
                // If this is a wall, we stop checking further along this line
                if (target instanceof CombatHexagon && ((CombatHexagon) target).isWall) {
                    continue; // Don't check what's behind this wall
                }
            }
        }

        return visible;
    }

    private static Set<Hexagon> getHexagonsInRange(Hexagon start, int range, Grid gridClass) {
        Set<Hexagon> inRange = new HashSet<>();
        Queue<Hexagon> queue = new LinkedList<>();
        Map<Hexagon, Integer> distances = new HashMap<>();

        queue.add(start);
        distances.put(start, 0);
        inRange.add(start);

        while (!queue.isEmpty()) {
            Hexagon current = queue.poll();
            int currentDist = distances.get(current);

            if (currentDist >= range) {
                continue;
            }

            for (int dir = 0; dir < 6; dir++) {
                Hexagon neighbor = gridClass.getHexagonAt(getCubeNeighbour(current.getCubePos(), dir));
                if (neighbor != null && !distances.containsKey(neighbor)) {
                    distances.put(neighbor, currentDist + 1);
                    inRange.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return inRange;
    }

    private static boolean hasLineOfSight(Hexagon start, Hexagon end, Grid gridClass) {
        if (start.equals(end)) return true;

        Vector3i a = start.getCubePos();
        Vector3i b = end.getCubePos();
        int distance = cubeDistance(a, b);

        // Walk along the line
        for (int i = 1; i <= distance; i++) {
            float t = 1.0f/distance * i;
            Vector3i interp = cubeRound(cubeLerp(a, b, t));
            Hexagon hex = gridClass.getHexagonAt(interp);

            // If we hit a wall before reaching our target, no LOS
            if (hex instanceof CombatHexagon && ((CombatHexagon) hex).isWall) {
                // Only block if the wall is before our target position
                if (i < distance) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Set<Hexagon> hexCone(Vector3i origin, int direction, int size, Grid gridClass) {
        Set<Hexagon> cone = new HashSet<>();
        cone.add(gridClass.getHexagonAt(origin));  // Origin is always part of cone

        if (size == 0) {
            return cone;
        }

        // Get primary direction vector
        Vector3i primaryDir = cubeDirection(direction);

        // For each step along primary direction
        for (int r = 1; r <= size; r++) {
            // Current position along primary direction
            Vector3i current = new Vector3i(
                    origin.x + primaryDir.x * r,
                    origin.y + primaryDir.y * r,
                    origin.z + primaryDir.z * r
            );

            Hexagon hex = gridClass.getHexagonAt(current);
            if (hex != null) {
                cone.add(hex);
            }

            // Get left and right perpendicular directions
            int leftDir = (direction + 2) % 6;
            int rightDir = (direction + 4) % 6;

            // Expand perpendicularly to form cone
            for (int s = 1; s < r; s++) {
                // Left side
                Vector3i left = cubeAddDirection(current,
                        cubeDirection(leftDir).mul(s, new Vector3i()));
                Hexagon leftHex = gridClass.getHexagonAt(left);
                if (leftHex != null) {
                    cone.add(leftHex);
                }

                // Right side
                Vector3i right = cubeAddDirection(current,
                        cubeDirection(rightDir).mul(s, new Vector3i()));
                Hexagon rightHex = gridClass.getHexagonAt(right);
                if (rightHex != null) {
                    cone.add(rightHex);
                }
            }
        }

        return cone;
    }

    public static Set<Hexagon> hexCone(Hexagon origin, int direction, int size, Grid gridClass) {
        return hexCone(origin.getCubePos(), direction, size, gridClass);
    }



    public boolean isMovementHighlighted() {
        return movementHighlighted;
    }

    public void setMovementHighlighted(boolean movementHighlighted) {
        this.movementHighlighted = movementHighlighted;
    }

    public boolean isSpellHighlighted() {
        return spellHighlighted;
    }

    public void setSpellHighlighted(boolean spellHighlighted) {
        this.spellHighlighted = spellHighlighted;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
