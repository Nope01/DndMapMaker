package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.lwjgl.Scene;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.entities.Creature;
import org.lwjgl.objects.entities.Player;
import org.lwjgl.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InitiativeTracker extends ImGuiWindow {
    private List<Pair<String, Integer>> initiativeList;
    private List<Creature> characterList = new ArrayList<>();
    private String[] nameList = new String[]{};
    private ImInt characterInt = new ImInt(0);
    private ImInt initiative = new ImInt(0);
    List<String> tempList = new ArrayList<>();

    public InitiativeTracker(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "Initiative Tracker");
        uiWidth = 400;
        uiHeight = 250;
        uiXPos = scene.getScreenWidth()-400;
        uiYPos = 20;

        initiativeList = new ArrayList<>();
    }

    @Override
    protected void init(Scene scene) {
        ImGui.setNextWindowPos(uiXPos, uiYPos);
        ImGui.setNextWindowSize(uiWidth, uiHeight);
        renderContent();
    }

    @Override
    protected void update() {
        CityEditor cityEditor = (CityEditor) imGuiManager.getWindow("City Editor");
        characterList = cityEditor.getCharacterList();

        tempList.clear();
        for (Creature creature : characterList) {
            tempList.add(creature.getName());
        }
        nameList = tempList.toArray(nameList);
    }

    @Override
    protected void renderContent() {
        ImGui.begin("Initiative Tracker", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);

        if (ImGui.button("Add character")) {
            if (characterList.isEmpty()) {
                ImGui.openPopup("Empty list");
            }
            else {
                ImGui.openPopup("Add initiative");
            }
        }

        for (Pair<String, Integer> entry : initiativeList) {
            ImGui.text(entry.getLeft());
            ImGui.sameLine();
            ImGui.text(entry.getRight().toString());
        }
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center, ImGuiCond.Appearing, new ImVec2(0.5f, 0.5f));
        openInitiativeAdder();
        openEmptyListPopup();

        ImGui.end();
    }

    private void openInitiativeAdder() {
        ImGui.setNextWindowSize(new ImVec2(400, 400));
        if (ImGui.beginPopupModal("Add initiative", ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoMove)) {

            ImGui.inputInt("Initiative", initiative);
            ImGui.combo("Character", characterInt, nameList);

            if (ImGui.button("Add")) {
                Pair<String, Integer> initiativeEntry =
                        new Pair<>(characterList.get(characterInt.intValue()).getName(), initiative.intValue());

                initiativeList.add(initiativeEntry);
                //Funky way of sorting the pair
                initiativeList.sort(Comparator.comparing(p -> -p.getRight()));
            }
            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }

    }

    private void openEmptyListPopup() {
        ImGui.setNextWindowSize(new ImVec2(200, 100));
        if (ImGui.beginPopupModal("Empty list", ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar)) {
            ImGui.newLine();
            GuiUtils.textCentered("No creatures added");
            ImGui.newLine();
            if (GuiUtils.buttonCentered("OK")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
    }
}
