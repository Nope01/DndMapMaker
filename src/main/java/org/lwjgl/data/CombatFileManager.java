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

    /**
     * Constructor for CombatFileManager.
     * Initializes file choosers with appropriate filters and current directory.
     */
    public CombatFileManager() {
        FileSystemView fsv = FileSystemView.getFileSystemView(); //For getting desktop path

        mapFilter = new FileNameExtensionFilter(
                "Map file *.map", "map"
        );

        characterFilter = new FileNameExtensionFilter(
                "Character file *.pl", "pl"
        );

        // Initialize file choosers with filters and current directory
        saveChooser = new JFileChooser();
        saveChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveChooser.setFileFilter(mapFilter);
        saveChooser.setCurrentDirectory(fsv.getHomeDirectory());

        loadChooser = new JFileChooser();
        loadChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        loadChooser.setFileFilter(mapFilter);
        loadChooser.setCurrentDirectory(fsv.getHomeDirectory());
    }

    /**
     * Displays a file chooser dialog for saving a file.
     * If the file already exists, prompts the user for confirmation to override.
     * @param object The object to be serialized and saved.
     * @param extension The file extension to use for the saved file.
     */
    public void saveFileDialog(Object object, String extension) {
        File file;
        String path;

        int returnVal = saveChooser.showSaveDialog(null);
        //Get the file from the chooser
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = saveChooser.getSelectedFile();
            path = file.getAbsolutePath();
            System.out.println("Saving: " + path);

            //Override the file if it already exists
            if (file.exists()) {
                System.out.println("File already exists!");
                path = path.substring(0, path.lastIndexOf("."));

                int userChoice = fileOverridePopup();
                //Delete the old file and save the new one
                if (userChoice == JOptionPane.OK_OPTION) {
                    System.out.println(file.delete());
                    serializeObject(object, path, extension);
                }
                else {
                    //Reopen the save dialog if the user chooses to override
                    if (userChoice == JOptionPane.NO_OPTION) {
                        saveFileDialog(object, extension);
                    }
                    else {
                        //Exit the dialog if the user cancels
                        if (userChoice == JOptionPane.CANCEL_OPTION) {
                            System.out.println("Cancelled by user");
                        }
                    }
                }
            }
            else {
                //If the file does not exist, serialize the object and save it as a new file
                serializeObject(object, path, extension);
            }
        }
    }

    /**
     * Saves a character file with the specified List<Creature> object.
     * Uses the character file filter for the save dialog and passes the extension ".pl" for saving the file
     * @param object The list of creatures to be saved.
     */
    public void saveCharacterFile(List<Creature> object) {
        saveChooser.setFileFilter(characterFilter);
        saveFileDialog(object, ".pl");
    }

    /**
     * Saves a map file with the specified Grid object.
     * Uses the map file filter for the save dialog and passes the extension ".map" for saving the file
     * @param object The grid object to be saved.
     */
    public void saveMapFile(Grid object) {
        saveChooser.setFileFilter(mapFilter);
        saveFileDialog(object, ".map");
    }

    /**
     * Displays a file chooser dialog for loading a map file.
     * If the file exists, it deserializes the Grid object from the file.
     * @return The loaded Grid object, or null if the operation was cancelled or failed.
     */
    public Grid loadMapFileDialog() {
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

    /**
     * Displays a file chooser dialog for loading a character file.
     * If the file exists, it deserializes the List<Creature> object from the file.
     * @return The loaded List<Creature> object, or null if the operation was cancelled or failed.
     */
    public List<Creature> loadCharacterFileDialog() {
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

    /**
     * Serializes an object to a file at the specified path with the given extension.
     * @param object The object to be serialized.
     * @param path The file path where the object will be saved.
     * @param extension The file extension to use for the saved file.
     */
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

    /**
     * Deserializes a Grid object from a file.
     * @param file The file from which the Grid object will be deserialized.
     * @return The deserialized Grid object, or null if an error occurred.
     */
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

    /**
     * Deserializes a List<Creature> object from a file.
     * @param file The file from which the List<Creature> object will be deserialized.
     * @return The deserialized List<Creature> object, or null if an error occurred.
     */
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
