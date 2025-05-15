package org.lwjgl.UI;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.lwjgl.Scene;
import org.lwjgl.input.InputHandler;
import org.lwjgl.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InitiativeTracker extends ImGuiWindow {
    private List<Pair<String, Integer>> initiativeList;
    private ImInt initiative = new ImInt(0);
    private ImString character = new ImString(20);

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
    }

    @Override
    protected void renderContent() {
        ImGui.begin("Initiative Tracker", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove);

        if (ImGui.button("Add character")) {
            ImGui.openPopup("Add initiative");
        }

        for (Pair<String, Integer> entry : initiativeList) {
            ImGui.text(entry.getLeft());
            ImGui.sameLine();
            ImGui.text(entry.getRight().toString());
        }
        ImVec2 center = ImGui.getMainViewport().getCenter();
        ImGui.setNextWindowPos(center, ImGuiCond.Appearing, new ImVec2(0.5f, 0.5f));
        openInitiativeAdder();

        ImGui.end();
    }

    private void openInitiativeAdder() {
        ImGui.setNextWindowSize(new ImVec2(400, 400));
        if (ImGui.beginPopupModal("Add initiative", ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoMove)) {

            ImGui.inputInt("Initiative", initiative);
            ImGui.inputText("Character", character);

            if (ImGui.button("Add")) {
                Pair<String, Integer> initiativeEntry = new Pair<>(character.toString(), initiative.intValue());
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
}
