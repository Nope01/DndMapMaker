package org.lwjgl.dndMechanics.spells;

import org.lwjgl.objects.Grid;
import org.lwjgl.objects.Hexagon;

import java.util.Set;

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

    public static void clearSpellHighlightedTiles(Set<Hexagon> spellHighlightedTiles) {
        for (Hexagon hex : spellHighlightedTiles) {
            hex.setSpellHighlighted(false);
        }
    }

    public static Set<Hexagon> getHexesInLineSpell(Hexagon start, Hexagon end, Grid gridClass ) {
        return Hexagon.cubeLineDraw(start.getCubePos(), end.getCubePos(), gridClass);
    }

    public static Set<Hexagon> getHexesInCircleSpell(Hexagon origin, int size, Grid gridClass) {
        return Hexagon.hexVisible(origin, size, gridClass);
    }

    public static Set<Hexagon> getHexesInConeSpell(Hexagon origin, int direction, int size, Grid gridClass) {
        return Hexagon.hexCone(origin, direction, size, gridClass);
    }
}
