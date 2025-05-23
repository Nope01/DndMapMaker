package org.lwjgl.textures;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.*;

import static org.lwjgl.opengl.GL30.*;

public class Texture implements Serializable {

    private int textureId;
    private String texturePath;
    private String textureName;

    public Texture(int width, int height, ByteBuffer buf) {
        this.texturePath = "";
        generateTexture(width, height, buf);
    }

    public Texture(String texturePath, String name) {
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            this.texturePath = texturePath;
//            this.textureName = name;
//            IntBuffer w = stack.mallocInt(1);
//            IntBuffer h = stack.mallocInt(1);
//            IntBuffer channels = stack.mallocInt(1);
//
//            InputStream stream = getClass().getClassLoader().getResourceAsStream(texturePath);
//            ByteBuffer buf = stbi_load(texturePath, w, h, channels, 4);
//            if (buf == null) {
//                throw new RuntimeException("Image file [" + texturePath + "] not loaded: " + stbi_failure_reason());
//            }
//
//            int width = w.get();
//            int height = h.get();
//
//            generateTexture(width, height, buf);
//
//            stbi_image_free(buf);
//        }

        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(texturePath)){
            this.texturePath = texturePath;
            this.textureName = name;

            PNGDecoder decoder = new PNGDecoder(stream);
            int width = decoder.getWidth();
            int height = decoder.getHeight();

            ByteBuffer buffer = ByteBuffer.allocateDirect(4*width*height);
            decoder.decode(buffer, width*4, PNGDecoder.Format.RGBA);
            buffer.flip();

            //System.out.println("Generating texture: " + textureName);
            generateTexture(width, height, buffer);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureId);
        //System.out.println("Binding texture: " + textureName);
    }

    public void cleanup() {
        glDeleteTextures(textureId);
    }

    private void generateTexture(int width, int height, ByteBuffer buf) {
        textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getTextureName() {
        return textureName;
    }
    public int getTextureId() {
        return textureId;
    }
}