package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector3i;
import org.lwjgl.objects.hexagons.CityHexagon;
import org.lwjgl.objects.entities.Creature;
import org.lwjgl.objects.hexagons.Hexagon;
import org.lwjgl.objects.hexagons.HexagonMath;
import org.lwjgl.textures.Texture;
import org.lwjgl.utils.GuiUtils;
import org.lwjgl.utils.VectorUtils;
import org.lwjgl.engine.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.objects.*;
import org.lwjgl.objects.entities.Player;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.data.ApiCalls.getRandomName;
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
    private CityHexagon[] neighbours = new CityHexagon[6];
    private Texture selectedTerrain;


    public CityEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "City Editor");
        uiWidth = 400 * imGuiManager.getScale();
        uiHeight = 600 * imGuiManager.getScale();
        uiXPos = 0;

        hoveredObject = scene.getHoveredObject();
        grid = scene.getGrid().getGrid();
        gridClass = scene.getGrid();

        placeUiWindow();
    }

    @Override
    protected void update() {
        hoveredObject = scene.getHoveredObject();
        gridClass = scene.getGrid();
        grid = gridClass.getGrid();

        boolean clickInput = inputHandler.isLeftClicked();

        //Movement logic
        if (clickInput && selectedObject instanceof Player player) {
            selectedTerrain = null;
            player.moveIfValid(selectedObject);
            //Neighbours
            if (selectedObject.parent instanceof CityHexagon hexUnderPlayer) {
                Vector3i[] neighbourCoords = HexagonMath.getAllNeighbours(hexUnderPlayer.getCubePos());
                for (int i = 0; i < neighbours.length; i++) {
                    neighbours[i] = (CityHexagon) gridClass.getHexagonAt(neighbourCoords[i]);
                }
            }
        }

        //Selection logic
        if (clickInput && hoveredObject != null) {
            if (selectedObject != null) {
                selectedObject.setSelected(false);
            }
            selectedObject = hoveredObject;
            selectedObject.setSelected(true);

            //Highlight moveable tiles
            if (selectedObject instanceof Player player) {
                HexagonMath.hexReachable((Hexagon) player.parent, player.getMoveSpeed(), gridClass);
            }
        }

        //Icons
        if (clickInput && selectedObject instanceof CityHexagon) {
            System.out.println("Doing a tile");
            if (selectedTerrain != null) {
                ((CityHexagon) selectedObject).setIconTexture(selectedTerrain);
                ((CityHexagon) selectedObject).isHalfCover = true;
            }
        }

        //Deselect
        if (selectedObject != null && inputHandler.isRightClickedAndHeld()) {
            selectedObject.setSelected(false);
            selectedObject = null;
            selectedTerrain = null;
        }

        //Icon eraser
        if (hoveredObject instanceof CityHexagon && inputHandler.isRightClickedAndHeld()) {
            ((CityHexagon) hoveredObject).setIconTexture(scene.getTextureCache().getTexture("empty"));
            ((CityHexagon) hoveredObject).isHalfCover = false;
        }

        //Trap hover
        if (hoveredObject != null) {
            if (hoveredObject instanceof Trap) {
                hoveredObject = hoveredObject.parent;
            }
        }
    }

    @Override
    protected void renderContent() {
        ImGui.begin("City Editor",
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

        //Icon selection
        ImGui.setNextItemOpen(true);
        if (ImGui.treeNode("Grid", "Terrain")) {
            if (GuiUtils.createTerrainGrid(4, 1, tileNames, scene, this)) {
            }
        }

        ImGui.separator();
        //Delete players
        if (selectedObject != null) {
            ImGui.text("Selected: " + selectedObject.getId());
            if (selectedObject instanceof Player) {
                if (ImGui.button("Delete")) {
                    ImGui.openPopup("Delete creature");
                }
                ImGui.setNextWindowPos(center, ImGuiCond.Appearing, new ImVec2(0.5f, 0.5f));
                ImGui.setNextWindowSize(150 * imGuiManager.getScale(), 100 * imGuiManager.getScale());
                if (ImGui.beginPopupModal("Delete creature", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar)) {
                    GuiUtils.textCentered("Delete creature?");
                    //TODO: Make yes and no look nicer
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

        if (selectedObject instanceof CityHexagon) {
            ImGui.text("Cover: " + ((CityHexagon) selectedObject).isHalfCover);
        }

        for (int i = 0; i < neighbours.length; i++) {
            if (neighbours[i] != null) {
                if (neighbours[i].isHalfCover) {
                    ImGui.text("Covered " + HexagonMath.intToDirection(i));
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
            ImGui.inputInt("Health", HP);

            if (ImGui.button("Surprise me:)")) {
                classType = new ImInt(VectorUtils.randomInt(0, classList.length-1));
                raceType = new ImInt(VectorUtils.randomInt(0, raceList.length-1));
                name = new ImString(getRandomName());
            }
            ImGui.sameLine();
            if (ImGui.button("Add player")) {
                Player player = createCreatureRandomPos(name.toString(), classType.intValue(), raceType.intValue(), moveSpeed[0], AC[0], HP.intValue());
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

    public List<Creature> getCharacterList() {
        return characterList;
    }

    public void setSelectedTerrain(Texture selectedTerrain) {
        this.selectedTerrain = selectedTerrain;
    }
}
