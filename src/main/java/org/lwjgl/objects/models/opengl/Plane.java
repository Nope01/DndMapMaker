package org.lwjgl.objects.models.opengl;

public class Plane {

    public static float[] vertices() {
        return new float[] {
                -0.8f, 0.0f, -0.8f,
                -0.8f, 0.0f, 0.8f,
                0.8f, 0.0f, 0.8f,
                0.8f, 0.0f, -0.8f,
        };
    }

    public static float[] texCoords() {
        return new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
        };
    }

    public static int[] indices() {
        return new int[] {
                0, 1, 2,
                2, 3, 0
        };
    }
}
