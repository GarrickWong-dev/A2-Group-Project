package ca.mcmaster.se2aa4.island.teamXXX;

public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public static Direction fromString(String s) {
        switch (s.toUpperCase()) {
            case "N":
            case "NORTH":
                return NORTH;
            case "E":
            case "EAST":
                return EAST;
            case "S":
            case "SOUTH":
                return SOUTH;
            case "W":
            case "WEST":
                return WEST;
            default:
                throw new IllegalArgumentException("Invalid direction: " + s);
        }
    }
}