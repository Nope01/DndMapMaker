package org.lwjgl.dndMechanics.statusEffects;

import org.lwjgl.objects.entities.Creature;

public class Blinded implements StatusEffect{
    private final int originalDungeonVisibleRange;

    //Pass the creature being blinded to init the original range
    public Blinded(Creature creature) {
        originalDungeonVisibleRange = creature.getDungeonVisibleRange();
        creature.setDungeonVisibleRange(1);
    }
    @Override
    public String getName() {
        return "Blinded";
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
        System.out.println("Creature is blinded");
    }

    @Override
    public void removeEffect(Creature creature) {
        creature.setDungeonVisibleRange(originalDungeonVisibleRange);
    }
}
