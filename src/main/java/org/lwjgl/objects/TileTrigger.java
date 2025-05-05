package org.lwjgl.objects;

import org.joml.Vector2i;
import org.joml.Vector3i;

public abstract class TileTrigger extends SceneObject {

    private int triggerRadius;
    private boolean isHidden;

    public TileTrigger(int triggerRadius, Vector2i offsetPos) {
        super();
        this.triggerRadius = triggerRadius;
        this.offsetPos = offsetPos;
        this.cubePos = Hexagon.offsetToCubeCoords(offsetPos);
    }

    public int getTriggerRadius() {
        return triggerRadius;
    }
    public void setTriggerRadius(int triggerRadius) {
        this.triggerRadius = triggerRadius;
    }
    public boolean getIsHidden() {
        return isHidden;
    }
    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }
    public boolean swapIsHidden() {
        isHidden = !isHidden;
        return isHidden;
    }

    public boolean isInRange(Vector3i position) {
        if (Hexagon.cubeDistance(position, cubePos) < 0) {
            return true;
        }
        return false;
    }
}
