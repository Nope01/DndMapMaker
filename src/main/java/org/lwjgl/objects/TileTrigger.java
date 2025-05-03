package org.lwjgl.objects;

public abstract class TileTrigger extends SceneObject {

    private int triggerRadius;
    private boolean isHidden;

    public TileTrigger(int triggerRadius) {
        super();
        this.triggerRadius = triggerRadius;
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
}
