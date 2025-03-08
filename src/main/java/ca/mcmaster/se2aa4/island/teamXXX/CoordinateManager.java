package ca.mcmaster.se2aa4.island.teamXXX;
import java.util.*;
public class CoordinateManager{
    private static CoordinateManager instance = new CoordinateManager();
    DroneCoordinates location = DroneCoordinates.getInstance();
    XYCoordinates emergencyZone;
    ArrayList<XYCoordinates> creeks = new ArrayList<>();

    private CoordinateManager(){
    }

    public static CoordinateManager getInstance(){
        return instance;
    }

    public void store_creek(){//Stores a creek when it is above it
        this.creeks.add(new XYCoordinates(getX(), getY()));
    }

    public void store_emergencyZone(){
        this.emergencyZone = new XYCoordinates(getX(), getY());
    }

    public Direction getDirection(){
        return location.getDirection();
    }

    public int getX(){
        return location.getX();
    }

    public int getY(){
        return location.getY();
    }

    public void forward(){
        Forward forward = new Forward();
        forward.move(this.location);
    }

    public void north(){
        North north = new North();
        north.move(this.location);
    }

    public void south(){
        South south = new South();
        south.move(this.location);
    }

    public void west(){
        West west = new West();
        west.move(this.location);
    }

    public void east(){
        East east = new East();
        east.move(this.location);
    }
}