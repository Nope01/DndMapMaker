package org.lwjgl.dndMechanics.statusEffects;

import org.lwjgl.objects.entities.Creature;

//This class represents a status effect that blinds a creature, reducing its visibility range
public class Blinded implements StatusEffect{

    private final int originalDungeonVisibleRange;

    //Saves the original visibility and applies the blinded effect
    public Blinded(Creature creature) {
        originalDungeonVisibleRange = creature.getDungeonVisibleRange();
        applyEffect(creature);
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
        creature.setDungeonVisibleRange(1);
        System.out.println("Creature is blinded");
    }

    @Override
    public void removeEffect(Creature creature) {
        creature.setDungeonVisibleRange(originalDungeonVisibleRange);
    }
}
