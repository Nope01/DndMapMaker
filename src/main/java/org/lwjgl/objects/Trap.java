package org.lwjgl.objects;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.Scene;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.models.opengl.HexagonShape;
import org.lwjgl.objects.models.opengl.Plane;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Trap extends TileTrigger{

    public Trap(int triggerRadius, Vector2i offsetPos) {
        super(triggerRadius, offsetPos);
        numFloats = 7*3;

        verticesFloats = HexagonShape.vertices();
        verticesVecs = HexagonShape.verticesVecs(verticesFloats);
        texCoords = HexagonShape.texCoords();
        indices = HexagonShape.indices();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        ObjectUtils.bindVerticesList(verticesFloats);
        ObjectUtils.bindTexCoordList(texCoords);
        ObjectUtils.bindIndicesList(indices);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        initAabb();
    }

    @Override
    public void render() {
        glUseProgram(shaderProgram);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int modelLoc = glGetUniformLocation(shaderProgram, "model");
        glUniformMatrix4fv(modelLoc, false, worldMatrix.get(new float[16]));
        int hovered = glGetUniformLocation(shaderProgram, "hovered");
        glUniform1i(hovered, this.getHovered() ? 1 : 0);
        int texCoords = glGetUniformLocation(shaderProgram, "texCoords");
        glUniform2f(texCoords, this.texCoords[0], this.texCoords[1]);
        int isHidden = glGetUniformLocation(shaderProgram, "isHidden");
        glUniform1i(isHidden, this.getIsHidden() ? 1 : 0);

        glActiveTexture(GL_TEXTURE0);
        if (texture != null) {
            texture.bind();
        }

        glUniform1i(glGetUniformLocation(shaderProgram, "iconTexture"), 0);

        // Render hexagon
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, numFloats, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        int e = glGetError();
        if (e != GL_NO_ERROR) {
            System.out.println("Error trap: " + e);
        }

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
