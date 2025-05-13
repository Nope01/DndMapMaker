package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.lwjgl.Utils;
import org.lwjgl.input.InputHandler;
import org.lwjgl.Scene;
import org.lwjgl.objects.*;
import org.lwjgl.objects.entities.Player;

import static org.lwjgl.data.ApiCalls.getRandomName;
import static org.lwjgl.objects.Hexagon.areaSelectClear;
import static org.lwjgl.objects.Hexagon.showMovementRange;
import static org.lwjgl.objects.entities.Classes.*;
import static org.lwjgl.objects.entities.Player.createCreatureRandomPos;
import static org.lwjgl.objects.entities.Races.*;

public class CityEditor extends ImGuiWindow {

    private SceneObject hoveredObject;
    private SceneObject selectedObject;
    private Grid gridClass;
    private Hexagon[][] grid;
    private int viewRadius = 4;

    ImString name = new ImString("Boris");
    ImInt classType = new ImInt(FIGHTER);
    ImInt raceType = new ImInt(AASIMAR);
    int[] moveSpeed = new int[] {
            0
    };
    ImInt HP = new ImInt(15);
    int[] AC = new int[] {
            0
    };

    public CityEditor(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "City Editor");
        uiWidth = 400;
        uiHeight = 250;
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
        grid = scene.getGrid().getGrid();
        gridClass = scene.getGrid();
        boolean clickInput = inputHandler.isLeftClicked();

        if (hoveredObject != null) {
            if (hoveredObject instanceof Trap) {
                hoveredObject = hoveredObject.parent;
            }
        }

        //Movement logic
        if (clickInput && selectedObject instanceof Player) {
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
        }

        if (selectedObject != null && inputHandler.isRightClicked()) {
            selectedObject.selected = false;
            selectedObject = null;
            areaSelectClear(gridClass);
        }



    }

    @Override
    protected void renderContent() {
        ImGui.begin("City Editor", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);

        if (ImGui.button("Show/hide traps")) {
            for (SceneObject obj : scene.getAllObjects()) {
                if (obj instanceof TileTrigger) {
                    ((TileTrigger) obj).swapIsHidden();
                }
            }
        }

        if (ImGui.button("New character")) {
            ImGui.openPopup("Create a character");
        }
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center, ImGuiCond.Appearing, new ImVec2(0.5f, 0.5f));
        openCharacterCreator();

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
        if (hoveredObject != null) {
            ImGui.text("Hovered: " + hoveredObject.getId());
            if (hoveredObject instanceof Player) {
                ImGui.text("Name: " + ((Player) hoveredObject).getName());
                ImGui.text("Pos: " + hoveredObject.getOffsetPos().x + ", " + hoveredObject.getOffsetPos().y);
                ImGui.text("Move speed: " + ((Player) hoveredObject).getMoveSpeed());
                ImGui.text("HP: " + ((Player) hoveredObject).getHP());
            }
        }

        ImGui.end();
    }

    private void openCharacterCreator() {
        if (ImGui.beginPopupModal("Create a character",
                ImGuiWindowFlags.NoResize
                        | ImGuiWindowFlags.NoMove)) {

            ImGui.text("Bingus");
            ImGui.inputText("Name", name);
            ImGui.sameLine();
            if (ImGui.button("Random##name")) {
                name = new ImString(getRandomName());
            }
            ImGui.combo("Class", classType, classList);
            ImGui.sameLine();
            if (ImGui.button("Random##class")) {
                classType = new ImInt(Utils.randomInt(0, classList.length-1));
            }
            ImGui.combo("Race", raceType, raceList);
            ImGui.sameLine();
            if (ImGui.button("Random##race")) {
                raceType = new ImInt(Utils.randomInt(0, raceList.length-1));
            }
            ImGui.sliderInt("Move speed", moveSpeed, 0, 10, "");
            ImGui.sameLine();
            ImGui.text(String.valueOf(moveSpeed[0] * 5));
            ImGui.sliderInt("AC", AC, 0, 25);
            ImGui.inputInt("Health", HP);

            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.sameLine();
            if (ImGui.button("Surprise me:)")) {
                classType = new ImInt(Utils.randomInt(0, classList.length-1));
                raceType = new ImInt(Utils.randomInt(0, raceList.length-1));
                name = new ImString(getRandomName());
            }
            ImGui.sameLine();
            if (ImGui.button("Add player")) {
                Player player = createCreatureRandomPos(name.toString(), classType.intValue(), raceType.intValue(), moveSpeed[0], AC[0], HP.intValue());
                player.setTexture(scene.getTextureCache().getTexture("sandvich"));
                player.setId("New player");
                player.setShaderProgram(scene.getShaderCache().getShader("creature"));
                player.setParent(gridClass.getHexagonAt(player.getOffsetPos()));
                player.setPosition(0.0f, 0.2f, 0.0f);

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
            ImGui.endPopup();
        }
    }
}
