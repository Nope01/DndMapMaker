package org.lwjgl.data.update;

import java.io.File;

public class MainUpdater {
    public static void main(String[] args) {
        try {
            // 1. Check for updates
            String[] updateInfo = UpdateChecker.checkForUpdates();
            if (updateInfo == null) {
                System.out.println("No updates available.");
                return;
            }

            System.out.println("Updating to v" + updateInfo[0]);

            // 2. Download update
            File updateZip = UpdateDownloader.downloadUpdate(updateInfo[1]);

            // 3. Clean old version
            String installDir = UpdateInstaller.getInstallDir();
            UpdateInstaller.cleanOldVersion(installDir);

            // 4. Extract new version
            UpdateDownloader.extractUpdate(updateZip, installDir);

            // 5. Restart application
            UpdateInstaller.restartApplication(installDir);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}