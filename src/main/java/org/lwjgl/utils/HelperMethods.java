package org.lwjgl.utils;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class HelperMethods {
    public static Vector3f RGBToVec3(int r, int g, int b) {
        return new Vector3f((float) r /255, (float) g /255, (float) b /255);
    }

    public static float[] vector2fToFloat (Vector2f[] vecList) {
        float[] result = new float[vecList.length * 2];
        int count = 0;
        for (Vector2f vec : vecList) {
            result[count++] = vec.x;
            result[count++] = vec.y;
        }
        return result;
    }

    public static Vector2f[] floatToVector2f (float[] floatList) {
        Vector2f[] result = new Vector2f[floatList.length / 2];
        int count = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] = new Vector2f(floatList[count++], floatList[count++]);
        }
        return result;
    }

    public static float[] vector3fToFloat (Vector3f[] vecList) {
        float[] result = new float[vecList.length * 2];
        int count = 0;
        for (Vector3f vec : vecList) {
            result[count++] = vec.x;
            result[count++] = vec.y;
        }
        return result;
    }

    public static Vector3f[] float2fToVector3f (float[] floatList) {
        Vector3f[] result = new Vector3f[floatList.length / 2];
        int count = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] = new Vector3f(floatList[count++], floatList[count++], 0);
        }
        return result;
    }

    public static Vector2f getCenterOfScreen(float screenWidth, float screenHeight, float uiWidth, float uiHeight) {
        Vector2f result = new Vector2f();
        result.x = (screenWidth / 2) - (uiWidth/2);
        result.y = (screenHeight / 2) - (uiHeight/2);
        return result;
    }

    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

}
