package org.lwjgl;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    public static final String DEFAULT_TEXTURE = "resources/textures/default_texture.png";
    public static final String SANDVICH = "resources/textures/sandvich.png";
    private Map<String, Texture> textureMap;

    public TextureCache() {
        textureMap = new HashMap<String, Texture>();
        initTextures();
    }

    public void clear() {
        textureMap.clear();
    }

    public Texture addNewTexture(String path) {
        return textureMap.computeIfAbsent(path, Texture::new);
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
        textureMap.put(DEFAULT_TEXTURE, new Texture(DEFAULT_TEXTURE));
        textureMap.put(SANDVICH, new Texture(SANDVICH));
    }
}
