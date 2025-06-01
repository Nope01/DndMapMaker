package org.lwjgl.dndMechanics.statusEffects;

import org.lwjgl.objects.entities.Creature;

public class Dash implements StatusEffect {

    public Dash(Creature creature) {
        creature.setMoveSpeed(creature.getMoveSpeed() + creature.getMaxMoveSpeed());
    }

    @Override
    public String getName() {
        return "Dash";
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public void setDuration(int duration) {

    }

    @Override
    public void applyEffect(Creature creature) {
        System.out.println("Got the zoomies");
    }

    @Override
    public void removeEffect(Creature creature) {
        creature.setMoveSpeed(creature.getMaxMoveSpeed());
    }
}
