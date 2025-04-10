package org.lwjgl.objects;

import org.lwjgl.InputHandler;
import org.lwjgl.Scene;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ImageQuad extends SceneObject{

    private int numVertices = 4;

    public ImageQuad() {
        super();
        initGeometry();
    }

    private void initGeometry() {
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

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        ObjectUtils.bindVerticesList(verticesFloats);
        ObjectUtils.bindTexCoordList(texCoords);
        ObjectUtils.bindIndicesList(indices);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    @Override
    public void render() {
        glUseProgram(shaderProgram);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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

        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, numVertices*3, GL_UNSIGNED_INT, 0);
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
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }


}
