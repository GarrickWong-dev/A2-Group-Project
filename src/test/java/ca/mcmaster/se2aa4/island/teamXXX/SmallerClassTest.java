package ca.mcmaster.se2aa4.island.teamXXX;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.json.JSONObject;

import java.beans.Transient;


public class SmallerClassTest {
    private Coordinates coords;
    private Drone drone;
    private DirectionToString dts;


    @BeforeEach
    public void initialize(){
        coords = new Coordinates(0,0);
        drone = drone.getInstance();
        drone.setCoordinates(coords);
        drone.setFacing(Direction.EAST);
        dts = dts.getInstance();
    }

    @Test
    public void testCoordinates() {
        coords.increaseX(2);
        coords.increaseY(-5);
        Integer x = 2;
        Integer y = -5;
        assertEquals(x,coords.getX());
        assertEquals(y,coords.getY());
    }

    @Test
    public void testDrone(){

        drone.setFacing(Direction.NORTH);
        assertEquals(Direction.NORTH, drone.getFacing());

        drone.setFacing(Direction.WEST);
        assertEquals(Direction.WEST, drone.getFacing());

        drone.setFacing(Direction.EAST);
        assertEquals(Direction.EAST, drone.getFacing());

        drone.setFacing(Direction.SOUTH);
        assertEquals(Direction.SOUTH, drone.getFacing());

        Coordinates coord = new Coordinates(12,-8);

        drone.setCoordinates(coord);

        assertEquals(coord, drone.getCoordinates());

    }

    @Test
    public void testDTS(){
        assertEquals("N", dts.toString(Direction.NORTH));
        assertEquals("E", dts.toString(Direction.EAST));
        assertEquals("S", dts.toString(Direction.SOUTH));
        assertEquals("W", dts.toString(Direction.WEST));

    }

}
