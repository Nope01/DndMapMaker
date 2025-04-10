package org.lwjgl;


import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.objects.Hexagon;
import org.lwjgl.objects.SceneObject;

import java.util.List;

public class ObjectSelection {
    public static boolean checkObjectMouseHover(SceneObject object,
                                                Scene scene,
                                                InputHandler inputHandler) {
        Vector3f worldPos = inputHandler.getWorldPos(scene);
        Vector3f camera = scene.getCamera().getPosition();
        Vector4f mouseDir = inputHandler.getMouseDir(scene);

        if (object instanceof Hexagon) {
            if (((Hexagon) object).rayIntersect(worldPos, mouseDir, camera)) {
                if (scene.getSelectedObject() != null) {
                    scene.getSelectedObject().selected = false;
                }
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    public static void selectObject(Scene scene,
                                           InputHandler inputHandler,
                                           List<SceneObject> rootObjects) {
        for (SceneObject object : rootObjects) {
            childrenIntersectionChecks(scene, inputHandler, object);
        }
    }

    private static void childrenIntersectionChecks(Scene scene,
                                                   InputHandler inputHandler,
                                                   SceneObject sceneObject) {

        if (checkObjectMouseHover(sceneObject, scene, inputHandler)) {
            sceneObject.selected = true;
            scene.setSelectedObject(sceneObject);
        }


        if (!sceneObject.children.isEmpty()) {
            for (SceneObject child : sceneObject.children) {
                childrenIntersectionChecks(scene, inputHandler, child);
            }
        }
    }

    public static void resetSelectedObject(SceneObject object) {
        object.selected = false;
    }
}
