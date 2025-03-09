package org.lwjgl;

import org.joml.*;

import static java.lang.Math.TAU;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

public class Hexagon extends SceneObject {
    private int vaoId, vboId; // Vertex Array Object and Vertex Buffer Object
    private Vector3f color;   // Random color for the hexagon
    private Vector3i[] cubeDirectionVectors;
    private Vector2i offsetCoords;
    private int[] indices;
    public static final float SIZE = 1.0f;
    private int numFloats = 7 * 3;

    private int type;

    private static final int N = 0;
    private static final int NE = 1;
    private static final int SE = 2;
    private static final int S = 3;
    private static final int SW = 4;
    private static final int NW = 5;

    public static final int FOREST = 0;
    public static final int PLAINS = 1;
    public static final int DESERT = 2;
    public static final int HILL = 3;
    public static final int WATER = 4;

    public static final Vector3f FOREST_COLOR = new Vector3f(0.5f, 0.8f, 0.4f);
    public static final Vector3f PLAINS_COLOR = new Vector3f(0.93f, 0.93f, 0.82f);
    public static final Vector3f DESERT_COLOR = new Vector3f(0.71f, 0.65f, 0.26f);
    public static final Vector3f HILL_COLOR = new Vector3f(0.28f, 0.28f, 0.28f);
    public static final Vector3f WATER_COLOR = new Vector3f(0.26f, 0.75f, 0.98f);

    public Hexagon(Vector2i offsetPos) {
        super();
        color = new Vector3f(0.2f, 0.2f, 0.2f);
        initGeometry();
        initAabb();
        this.offsetCoords = offsetPos;
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
        type = 99;
    }

