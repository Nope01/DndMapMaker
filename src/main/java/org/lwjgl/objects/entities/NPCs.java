package org.lwjgl.objects.entities;

public final class NPCs {

    public static final int ASSASSIN = 0;
    public static final int BEAST = 1;
    public static final int BEHOLDER = 2;
    public static final int CAMBION = 3;
    public static final int COMMONER = 4;
    public static final int DRAGON = 5;
    public static final int EFREETI = 6;
    public static final int ELEMENTAL = 7;
    public static final int FAERIE = 8;
    public static final int FLUMPH = 9;
    public static final int GOULSNGHOST = 10;
    public static final int GIANT = 11;
    public static final int GOBLIN = 13;
    public static final int HYDRA = 14;
    public static final int IMP = 15;
    public static final int KNIGHT = 16;
    public static final int LICH = 17;
    public static final int MAGE = 18;
    public static final int MIMIC = 19;
    public static final int NOBLE = 20;
    public static final int PIRATE = 21;
    public static final int PRIEST = 22;
    public static final int SKELETON = 23;
    public static final int VAMPIRE = 24;
    public static final int ZOMBIE = 25;


    private NPCs() {
        // Prevent instantiation
    }

    // Array of NPC names for easy reference
    public static final String[] npcList = new String[] {
            "Assassin",
            "Beast",
            "Beholder",
            "Cambion",
            "Commoner",
            "Dragon",
            "Efreeti",
            "Elemental",
            "Faerie",
            "Flumph",
            "GhoulsNghost",
            "Giant",
            "Goblin",
            "Hydra",
            "Imp",
            "Knight",
            "Lich",
            "Mage",
            "Mimic",
            "Noble",
            "Pirate",
            "Priest",
            "Skeleton",
            "Vampire",
            "Zombie",
    };

    /**
     * Returns the NPC name as a string based on the NPC type.
     *
     * @param npcType The NPC type as an integer.
     * @return The NPC name as a string.
     */
    public static String getNPCAsString(int npcType) {
        if (npcType < 0 || npcType >= npcList.length) {
            throw new IllegalArgumentException("Invalid NPC type: " + npcType);
        }
        return npcList[npcType];
    }

    /**
     * Returns the NPC type as an integer based on the NPC name.
     *
     * @param npcName The NPC name as a string.
     * @return The NPC type as an integer, or -1 if not found.
     */
    public static int getNPCType(String npcName) {
        for (int i = 0; i < npcList.length; i++) {
            if (npcList[i].equalsIgnoreCase(npcName)) {
                return i;
            }
        }
        return -1; // Not found
    }
}