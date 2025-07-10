package org.lwjgl.objects.entities;

public final class Classes {

    public static final int BARBARIAN = 0;
    public static final int BARD = 1;
    public static final int CLERIC = 2;
    public static final int DRUID = 3;
    public static final int FIGHTER = 4;
    public static final int MONK = 5;
    public static final int PALADIN = 6;
    public static final int RANGER = 7;
    public static final int ROGUE = 8;
    public static final int SORCERER = 9;
    public static final int WARLOCK = 10;
    public static final int WIZARD = 11;
    public static final int ARTIFICER = 12;

    private Classes() {
        // Prevent instantiation
    }

    //Array of class names for easy reference
    public static final String[] classList = new String[] {
            "Barbarian",
            "Bard",
            "Cleric",
            "Druid",
            "Fighter",
            "Monk",
            "Paladin",
            "Ranger",
            "Rogue",
            "Sorcerer",
            "Warlock",
            "Wizard",
            "Artificer",
    };

    /**
     * Returns the class name as a string based on the class type.
     *
     * @param classType The class type as an integer.
     * @return The class name as a string.
     */
    public static String getClassAsString(int classType) {
        if (classType < 0 || classType >= classList.length) {
            throw new IllegalArgumentException("Invalid class type: " + classType);
        }
        return classList[classType];
    }

    /**
     * Returns the class type as an integer based on the class name.
     *
     * @param className The class name as a string.
     * @return The class type as an integer, or -1 if not found.
     */
    public static int getClassType(String className) {
        for (int i = 0; i < classList.length; i++) {
            if (classList[i].equals(className)) {
                return i;
            }
        }
        return -1;
    }
}
