package org.lwjgl.objects;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.cityMap.CityHexagon;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.objects.models.opengl.HexagonShape;
import org.lwjgl.textures.Texture;

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
    protected Vector3i[] cubeDirectionVectors;
    protected Vector2i offsetCoords;
    protected Vector3i cubeCoords;
    protected Vector2i axialCoords;

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
        this.offsetCoords = offsetPos;
        this.cubeCoords = offsetToCubeCoords(offsetPos);
        this.axialCoords = cubeToAxialCoords(cubeCoords);
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
            System.out.println("Error hex: " + offsetCoords.x + "," + offsetCoords.y + "-" + e);
        }
        glUseProgram(shaderProgram);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int modelLoc = glGetUniformLocation(shaderProgram, "model");
        glUniformMatrix4fv(modelLoc, false, worldMatrix.get(new float[16]));
        int colorLoc = glGetUniformLocation(shaderProgram, "color");
        glUniform3f(colorLoc, colour.x, colour.y, colour.z);
        int hovered = glGetUniformLocation(shaderProgram, "hovered");
        glUniform1i(hovered, this.hovered ? 1 : 0);
        int inLine = glGetUniformLocation(shaderProgram, "inLine");
        glUniform1i(inLine, this.inLine ? 1 : 0);
        int texCoords = glGetUniformLocation(shaderProgram, "texCoords");
        glUniform2f(texCoords, this.texCoords[0], this.texCoords[1]);

        glActiveTexture(GL_TEXTURE0);
        if (texture != null) {
            texture.bind();
        }
        glActiveTexture(GL_TEXTURE1);
        if (iconTexture != null) {
            iconTexture.bind();
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

    public Vector2i getOffsetCoords() {
        return offsetCoords;
    }

    public Vector3i getCubeCoords() {
        return cubeCoords;
    }

    public Vector2i getAxialCoords() {
        return axialCoords;
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
    public Vector3i cubeDirection(int direction) {
        return cubeDirectionVectors[direction];
    }

    //Adds the directional vec to a target hexagon, returning the target neighbour hex
    public Vector3i cubeAddDirection(Vector3i hex, Vector3i vec ) {
        return new Vector3i(hex.x + vec.x, hex.y + vec.y, hex.z + vec.z);
    }

    //Given a hex and directional value, returns the coords for the neighbour in that direction
    public Vector3i getCubeNeighbour(Vector3i hex, int direction) {
        return cubeAddDirection(hex, cubeDirection(direction));
    }

    public Vector3i[] getAllNeighbours() {
        Vector3i hex = this.cubeCoords;
        Vector3i[] neighbours = new Vector3i[6];
        for (int i = 0; i < 6; i++) {
            neighbours[i] = getCubeNeighbour(hex, i);
        }
        return neighbours;
    }

    public Vector3i getCubeNeighbour(int direction) {
        return cubeAddDirection(cubeCoords, cubeDirection(direction));
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
        return cubeDistance(a, cubeCoords);
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

    public static Vector3i[] cubeLineDraw(Vector3i a, Vector3i b) {
        int length = cubeDistance(a, b);
        Vector3i[] results = new Vector3i[length];
        for (int i = 0; i < length; i++) {
            results[i] = cubeRound(cubeLerp(a, b, (float) (1.0/length * i)));
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
                if (grid[i][j].cubeDistance(hexagon.getCubeCoords()) <= moveRange) {
                    CityHexagon hex = (CityHexagon) grid[i][j];
                    hex.highlighted = true;
                }
            }
        }
    }

    public static void areaSelectClear(Grid gridClass) {
        CityHexagon[][] grid = (CityHexagon[][]) gridClass.getGrid();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j].highlighted = false;
            }
        }
    }


}
