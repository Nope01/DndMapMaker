package org.lwjgl.data.update;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UpdateDownloader {
    public static File downloadUpdate(String downloadUrl) throws IOException {
        // Download to temp file
        Path tempFile = Files.createTempFile("update", ".zip");
        try (InputStream in = new URL(downloadUrl).openStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile.toFile();
    }

    public static void extractUpdate(File zipFile, String installDir) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                Path filePath = Paths.get(installDir, entry.getName());
                if (!entry.isDirectory()) {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zipIn, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
        Files.delete(zipFile.toPath()); // Cleanup
    }
}