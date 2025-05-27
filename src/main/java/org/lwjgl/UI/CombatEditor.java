package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector3i;
import org.lwjgl.Scene;
import org.lwjgl.Spells;
import org.lwjgl.combatMap.CombatHexagon;
import org.lwjgl.dndMechanics.statusEffects.Blinded;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.Grid;
import org.lwjgl.objects.Hexagon;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.objects.entities.Creature;
import org.lwjgl.objects.entities.Player;
import org.lwjgl.textures.Texture;
import org.lwjgl.utils.HelperMethods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.data.ApiCalls.getRandomName;
import static org.lwjgl.dndMechanics.statusEffects.StatusEffects.*;
import static org.lwjgl.objects.Hexagon.*;
import static org.lwjgl.objects.entities.Classes.FIGHTER;
import static org.lwjgl.objects.entities.Classes.classList;
import static org.lwjgl.objects.entities.Player.createCreatureRandomPos;
import static org.lwjgl.objects.entities.Races.AASIMAR;
import static org.lwjgl.objects.entities.Races.raceList;

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
    ImInt HP = new ImInt(15);
    int[] AC = new int[] {12};

    private List<Creature> characterList = new ArrayList<>();
    private CombatHexagon[] neighbours = new CombatHexagon[6];

    private boolean fogOfWar = false;

    private int spellType = 0;
    private int[] spellSize = new int[]{1};
    private Set<Hexagon> spellHighlightedTiles = new HashSet<>();
    private Set<Hexagon> reachableTiles = new HashSet<>();
    private Set<Hexagon> visibleTiles = new HashSet<>();

    private int menuCurrentlyOpen = 0;
    private int terrainButtonIcon;
    private int spellButtonIcon;
    private int combatButtonIcon;

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
            //Spell highlighting
            if (hoveredObject instanceof CombatHexagon hoveredHex) {
                //Line between hovered and selected hex specifically
                if (selectedObject instanceof CombatHexagon selectedHex) {
                    if (spellType == 0) {
                        spellHighlightedTiles = Hexagon.cubeLineDraw(hoveredHex.getCubeCoords(), selectedHex.getCubeCoords(), gridClass);
                    }
                }
                if (spellType == 1) {
                    spellHighlightedTiles = hexVisible(hoveredHex,spellSize[0], gridClass);
                }
                if (spellType == 2) {

                }
            }
            for (Hexagon hex : spellHighlightedTiles) {
                hex.setSpellHighlighted(true);
            }
        }

        //Combat
        if (true) {
            //Movement logic
            if (clickInput && selectedObject instanceof Player) {
                selectedObstacle = null;
                selectedTerrain = null;
                reachableTiles = hexReachable((CombatHexagon)selectedObject.parent, ((Player) selectedObject).getMoveSpeed(), gridClass);
                if (reachableTiles.contains(hoveredObject)) {
                    selectedObject.setParent(hoveredObject);
                    selectedObject.setOffsetPos(((Hexagon) selectedObject.parent).getOffsetCoords());
                    selectedObject.initAabb();
                    clearReachableTiles(gridClass, fogOfWar);
                }
                //Neighbours
                if (selectedObject.parent instanceof CombatHexagon hexUnderPlayer) {
                    Vector3i[] neighbourCoords = hexUnderPlayer.getAllNeighbours();
                    for (int i = 0; i < neighbours.length; i++) {
                        neighbours[i] = (CombatHexagon) gridClass.getHexagonAt(neighbourCoords[i]);
                    }
                }
            }

            //Highlight moveable tiles for selected player
            if (selectedObject instanceof Player player) {
                reachableTiles =
                        hexReachable((CombatHexagon)player.parent, player.getMoveSpeed(), gridClass);
                for (Hexagon hex : reachableTiles) {
                    hex.setMovementHighlighted(true);
                }
            }
            //Highlight all visible tiles
            for (Creature player : characterList) {
                visibleTiles =
                        hexVisible((CombatHexagon)player.parent, player.getDungeonVisibleRange(), gridClass);
                for (Hexagon hex : visibleTiles) {
                    hex.isVisible = true;
                }
            }
        }

        //Selection logic
        if (clickInput && hoveredObject != null) {
            clearReachableTiles(gridClass, fogOfWar);
            if (selectedObject != null) {
                selectedObject.selected = false;
            }
            selectedObject = hoveredObject;
            selectedObject.selected = true;
        }

        //Deselect
        if (selectedObject != null && inputHandler.isRightClicked()) {
            selectedObject.selected = false;
            selectedObject = null;
            clearReachableTiles(gridClass, fogOfWar);
            selectedObstacle = null;
            selectedTerrain = null;
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
            clearReachableTiles(gridClass, fogOfWar);
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
                ImGui.sliderInt("Spell size", spellSize, 1, 10 );
            }
        }

        if (menuCurrentlyOpen == 2) {
            if (selectedObject instanceof Player player) {
                if (ImGui.button("Blindness")) {
                    player.addStatusEffect(BLINDED);
                }
                if (ImGui.button("Incapacitated")) {
                    player.addStatusEffect(INCAPACITATED);
                }
                if (ImGui.button("Invisible")) {
                    player.addStatusEffect(INVISIBLE);
                    player.setHidden(true);
                }
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
            ImGui.inputInt("Health", HP);

            if (ImGui.button("Surprise me:)")) {
                classType = new ImInt(HelperMethods.randomInt(0, classList.length-1));
                raceType = new ImInt(HelperMethods.randomInt(0, raceList.length-1));
                name = new ImString(getRandomName());
            }
            ImGui.sameLine();
            if (ImGui.button("Add player")) {
                Player player = createCreatureRandomPos(name.toString(), classType.intValue(), raceType.intValue(), moveSpeed[0], AC[0], HP.intValue());
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
