package org.lwjgl.dndMechanics.statusEffects;

import org.lwjgl.objects.entities.Creature;

public interface StatusEffect {

    //Use constructors to "apply" the initial effect e.g. blindess, rather than possibly have it called
    //multiple times by the creature

    String getName();
    int getDuration();
    void setDuration(int duration);
    void applyEffect(Creature creature);
    void removeEffect(Creature creature);
    
}
