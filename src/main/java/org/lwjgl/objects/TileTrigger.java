package org.lwjgl.objects;

public abstract class TileTrigger extends SceneObject {

    private int triggerRadius;

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
}
