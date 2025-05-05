package org.lwjgl.input;


import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.Scene;
import org.lwjgl.objects.SceneObject;

import java.util.List;

public class ObjectSelection {
    public static boolean checkObjectMouseHover(SceneObject object,
                                                Scene scene,
                                                InputHandler inputHandler) {
        Vector3f worldPos = inputHandler.getWorldPos(scene);
        Vector3f camera = scene.getCamera().getPosition();
        Vector4f mouseDir = inputHandler.getMouseDir(scene);


        if (object.rayIntersect(worldPos, mouseDir, camera)) {
            if (scene.getHoveredObject() != null) {
                scene.getHoveredObject().hovered = false;
            }
            return true;
        }
        else {
            return false;
        }

    }

    public static void hoverObject(Scene scene,
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
            sceneObject.hovered = true;
            scene.setHoveredObject(sceneObject);
        }


        if (!sceneObject.children.isEmpty()) {
            for (SceneObject child : sceneObject.children) {
                childrenIntersectionChecks(scene, inputHandler, child);
            }
        }
    }

    public static void resetHoveredObject(SceneObject object) {
        object.hovered = false;
    }
}
