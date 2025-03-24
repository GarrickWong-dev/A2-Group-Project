package ca.mcmaster.se2aa4.island.teamXXX;

public class Drone {
    private static Drone instance; // Singleton instance of the Drone
    private Direction facing; // The direction the drone is facing
    private Coordinates coords; // The current coordinates of the drone

    // Private constructor to initialize the drone's starting position
    private Drone(){
        this.coords = new Coordinates(0, 0); // Initialize coordinates at (0, 0)
    }

    // Singleton pattern to get the instance of Drone
    public static Drone getInstance(){
        if (instance == null) {
            instance = new Drone(); // Create a new instance if it doesn't exist
        }
        return instance; // Return the singleton instance
    }

    // Get the direction the drone is facing
    public Direction getFacing(){
        return this.facing;
    }

    // Set the direction the drone is facing
    public void setFacing(Direction face){
        this.facing = face;
    }

    // Get the current coordinates of the drone
    public Coordinates getCoordinates(){
        return this.coords;
    }

    // Set new coordinates for the drone
    public void setCoordinates(Coordinates newCoords){
        this.coords = newCoords;
    }
}
