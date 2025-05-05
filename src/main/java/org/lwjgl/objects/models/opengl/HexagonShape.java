package org.lwjgl.objects.models.opengl;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import static java.lang.Math.TAU;

public class HexagonShape {

    public static float[] vertices() {
        // Hexagon vertices (6 vertices forming a regular hexagon)
        float[] verticesFloats = new float[21];

        Vector3f[] vecs = new Vector3f[7];
        //Rotates a point to create a circle with 6 points (hexagon)
        vecs[0] = new Vector3f(0, 0, 0);

        Matrix3f rotation = new Matrix3f();
        for (int i = 0; i < 6; i++) {
            float angle = (float) (TAU/6);
            rotation.rotationY(angle*i);
            vecs[i+1] = new Vector3f(1.0f, 0.0f, 0.0f);
            vecs[i+1].mul(rotation);
        }

        int count = 0;
        for (Vector3f vec : vecs) {
            verticesFloats[count++] = vec.x;
            verticesFloats[count++] = vec.y;
            verticesFloats[count++] = vec.z;
        }
        return verticesFloats;
    }

    public static Vector3f[] verticesVecs(float[] verticesFloats) {
        Vector3f[] verticesVecs = new Vector3f[21 / 3];
        int count = 0;
        for (int i = 0; i < verticesFloats.length; i += 3) {
            verticesVecs[count] = new Vector3f(verticesFloats[i], verticesFloats[i + 1], verticesFloats[i + 2]);
            count++;
        }
        return verticesVecs;
    }

    public static float[] texCoords() {
//        Tilted
//        texCoords = new float[] {
//                0.5f, 0.5f,
//                1.0f, 0.75f,
//                1.0f, 0.25f,
//                0.5f, 0.0f,
//                0.0f, 0.25f,
//                0.0f, 0.75f,
//                0.5f, 1.0f
//        };

        return new float[] {
                0.5f, 0.5f,
                1.0f, 0.5f,
                0.75f, 0.0f,
                0.25f, 0.0f,
                0.0f, 0.5f,
                0.25f, 1.0f,
                0.75f, 1.0f
        };
    }

    public static int[] indices() {
        int[] indices = new int[18];
        int k = 0;
        for (int i = 1; i <= 6; i++) {
            indices[k++] = 0;
            indices[k++] = i;
            indices[k++] = (i%6)+1;
        }

        return indices;
    }
}
