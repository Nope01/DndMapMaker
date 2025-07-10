package org.lwjgl.objects.hexagons;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.objects.Grid;

import java.util.*;

import static java.lang.Math.abs;

public final class HexagonMath {
    public static final int N = 0;
    public static final int NE = 1;
    public static final int SE = 2;
    public static final int S = 3;
    public static final int SW = 4;
    public static final int NW = 5;

    public static Vector3i[] cubeDirectionVectors = new Vector3i[]{
        new Vector3i(0, -1, 1), //N
                new Vector3i(1, -1, 0), //NE
                new Vector3i(1, 0, -1), //SE
                new Vector3i(0, 1, -1), //S
                new Vector3i(-1, 1, 0), //SW
                new Vector3i(-1, 0, 1), //NW
                /*
               5  0  1
                ↖ ↑ ↗
                  ·
                ↙ ↓ ↘
               4  3  2
                 */
    };

    private HexagonMath() {
        // Private constructor to prevent instantiation
    }

    public static String intToDirection(int direction) {
        if (direction == N) {
            return "N";
        } else if (direction == NE) {
            return "NE";
        } else if (direction == SE) {
            return "SE";
        } else if (direction == S) {
            return "S";
        } else if (direction == SW) {
            return "SW";
        } else if (direction == NW) {
            return "NW";
        }
        return "What";
    }

    public static Vector2i cubeToAxialCoords(Vector3i cube) {
        int q = cube.x;
        int r = cube.y;
        return new Vector2i(q, r);
    }

    public static Vector3i axialToCubeCoords(Vector2i axial) {
        int q = axial.x;
        int r = axial.y;
        int s = -q-r;
        return new Vector3i(q, r, s);
    }

    public static Vector2i cubeToOffsetCoords(Vector3i cube) {
        int col = cube.x;
        int row = cube.y + (cube.x - (cube.x&1))/2;
        return new Vector2i(col, row);
    }

    public static Vector3i offsetToCubeCoords(Vector2i offset) {
        int q = offset.x;
        int r = offset.y - (offset.x - (offset.x&1))/2;
        int s = -q-r;
        return new Vector3i(q, r, s);
    }

    //Converts direction int value to a vector for the given direction
    public static Vector3i cubeDirection(int direction) {
        return cubeDirectionVectors[direction];
    }

    //Adds the directional vec to a target hexagon, returning the target neighbour hex
    public static Vector3i cubeAddDirection(Vector3i hex, Vector3i vec ) {
        return new Vector3i(hex.x + vec.x, hex.y + vec.y, hex.z + vec.z);
    }

    //Given a hex and directional value, returns the coords for the neighbour in that direction
    public static Vector3i getCubeNeighbour(Vector3i hex, int direction) {
        return cubeAddDirection(hex, cubeDirection(direction));
    }

    public static Vector3i[] getAllNeighbours(Vector3i hex) {
        Vector3i[] neighbours = new Vector3i[6];
        for (int i = 0; i < 6; i++) {
            neighbours[i] = getCubeNeighbour(hex, i);
        }
        return neighbours;
    }

