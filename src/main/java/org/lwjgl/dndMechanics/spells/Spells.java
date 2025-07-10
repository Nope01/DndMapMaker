package org.lwjgl.dndMechanics.spells;

import org.lwjgl.objects.Grid;
import org.lwjgl.objects.hexagons.Hexagon;
import org.lwjgl.objects.hexagons.HexagonMath;

import java.util.Set;

public final class Spells {

    public static final int LINE = 0;
    public static final int CIRCLE = 1;
    public static final int CONE = 2;

    private Spells() {
        // Prevent instantiation
    }

    /**
     * Returns the name of the spell type based on its integer value.
     *
     * @param spellType The integer representing the spell type.
     * @return The name of the spell type as a String.
     */
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

    /**
     * Unhighlights all hexagons that are currently highlighted for spell casting.
     *
     * @param spellHighlightedTiles The set of hexagons to unhighlight.
     */
    public static void clearSpellHighlightedTiles(Set<Hexagon> spellHighlightedTiles) {
        for (Hexagon hex : spellHighlightedTiles) {
            hex.setSpellHighlighted(false);
        }
    }

    /**
     * Returns a set of hexagons that are in a line between two specified hexagons.
     *
     * @param start the starting hexagon
     * @param end the ending hexagon
     * @param gridClass the grid class used for calculations
     * @return a set of hexagons in the line between start and end
     */
    public static Set<Hexagon> getHexesInLineSpell(Hexagon start, Hexagon end, Grid gridClass ) {
        return HexagonMath.cubeLineDraw(start.getCubePos(), end.getCubePos(), gridClass);
    }

    /**
     * Returns a set of hexagons that are within a specified radius of a given origin hexagon.
     *
     * @param origin the origin hexagon
     * @param size the radius size
     * @param gridClass the grid class used for calculations
     * @return a set of hexagons in a circle around the origin
     */
    public static Set<Hexagon> getHexesInCircleSpell(Hexagon origin, int size, Grid gridClass) {
        return HexagonMath.hexVisible(origin, size, gridClass);
    }

    /**
     * Returns a set of hexagons that are in a cone shape originating from a specified hexagon.
     *
     * @param origin the origin hexagon
     * @param direction the direction of the cone
     * @param size the size of the cone
     * @param gridClass the grid class used for calculations
     * @return a set of hexagons in the cone shape
     */
    public static Set<Hexagon> getHexesInConeSpell(Hexagon origin, int direction, int size, Grid gridClass) {
        return HexagonMath.hexCone(origin, direction, size, gridClass);
    }
}
