package org.lwjgl.combatMap;

import org.joml.Vector2i;
import org.lwjgl.objects.Hexagon;
import org.lwjgl.textures.Texture;
import org.lwjgl.textures.TextureCache;

import java.io.Serializable;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class CombatHexagon extends Hexagon implements Serializable {
    private int movementModifier;
    private int visibilityModifier;

    //TODO: use static ints instead of booleans for tile types
    public boolean isHalfCover;
    public boolean isFullCover;
    public boolean isWall;

    public CombatHexagon(Vector2i offsetPos) {
        super(offsetPos);
    }

    @Override
    public void render() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glUseProgram(shaderProgram);
        int selected = glGetUniformLocation(shaderProgram, "selected");
        glUniform1i(selected, this.getSelected() ? 1 : 0);
        int highlighted = glGetUniformLocation(shaderProgram, "highlighted");
        glUniform1i(highlighted, this.highlighted ? 1 : 0);
        int isVisible = glGetUniformLocation(shaderProgram, "isVisible");
        glUniform1i(isVisible, this.isVisible() ? 1 : 0);
        int movementHighlighted = glGetUniformLocation(shaderProgram, "movementHighlighted");
        glUniform1i(movementHighlighted, this.isMovementHighlighted() ? 1 : 0);
        int spellHighlighted = glGetUniformLocation(shaderProgram, "spellHighlighted");
        glUniform1i(spellHighlighted, this.isSpellHighlighted() ? 1 : 0);
        super.render();
    }

    public boolean isBlocked() {
        return isWall || isFullCover || isHalfCover;
    }

    public void paintTerrainTexture(Texture selectedTerrain) {
        if (selectedTerrain != null) {
            this.setTexture(selectedTerrain);

            if (selectedTerrain.getTextureName().contains("wall")) {
                this.isWall = true;
            }
            else {
                this.isWall = false;
            }
        }
    }

    public void paintIconTexture(Texture selectedObstacle) {
        if (selectedObstacle != null) {
            this.setIconTexture(selectedObstacle);

            if (selectedObstacle.getTextureName().contains("barrel")) {
                this.isHalfCover = true;
            }
            else {
                this.isHalfCover = false;
            }
            if (selectedObstacle.getTextureName().contains("table")) {
                this.isFullCover = true;
            }
            else {
                this.isFullCover = false;
            }
        }
    }

    public void clearAllTerrainFeatures(TextureCache textureCache) {
        this.setIconTexture(textureCache.getTexture("empty"));
        this.setTexture(textureCache.getTexture("floor_01"));
        this.isHalfCover = false;
        this.isWall = false;
        this.isFullCover = false;
    }
}
