package org.lwjgl.objects;

import org.joml.Vector3f;
import org.lwjgl.Scene;
import org.lwjgl.input.InputHandler;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Trap extends TileTrigger{

    public Trap(int triggerRadius) {
        super(triggerRadius);
        numFloats = 4*3;
        verticesFloats = new float[]{
                -0.5f, 0.0f, -0.5f,
                -0.5f, 0.0f, 0.5f,
                0.5f, 0.0f, 0.5f,
                0.5f, 0.0f, -0.5f,
        };

        texCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
        };

        indices = new int[] {
                0, 1, 2,
                2, 3, 0
        };

        Vector3f[] verticesVecs = new Vector3f[numFloats / 3];
        int count = 0;
        for (int i = 0; i < verticesFloats.length; i += 3) {
            verticesVecs[count] = new Vector3f(verticesFloats[i], verticesFloats[i + 1], verticesFloats[i + 2]);
            count++;
        }

        this.verticesVecs = verticesVecs;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        ObjectUtils.bindVerticesList(verticesFloats);
        ObjectUtils.bindTexCoordList(texCoords);
        ObjectUtils.bindIndicesList(indices);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        initAabb();

    }

    protected void initAabb() {
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
    public void render() {
        glUseProgram(shaderProgram);

        int modelLoc = glGetUniformLocation(shaderProgram, "model");
        glUniformMatrix4fv(modelLoc, false, worldMatrix.get(new float[16]));
        int colorLoc = glGetUniformLocation(shaderProgram, "color");
        glUniform3f(colorLoc, colour.x, colour.y, colour.z);
        int selected = glGetUniformLocation(shaderProgram, "selected");
        glUniform1i(selected, this.selected ? 1 : 0);
        int texCoords = glGetUniformLocation(shaderProgram, "texCoords");
        glUniform2f(texCoords, this.texCoords[0], this.texCoords[1]);

        glActiveTexture(GL_TEXTURE0);
        if (texture != null) {
            texture.bind();
        }

        // Render hexagon
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, 4*3, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        for (SceneObject child : children) {
            child.render();

        }
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler inputHandler) {
    }

    @Override
    public void cleanup() {

    }
}
