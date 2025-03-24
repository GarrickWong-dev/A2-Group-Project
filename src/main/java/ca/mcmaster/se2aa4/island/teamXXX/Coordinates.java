package ca.mcmaster.se2aa4.island.teamXXX;

import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class CoordinateManager {

    private static CoordinateManager instance;
    private final Drone drone; // Instance of Drone class
    private final DirectionToString dts; // Converter to handle direction as strings
    private final Logger logger = LogManager.getLogger(); // Logger to track operations

    // Private constructor to initialize the drone and direction converter
    private CoordinateManager(){
        this.drone = Drone.getInstance();
        this.dts = DirectionToString.getInstance();
    }

    // Singleton pattern to ensure only one instance of CoordinateManager exists
    public static CoordinateManager getInstance(){
        if (instance == null) {
            instance = new CoordinateManager();
        }
        return instance;
    }

    // Updates the coordinates based on the decision action (either "heading" or "fly")
    public void updateCoords(JSONObject decision){
        // Check action type and delegate to the appropriate method
        if(decision.getString("action").equals("heading")) updateTurn(decision);
        else if(decision.getString("action").equals("fly")) updateForward();
    }

    // Logic for updating the coordinates when turning the drone
    private void updateTurn(JSONObject decision){
        Direction directions[] = Direction.values(); // Get all possible directions (N, E, S, W)
        Map<String, Direction> directionMap = Map.of( // Map string directions to Direction enum
            "N", Direction.NORTH,
            "E", Direction.EAST,
            "W", Direction.WEST,
            "S", Direction.SOUTH
        );

        String currFacing = dts.toString(drone.getFacing()); // Get current facing direction (as a string)
        String newFacing = decision.getJSONObject("parameters").getString("direction"); // Get new direction from decision

        Direction currDirection = directionMap.get(currFacing); // Current direction as Direction enum
        Direction newDirection = directionMap.get(newFacing); // New direction as Direction enum

        // Get indices of current and new directions in the directions array
        int currIndex = Arrays.asList(directions).indexOf(currDirection);
        int newIndex = Arrays.asList(directions).indexOf(newDirection);

        // Calculate the turn offset (1 for right turn, 3 for left turn)
        int turnOffset = (newIndex - currIndex + 4) % 4;

        // Perform actions based on the turn direction (right or left)
        switch(turnOffset) {
            case 1 -> { // Right turn
                updatePosition(currFacing); // Update position for the current facing direction
                updatePosition(newFacing); // Update position for the new facing direction
            }
            case 3 -> { // Left turn
                updatePosition(newFacing); // Update position for the new facing direction
                updatePosition(currFacing); // Update position for the current facing direction
            }
        }
    }

    // Updates the coordinates when the drone moves forward in its current direction
    private void updateForward(){
        String currFacing = dts.toString(drone.getFacing()); // Get current facing direction as a string
        updatePosition(currFacing); // Update the position based on the current facing direction
    }

    // Updates the drone's coordinates based on its facing direction
    private void updatePosition(String facing) {
        Coordinates coords = drone.getCoordinates(); // Get current coordinates of the drone
        switch(facing) {
            case "N" -> coords.increaseY(1); // Move North (increase Y)
            case "E" -> coords.increaseX(1); // Move East (increase X)
            case "S" -> coords.increaseY(-1); // Move South (decrease Y)
            case "W" -> coords.increaseX(-1); // Move West (decrease X)
        }
    }
}
