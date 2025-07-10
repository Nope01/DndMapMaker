package org.lwjgl.objects.entities;

public final class Races {
    public static final int AASIMAR = 0;
    public static final int DRAGONBORN = 1;
    public static final int DWARF = 2;
    public static final int ELF = 3;
    public static final int GNOME = 4;
    public static final int GOLIATH = 5;
    public static final int HALFLING = 6;
    public static final int HUMAN = 7;
    public static final int ORC = 8;
    public static final int TIEFLING = 9;

    private Races() {
        // Prevent instantiation
    }

    //Array of all race names for easy reference
    public static final String[] raceList = new String[] {
            "Aasimar",
            "Dragonborn",
            "Dwarf",
            "Elf",
            "Gnome",
            "Goliath",
            "Halfling",
            "Human",
            "Orc",
            "Tiefling",
    };


    /**
     * Returns the race as a string based on the race index
     * @param race the race as an integer
     * @return the race as a string
     */
    public static String getRaceAsString(int race) {
        if (race < 0 || race >= raceList.length) {
            throw new IllegalArgumentException("Race does not exist: " + race);
        }
        return raceList[race];
    }

    public static int getRaceType(String raceName) {
        for (int i = 0; i < raceList.length; i++) {
            if (raceList[i].equalsIgnoreCase(raceName)) {
                return i;
            }
        }
        return -1; // Not found
    }
}
