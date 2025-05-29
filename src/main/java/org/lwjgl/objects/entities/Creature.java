package org.lwjgl.objects.entities;

import org.joml.Vector2i;
import org.joml.Vector3i;
import org.lwjgl.cityMap.CityHexagon;
import org.lwjgl.combatMap.CombatHexagon;
import org.lwjgl.dndMechanics.statusEffects.StatusEffect;
import org.lwjgl.objects.Hexagon;
import org.lwjgl.objects.ObjectUtils;
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
    private int HP;
    private int AC;
    private int dungeonVisibleRange;

    private boolean isVisible;

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

    public static int moveSpeedToHexSpeed(int moveSpeed) {
        return moveSpeed / 5;
    }

    //NOT USEFUL FOR PROPER CREATURE MOVEMENT, USE HEXREACHABLE INSTEAD
    public boolean canMoveCreature(SceneObject origin, SceneObject destination) {
        if (!(origin instanceof Hexagon) && !(destination instanceof Hexagon)) {
            System.out.println("Selected destination is not a Hexagon");
        }
        else {
            Vector3i originCoords = ((Hexagon) origin.parent).getCubeCoords();
            Vector3i destinationCoords = ((Hexagon) destination).getCubeCoords();

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
}
