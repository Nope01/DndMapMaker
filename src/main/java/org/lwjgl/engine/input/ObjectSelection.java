package org.lwjgl.engine.input;


import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.Scene;
import org.lwjgl.engine.Camera;
import org.lwjgl.objects.SceneObject;

import java.util.List;

public class ObjectSelection {

    /**
     * Searches the scene for the first hovered object based on the mouse position,
     * checking children first. Returns the first SceneObject that the mouse ray intersects,
     * or null if none.
     *
     * @param inputHandler  The InputHandler to get mouse position and direction.
     * @param rootObjects   The list of root SceneObjects to check for intersection.
     * @param camera        The camera to use for ray origin and direction.
     * @return The first hovered SceneObject, or null if none.
     */
    public static SceneObject findHoveredObject(InputHandler inputHandler,
                                                List<SceneObject> rootObjects,
                                                Camera camera) {
        for (SceneObject object : rootObjects) {
            SceneObject hovered = findHoveredInChildren(object, inputHandler, camera);
            if (hovered != null) {
                return hovered;
            }
        }
        return null;
    }

    /**
     * Recursively checks if the mouse ray intersects with the SceneObject or its children.
     *
     * @param sceneObject   The SceneObject to check for intersection.
     * @param inputHandler  The InputHandler to get mouse position and direction.
     * @param camera        The camera to use for ray origin and direction.
     * @return The first hovered SceneObject, or null if none.
     */
    private static SceneObject findHoveredInChildren(SceneObject sceneObject,
                                                     InputHandler inputHandler,
                                                     Camera camera) {
        Vector4f mouseDir = inputHandler.getMouseDir(camera);
        Vector3f cameraPos = camera.getPosition();

        for (SceneObject child : sceneObject.children) {
            SceneObject hovered = findHoveredInChildren(child, inputHandler, camera);
            if (hovered != null) {
                return hovered;
            }
        }

        if (cameraToMouseRayIntersect(sceneObject, mouseDir, cameraPos)) {
            return sceneObject;
        }

        return null;
    }

    /**
     * Resets the hovered state of a SceneObject.
     *
     * @param object The SceneObject to reset.
     */
    public static void resetHoveredObject(SceneObject object) {
        object.setHovered(false);
    }

    /**
     * Selects the hovered object and deselects the previously selected object.
     *
     * @param hoveredObject The currently hovered SceneObject.
     * @param oldSelectedObject The previously selected SceneObject.
     * @return The newly selected SceneObject.
     */

    public static SceneObject selectHovered(SceneObject hoveredObject, SceneObject oldSelectedObject) {
        if (oldSelectedObject != null) {
            oldSelectedObject.setSelected(false);
        }
        oldSelectedObject = hoveredObject;
        oldSelectedObject.setSelected(true);
        return oldSelectedObject;
    }

    /**
     * Checks if a ray originating from the camera
     * intersects with the Axis-Aligned Bounding Box (AABB) of a SceneObject.
     *
     * @param object The SceneObject whose AABB is being checked.
     * @param mouseDir The direction of the mouse ray in world coordinates.
     * @param cameraPos The position of the camera in world coordinates.
     * @return true if the ray intersects with the AABB, false otherwise.
     */
    public static boolean cameraToMouseRayIntersect(SceneObject object, Vector4f mouseDir, Vector3f cameraPos) {
        float tMin = Float.MIN_VALUE;
        float tMax = Float.MAX_VALUE;
        Vector3f rayDirection = new Vector3f(mouseDir.x, mouseDir.y, mouseDir.z);

        for (int i = 0; i < 3; i++) {  // Iterate over x, y, z axes
            float rayDirComponent = rayDirection.get(i);
            float rayOriginComponent = cameraPos.get(i);
            float aabbMinComponent = object.getAabbMin().get(i);
            float aabbMaxComponent = object.getAabbMax().get(i);

            if (Math.abs(rayDirComponent) < 1E-6) {  // Ray is parallel to the slab
                if (rayOriginComponent < aabbMinComponent || rayOriginComponent > aabbMaxComponent) {
                    return false;  // Ray is outside the slab
                }
            } else {
                float invDir = 1.0f / rayDirComponent;
                float t1 = (aabbMinComponent - rayOriginComponent) * invDir;
                float t2 = (aabbMaxComponent - rayOriginComponent) * invDir;

                if (t1 > t2) {  // Swap t1 and t2 if t1 > t2
                    float temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                tMin = Math.max(tMin, t1);  // Update tMin
                tMax = Math.min(tMax, t2);  // Update tMax

                if (tMin > tMax) {  // No intersection
                    return false;
                }
            }
        }
        return true;  // Intersection found
    }


}
