package org.lwjgl;

public final class Spells {
    public static final int LINE = 0;
    public static final int CIRCLE = 1;
    public static final int CONE = 2;

    public static String getSpellName(int spellType) {
        if (spellType == LINE) {
            return "Line";
        } else if (spellType == CIRCLE) {
            return "Circle";
        } else if (spellType == CONE) {
            return "Cone";
        }
        return "Unknown";
    }
}
