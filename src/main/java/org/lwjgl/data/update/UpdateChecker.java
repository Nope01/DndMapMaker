package org.lwjgl.data.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private static final String VERSION_URL = "https://github.com/Nope01/DndMapMaker/blob/master/latest-version.txt";
    private static final String CURRENT_VERSION = "0.1.0"; // Should be dynamic (e.g., from MANIFEST.MF)

    public static String[] checkForUpdates() throws IOException {
        URL url = new URL(VERSION_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String latestVersion = reader.readLine().trim();
            String downloadUrl = reader.readLine().trim();

            if (!latestVersion.equals(CURRENT_VERSION)) {
                return new String[]{latestVersion, downloadUrl};
            }
        }
        return null; // No update available
    }
}