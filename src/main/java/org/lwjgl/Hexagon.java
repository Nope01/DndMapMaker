package org.lwjgl;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.Random;

public class Hexagon extends SceneObject {
    private int vaoId, vboId; // Vertex Array Object and Vertex Buffer Object
    private Vector3f color;   // Random color for the hexagon
    public static final float SIZE = 1.0f;

    public Hexagon() {
        super();
        Random rand = new Random();
        color = new Vector3f(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        initGeometry();
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
}