package org.lwjgl.data.update;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;

import java.io.*;
import java.nio.file.*;

public class UpdateInstaller {
    public static String getInstallDir() {
        // Default JPackage install location (Windows)
        return System.getenv("LOCALAPPDATA") + "\\Programs\\DndMap";
    }

    public static void cleanOldVersion(String installDir) throws IOException {
        Path installPath = Paths.get(installDir);
        if (Files.exists(installPath)) {
            // Delete all files except the updater
            Files.walk(installPath)
                    .filter(path -> !path.endsWith("updater.jar"))
                    .sorted((a, b) -> b.compareTo(a)) // Reverse for deletion
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete: " + path);
                        }
                    });
        }
    }

    public static void restartApplication(String installDir) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            new ProcessBuilder(installDir + "\\DndMap.exe").start();
        } else if (os.contains("mac")) {
            new ProcessBuilder("open", installDir + "/DndMap.app").start();
        } else {
            new ProcessBuilder("bash", installDir + "/DndMap").start();
        }
    }
}