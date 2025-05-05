package org.lwjgl.objects.entities;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.objects.Hexagon;
import org.lwjgl.objects.ObjectUtils;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.objects.models.opengl.HexagonShape;
import org.lwjgl.objects.models.opengl.Plane;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public abstract class Creature extends SceneObject {
    String name;
    int type;

    public static final int BARD = 6;
    public static final int ORC = 9;
    /*
    perception
    investigation
    race
    creature type
    class

    health
    resistances
    weaknesses
    AC
    bloodied
     */


    public Creature(Vector2i offsetPos) {
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
        glUniform1i(hovered, this.hovered ? 1 : 0);
        int texCoords = glGetUniformLocation(shaderProgram, "texCoords");
        glUniform2f(texCoords, this.texCoords[0], this.texCoords[1]);

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
            System.out.println("Error creature: " + e);
        }

        for (SceneObject child : children) {
            child.render();
        }
    }
}