    private void initGeometry() {
        // Hexagon vertices (6 vertices forming a regular hexagon)
        float[] vertices = new float[numFloats];
        this.verticesFloats = vertices;

        Vector3f[] vecs = new Vector3f[7];

        //Rotates a point to create a circle with 6 points (hexagon)
        vecs[0] = new Vector3f(0, 0, 0);
        Matrix3f rotation = new Matrix3f();
        for (int i = 0; i < 6; i++) {
            float angle = (float) (TAU/6);
            rotation.rotationY(angle*i);
            vecs[i+1] = new Vector3f(1.0f, 0.0f, 0.0f);
            vecs[i+1].mul(rotation);
        }

        int count = 0;
        for (Vector3f vec : vecs) {
            vertices[count++] = vec.x;
            vertices[count++] = vec.y;
            vertices[count++] = vec.z;
        }

        Vector3f[] verticesVecs = new Vector3f[numFloats / 3];
        count = 0;
        for (int i = 0; i < vertices.length; i += 3) {
            verticesVecs[count] = new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]);
            count++;
        }
        this.verticesVecs = verticesVecs;

        indices = new int[18];
        int k = 0;
        for (int i = 1; i <= 6; i++) {
            indices[k++] = 0;
            indices[k++] = i;
            indices[k++] = (i%6)+1;
        }

        // Vertices
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);


        // Indices
        vboId = glGenBuffers();
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private void initAabb() {
        for (Vector3f vertex : verticesVecs) {
            min.x = Math.min(min.x, vertex.x);
            min.y = Math.min(min.y, vertex.y);
            min.z = Math.min(min.z, vertex.z);

            max.x = Math.max(max.x, vertex.x);
            max.y = Math.max(max.y, vertex.y);
            max.z = Math.max(max.z, vertex.z);
        }

        Vector3f[] aabbVertices = {
                new Vector3f(min.x, min.y, min.z),  // Bottom-left-back corner
                new Vector3f(max.x, min.y, min.z),  // Bottom-right-back corner
                new Vector3f(max.x, max.y, min.z),  // Top-right-back corner
                new Vector3f(min.x, max.y, min.z),  // Top-left-back corner
                new Vector3f(min.x, min.y, max.z),  // Bottom-left-front corner
                new Vector3f(max.x, min.y, max.z),  // Bottom-right-front corner
                new Vector3f(max.x, max.y, max.z),  // Top-right-front corner
                new Vector3f(min.x, max.y, max.z)   // Top-left-front corner
        };
        this.aabbVertices = aabbVertices;

        aabbMin = min;
        aabbMax = max;
    }

    @Override
    public void render(int shaderProgram) {
        // Bind shader and set uniforms
        glUseProgram(shaderProgram);
        int modelLoc = glGetUniformLocation(shaderProgram, "model");
        glUniformMatrix4fv(modelLoc, false, worldMatrix.get(new float[16]));
        int colorLoc = glGetUniformLocation(shaderProgram, "color");
        glUniform3f(colorLoc, color.x, color.y, color.z);
        int selected = glGetUniformLocation(shaderProgram, "selected");
        glUniform1i(selected, this.selected ? 1 : 0);

        // Render hexagon
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, 21, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        // Render children
        for (SceneObject child : children) {
            child.render(shaderProgram);
        }
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler input) {
//        if (input.isLeftClicked()) {
//            type = FOREST;
//        }
    }


    // Cleanup method (call when done)
    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }

    public float[] getVerticesFloats() {
        return verticesFloats;
    }

    public String getTypeAsString() {
        return switch (type) {
            case 0 -> "Forest";
            case 1 -> "Plains";
            case 2 -> "Desert";
            case 3 -> "Hill";
            case 4 -> "Water";
            default -> "Unknown";
        };
    }

    public static String getTypeAsString(int type) {
        return switch (type) {
            case 0 -> "Forest";
            case 1 -> "Plains";
            case 2 -> "Desert";
            case 3 -> "Hill";
            case 4 -> "Water";
            default -> "Unknown";
        };
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        setColor(type);
    }

    public void setColor(float r, float g, float b) {
        this.color = new Vector3f(r, g, b);
    }

    public void setColor(int type) {
        switch (type) {
            case 0 -> this.color = FOREST_COLOR;
            case 1 -> this.color = PLAINS_COLOR;
            case 2 -> this.color = DESERT_COLOR;
            case 3 -> this.color = HILL_COLOR;
            case 4 -> this.color = WATER_COLOR;
        };
    }

    public Vector2i getOffset() {
        return offsetCoords;
    }

    public Vector3f[] getVerticesAsVecs() {
        return verticesVecs;
    }

    public float[] getVerticesAsFloats() {
        return verticesFloats;
    }

    //Below methods convert between coordinate types, namely cube and offset coords
    //Axial coords is the same as cube coords, but hides the S coord
    //Might as well keep all the data?
    public Vector2i cubeToAxialCoords(Vector3i cube) {
        int q = cube.x;
        int r = cube.y;
        return new Vector2i(q, r);
    }

    public Vector3i axialToCubeCoords(Vector2i axial) {
        int q = axial.x;
        int r = axial.y;
        int s = -q-r;
        return new Vector3i(q, r, s);
    }

    public Vector2i cubeToOffsetCoords(Vector3i cube) {
        int col = cube.x;
        int row = cube.y + (cube.x - (cube.x&1))/2;
        return new Vector2i(col, row);
    }

    public Vector3i offsetToCubeCoords(Vector2i offset) {
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

    public boolean rayIntersect(Vector3f worldPos, Vector4f mouseDir, Vector3f cameraPos) {
        float tMin = Float.MIN_VALUE;
        float tMax = Float.MAX_VALUE;
        Vector3f rayDirection = new Vector3f(mouseDir.x, mouseDir.y, mouseDir.z);

        for (int i = 0; i < 3; i++) {  // Iterate over x, y, z axes
            float rayDirComponent = rayDirection.get(i);
            float rayOriginComponent = cameraPos.get(i);
            float aabbMinComponent = aabbMin.get(i);
            float aabbMaxComponent = aabbMax.get(i);

            if (Math.abs(rayDirComponent) < 1E-6) {  // Ray is parallel to the slab
                if (rayOriginComponent < aabbMinComponent || rayOriginComponent > aabbMaxComponent) {
                    return false;  // Ray is outside the slab
                }
            } else {
                float invDir = 1.0f / rayDirComponent;
                float t1 = (aabbMinComponent - rayOriginComponent) * invDir;
                float t2 = (aabbMaxComponent - rayOriginComponent) * invDir;

                if (t1 > t2) {  // Swap t1 and t2 if t1 > t2
                    float temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                tMin = Math.max(tMin, t1);  // Update tMin
                tMax = Math.min(tMax, t2);  // Update tMax

                if (tMin > tMax) {  // No intersection
                    return false;
                }
            }
        }
        return true;  // Intersection found
    }
}
