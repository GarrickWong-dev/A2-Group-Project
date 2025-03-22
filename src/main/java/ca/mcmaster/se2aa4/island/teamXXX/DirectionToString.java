package ca.mcmaster.se2aa4.island.teamXXX;

public class DirectionToString {
    private final Direction[] directions;
    private static DirectionToString instance;

    private DirectionToString(){
        this.directions = Direction.values();
    }

    public static DirectionToString getInstance() {
        if (instance == null) {
            instance = new DirectionToString();
        }
        return instance;
    }

    public String toString(Direction dir){
        for (Direction direction : directions) {
            if (direction.equals(dir)) {
                return String.valueOf(direction.name().charAt(0));
            }
        }
        return null;
    }

    public Direction fromString(String initialHeaing) {
        char charac = initialHeaing.toUpperCase().charAt(0);
        switch (charac) {
            case 'N':
                return Direction.NORTH;
            case 'E':
                return Direction.EAST;
            case 'S':
                return Direction.SOUTH;
            case 'W':
                return Direction.WEST;
            default:
                throw new IllegalArgumentException("Invalid direction: " + initialHeaing);
        }
    }
}
