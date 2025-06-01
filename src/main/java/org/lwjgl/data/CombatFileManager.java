package org.lwjgl.data;

import org.lwjgl.objects.Grid;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.*;

import static org.lwjgl.data.MapSaveLoad.fileOverridePopup;

public class CombatFileManager {

    private JFileChooser saveMapChooser;
    private JFileChooser loadMapChooser;

    private JFileChooser saveCharacterChooser;
    private JFileChooser loadCharacterChooser;

    public CombatFileManager() {
        FileSystemView fsv = FileSystemView.getFileSystemView(); //For getting desktop path

        FileNameExtensionFilter mapFilter = new FileNameExtensionFilter(
                "Combat file *.ser", "ser"
        );

        FileNameExtensionFilter characterFilter = new FileNameExtensionFilter(
                "Character file *.ser", "ser"
        );

        saveMapChooser = new JFileChooser();
        saveMapChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveMapChooser.setFileFilter(mapFilter);
        saveMapChooser.setCurrentDirectory(fsv.getHomeDirectory());

        loadMapChooser = new JFileChooser();
        loadMapChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        loadMapChooser.setFileFilter(mapFilter);
        loadMapChooser.setCurrentDirectory(fsv.getHomeDirectory());
    }

    public void saveMapFile(Object object) {
        File file;
        String path;

        int returnVal = saveMapChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = saveMapChooser.getSelectedFile();
            path = file.getAbsolutePath();
            System.out.println("Saving: " + path);

            if (file.exists()) {
                System.out.println("File already exists!");
                path = path.substring(0, path.lastIndexOf("."));
                if (fileOverridePopup() == JOptionPane.OK_OPTION) {
                    System.out.println(file.delete());
                    serializeObject(object, path);
                    return;
                }
                if (fileOverridePopup() == JOptionPane.NO_OPTION) {
                    saveMapFile(object);
                    return;
                }
                if (fileOverridePopup() == JOptionPane.CANCEL_OPTION) {
                    System.out.println("Cancelled by user");
                }
            }
            else {
                serializeObject(object, path);
            }
        }
    }

    public Grid loadMapFile() {
        File file;

        int returnVal = loadMapChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = loadMapChooser.getSelectedFile();
            System.out.println("Loading file: " + file.getAbsolutePath());
            if (file.exists()) {
                return deserializeObject(file);
            }
        }
        return null;
    }

    private static void serializeObject(Object object, String path) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
            System.out.println("Saved Combat map");
        } catch (IOException e) {
            System.out.println("Error saving object");
            e.printStackTrace();
        }
    }

    private static Grid deserializeObject(File file) {
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Grid returnObject = (Grid)in.readObject();
            System.out.println("Deserialised Combat map");
            return returnObject;
        } catch (IOException e) {
            System.out.println("Error reading file");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            System.out.println("Error deserializing file");
            e.printStackTrace();
        }
        return null;
    }
}
