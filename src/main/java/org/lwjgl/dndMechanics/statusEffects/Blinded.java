package org.lwjgl.dndMechanics.statusEffects;

import org.lwjgl.objects.entities.Creature;

public class Blinded implements StatusEffect{
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
        creature.setDungeonVisibleRange(1);
    }

    @Override
    public void removeEffect(Creature creature) {

    }
}
