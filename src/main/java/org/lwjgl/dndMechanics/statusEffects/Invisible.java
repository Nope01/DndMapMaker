package org.lwjgl.dndMechanics.statusEffects;

import org.lwjgl.objects.entities.Creature;

public class Invisible implements StatusEffect {


    public Invisible(Creature creature) {
        applyEffect(creature);
    }

    @Override
    public String getName() {
        return "Invisible";
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
        creature.setVisible(true);
        System.out.println("Creature is a spooky ghost");
    }

    @Override
    public void removeEffect(Creature creature) {
        creature.setVisible(false);
    }
}
