package org.lwjgl.dndMechanics.statusEffects;

import org.lwjgl.objects.entities.Creature;

public class Grappled implements StatusEffect {

    private final int originalMoveSpeed;

    public Grappled(Creature creature) {
        originalMoveSpeed = creature.getMoveSpeed();
        applyEffect(creature);
    }

    @Override
    public String getName() {
        return "Grappled";
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
        creature.setMoveSpeed(0);
        System.out.println("Creature is grappled");
    }

    @Override
    public void removeEffect(Creature creature) {
        creature.setMoveSpeed(originalMoveSpeed);
    }
}
