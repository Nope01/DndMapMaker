package org.lwjgl.utils;

import imgui.ImVec4;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class VectorUtils {
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

    //TODO: move out of this class
    public static Vector2f getCenterOfScreen(float screenWidth, float screenHeight, float uiWidth, float uiHeight) {
        Vector2f result = new Vector2f();
        result.x = (screenWidth / 2) - (uiWidth/2);
        result.y = (screenHeight / 2) - (uiHeight/2);
        return result;
    }

    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static ImVec4 vec4ToImVec4(Vector4f vec) {
        ImVec4 result = new ImVec4();
        result.x = vec.x;
        result.y = vec.y;
        result.z = vec.z;
        result.w = vec.w;
        return result;
    }

    public static Vector4f rgbToVec4(int r, int g, int b, int a) {
        return new Vector4f((float) r /255, (float) g /255, (float) b /255, (float) a /255);
    }

    public static ImVec4 rgbToImVec4(int r, int g, int b, int a) {
        return new ImVec4((float) r /255, (float) g /255, (float) b /255, (float) a /255);
    }

}
