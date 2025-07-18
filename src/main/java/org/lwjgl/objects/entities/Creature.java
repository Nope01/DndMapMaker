package org.lwjgl.objects.entities;

import org.joml.Vector2i;
import org.joml.Vector3i;
import org.lwjgl.objects.hexagons.CityHexagon;
import org.lwjgl.objects.hexagons.CombatHexagon;
import org.lwjgl.dndMechanics.statusEffects.StatusEffect;
import org.lwjgl.objects.hexagons.Hexagon;
import org.lwjgl.utils.ObjectUtils;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.objects.models.opengl.HexagonShape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private String name;
    private int classType;
    private int raceType;
    private int moveSpeed;
    private int maxMoveSpeed;
    private int HP;
    private int maxHP;
    private int AC;
    private int dungeonVisibleRange;
    private boolean isVisible;
    private int actions;
    private int maxActions;
    private int bonusActions;
    private int maxBonusActions;
    private int reaction;

    private List<StatusEffect> statusEffects = new ArrayList<>();
    private Set<Hexagon> visibleTiles = new HashSet<>();
    private Set<Hexagon> reachableTiles = new HashSet<>();


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


    public Creature() {
        initGeometry();
        initAabb();
    }
    public Creature(Vector2i offsetPos) {
        initGeometry();
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
        int selected = glGetUniformLocation(shaderProgram, "selected");
        glUniform1i(selected, this.getSelected() ? 1 : 0);
        int texCoords = glGetUniformLocation(shaderProgram, "texCoords");
        glUniform2f(texCoords, this.texCoords[0], this.texCoords[1]);
        int isHidden = glGetUniformLocation(shaderProgram, "isHidden");
        glUniform1i(isHidden, this.isVisible ? 1 : 0);

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

    private void initGeometry() {
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
    }

    //Continent useage, should get rid of soon
    //TODO replace these in continent editor
    public static int moveSpeedToHexSpeed(int moveSpeed) {
        return moveSpeed / 5;
    }

    //NOT USEFUL FOR PROPER CREATURE MOVEMENT, USE HEXREACHABLE INSTEAD
    public boolean canMoveCreature(SceneObject origin, SceneObject destination) {
        if (!(origin instanceof Hexagon) && !(destination instanceof Hexagon)) {
            System.out.println("Selected destination is not a Hexagon");
        }
        else {
            Vector3i originCoords = ((Hexagon) origin.parent).getCubePos();
            Vector3i destinationCoords = ((Hexagon) destination).getCubePos();

            if (isValidMove(originCoords, destinationCoords)) {
                if (destination instanceof CityHexagon) {
                    if (((CityHexagon) destination).isHalfCover) {
                        System.out.println("Blocked from moving because of half cover");
                        return false;
                    }
                }
                else if (destination instanceof CombatHexagon) {
                    if ((((CombatHexagon) destination).isHalfCover)) {
                        System.out.println("Blocked from moving because of half cover");
                        return false;
                    }
                    else if ((((CombatHexagon) destination).isFullCover)) {
                        System.out.println("Blocked from moving because of full cover");
                        return false;
                    }
                    if (((CombatHexagon) destination).isWall) {
                        System.out.println("Blocked from moving because of wall");
                        return false;
                    }
                }
                System.out.println("Moving Creature");
                return true;
            }
            else {
                System.out.println("Too far");
                return false;
            }
        }
        return false;
    }

    private boolean isValidMove(Vector3i from, Vector3i to) {
        return Hexagon.cubeDistance(from, to) <= moveSpeed;
    }

    public void moveIfValid(SceneObject selectedObject, SceneObject hoveredObject) {
        if (reachableTiles.contains(selectedObject)) {
            this.setMoveSpeed(this.getMoveSpeed() - this.getDistanceToHexagon((Hexagon) hoveredObject));
            this.setParent(selectedObject);
            this.setOffsetAndCubePos(selectedObject.getOffsetPos());
            this.initAabb();
        }
        this.clearReachableTiles();
    }


    //Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getClassType() {
        return classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getAC() {
        return AC;
    }

    public void setAC(int AC) {
        this.AC = AC;
    }

    public int getRaceType() {
        return raceType;
    }

    public void setRaceType(int raceType) {
        this.raceType = raceType;
    }

    public int getDungeonVisibleRange() {
        return dungeonVisibleRange;
    }

    public void setDungeonVisibleRange(int dungeonVisibleRange) {
        this.dungeonVisibleRange = dungeonVisibleRange;
    }

    public void addStatusEffect(StatusEffect statusEffect) {
        statusEffects.add(statusEffect);
    }

    public boolean hasStatusEffect(Class<? extends StatusEffect> statusEffect) {
        return statusEffects.stream()
                .anyMatch(e -> e.getClass() == statusEffect);
    }
    public boolean hasStatusEffect(StatusEffect statusEffect) {
        return statusEffects.contains(statusEffect);
    }
    public boolean hasStatusEffect(String statusEffect) {
        return statusEffects.stream()
                .anyMatch(e -> e.getName().equals(statusEffect));
    }

    public void removeStatusEffect(Class<? extends StatusEffect> statusEffect) {
        statusEffects.stream()
                .filter(e -> e.getClass() == statusEffect)
                .findFirst()
                .ifPresent(e -> {
                    e.removeEffect(this);
                    statusEffects.remove(e);
                });
    }

    public void removeAllStatusEffects() {
        new ArrayList<>(statusEffects).forEach(e -> removeStatusEffect(e.getClass()));
        statusEffects.clear();
    }

    public List<StatusEffect> getStatusEffects() {
        return statusEffects;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Set<Hexagon> getVisibleTiles() {
        return visibleTiles;
    }

    public void setVisibleTiles(Set<Hexagon> visibleTiles) {
        for (Hexagon hex : visibleTiles) {
            hex.setVisible(true);
        }
        this.visibleTiles = visibleTiles;

    }

    public Set<Hexagon> getReachableTiles() {
        return reachableTiles;
    }

    public void setReachableTiles(Set<Hexagon> reachableTiles) {
        this.reachableTiles = reachableTiles;
    }

    public void clearReachableTiles() {
        for (Hexagon hexagon : reachableTiles) {
            hexagon.setMovementHighlighted(false);
        }
        reachableTiles.clear();
    }

    public void clearVisibleTiles() {
        for (Hexagon hexagon : visibleTiles) {
            hexagon.setVisible(false);
        }
        visibleTiles.clear();
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getMaxMoveSpeed() {
        return maxMoveSpeed;
    }

    public void setMaxMoveSpeed(int maxMoveSpeed) {
        this.maxMoveSpeed = maxMoveSpeed;
    }

    public int getDistanceToHexagon(Hexagon hexagon) {
        return Hexagon.cubeDistance(this.getCubePos(), hexagon.getCubePos());
    }

    public int getActions() {
        return actions;
    }

    public void setActions(int actions) {
        this.actions = actions;
    }

    public void subAction() {
        actions--;
        if (actions < 0) {
            actions = 0;
        }
    }

    public void addAction() {
        actions++;
    }

    public int getMaxActions() {
        return maxActions;
    }

    public void setMaxActions(int maxActions) {
        this.maxActions = maxActions;
    }

    public int getBonusActions() {
        return bonusActions;
    }

    public void setBonusActions(int bonusActions) {
        this.bonusActions = bonusActions;
    }

    public void subBonusAction() {
        bonusActions--;
        if (bonusActions < 0) {
            bonusActions = 0;
        }
    }

    public void addBonusAction() {
        bonusActions++;
    }

    public int getMaxBonusActions() {
        return maxBonusActions;
    }

    public void setMaxBonusActions(int maxBonusActions) {
        this.maxBonusActions = maxBonusActions;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
    public void subReaction() {
        reaction--;
        if (reaction < 0) {
            reaction = 0;
        }
    }

    public void addReaction() {
        reaction++;
    }
}
