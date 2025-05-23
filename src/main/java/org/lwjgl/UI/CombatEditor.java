package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector3i;
import org.lwjgl.Scene;
import org.lwjgl.cityMap.CityHexagon;
import org.lwjgl.combatMap.CombatHexagon;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.Grid;
import org.lwjgl.objects.Hexagon;
import org.lwjgl.objects.SceneObject;
import org.lwjgl.objects.entities.Creature;
import org.lwjgl.objects.entities.Player;
import org.lwjgl.textures.Texture;
import org.lwjgl.utils.HelperMethods;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.data.ApiCalls.getRandomName;
import static org.lwjgl.objects.Hexagon.areaSelectClear;
import static org.lwjgl.objects.Hexagon.showMovementRange;
import static org.lwjgl.objects.entities.Classes.FIGHTER;
import static org.lwjgl.objects.entities.Classes.classList;
import static org.lwjgl.objects.entities.Player.createCreatureRandomPos;
import static org.lwjgl.objects.entities.Races.AASIMAR;
import static org.lwjgl.objects.entities.Races.raceList;

public class CombatEditor extends ImGuiWindow {
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
    private CombatHexagon[] neighbours = new CombatHexagon[6];
    private Texture selectedObstacle;

    public CombatEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "Combat Editor");
        uiWidth = 400 * imGuiManager.getScale();
        uiHeight = 600 * imGuiManager.getScale();
        uiXPos = 0;
        uiYPos = 0;

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
        if (clickInput && selectedObject instanceof Player) {
            selectedObstacle = null;
            if (((Player) selectedObject).canMoveCreature(selectedObject, hoveredObject)) {
                selectedObject.setParent(hoveredObject);
                selectedObject.setOffsetPos(((Hexagon) selectedObject.parent).getOffsetCoords());
                selectedObject.initAabb();
            }
            //Neighbours
            if (selectedObject.parent instanceof CombatHexagon hexUnderPlayer) {
                Vector3i[] neighbourCoords = hexUnderPlayer.getAllNeighbours();
                for (int i = 0; i < neighbours.length; i++) {
                    neighbours[i] = (CombatHexagon) gridClass.getHexagonAt(neighbourCoords[i]);
                }
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
        }

        //Icons
        if (clickInput && selectedObject instanceof CombatHexagon) {
            if (selectedObstacle != null) {
                ((CombatHexagon) selectedObject).setIconTexture(selectedObstacle);
                ((CombatHexagon) selectedObject).isHalfCover = true;
            }
        }

        //Deselect
        if (selectedObject != null && inputHandler.isRightClicked()) {
            selectedObject.selected = false;
            selectedObject = null;
            areaSelectClear(gridClass);
            selectedObstacle = null;
        }

        //Icon eraser
        if (hoveredObject instanceof CombatHexagon && inputHandler.isRightClicked()) {
            ((CombatHexagon) hoveredObject).setIconTexture(scene.getTextureCache().getTexture("empty"));
            ((CombatHexagon) hoveredObject).isHalfCover = false;
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
}
