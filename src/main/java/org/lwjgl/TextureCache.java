package org.lwjgl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    public static final String DEFAULT_PATH = "src/main/resources/textures/";
    public static String EXE_PATH = "textures/";
    public static final String TILE_PATH = EXE_PATH + "tiles/";
    public static final String DEFAULT_TEXTURE = EXE_PATH + "default_texture.png";
    public static final String SANDVICH = EXE_PATH + "sandvich.png";
    public static final String MAP = EXE_PATH + "map.png";
    private Map<String, Texture> textureMap;

    public TextureCache() {
        textureMap = new HashMap<String, Texture>();
        initTextures();
    }

    public void clear() {
        textureMap.clear();
    }

    public void addNewTexture(String path, String name) {
        textureMap.put(name, new Texture(path, name));
    }

    public void addNewTexture(String name) {
        addNewTexture(EXE_PATH + name + ".png", name);
    }

    public Texture getTexture(String path) {
        Texture texture = null;
        if (path != null) {
            texture = textureMap.get(path);
        }
        if (texture == null) {
            texture = textureMap.get(DEFAULT_TEXTURE);
        }
        return texture;
    }

    public void initTextures() {
        addNewTexture(DEFAULT_TEXTURE, "default_texture");
        addNewTexture("default_tile");
        addNewTexture(EXE_PATH + "empty.png", "empty");
        addNewTexture(SANDVICH, "sandvich");

        //Life is pain
        addNewTexture(EXE_PATH + "grass_05.png", "grass_05");
        addNewTexture(EXE_PATH + "grass_10.png", "grass_10");
        addNewTexture(EXE_PATH + "grass_11.png", "grass_11");
        addNewTexture(EXE_PATH + "grass_12.png", "grass_12");
        addNewTexture(EXE_PATH + "grass_13.png", "grass_13");

        addNewTexture(EXE_PATH + "sand_07.png", "sand_07");
        addNewTexture(EXE_PATH + "sand_12.png", "sand_12");
        addNewTexture(EXE_PATH + "sand_13.png", "sand_13");
        addNewTexture(EXE_PATH + "sand_14.png", "sand_14");
        addNewTexture(EXE_PATH + "sand_15.png", "sand_15");
        addNewTexture(EXE_PATH + "tavern.png", "tavern");
        addNewTexture(EXE_PATH + "soda.png", "soda");

        addNewTexture("tavern (2)");
        addNewTexture("caravel");
        addNewTexture("castle");
        addNewTexture("anchor");
        addNewTexture("anvil");
        addNewTexture("coffer");

        addNewTexture("water_01");
        addNewTexture("snow_01");
        addNewTexture("volcanic_mountain_01");
        addNewTexture("jungle_01");
        addNewTexture("mountain_01");
        addNewTexture("swamp_01");

        //Open main tile folder
//        Path tileFolder = Paths.get(TILE_PATH);
//        File tileCategories = tileFolder.toFile();
//
//        System.out.println("Loading textures...");
//        ClassLoader classLoader = TextureCache.class.getClassLoader();
//        try {
//            InputStream inputStream = classLoader.getResourceAsStream(EXE_PATH);
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
//            BufferedReader reader = new BufferedReader(inputStreamReader);
//            System.out.println("Reader created");
//            reader.lines().forEach(line -> {
//                System.out.println(line);
//                addNewTexture(EXE_PATH + line, line.substring(0, line.lastIndexOf('.')));});
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        //Recursively iterate through subfolders and add textures to cache
        //openAllFilesInFolder(tileCategories);
    }

    private void openAllFilesInFolder(String line) {
//        File[] fileList = dir.listFiles();
//        for (File file : fileList) {
//            if (file.isFile()) {
//                addNewTexture(file.getPath(), getNameFromFile(file.getName()));
//            }
//            else {
//                openAllFilesInFolder(file);
//            }
//        }

        if (line.substring(line.lastIndexOf('.') + 1).equals(".png")) {
            addNewTexture(line, line.substring(0, line.lastIndexOf('.') + 1));
        }
        else {
            openAllFilesInFolder(line.substring(0, line.lastIndexOf('.') + 1));
        }
    }

    private String getNameFromFile(String fileName) {
        String imageName = fileName.substring(0, fileName.lastIndexOf("."));
        return imageName;
    }
}
