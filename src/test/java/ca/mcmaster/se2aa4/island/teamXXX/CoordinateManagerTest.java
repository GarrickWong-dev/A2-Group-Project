package ca.mcmaster.se2aa4.island.teamXXX;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.json.JSONObject;

import java.beans.Transient;

public class CoordinateManagerTest {
    private Coordinates coords;
    private Drone drone;
    private DirectionToString dts;
    private CoordinateManager cm;
    private JSONObject decision;

    @BeforeEach
    public void initialize(){
        coords = new Coordinates(0,0);
        drone = drone.getInstance();
        drone.setCoordinates(coords);
        drone.setFacing(Direction.EAST);
        dts = dts.getInstance();
        cm = cm.getInstance();
        decision = new JSONObject();
    }

    @Test
    public void testCMForward(){

        decision.put("action", "fly");
        cm.updateCoords(decision);
        Integer x = 1;
        Integer y = 0;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
        
    }


    @Test
    public void testCMEtoN(){

        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "N"));
        cm.updateCoords(decision);
        Integer x = 1;
        Integer y = 1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
    }

    @Test
    public void testCMEtoS(){

        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "S"));
        cm.updateCoords(decision);
        Integer x = 1;
        Integer y = -1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
    }

    @Test
    public void testCMStoE(){

        drone.setFacing(Direction.SOUTH);
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "E"));
        cm.updateCoords(decision);
        Integer x = 1;
        Integer y = -1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
    }

    @Test
    public void testCMStoW(){

        drone.setFacing(Direction.SOUTH);
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "W"));
        cm.updateCoords(decision);
        Integer x = -1;
        Integer y = -1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
    }

    @Test
    public void testCMWtoS(){

        drone.setFacing(Direction.WEST);
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "S"));
        cm.updateCoords(decision);
        Integer x = -1;
        Integer y = -1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
    }

    @Test
    public void testCMWtoN(){

        drone.setFacing(Direction.WEST);
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "N"));
        cm.updateCoords(decision);
        Integer x = -1;
        Integer y = 1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
    }

        @Test
    public void testCMNtoW(){

        drone.setFacing(Direction.NORTH);
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "W"));
        cm.updateCoords(decision);
        Integer x = -1;
        Integer y = 1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
    }

    @Test
    public void testCMNtoE(){

        drone.setFacing(Direction.NORTH);
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "E"));
        cm.updateCoords(decision);
        Integer x = 1;
        Integer y = 1;
        assertEquals(x,drone.getCoordinates().getX());
        assertEquals(y,drone.getCoordinates().getY());
    }

}
