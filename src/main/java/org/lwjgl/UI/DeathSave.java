package org.lwjgl.UI;

import imgui.ImGui;
import imgui.flag.ImGuiButtonFlags;
import imgui.flag.ImGuiDir;
import org.lwjgl.objects.entities.Player;

public class DeathSave {
    private Player player;
    private int successfulRolls = 0;
    private int failedRolls = 0;

    public DeathSave(Player player) {
        this.player = player;
    }
    public void drawDeathSaveUI() {
        ImGui.text("Its death time for " + player.getName() + "!");
        ImGui.text("Successes: " + successfulRolls);
        ImGui.sameLine();
        if (ImGui.arrowButton("Success", ImGuiDir.Right)) {
            addSuccess();
        }
        ImGui.text("Failures: " + failedRolls);
        ImGui.sameLine();
        if (ImGui.arrowButton("Failure", ImGuiDir.Right)) {
            addFailure();
        }

        if (failedRolls >= 3) {
            ImGui.text("You have failed to save " + player.getName() + "!");
        }
        if (successfulRolls >= 3) {
            ImGui.text("You have saved " + player.getName() + "!");
        }
    }

    public void addSuccess() {
        successfulRolls++;
    }

    public void addFailure() {
        failedRolls++;
    }

    public String getPlayerName() {
        return player.getName();
    }

}
