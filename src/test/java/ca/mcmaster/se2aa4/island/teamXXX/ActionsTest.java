package ca.mcmaster.se2aa4.island.teamXXX;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.json.JSONObject;

import java.beans.Transient;

public class ActionsTest {
    private Coordinates coords;
    private Drone drone;
    private DirectionToString dts;
    private CoordinateManager cm;
    private JSONObject decision;
    private Actions actions;

    @BeforeEach
    public void initialize(){
        coords = new Coordinates(0,0);
        drone = drone.getInstance();
        drone.setCoordinates(coords);
        drone.setFacing(Direction.EAST);
        dts = dts.getInstance();
        cm = cm.getInstance();
        decision = new JSONObject();
        actions = actions.getInstance();
    }

    @Test
    public void testCMForward(){
        actions.moveForward(decision);
        Integer x = 1;
        Integer y = 0;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
        
    }


    @Test
    public void testCMEtoN(){

        actions.turnLeft(decision);
        Integer x = 1;
        Integer y = 1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
        assertEquals(Direction.NORTH, drone.getFacing());
    }

    @Test
    public void testCMEtoS(){

        actions.turnRight(decision);
        Integer x = 1;
        Integer y = -1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
        assertEquals(Direction.SOUTH, drone.getFacing());
    }

    @Test
    public void testCMStoE(){

        drone.setFacing(Direction.SOUTH);
        actions.turnLeft(decision);
        Integer x = 1;
        Integer y = -1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
        assertEquals(Direction.EAST, drone.getFacing());
    }

    @Test
    public void testCMStoW(){

        drone.setFacing(Direction.SOUTH);
        actions.turnRight(decision);
        Integer x = -1;
        Integer y = -1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
        assertEquals(Direction.WEST, drone.getFacing());
    }

    @Test
    public void testCMWtoS(){

        drone.setFacing(Direction.WEST);
        actions.turnLeft(decision);
        Integer x = -1;
        Integer y = -1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
        assertEquals(Direction.SOUTH, drone.getFacing());
    }

    @Test
    public void testCMWtoN(){

        drone.setFacing(Direction.WEST);
        actions.turnRight(decision);
        Integer x = -1;
        Integer y = 1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
        assertEquals(Direction.NORTH, drone.getFacing());
    }

    @Test
    public void testCMNtoW(){

        drone.setFacing(Direction.NORTH);
        actions.turnLeft(decision);
        Integer x = -1;
        Integer y = 1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
        assertEquals(Direction.WEST, drone.getFacing());
    }

    @Test
    public void testCMNtoE(){

        drone.setFacing(Direction.NORTH);
        actions.turnRight(decision);
        Integer x = 1;
        Integer y = 1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
        assertEquals(Direction.EAST, drone.getFacing());
    }

}

