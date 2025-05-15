package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector3i;
import org.lwjgl.cityMap.CityHexagon;
import org.lwjgl.objects.entities.Creature;
import org.lwjgl.textures.Texture;
import org.lwjgl.utils.HelperMethods;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.objects.*;
import org.lwjgl.objects.entities.Player;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.data.ApiCalls.getRandomName;
import static org.lwjgl.objects.Hexagon.areaSelectClear;
import static org.lwjgl.objects.Hexagon.showMovementRange;
import static org.lwjgl.objects.entities.Classes.*;
import static org.lwjgl.objects.entities.Player.createCreatureRandomPos;
import static org.lwjgl.objects.entities.Races.*;

public class CityEditor extends ImGuiWindow {
    private String[] tileNames = new String[]{
            "table",
            "barrel",
            "jungle_01",
            "sand_07",
    };

    private SceneObject hoveredObject;
    private SceneObject selectedObject;
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
    private Vector3i[] neighbours = new Vector3i[6];
    private Texture selectedTerrain;

    public CityEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "City Editor");
        uiWidth = 400;
        uiHeight = 600;
        uiXPos = 0;
        uiYPos = 20;

        hoveredObject = scene.getHoveredObject();
        grid = scene.getGrid().getGrid();
        gridClass = scene.getGrid();
    }

    @Override
    protected void init(Scene scene) {
        ImGui.setNextWindowPos(uiXPos, uiYPos);
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        renderContent();
    }

    @Override
    protected void update() {
        hoveredObject = scene.getHoveredObject();
        gridClass = scene.getGrid();
        grid = gridClass.getGrid();

        boolean clickInput = inputHandler.isLeftClicked();

        //Movement logic
        if (clickInput && selectedObject instanceof Player) {
            selectedTerrain = null;
            if (((Player) selectedObject).canMoveCreature(selectedObject, hoveredObject)) {
                selectedObject.setParent(hoveredObject);
                selectedObject.setOffsetPos(((Hexagon) selectedObject.parent).getOffsetCoords());
                selectedObject.initAabb();
            }
        }

        //Selection logic
        if (clickInput && hoveredObject != null) {
            areaSelectClear(gridClass);
            if (selectedObject != null) {
                selectedObject.selected = false;
            }
            selectedObject = hoveredObject;
            selectedObject.selected = true;

            //Highlight moveable tiles
            if (selectedObject instanceof Player) {
                showMovementRange(gridClass, (Hexagon) selectedObject.parent, ((Player) selectedObject).getMoveSpeed());
            }
            //Neighbours
            //TODO: get grid and match coords to hex object to see if they have an obstacle on it
            if (selectedObject.parent instanceof CityHexagon) {
                CityHexagon hexUnderPlayer = (CityHexagon) selectedObject.parent;
                neighbours = hexUnderPlayer.getAllNeighbours();
            }
        }

        //Icons
        if (clickInput && selectedObject instanceof CityHexagon) {
            if (selectedTerrain != null) {
                ((CityHexagon) selectedObject).setIconTexture(selectedTerrain);
                ((CityHexagon) selectedObject).isHalfCover = true;
            }
        }

        //Deselect
        if (selectedObject != null && inputHandler.isRightClicked()) {
            selectedObject.selected = false;
            selectedObject = null;
            areaSelectClear(gridClass);
            selectedTerrain = null;
        }

        //Icon eraser
        if (hoveredObject instanceof CityHexagon && inputHandler.isRightClicked()) {
            ((CityHexagon) hoveredObject).setIconTexture(scene.getTextureCache().getTexture("empty"));
            ((CityHexagon) hoveredObject).isHalfCover = false;
        }

        if (hoveredObject != null) {
            if (hoveredObject instanceof Trap) {
                hoveredObject = hoveredObject.parent;
            }
        }
    }

    @Override
    protected void renderContent() {
        ImGui.begin("City Editor", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);

        if (ImGui.button("New character")) {
            ImGui.openPopup("Create a character");
        }
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center, ImGuiCond.Appearing, new ImVec2(0.5f, 0.5f));
        openCharacterCreator();

        //Icon selection
        ImGui.setNextItemOpen(true);
        if (ImGui.treeNode("Grid", "Terrain")) {
            if (GuiUtils.createTerrainGrid(4, 1, tileNames, scene, this)) {
            }
        }


        //Delete players
        if (selectedObject != null) {
            ImGui.text("Selected: " + selectedObject.getId());
            if (selectedObject instanceof Player) {
                if (ImGui.button("Delete")) {
                    ImGui.openPopup("Delete creature");
                }
                ImGui.setNextWindowPos(center, ImGuiCond.Appearing, new ImVec2(0.5f, 0.5f));
                if (ImGui.beginPopupModal("Delete creature", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar)) {
                    ImGui.text("Delete creature?");
                    if (ImGui.button("Yes")) {
                        scene.removeObject(selectedObject);
                        characterList.remove(selectedObject);
                        ImGui.closeCurrentPopup();
                    }
                    ImGui.sameLine();
                    if (ImGui.button("No")) {
                        ImGui.closeCurrentPopup();
                    }
                    ImGui.endPopup();
                }
            }
        }

        ImGui.separator();
        if (selectedObject instanceof CityHexagon) {
            ImGui.text("Cover: " + ((CityHexagon) selectedObject).isHalfCover);
        }

        for (int i = 0; i < neighbours.length; i++) {
            if (neighbours[i] != null) {
                ImGui.text("Neighbour " + i + ": " + neighbours[i].x + ", " + neighbours[i].y + ", " + neighbours[i].z);
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
            if (ImGui.beginPopupModal("Success", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
                ImGui.text("Character added");
                if (ImGui.button("Noice")) {
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

    public void setSelectedTerrain(Texture selectedTerrain) {
        this.selectedTerrain = selectedTerrain;
    }
}
