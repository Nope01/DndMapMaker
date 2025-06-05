package org.lwjgl.data;

import org.lwjgl.objects.Grid;
import org.lwjgl.objects.entities.Creature;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.List;

import static org.lwjgl.data.MapSaveLoad.fileOverridePopup;

public class CombatFileManager {

    private JFileChooser saveChooser;
    private JFileChooser loadChooser;

    private FileNameExtensionFilter mapFilter;
    private FileNameExtensionFilter characterFilter;

    public CombatFileManager() {
        FileSystemView fsv = FileSystemView.getFileSystemView(); //For getting desktop path

        mapFilter = new FileNameExtensionFilter(
                "Map file *.map", "map"
        );

        characterFilter = new FileNameExtensionFilter(
                "Character file *.pl", "pl"
        );

        saveChooser = new JFileChooser();
        saveChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveChooser.setFileFilter(mapFilter);
        saveChooser.setCurrentDirectory(fsv.getHomeDirectory());

        loadChooser = new JFileChooser();
        loadChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        loadChooser.setFileFilter(mapFilter);
        loadChooser.setCurrentDirectory(fsv.getHomeDirectory());
    }

    public void saveFileDialog(Object object, String extension) {
        File file;
        String path;

        int returnVal = saveChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = saveChooser.getSelectedFile();
            path = file.getAbsolutePath();
            System.out.println("Saving: " + path);

            if (file.exists()) {
                System.out.println("File already exists!");
                path = path.substring(0, path.lastIndexOf("."));
                if (fileOverridePopup() == JOptionPane.OK_OPTION) {
                    System.out.println(file.delete());
                    serializeObject(object, path, extension);
                    return;
                }
                if (fileOverridePopup() == JOptionPane.NO_OPTION) {
                    saveFileDialog(object, extension);
                    return;
                }
                if (fileOverridePopup() == JOptionPane.CANCEL_OPTION) {
                    System.out.println("Cancelled by user");
                }
            }
            else {
                serializeObject(object, path, extension);
            }
        }
    }

    public void saveCharacterFile(List<Creature> object) {
        saveChooser.setFileFilter(characterFilter);
        saveFileDialog(object, ".pl");
    }

    public void saveMapFile(Grid object) {
        saveChooser.setFileFilter(mapFilter);
        saveFileDialog(object, ".map");
    }

    public Grid loadMapFile() {
        File file;
        loadChooser.setFileFilter(mapFilter);

        int returnVal = loadChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = loadChooser.getSelectedFile();
            System.out.println("Loading file: " + file.getAbsolutePath());
            if (file.exists()) {
                return deserializeGrid(file);
            }
        }
        return null;
    }

    public List<Creature> loadCharacterFile() {
        File file;
        loadChooser.setFileFilter(characterFilter);

        int returnVal = loadChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = loadChooser.getSelectedFile();
            System.out.println("Loading file: " + file.getAbsolutePath());
            if (file.exists()) {
                return deserializeList(file);
            }
        }
        return null;
    }

    private static void serializeObject(Object object, String path, String extension) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path + extension);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
            System.out.println("Saved!");
        } catch (IOException e) {
            System.out.println("Error saving object");
            e.printStackTrace();
        }
    }

    private static Grid deserializeGrid(File file) {
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            return (Grid)in.readObject();
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

    private static List<Creature> deserializeList(File file) {
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            return (List<Creature>)in.readObject();
        } catch (IOException e) {
            System.out.println("Error reading list file");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            System.out.println("Error deserializing file");
            e.printStackTrace();
        }
        return null;
    }


}
