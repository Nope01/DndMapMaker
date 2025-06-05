package org.lwjgl.textures;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    public static String DEFAULT_PATH = "textures/";
    public static final String DEFAULT_TEXTURE = DEFAULT_PATH + "default_texture.png";

    private Map<String, Texture> textureMap;
    private static final String[] directories = new String[]{
            "default/",
            "icons/",
            "misc/",
            "tiles/",
    };

    private static final String[] defaultTextures = new String[]{
            "default_texture",
            "default_tile",
            "empty",
    };

    private static final String[] iconTextures = new String[]{
            "anchor",
            "anvil",
            "barrel",
            "caravel",
            "castle",
            "coffer",
            "magic",
            "old-key",
            "repair-tools",
            "soda",
            "sword",
            "table",
            "tavern",
    };

    private static final String[] miscTextures = new String[]{
            "sandvich",
    };

    private static final String[] tilesTextures = new String[]{
            "dead_forest_01",
            "dirt_01",
            "dirt_02",
            "dirt_03",
            "dirt_04",
            "floor_01",
            "forest_01",
            "grass_05",
            "jungle_01",
            "lava_01",
            "path_01",
            "rock_01",
            "rock_02",
            "rock_03",
            "sand_01",
            "sand_07",
            "snow_01",
            "wall_01",
            "wall_02",
            "water_01",
    };

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
        addNewTexture(DEFAULT_PATH + name + ".png", name);
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
        for (String directory : directories) {
            switch (directory) {
                case "default/": for (String textureName : defaultTextures) {
                    String path = DEFAULT_PATH + "default/" + textureName + ".png";
                    addNewTexture(path, textureName);
                }
                case "icons/": for (String textureName : iconTextures) {
                    String path = DEFAULT_PATH + "icons/" + textureName + ".png";
                    addNewTexture(path, textureName);
                }
                case "misc/": for (String textureName : miscTextures) {
                    String path = DEFAULT_PATH + "misc/" + textureName + ".png";
                    addNewTexture(path, textureName);
                }
                case "tiles/": for (String textureName : tilesTextures) {
                    String path = DEFAULT_PATH + "tiles/" + textureName + ".png";
                    addNewTexture(path, textureName);
                }
                default: //System.out.println("texture not found");
            }
        }
//        addNewTexture(DEFAULT_TEXTURE, "default_texture");
//        addNewTexture("default_tile");
//        addNewTexture(DEFAULT_PATH + "empty.png", "empty");
//
//        //Life is pain
//        addNewTexture(DEFAULT_PATH + "grass_05.png", "grass_05");
//        addNewTexture(DEFAULT_PATH + "grass_10.png", "grass_10");
//        addNewTexture(DEFAULT_PATH + "grass_11.png", "grass_11");
//        addNewTexture(DEFAULT_PATH + "grass_12.png", "grass_12");
//        addNewTexture(DEFAULT_PATH + "grass_13.png", "grass_13");
//
//        addNewTexture(DEFAULT_PATH + "sand_07.png", "sand_07");
//        addNewTexture(DEFAULT_PATH + "sand_12.png", "sand_12");
//        addNewTexture(DEFAULT_PATH + "sand_13.png", "sand_13");
//        addNewTexture(DEFAULT_PATH + "sand_14.png", "sand_14");
//        addNewTexture(DEFAULT_PATH + "sand_15.png", "sand_15");
//        addNewTexture(DEFAULT_PATH + "tavern.png", "tavern");
//        addNewTexture(DEFAULT_PATH + "soda.png", "soda");
//
//        addNewTexture("tavern (2)");
//        addNewTexture("caravel");
//        addNewTexture("castle");
//        addNewTexture("anchor");
//        addNewTexture("anvil");
//        addNewTexture("coffer");
//
//        addNewTexture("water_01");
//        addNewTexture("snow_01");
//        addNewTexture("volcanic_mountain_01");
//        addNewTexture("jungle_01");
//        addNewTexture("mountain_01");
//        addNewTexture("swamp_01");

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
