package org.lwjgl;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    public static final String DEFAULT_PATH = "resources/textures/";
    public static final String TILE_PATH = DEFAULT_PATH + "tiles/";
    public static final String DEFAULT_TEXTURE = DEFAULT_PATH + "default_texture.png";
    public static final String SANDVICH = DEFAULT_PATH + "sandvich.png";
    public static final String MAP = DEFAULT_PATH + "map.png";
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
        addNewTexture(SANDVICH, "sandvich");
        addNewTexture(MAP, "map");

        //Open main tile folder
        Path tileFolder = Paths.get(TILE_PATH);
        File tileCategories = tileFolder.toFile();

        //Recursively iterate through subfolders and add textures to cache
        openAllFilesInFolder(tileCategories);
    }

    private void openAllFilesInFolder(File dir) {
        File[] fileList = dir.listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                addNewTexture(file.getPath(), getNameFromFile(file.getName()));
            }
            else {
                openAllFilesInFolder(file);
            }
        }
    }

    private String getNameFromFile(String fileName) {
        String imageName = fileName.substring(0, fileName.lastIndexOf("."));
        return imageName;
    }
}
