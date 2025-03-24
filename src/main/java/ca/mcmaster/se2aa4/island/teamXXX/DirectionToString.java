package ca.mcmaster.se2aa4.island.teamXXX;

public class DirectionToString {
    private final Direction[] directions; // Array to store all direction values
    private static DirectionToString instance; // Singleton instance of the class

    // Private constructor to initialize the directions array
    private DirectionToString(){
        this.directions = Direction.values();
    }

    // Singleton pattern to get the instance of DirectionToString
    public static DirectionToString getInstance() {
        if (instance == null) {
            instance = new DirectionToString();
        }
        return instance;
    }

    // Converts a Direction enum to its corresponding string representation (first letter)
    public String toString(Direction dir){
        for (Direction direction : directions) {
            if (direction.equals(dir)) {
                return String.valueOf(direction.name().charAt(0)); // Return the first letter of the direction
            }
        }
        return null;
    }

    // Converts a string (first letter) back to a Direction enum
    public Direction fromString(String initialHeaing) {
        char charac = initialHeaing.toUpperCase().charAt(0); // Get the first character and convert to uppercase
        switch (charac) {
            case 'N': return Direction.NORTH; // 'N' for North
            case 'E': return Direction.EAST;  // 'E' for East
            case 'S': return Direction.SOUTH; // 'S' for South
            case 'W': return Direction.WEST;  // 'W' for West
            default:
                throw new IllegalArgumentException("Invalid direction: " + initialHeaing); // Handle invalid input
        }
    }
}
