package ca.mcmaster.se2aa4.island.teamXXX;

public class Drone {
    private static Drone instance;
    private Direction facing;
    private Coordinates coords;

    private Drone(String initialHeading){
        this.coords = new Coordinates(0,0);
        this.facing = Direction.fromString(initialHeading);
    }

    public static Drone getInstance(String initialHeading){
        if (instance == null) {
            instance = new Drone(initialHeading);
        }
        return instance;
    }

    public Direction getFacing(){
        return this.facing;
    }

    public void setFacing(Direction face){
        this.facing = face;
    }

    public Coordinates getCoordinates(){
        return this.coords;
    }

    public void setCoordinates(Coordinates newCoords){
        this.coords = newCoords;
    }
}