package org.lwjgl.data;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class ImageGeneration {

    /**
     * Saves the current OpenGL context as an image file to the desktop
     *
     * @param window The GLFW window handle.
     * @param width  The width of the image.
     * @param height The height of the image.
     */
    public static void saveImageAsFile(long window, int width, int height) {
        BufferedImage image = makeImage(window, width, height);

        //Need to add file explorer popup
        try {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            System.out.println(fsv.getHomeDirectory());
            ImageIO.write(image, "png", new File(fsv.getHomeDirectory() + "/map.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a BufferedImage from the current OpenGL context.
     *
     * @param window The GLFW window handle.
     * @param width  The width of the image.
     * @param height The height of the image.
     * @return A BufferedImage containing the current OpenGL context.
     */
    private static BufferedImage makeImage(long window, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 3);

        glReadPixels(0, 0, width, height, GL_RGB, GL_BYTE, buffer);

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                graphics.setColor(new Color(buffer.get()*2, buffer.get()*2, buffer.get()*2));
                graphics.drawRect(w, height - h, 1, 1);
            }
        }
        return image;
    }
}
