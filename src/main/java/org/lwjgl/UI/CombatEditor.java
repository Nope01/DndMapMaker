package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDir;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector2i;
import org.lwjgl.Scene;
import org.lwjgl.dndMechanics.spells.Spells;
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
import org.lwjgl.utils.GuiUtils;
import org.lwjgl.utils.VectorUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.data.ApiCalls.getRandomName;
import static org.lwjgl.dndMechanics.spells.Spells.clearSpellHighlightedTiles;
import static org.lwjgl.input.InputUtils.*;
import static org.lwjgl.objects.Hexagon.*;
import static org.lwjgl.objects.entities.Classes.FIGHTER;
import static org.lwjgl.objects.entities.Classes.classList;
import static org.lwjgl.objects.entities.Player.createCreatureRandomPos;
import static org.lwjgl.objects.entities.Player.remakePlayer;
import static org.lwjgl.objects.entities.Races.*;
import static org.lwjgl.utils.GridUtils.*;
import static org.lwjgl.utils.VectorUtils.rgbToImVec4;

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
            selectedObject = selectHovered(hoveredObject, selectedObject);
        }

        //Terrain
        if (menuCurrentlyOpen == 0) {
            //Terrain
            if (inputHandler.isLeftClickedAndHeld() && hoveredObject instanceof CombatHexagon combatHexagon) {
                //Paint terrain
                combatHexagon.paintTerrainTexture(selectedTerrain);
            }

            //Icons
            if (clickInput && selectedObject instanceof CombatHexagon combatHexagon) {
                combatHexagon.paintIconTexture(selectedObstacle);
            }

            //Eraser
            if (hoveredObject instanceof CombatHexagon hoveredHex && inputHandler.isRightClicked()) {
                hoveredHex.clearAllTerrainFeatures(scene.getTextureCache());
            }
        }

        //Spells
        else if (menuCurrentlyOpen == 1) {
            clearSpellHighlightedTiles(spellHighlightedTiles);

            //Spell highlighting
            if (hoveredObject instanceof CombatHexagon || hoveredObject instanceof Creature) {
                CombatHexagon hoveredHex;
                if (hoveredObject instanceof CombatHexagon) {
                    hoveredHex = (CombatHexagon) hoveredObject;
                }
                else {
                    hoveredHex = (CombatHexagon) hoveredObject.parent;
                }
                //Line between hovered and selected hex specifically
                if (selectedObject instanceof CombatHexagon selectedHex) {
                    if (spellType == 0) {
                        spellHighlightedTiles = Spells.getHexesInLineSpell(selectedHex, hoveredHex, gridClass);
                    }
                }
                if (spellType == 1) {
                    spellHighlightedTiles =
                            Spells.getHexesInCircleSpell(hoveredHex, spellSize[0], gridClass);
                }
                if (spellType == 2) {
                    spellHighlightedTiles =
                            Spells.getHexesInConeSpell(hoveredHex, spellDirection[0], spellSize[0], gridClass);
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
                creatureToMove.moveIfValid(selectedObject, hoveredObject);

                if (fogOfWar) {
                    creatureToMove.clearVisibleTiles();
                }
                creatureToMove = null;
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

        }

        if (clickInput || inputHandler.isRightClicked()) {
            //Vision logic
            //Highlight all visible tiles unless a creature is selected, then only show their visible tiles
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
                creatureToMove.clearReachableTiles();
                creatureToMove = null;
            }
        }

        if (inputHandler.isRightClicked()) {
            clearSpellHighlightedTiles(spellHighlightedTiles);
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

                int scrollDirection = inputHandler.isMouseWheelMoved();
                if (scrollDirection != 0) {
                    spellDirection[0] += scrollDirection;
                    if (spellDirection[0] > 5) {
                        spellDirection[0] = 0;
                    }
                    if (spellDirection[0] < 0) {
                        spellDirection[0] = 5;
                    }
                }
            }
        }

        if (menuCurrentlyOpen == 2) {
            if (selectedObject instanceof Player player) {
                if (ImGui.button("Delete character")) {
                    scene.removeObject(player);
                    characterList.remove(player);
                    player.cleanup();
                    selectedObject = null;
                }
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
                classType = new ImInt(VectorUtils.randomInt(0, classList.length-1));
            }
            ImGui.combo("Race", raceType, raceList);
            ImGui.sameLine();
            if (ImGui.button("Random##race")) {
                raceType = new ImInt(VectorUtils.randomInt(0, raceList.length-1));
            }
            ImGui.sliderInt("Move speed", moveSpeed, 0, 10, "");
            ImGui.sameLine();
            ImGui.text(String.valueOf(moveSpeed[0] * 5));
            ImGui.sliderInt("AC", AC, 0, 25);
            ImGui.inputInt("Health", maxHP);

            if (ImGui.button("Surprise me:)")) {
                classType = new ImInt(VectorUtils.randomInt(0, classList.length-1));
                raceType = new ImInt(VectorUtils.randomInt(0, raceList.length-1));
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
                player.setParent(gridClass.getHexagonAt(player.getCubePos()));
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
        ImGui.text("AC: " + player.getAC());

        ImGui.text("Movement left: " + player.getMoveSpeed() * 5);
        ImGui.text("Health");
        ImGui.sameLine();
        if (ImGui.arrowButton("Health arrow left", ImGuiDir.Left)) {
            player.setHP(player.getHP() - 1);
        }
        ImGui.sameLine();
        ImGui.text(String.valueOf(player.getHP()));
        ImGui.sameLine();
        if (ImGui.arrowButton("Health arrow right", ImGuiDir.Right)) {
            player.setHP(player.getHP() + 1);
        }
        ImGui.pushStyleColor(ImGuiCol.PlotHistogram, rgbToImVec4(136, 8, 8, 255));
        ImGui.progressBar((float) player.getHP() / player.getMaxHP(), player.getHP() + "/" +  player.getMaxHP() );
        ImGui.popStyleColor();
        ImGui.separator();

        ImGui.text("Actions: " + player.getActions());
        ImGui.sameLine();
        if (ImGui.button("Use##Action")) {
            player.subAction();
        }
        ImGui.text("Bonus actions: " + player.getBonusActions());
        ImGui.sameLine();
        if (ImGui.button("Use##Bonus action")) {
            player.subBonusAction();
        }

        ImGui.text("Reactions: " + player.getReaction());
        ImGui.sameLine();
        if (ImGui.button("Use##Reaction")) {
            player.subReaction();
        }
    }

    public List<Creature> getCharacterList() {
        return characterList;
    }
    public void setCharacterList(List<Creature> characterList) {
        this.characterList = characterList;
    }
    public void clearCharacterList() {
        for (Creature creature : characterList) {
            scene.removeObject(creature);
            creature.cleanup();
        }
        characterList.clear();

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

    public void remakeSameCharacterList() {
        for (int i = 0; i < characterList.size(); i++) {
            Creature character = characterList.get(i);
            if (character instanceof Player player) {
                characterList.set(i,remakePlayer(player, scene.getGrid()));
            }
        }
    }

    public void remakeLoadedCharacterList(List<Creature> characterList) {
        for (int i = 0; i < characterList.size(); i++) {
            Creature character = characterList.get(i);
            if (character instanceof Player player) {
                characterList.set(i,remakePlayer(player, scene.getGrid()));
            }
        }
        setCharacterList(characterList);
    }
}
