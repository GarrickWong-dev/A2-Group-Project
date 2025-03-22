package ca.mcmaster.se2aa4.island.teamXXX;

public class Drone {
    private static Drone instance;
    private Direction facing;
    private Coordinates coords;

    private Drone(Direction facing){
        this.coords = new Coordinates(0,0);
    //    this.facing = Direction.EAST;
        this.facing = facing;
    }

    public static Drone getInstance(Direction facing){
        if (instance == null) {
            instance = new Drone(facing);
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