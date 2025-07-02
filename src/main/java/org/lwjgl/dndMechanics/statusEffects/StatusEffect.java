package org.lwjgl.dndMechanics.statusEffects;

import org.lwjgl.objects.entities.Creature;

public interface StatusEffect {

    String getName();
    int getDuration();
    void setDuration(int duration);
    void applyEffect(Creature creature);
    void removeEffect(Creature creature);
    
}
