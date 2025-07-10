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
import static org.lwjgl.objects.hexagons.HexagonMath.*;
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
    protected Texture iconTexture;
    protected int numFloats = 7 * 3;

    private boolean movementHighlighted;
    private boolean spellHighlighted;

    private boolean isVisible;
    public static final float SIZE = 1.0f;

    public Hexagon(Vector2i offsetPos) {
        colour = new Vector3f(0.2f, 0.2f, 0.2f);
        initGeometry();
        initAabb();
        this.setOffsetPos(offsetPos);
        this.setCubePos(offsetToCubeCoords(offsetPos));
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
        int texCoords = glGetUniformLocation(shaderProgram, "texCoords");
        glUniform2f(texCoords, this.texCoords[0], this.texCoords[1]);
        int isVisible = glGetUniformLocation(shaderProgram, "isVisible");
        glUniform1i(isVisible, this.isVisible() ? 1 : 0);
        int movementHighlighted = glGetUniformLocation(shaderProgram, "movementHighlighted");
        glUniform1i(movementHighlighted, this.isMovementHighlighted() ? 1 : 0);
        int spellHighlighted = glGetUniformLocation(shaderProgram, "spellHighlighted");
        glUniform1i(spellHighlighted, this.isSpellHighlighted() ? 1 : 0);

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

    /*
    -------------------------------------------------------------------------------------------------------
    Getters and Setters
    -------------------------------------------------------------------------------------------------------
     */

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
