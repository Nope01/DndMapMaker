package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import org.lwjgl.Scene;
import org.lwjgl.input.InputHandler;
import org.lwjgl.objects.entities.Creature;
import org.lwjgl.utils.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InitiativeTracker extends ImGuiWindow {
    private List<Pair<Creature, Integer>> initiativeList;
    private List<Creature> characterList = new ArrayList<>();
    private String[] nameList = new String[]{};
    private ImInt characterInt = new ImInt(0);
    private ImInt initiative = new ImInt(0);
    List<String> tempList = new ArrayList<>();
    private int selectedIndex = -1;

    private Creature currentTurn;

    public InitiativeTracker(ImGuiManager imGuiManager, Scene scene, InputHandler inputHandler) {
        super(imGuiManager, scene, inputHandler, "Initiative Tracker");
        uiWidth = 400;
        uiHeight = 250;
        uiXPos = scene.getScreenWidth()-400;
        uiYPos = imGuiManager.getWindow("Menu Bar").getUiHeight();

        initiativeList = new ArrayList<>();
        currentTurn = null;

        placeUiWindow();
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

        if (selectedIndex > -1) {
            currentTurn = initiativeList.get(selectedIndex).getLeft();
            currentTurn.hovered = true;
        }
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

        ImGui.sameLine();
        if (ImGui.button("Clear")) {
            initiativeList.clear();
            selectedIndex = -1;
            currentTurn = null;
        }

        //Displays names in order of initiative
        for (int i = 0; i < initiativeList.size(); i++) {
            Pair<Creature, Integer> entry = initiativeList.get(i);
            String label = entry.getLeft().getName() + " " + entry.getRight();
            if (ImGui.selectable(label, selectedIndex == i)) {
                selectedIndex = i;
            }
        }

        ImGui.text("Current turn: " + (currentTurn == null ? "None" : currentTurn.getName()));

        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center, ImGuiCond.Appearing, new ImVec2(0.5f, 0.5f));
        openInitiativeAdder();
        openEmptyListPopup();

        ImGui.end();
    }

    private void openInitiativeAdder() {
        ImGui.setNextWindowSize(new ImVec2(200 * imGuiManager.getScale(), 120 * imGuiManager.getScale()));
        if (ImGui.beginPopupModal("Add initiative", ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoMove)) {

            ImGui.inputInt("Initiative", initiative);
            ImGui.combo("Character", characterInt, nameList);

            if (ImGui.button("Add")) {
                Pair<Creature, Integer> initiativeEntry =
                        new Pair<>(characterList.get(characterInt.intValue()), initiative.intValue());

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
        ImGui.setNextWindowSize(new ImVec2(150 * imGuiManager.getScale(), 60 * imGuiManager.getScale()));
        if (ImGui.beginPopupModal("Empty list", ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar)) {
            GuiUtils.textCentered("No creatures added");
            if (GuiUtils.buttonCentered("OK")) {
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
    }
}
