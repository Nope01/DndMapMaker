package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.lwjgl.Scene;
import org.lwjgl.Spells;
import org.lwjgl.combatMap.CombatHexagon;
import org.lwjgl.dndMechanics.statusEffects.Blinded;
import org.lwjgl.dndMechanics.statusEffects.Dash;
import org.lwjgl.dndMechanics.statusEffects.Grappled;
import org.lwjgl.dndMechanics.statusEffects.Invisible;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.Grid;
import org.lwjgl.objects.Hexagon;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.objects.entities.Classes;
import org.lwjgl.objects.entities.Creature;
import org.lwjgl.objects.entities.Player;
import org.lwjgl.objects.entities.Races;
import org.lwjgl.textures.Texture;
import org.lwjgl.utils.HelperMethods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.data.ApiCalls.getRandomName;
import static org.lwjgl.objects.Hexagon.*;
import static org.lwjgl.objects.entities.Classes.FIGHTER;
import static org.lwjgl.objects.entities.Classes.classList;
import static org.lwjgl.objects.entities.Player.createCreatureRandomPos;
import static org.lwjgl.objects.entities.Races.*;

public class CombatEditor extends ImGuiWindow {
    private String[] terrainNames = new String[]{
            "wall_01",
            "wall_02",
            "floor_01",
    };
    private String[] obstacleNames = new String[]{
            "barrel",
            "table",
            "coffer",
    };

    private SceneObject hoveredObject;
    private SceneObject selectedObject;
    private Texture selectedObstacle;
    private Texture selectedTerrain;
    private Grid gridClass;
    private Hexagon[][] grid;


    //Character creator variables
    ImString name = new ImString(20);
    ImInt classType = new ImInt(FIGHTER);
    ImInt raceType = new ImInt(AASIMAR);
    int[] moveSpeed = new int[] {4};
    ImInt maxHP = new ImInt(15);
    int[] AC = new int[] {12};

    private List<Creature> characterList = new ArrayList<>();
    private CombatHexagon[] neighbours = new CombatHexagon[6];

    private boolean fogOfWar = false;
    private boolean overrideMovement = false;

    private int spellType = 0;
    private int[] spellSize = new int[]{1};
    private int[] spellDirection = new int[]{N};
    private Set<Hexagon> spellHighlightedTiles = new HashSet<>();

    private int menuCurrentlyOpen = 0;
    private int terrainButtonIcon;
    private int spellButtonIcon;
    private int combatButtonIcon;

    private Creature creatureToMove;

    private List<DeathSave> deathSavesList = new ArrayList<>();

