package org.lwjgl.input;

import org.lwjgl.objects.SceneObject;

public class InputUtils {

    //Deselects old object and returns the new one as selected
    public static SceneObject selectHovered(SceneObject hoveredObject, SceneObject selectedObject) {
        if (selectedObject != null) {
            selectedObject.setSelected(false);
        }
        selectedObject = hoveredObject;
        selectedObject.setSelected(true);
        return selectedObject;
    }
}
