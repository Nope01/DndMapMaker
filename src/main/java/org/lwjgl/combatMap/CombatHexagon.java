package org.lwjgl.combatMap;

import org.joml.Vector2i;
import org.lwjgl.objects.Hexagon;

import java.io.Serializable;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;

public class CombatHexagon extends Hexagon implements Serializable {
    private int movementModifier;
    private int visibilityModifier;
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
        glUniform1i(selected, this.selected ? 1 : 0);
        int highlighted = glGetUniformLocation(shaderProgram, "highlighted");
        glUniform1i(highlighted, this.highlighted ? 1 : 0);
        int isVisible = glGetUniformLocation(shaderProgram, "isVisible");
        glUniform1i(isVisible, this.isVisible ? 1 : 0);
        int movementHighlighted = glGetUniformLocation(shaderProgram, "movementHighlighted");
        glUniform1i(movementHighlighted, this.isMovementHighlighted() ? 1 : 0);
        int spellHighlighted = glGetUniformLocation(shaderProgram, "spellHighlighted");
        glUniform1i(spellHighlighted, this.isSpellHighlighted() ? 1 : 0);
        super.render();
    }

    public boolean isBlocked() {
        return isWall || isFullCover || isHalfCover;
    }
}
