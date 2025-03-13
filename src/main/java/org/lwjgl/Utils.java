package org.lwjgl;

import org.joml.Vector3f;

public class Utils {
    public static Vector3f RGBToVec3(int r, int g, int b) {
        return new Vector3f((float) r /255, (float) g /255, (float) b /255);
    }
}
