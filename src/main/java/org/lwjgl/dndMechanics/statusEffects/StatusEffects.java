package org.lwjgl.dndMechanics.statusEffects;

public final class StatusEffects {
    public static final int BLINDED = 0;
    public static final int FRIGHTENED = 1;
    public static final int GRAPPLED = 2;
    public static final int INCAPACITATED = 3;
    public static final int INVISIBLE = 4;
    public static final int PRONE = 5;
    public static final int DASHING = 6;


    public static String[] statusEffectList = new String[] {
            "Blinded",
            "Frightened",
            "Grappled",
            "Incapacitated",
            "Invisible",
            "Prone",
            "Dashing",
    };

    public static String getStatusEffectName(int statusEffect) {
        if (statusEffect == BLINDED) {
            return "Blinded";
        } else if (statusEffect == FRIGHTENED) {
            return "Frightened";
        } else if (statusEffect == GRAPPLED) {
            return "Grappled";
        } else if (statusEffect == INCAPACITATED) {
            return "Incapacitated";
        } else if (statusEffect == INVISIBLE) {
            return "Invisible";
        } else if (statusEffect == PRONE) {
            return "Prone";
        } else if (statusEffect == DASHING) {
            return "Dashing";
        } else if (statusEffect == -1) {
            return "None";
        } else {
            System.out.println("Unknown status effect: " + statusEffect);
        }
        return "Unknown";
    }

}
