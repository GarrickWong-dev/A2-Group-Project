package ca.mcmaster.se2aa4.island.teamXXX;
import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
public class CoordinateManager{
    private static CoordinateManager instance;
    private final Drone drone;
    private final DirectionToString dts;
    private final Logger logger = LogManager.getLogger();

    private CoordinateManager(){
        this.drone = Drone.getInstance();
        this.dts = DirectionToString.getInstance();
    }

    public static CoordinateManager getInstance(){
        if (instance == null) {
            instance = new CoordinateManager();
        }
        return instance;
    }

    public void updateCoords(JSONObject decision){
        if(decision.getString("action").equals("heading")) updateTurn(decision);
        else if(decision.getString("action").equals("fly")) updateForward();
    }

    private void updateTurn(JSONObject decision){
        Direction directions[] = Direction.values();
        Map<String, Direction> directionMap = Map.of(
            "N", Direction.NORTH,
            "E", Direction.EAST,
            "W", Direction.WEST,
            "S", Direction.SOUTH
        );

        String currFacing = dts.toString(drone.getFacing()); // "N", "E", "W", or "S"
        String newFacing = decision.getJSONObject("parameters").getString("direction");

        Direction currDirection = directionMap.get(currFacing);
        Direction newDirection = directionMap.get(newFacing);

        int currIndex = Arrays.asList(directions).indexOf(currDirection);
        int newIndex = Arrays.asList(directions).indexOf(newDirection);
        int turnOffset = (newIndex - currIndex + 4) % 4;

        switch(turnOffset) {
            case 1 -> { // Right turn
                updatePosition(currFacing);
                updatePosition(newFacing);
            }
            case 3 -> { // Left turn
                updatePosition(newFacing);
                updatePosition(currFacing);
            }
        }
    }

    private void updateForward(){
        String currFacing = dts.toString(drone.getFacing());
        updatePosition(currFacing);
    }

    private void updatePosition(String facing) {
        Coordinates coords = drone.getCoordinates();
        switch(facing) {
            case "N" -> coords.increaseY(1);
            case "E" -> coords.increaseX(1);
            case "S" -> coords.increaseY(-1);
            case "W" -> coords.increaseX(-1);
        }
    }
}