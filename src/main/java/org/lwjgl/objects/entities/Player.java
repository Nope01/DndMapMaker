package org.lwjgl.objects.entities;

import org.joml.Vector2i;
import org.lwjgl.Scene;
import org.lwjgl.dndMechanics.statusEffects.Dash;
import org.lwjgl.objects.Grid;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.objects.hexagons.Hexagon;
import org.lwjgl.shaders.ShaderProgramCache;
import org.lwjgl.textures.TextureCache;
import org.lwjgl.utils.VectorUtils;
import org.lwjgl.engine.input.InputHandler;

import static org.lwjgl.objects.entities.Classes.BARD;
import static org.lwjgl.objects.entities.Races.HUMAN;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class Player extends Creature {

    public Player(Vector2i offsetPos) {
        super(offsetPos);

        new Player("A dude", BARD, HUMAN, 10, 10, 100, offsetPos);
    }

    public Player(String name, int classType, int raceType, int moveSpeed, int AC, int HP, Vector2i offsetPos) {
        super(offsetPos);
        this.setName(name);
        this.setClassType(classType);
        this.setRaceType(raceType);
        this.setMoveSpeed(moveSpeed);
        this.setMaxMoveSpeed(moveSpeed);
        this.setAC(AC);
        this.setHP(HP);
        this.setMaxHP(HP);
        this.setDungeonVisibleRange(10);

        this.setMaxActions(1);
        this.setMaxBonusActions(1);

        this.setActions(getMaxActions());
        this.setBonusActions(getMaxBonusActions());
        this.setReaction(1);

        this.setOffsetPos(offsetPos);
        this.setCubePos(Hexagon.offsetToCubeCoords(offsetPos));
    }



    @Override
    public void render() {
        super.render();
    }

    @Override
    public void update(Scene scene, float deltaTime, InputHandler inputHandler) {
    }

    @Override
    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }

    public static Player createCreatureRandomPos(String name, int classType, int raceType, int moveSpeed, int AC, int HP) {
        Vector2i offset = new Vector2i(VectorUtils.randomInt(30, 50), VectorUtils.randomInt(20, 30));
        return new Player(name, classType, raceType, moveSpeed, AC, HP, offset);
    }

    public static Player remakePlayer(Player oldPlayer, Grid grid) {
        Player newPlayer = new Player(oldPlayer.getName(),
                oldPlayer.getClassType(),
                oldPlayer.getRaceType(),
                oldPlayer.getMoveSpeed(),
                oldPlayer.getAC(),
                oldPlayer.getHP(),
                oldPlayer.getOffsetPos());

        newPlayer.setTexture(oldPlayer.getTexture());
        newPlayer.setParent(grid.getHexagonAt(oldPlayer.parent.getCubePos()));
        newPlayer.setPosition(0.0f, 0.2f, 0.0f);
        newPlayer.setShaderProgram(oldPlayer.getShaderProgram());
        newPlayer.setId(oldPlayer.getId());

        return newPlayer;
    }

    public Player cloneForContext(Grid grid, TextureCache textureCache, ShaderProgramCache shaderCache) {
        Player newPlayer = new Player(
                this.getName(),
                this.getClassType(),
                this.getRaceType(),
                this.getMoveSpeed(),
                this.getAC(),
                this.getMaxHP(),
                this.getOffsetPos()
        );
        newPlayer.setTexture(textureCache.getTexture(this.getTexture().getTextureName()));
        newPlayer.setShaderProgram(shaderCache.getShader("creature"));
        newPlayer.setParent(grid.getHexagonAt(this.parent.getCubePos()));
        newPlayer.setPosition(this.getPosition());
        newPlayer.setId(this.getId());

        return newPlayer;
    }

    public void resetStats() {
        setMoveSpeed(getMaxMoveSpeed());
        setActions(getMaxActions());
        setBonusActions(getMaxBonusActions());
        setReaction(1);
        if (hasStatusEffect(Dash.class)) {
            removeStatusEffect(Dash.class);
        }
    }
}
