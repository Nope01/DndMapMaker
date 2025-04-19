package org.lwjgl.data;

import org.lwjgl.Grid;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.*;

public class MapSaveLoad {

    private JFileChooser saveFileChooser;
    private JFileChooser loadFileChooser;

    public MapSaveLoad() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Ser files *.ser", "ser"
        );
        FileSystemView fsv = FileSystemView.getFileSystemView(); //For getting desktop path

        saveFileChooser = new JFileChooser();
        saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        saveFileChooser.setFileFilter(filter);
        saveFileChooser.setCurrentDirectory(fsv.getHomeDirectory());

        loadFileChooser = new JFileChooser();
        loadFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        loadFileChooser.setFileFilter(filter);
        loadFileChooser.setCurrentDirectory(fsv.getHomeDirectory());
    }

    public void saveFile(Object object) {
        File file;
        String path;

        int returnVal = saveFileChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = saveFileChooser.getSelectedFile();
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
                    saveFile(object);
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

    public Grid loadFile() {
        File file;

        int returnVal = loadFileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = loadFileChooser.getSelectedFile();
            System.out.println("Loading: " + file.getAbsolutePath());
            if (file.exists()) {
                return deserializeObject(file);
            }
        }
        return null;
    }

    public JFileChooser getSaveFileChooser() {
        return saveFileChooser;
    }
    public JFileChooser getLoadFileChooser() {
        return loadFileChooser;
    }

    private static void serializeObject(Object object, String path) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path + ".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(object);
            out.close();
            fileOut.close();
            System.out.println("Saved Grid");
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

    private static void deleteFile(String path) {
        File file = new File(path);
        System.out.println(file.delete());
    }
    public static int fileOverridePopup() {
        return JOptionPane.showConfirmDialog(null, "Override existing file?");
    }
}
