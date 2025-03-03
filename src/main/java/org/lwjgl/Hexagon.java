package org.lwjgl;

import org.joml.*;
import org.joml.primitives.AABBf;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.util.Random;

public class Hexagon extends SceneObject {
    private int vaoId, vboId; // Vertex Array Object and Vertex Buffer Object
    private Vector3f color;   // Random color for the hexagon
    private Vector3i[] cubeDirectionVectors;
    private Vector2i offsetCoords;
    private AABBf aabb;
    public static final float SIZE = 1.0f;

    public Hexagon(Vector2i offsetPos) {
        super();
        Random rand = new Random();
        color = new Vector3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        initGeometry();
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
        aabb = new AABBf();
    }

    private void initGeometry() {
        // Hexagon vertices (6 vertices forming a regular hexagon)
        float[] vertices = new float[18]; // 6 vertices * 3 coordinates
        float radius = SIZE;
        for (int i = 0; i < 6; i++) {
            float angle = (float) (i * Math.PI / 3); // 60 degrees apart
            vertices[i * 3] = radius * (float) Math.cos(angle);     // x
            vertices[i * 3 + 1] = radius * (float) Math.sin(angle); // y
            vertices[i * 3 + 2] = 0.0f;                            // z
        }
        this.vertices = vertices;

        // Create VAO and VBO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Vertex attribute (position)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    @Override
    public void render(int shaderProgram) {
        // Bind shader and set uniforms
        glUseProgram(shaderProgram);
        int modelLoc = glGetUniformLocation(shaderProgram, "model");
        glUniformMatrix4fv(modelLoc, false, worldMatrix.get(new float[16]));
        int colorLoc = glGetUniformLocation(shaderProgram, "color");
        glUniform3f(colorLoc, color.x, color.y, color.z);

        // Render hexagon
        glBindVertexArray(vaoId);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6); // 6 vertices for hexagon
        glBindVertexArray(0);

        // Render children
        for (SceneObject child : children) {
            child.render(shaderProgram);
        }
    }

    @Override
    public void update(Scene scene, long deltaTime) {

    }

    // Cleanup method (call when done)
    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }

    public float[] getVertices() {
        return vertices;
    }

    public Vector2i getOffset() {
        return offsetCoords;
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

    public boolean isPointInside(Vector3f worldPos) {
        System.out.println(getOffset().x + " " + getOffset().y);
        System.out.println(position.x + " " + position.y + " " + position.z);
        return true;
    }

    private boolean isPointLeftOfLine(Vector2f point, Vector2f lineStart, Vector2f lineEnd) {
        // 2D cross product to determine if point is left of line
        return ((lineEnd.x - lineStart.x) * (point.y - lineStart.y) -
                (lineEnd.y - lineStart.y) * (point.x - lineStart.x)) > 0;
    }
}