    public CombatEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "Combat Editor");
        uiWidth = 400 * imGuiManager.getScale();
        uiHeight = 600 * imGuiManager.getScale();
        uiXPos = 0;
        uiYPos = 0;

        hoveredObject = scene.getHoveredObject();
        grid = scene.getGrid().getGrid();
        gridClass = scene.getGrid();

        terrainButtonIcon = scene.getTextureCache().getTexture("repair-tools").getTextureId();
        spellButtonIcon = scene.getTextureCache().getTexture("magic").getTextureId();
        combatButtonIcon = scene.getTextureCache().getTexture("sword").getTextureId();

        placeUiWindow();
    }

    @Override
    protected void update() {
        hoveredObject = scene.getHoveredObject();
        gridClass = scene.getGrid();
        grid = gridClass.getGrid();

        boolean clickInput = inputHandler.isLeftClicked();

        //Selection logic
        if (clickInput && hoveredObject != null) {
            if (selectedObject != null) {
                selectedObject.setSelected(false);
            }
            selectedObject = hoveredObject;
            selectedObject.setSelected(true);
        }


        //Terrain
        if (menuCurrentlyOpen == 0) {
            //Terrain
            if (inputHandler.isLeftClickedAndHeld() && hoveredObject instanceof CombatHexagon) {
                if (selectedTerrain != null) {
                    ((CombatHexagon) hoveredObject).setTexture(selectedTerrain);
                    if (selectedTerrain.getTextureName().contains("wall")) {
                        ((CombatHexagon) hoveredObject).isWall = true;
                    }
                    else {
                        ((CombatHexagon) hoveredObject).isWall = false;
                    }
                }
            }

            //Icons
            if (clickInput && selectedObject instanceof CombatHexagon) {
                if (selectedObstacle != null) {
                    ((CombatHexagon) selectedObject).setIconTexture(selectedObstacle);
                    if (selectedObstacle.getTextureName().contains("barrel")) {
                        ((CombatHexagon) selectedObject).isHalfCover = true;
                    }
                    else {
                        ((CombatHexagon) selectedObject).isHalfCover = false;
                    }
                    if (selectedObstacle.getTextureName().contains("table")) {
                        ((CombatHexagon) selectedObject).isFullCover = true;
                    }
                    else {
                        ((CombatHexagon) selectedObject).isFullCover = false;
                    }
                }
            }

            //Eraser
            if (hoveredObject instanceof CombatHexagon hoveredHex && inputHandler.isRightClicked()) {
                hoveredHex.setIconTexture(scene.getTextureCache().getTexture("empty"));
                hoveredHex.setTexture(scene.getTextureCache().getTexture("floor_01"));
                hoveredHex.isHalfCover = false;
                hoveredHex.isWall = false;
                hoveredHex.isFullCover = false;
            }
        }

        //Spells
        else if (menuCurrentlyOpen == 1) {

            //reset tiles
            for (Hexagon hex : spellHighlightedTiles) {
                hex.setSpellHighlighted(false);
            }

            //Spell highlighting
            if (hoveredObject instanceof CombatHexagon hoveredHex) {
                //Line between hovered and selected hex specifically
                if (selectedObject instanceof CombatHexagon selectedHex) {
                    if (spellType == 0) {
                        spellHighlightedTiles = Hexagon.cubeLineDraw(hoveredHex.getCubeCoords(), selectedHex.getCubeCoords(), gridClass);
                    }
                }
                if (spellType == 1) {
                    spellHighlightedTiles =
                            hexVisible(hoveredHex,spellSize[0], gridClass);
                }
                if (spellType == 2) {
                    spellHighlightedTiles =
                            Hexagon.hexCone(hoveredHex.getCubeCoords(), spellDirection[0], spellSize[0], gridClass);
                }
                for (Hexagon hex : spellHighlightedTiles) {
                    hex.setSpellHighlighted(true);
                }
            }

        }

        //Combat
        if (clickInput) {
            //Movement logic
            if (creatureToMove != null) {
                if (creatureToMove.getReachableTiles().contains(selectedObject)) {
                    creatureToMove.setParent(hoveredObject);
                    creatureToMove.setOffsetPos(((Hexagon) selectedObject).getOffsetCoords());
                    creatureToMove.initAabb();
                    creatureToMove.clearReachableTiles();
                    if (fogOfWar) {
                        creatureToMove.clearVisibleTiles();
                    }
                    creatureToMove = null;
                }
                else {
                    creatureToMove.clearReachableTiles();
                    if (fogOfWar) {
                        creatureToMove.clearVisibleTiles();
                    }
                    creatureToMove = null;
                }
            }

            if (selectedObject instanceof Player player) {
                selectedObstacle = null;
                selectedTerrain = null;
                player.setReachableTiles
                        (hexReachable((CombatHexagon)selectedObject.parent, player.getMoveSpeed(), gridClass));
                if (overrideMovement) {
                    player.setReachableTiles(gridClass.getAllHexagons());
                }

                creatureToMove = player;
            }


            //Vision logic
            if (selectedObject instanceof Creature creature) {
                for (Creature character : characterList) {
                    if (fogOfWar) {
                        character.clearVisibleTiles();
                    }
                }
                creature.setVisibleTiles(
                        hexVisible((CombatHexagon)creature.parent, creature.getDungeonVisibleRange(), gridClass));
            }
            else {
                for (Creature creature : characterList) {
                    creature.setVisibleTiles(
                            hexVisible((CombatHexagon)creature.parent, creature.getDungeonVisibleRange(), gridClass));
                }
            }

        }

        //Deselect
        if (selectedObject != null && inputHandler.isRightClicked()) {
            selectedObject.setSelected(false);
            selectedObject = null;
            selectedObstacle = null;
            selectedTerrain = null;

            if (creatureToMove != null) {
                if (fogOfWar) {
                    creatureToMove.clearVisibleTiles();
                }
                creatureToMove.clearReachableTiles();
                creatureToMove = null;
            }
        }

        if (inputHandler.isRightClicked()) {
            for (Hexagon hex : spellHighlightedTiles) {
                hex.setSpellHighlighted(false);
            }
            spellHighlightedTiles.clear();
        }
    }

    @Override
    protected void renderContent() {
        ImGui.begin("Combat Editor",
                ImGuiWindowFlags.NoResize |
                        ImGuiWindowFlags.NoMove |
                        ImGuiWindowFlags.MenuBar);

        imGuiManager.drawMenuBar(imGuiManager, scene, inputHandler);

        if (ImGui.button("New character")) {
            ImGui.openPopup("Create a character");
        }
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center, ImGuiCond.Appearing, new ImVec2(0.5f, 0.5f));
        ImGui.setNextWindowSize(450 * imGuiManager.getScale(), 220 * imGuiManager.getScale());
        openCharacterCreator();
        if (ImGui.checkbox("Fog of War", fogOfWar)) {
            fogOfWar = !fogOfWar;
            gridClass.applyFogOfWar(fogOfWar);
        }
        ImGui.sameLine();
        if (ImGui.checkbox("Override movement", overrideMovement)) {
            overrideMovement = !overrideMovement;
        }

        //Change which menu gets rendered
        if (ImGui.imageButton(terrainButtonIcon, 35.0f, 35.0f)) {
            menuCurrentlyOpen = 0;
        }
        ImGui.sameLine();
        if (ImGui.imageButton(spellButtonIcon, 35.0f, 35.0f)) {
            menuCurrentlyOpen = 1;
        }
        ImGui.sameLine();
        if (ImGui.imageButton(combatButtonIcon, 35.0f, 35.0f)) {
            menuCurrentlyOpen = 2;
        }
        ImGui.separator();

        //Terrain
        if (menuCurrentlyOpen == 0) {
            ImGui.setNextItemOpen(true);
            if (ImGui.treeNode("Grid", "Terrain")) {
                if (GuiUtils.createTerrainGrid(3, 1, terrainNames, scene, this)) {
                    selectedObstacle = null;
                }
            }

            ImGui.setNextItemOpen(true);
            if (ImGui.treeNode("Grid", "Obstacles")) {
                if (GuiUtils.createObstacleGrid(3, 1, obstacleNames, scene, this)) {
                    selectedTerrain = null;
                }
            }

            if (ImGui.button("Make all hexes floor")) {
                for (int row = 0; row < gridClass.rows; row++) {
                    for (int col = 0; col < gridClass.columns; col++) {
                        grid[row][col].setTexture(scene.getTextureCache().getTexture("floor_01"));
                        ((CombatHexagon) grid[row][col]).isWall = false;
                    }
                }
            }
        }
        //Spells
        if (menuCurrentlyOpen == 1) {
            if (ImGui.button("Select spell")) {
                ImGui.openPopup("Select spell");
            }
            if (ImGui.beginPopup("Select spell")) {
                if (ImGui.button("Line")) {
                    setSpellType(0);
                    ImGui.closeCurrentPopup();
                }
                if (ImGui.button("Circle")) {
                    setSpellType(1);
                    ImGui.closeCurrentPopup();
                }
                if (ImGui.button("Cone")) {
                    setSpellType(2);
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }
            ImGui.text("Spell type: " + Spells.getSpellName(spellType) );
            if (spellType != 0) {
                ImGui.sliderInt("Spell size", spellSize, 1, 10);
                ImGui.sliderInt("Direction", spellDirection, 0, 5, Hexagon.intToDirection(spellDirection[0]));
            }
        }

        if (menuCurrentlyOpen == 2) {
            if (selectedObject instanceof Player player) {
                ImGui.separator();
                if (ImGui.button("Blindness")) {
                    player.addStatusEffect(new Blinded(player));
                }
                if (player.hasStatusEffect(Blinded.class)) {
                    ImGui.sameLine();
                    if (ImGui.button("Clear")) {
                        player.removeStatusEffect(Blinded.class);
                    }
                }

                if (ImGui.button("Grappled")) {
                    player.addStatusEffect(new Grappled(player));
                }
                if (player.hasStatusEffect(Grappled.class)) {
                    ImGui.sameLine();
                    if (ImGui.button("Clear")) {
                        player.removeStatusEffect(Grappled.class);
                    }
                }

                if (ImGui.button("Invisible")) {
                    player.addStatusEffect(new Invisible(player));
                }
                if (player.hasStatusEffect(Invisible.class)) {
                    ImGui.sameLine();
                    if (ImGui.button("Clear")) {
                        player.removeStatusEffect(Invisible.class);
                    }
                }

                if (ImGui.button("Dash")) {
                    player.addStatusEffect(new Dash(player));
                }
                if (player.hasStatusEffect(Dash.class)) {
                    ImGui.sameLine();
                    if (ImGui.button("Clear")) {
                        player.removeStatusEffect(Dash.class);
                    }
                }

                ImGui.separator();
                if (ImGui.button("Clear all status effects")) {
                    player.removeAllStatusEffects();
                }
                ImGui.separator();
                if (ImGui.button("Toggle death save")) {
                    deathSavesList.add(new DeathSave(player));
                }
                if (!deathSavesList.isEmpty()) {
                    ImGui.sameLine();
                    for (DeathSave deathSave : deathSavesList) {
                        if (ImGui.button(deathSave.getPlayerName()) && player.getName().equals(deathSave.getPlayerName())) {
                            deathSavesList.remove(deathSave);
                            break;
                        }
                    }
                }
            }

            if (!deathSavesList.isEmpty()) {
                for (DeathSave deathSave : deathSavesList) {
                    deathSave.drawDeathSaveUI();
                }
            }

            //Player stat block
            if (selectedObject instanceof Player player) {
                renderStatBlock();
            }

        }

        ImGui.end();
    }

    private void openCharacterCreator() {
        if (ImGui.beginPopupModal("Create a character",
                ImGuiWindowFlags.NoResize
                        | ImGuiWindowFlags.NoMove)) {

            ImGui.inputText("Name", name);
            ImGui.sameLine();
            if (ImGui.button("Random##name")) {
                name = new ImString(getRandomName());
            }
            ImGui.combo("Class", classType, classList);
            ImGui.sameLine();
            if (ImGui.button("Random##class")) {
                classType = new ImInt(HelperMethods.randomInt(0, classList.length-1));
            }
            ImGui.combo("Race", raceType, raceList);
            ImGui.sameLine();
            if (ImGui.button("Random##race")) {
                raceType = new ImInt(HelperMethods.randomInt(0, raceList.length-1));
            }
            ImGui.sliderInt("Move speed", moveSpeed, 0, 10, "");
            ImGui.sameLine();
            ImGui.text(String.valueOf(moveSpeed[0] * 5));
            ImGui.sliderInt("AC", AC, 0, 25);
            ImGui.inputInt("Health", maxHP);

            if (ImGui.button("Surprise me:)")) {
                classType = new ImInt(HelperMethods.randomInt(0, classList.length-1));
                raceType = new ImInt(HelperMethods.randomInt(0, raceList.length-1));
                name = new ImString(getRandomName());
            }
            ImGui.sameLine();
            if (ImGui.button("Add player")) {
                if (name.isEmpty()) {
                    name = new ImString("Bingus");
                }
                Player player = createCreatureRandomPos(name.toString(), classType.intValue(), raceType.intValue(), moveSpeed[0], AC[0], maxHP.intValue());
                player.setTexture(scene.getTextureCache().getTexture("sandvich"));
                player.setId(name.toString());
                player.setShaderProgram(scene.getShaderCache().getShader("creature"));
                player.setParent(gridClass.getHexagonAt(player.getOffsetPos()));
                player.setPosition(0.0f, 0.2f, 0.0f);
                characterList.add(player);

                ImGui.openPopup("Success");
            }

            //Character created popup
            ImVec2 center = ImGui.getMainViewport().getCenter();
            ImGui.setNextWindowPos(center, ImGuiCond.Appearing, new ImVec2(0.5f, 0.5f));
            ImGui.setNextWindowSize(120 * imGuiManager.getScale(), 50 * imGuiManager.getScale());
            if (ImGui.beginPopupModal("Success",
                    ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar)) {
                GuiUtils.textCentered("Creature added!");
                if (GuiUtils.buttonCentered("Noice")) {
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }

            ImGui.sameLine();
            if (ImGui.button("Add NPC")) {
            }

            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }
    }

    private void renderStatBlock() {
        Player player = (Player) selectedObject;
        ImGui.separator();
        ImGui.text("Name: " + player.getName());
        ImGui.text("Class: " + Classes.getClassAsString(player.getClassType()));
        ImGui.text("Race: " + Races.getRaceAsString(player.getRaceType()));
        ImGui.text("Move speed: " + player.getMoveSpeed() * 5);
        ImGui.text("AC: " + player.getAC());
        ImGui.text("Health");
        if (ImGui.arrowButton("Health arrow left", ImGuiDir.Left)) {
            player.setHP(player.getHP() - 1);
        }
        ImGui.sameLine();
        ImGui.text(String.valueOf(player.getHP()));
        ImGui.sameLine();
        if (ImGui.arrowButton("Health arrow right", ImGuiDir.Right)) {
            player.setHP(player.getHP() + 1);
        }
        ImGui.progressBar((float) player.getHP() / player.getMaxHP(), player.getHP() + "/" +  player.getMaxHP() );
    }

    public List<Creature> getCharacterList() {
        return characterList;
    }

    public void setSelectedObstacle(Texture texture) {
        selectedObstacle = texture;
    }

    public void setSelectedTerrain(Texture texture) {
        selectedTerrain = texture;
    }

    public void setHoveredObjectAsWall() {
        if (hoveredObject instanceof CombatHexagon) {
            ((CombatHexagon) hoveredObject).isWall = true;
        }
    }

    public void setHoveredObjectAsFullCover() {
        if (hoveredObject instanceof CombatHexagon) {
            ((CombatHexagon) hoveredObject).isFullCover = true;
        }
    }

    public void setHoveredObjectAsHalfCover() {
        if (hoveredObject instanceof CombatHexagon) {
            ((CombatHexagon) hoveredObject).isHalfCover = true;
        }
    }

    public void setSpellType(int type) {
        this.spellType = type;
        spellHighlightedTiles.clear();
        if (spellType == 0) {

        }
    }
}