    public static Vector3i cubeSubtract(Vector3i a, Vector3i b) {
        return new Vector3i(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static int cubeDistance(Vector3i a, Vector3i b) {
        Vector3i vec = cubeSubtract(a, b);
        return new Vector3i(abs(vec.x) + abs(vec.y) + abs(vec.z)).div(2).x;
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static Vector3f cubeLerp(Vector3i a, Vector3i b, float t) {
        return new Vector3f(
                lerp(a.x, b.x, t),
                lerp(a.y, b.y, t),
                lerp(a.z, b.z, t));
    }

    public static Set<Hexagon> cubeLineDraw(Vector3i a, Vector3i b, Grid gridClass) {
        int length = cubeDistance(a, b);
        Set<Hexagon> results = new HashSet<>();
        for (int i = 0; i < length; i++) {
            Hexagon hexagon = gridClass.getHexagonAt(cubeRound(cubeLerp(a, b, (float) (1.0/length * i))));
            results.add(hexagon);
        }
        return results;
    }

    public static Vector3i cubeRound(Vector3f frac) {
        int x = Math.round(frac.x);
        int y = Math.round(frac.y);
        int z = Math.round(frac.z);

        float xDiff = Math.abs(x - frac.x);
        float yDiff = Math.abs(y - frac.y);
        float zDiff = Math.abs(z - frac.z);

        if (xDiff > yDiff && xDiff > zDiff) {
            x = -y - z;
        }
        else if (yDiff > zDiff) {
            y = -x-z;
        }
        else {
            z = -x-y;
        }
        return new Vector3i(x, y, z);
    }

    public static Set<Hexagon> hexReachable(Hexagon start, int range, Grid gridClass) {
        Set<Hexagon> visited = new HashSet<>();
        visited.add(start);

        List<List<Hexagon>> fringes = new ArrayList<>();
        fringes.add(new ArrayList<>());
        fringes.get(0).add(start);

        for (int k = 1; k <= range; k++) {
            fringes.add(new ArrayList<>());
            for (Hexagon hex : fringes.get(k-1)) {
                for (int dir = 0; dir < 6; dir++) {
                    Hexagon neighbor = gridClass.getHexagonAt(getCubeNeighbour(hex.getCubePos(), dir));
                    if (neighbor instanceof CombatHexagon && !((CombatHexagon) neighbor).isBlockedForMovement()) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            fringes.get(k).add(neighbor);
                            neighbor.setMovementHighlighted(true);
                        }
                    }
                }
            }
        }
        return visited;
    }

    public static Set<Hexagon> hexVisible(Hexagon start, int range, Grid gridClass) {
        Set<Hexagon> visible = new HashSet<>();
        visible.add(start); // Always visible to itself

        // Get all hexagons within range first
        Set<Hexagon> inRange = getHexagonsInRange(start, range, gridClass);

        // Check LOS to each hex in range
        for (Hexagon target : inRange) {
            if (hasLineOfSight(start, target, gridClass)) {
                visible.add(target);
                //target.setVisible(true);
                // If this is a wall, we stop checking further along this line
                if (target instanceof CombatHexagon && ((CombatHexagon) target).isWall) {
                    continue; // Don't check what's behind this wall
                }
            }
        }

        return visible;
    }

    private static Set<Hexagon> getHexagonsInRange(Hexagon start, int range, Grid gridClass) {
        Set<Hexagon> inRange = new HashSet<>();
        Queue<Hexagon> queue = new LinkedList<>();
        Map<Hexagon, Integer> distances = new HashMap<>();

        queue.add(start);
        distances.put(start, 0);
        inRange.add(start);

        while (!queue.isEmpty()) {
            Hexagon current = queue.poll();
            int currentDist = distances.get(current);

            if (currentDist >= range) {
                continue;
            }

            for (int dir = 0; dir < 6; dir++) {
                Hexagon neighbor = gridClass.getHexagonAt(getCubeNeighbour(current.getCubePos(), dir));
                if (neighbor != null && !distances.containsKey(neighbor)) {
                    distances.put(neighbor, currentDist + 1);
                    inRange.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return inRange;
    }

    private static boolean hasLineOfSight(Hexagon start, Hexagon end, Grid gridClass) {
        if (start.equals(end)) return true;

        Vector3i a = start.getCubePos();
        Vector3i b = end.getCubePos();
        int distance = cubeDistance(a, b);

        // Walk along the line
        for (int i = 1; i <= distance; i++) {
            float t = 1.0f/distance * i;
            Vector3i interp = cubeRound(cubeLerp(a, b, t));
            Hexagon hex = gridClass.getHexagonAt(interp);

            // If we hit a wall before reaching our target, no LOS
            if (hex instanceof CombatHexagon && ((CombatHexagon) hex).isWall) {
                // Only block if the wall is before our target position
                if (i < distance) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Set<Hexagon> hexCone(Vector3i origin, int direction, int size, Grid gridClass) {
        Set<Hexagon> cone = new HashSet<>();
        cone.add(gridClass.getHexagonAt(origin));  // Origin is always part of cone

        if (size == 0) {
            return cone;
        }

        // Get primary direction vector
        Vector3i primaryDir = cubeDirection(direction);

        // For each step along primary direction
        for (int r = 1; r <= size; r++) {
            // Current position along primary direction
            Vector3i current = new Vector3i(
                    origin.x + primaryDir.x * r,
                    origin.y + primaryDir.y * r,
                    origin.z + primaryDir.z * r
            );

            Hexagon hex = gridClass.getHexagonAt(current);
            if (hex != null) {
                cone.add(hex);
            }

            // Get left and right perpendicular directions
            int leftDir = (direction + 2) % 6;
            int rightDir = (direction + 4) % 6;

            // Expand perpendicularly to form cone
            for (int s = 1; s < r; s++) {
                // Left side
                Vector3i left = cubeAddDirection(current,
                        cubeDirection(leftDir).mul(s, new Vector3i()));
                Hexagon leftHex = gridClass.getHexagonAt(left);
                if (leftHex != null) {
                    cone.add(leftHex);
                }

                // Right side
                Vector3i right = cubeAddDirection(current,
                        cubeDirection(rightDir).mul(s, new Vector3i()));
                Hexagon rightHex = gridClass.getHexagonAt(right);
                if (rightHex != null) {
                    cone.add(rightHex);
                }
            }
        }

        return cone;
    }

    public static Set<Hexagon> hexCone(Hexagon origin, int direction, int size, Grid gridClass) {
        return hexCone(origin.getCubePos(), direction, size, gridClass);
    }

}
