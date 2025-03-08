package ca.mcmaster.se2aa4.island.teamXXX;

public class DroneCoordinates extends XYCoordinates{
    private static DroneCoordinates instance = new DroneCoordinates(0,0);
    private Direction facing;

    private DroneCoordinates(int X, int Y){
        super(X,Y);
        this.facing = Direction.EAST;
    }

    public static DroneCoordinates getInstance(){
        return instance;
    }

    public Direction getDirection(){
        return facing;
    }

    public void setFacing(Direction face){
        this.facing = face;
    }
}