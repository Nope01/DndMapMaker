package org.lwjgl.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private static final String VERSION_URL = "https://your-server.com/latest-version.txt";
    private static final String DOWNLOAD_URL = "https://your-server.com/downloads/";
    private static final String CURRENT_VERSION = "1.0.0"; // Should come from your build config

    public static boolean checkForUpdates() throws IOException {
        URL url = new URL(VERSION_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        String latestVersion;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            latestVersion = reader.readLine().trim();
        }

        return !latestVersion.equals(CURRENT_VERSION);
    }

    public static void downloadUpdate() throws IOException {
        String installerName = System.getProperty("os.name").toLowerCase().contains("win")
                ? "DndMap-Installer.exe"
                : "DndMap-Installer.jar";

        URL downloadUrl = new URL(DOWNLOAD_URL + installerName);
        // Implement download logic here
    }
}