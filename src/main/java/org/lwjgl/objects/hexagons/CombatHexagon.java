package org.lwjgl.objects.hexagons;

import org.joml.Vector2i;
import org.lwjgl.textures.Texture;
import org.lwjgl.textures.TextureCache;

import java.io.Serializable;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class CombatHexagon extends Hexagon implements Serializable {

    //TODO: use static ints instead of booleans for tile types?

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

        super.render();
    }

    public boolean isBlockedForMovement() {
        return isWall ;
    }

    /**
     * Paints the terrain texture of the hexagon.
     * <p> Also sets the isWall property based on the texture name.
     * @param selectedTerrain the texture to be applied to the hexagon
     */
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

    /**
     * Paints the icon texture of the hexagon.
     * <p> Also sets the isHalfCover and isFullCover properties based on the texture name.
     * @param selectedObstacle the texture to be applied as an icon
     */
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

    /**
     * Clears all terrain features of the hexagon.
     * <p> Resets the icon texture, terrain texture, and cover properties.
     * @param textureCache the texture cache to retrieve default textures
     */
    public void clearAllTerrainFeatures(TextureCache textureCache) {
        this.setIconTexture(textureCache.getTexture("empty"));
        this.setTexture(textureCache.getTexture("default_tile"));
        this.isHalfCover = false;
        this.isWall = false;
        this.isFullCover = false;
    }

}
