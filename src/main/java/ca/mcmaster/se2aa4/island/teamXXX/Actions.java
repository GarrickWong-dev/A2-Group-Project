package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Actions {
    private final Logger logger = LogManager.getLogger();
    private static Actions instance;
    private final CoordinateManager cm = CoordinateManager.getInstance();
    private final Direction[] directions = Direction.values();
    private final DirectionToString converter = DirectionToString.getInstance();
    private final Drone drone = Drone.getInstance();

    // Singleton pattern to get the instance of Actions
    public static Actions getInstance() {
        if (instance == null) {
            instance = new Actions();
        }
        return instance;
    }

    // Turn drone left and update its coordinates and facing direction
    public void turnLeft(JSONObject decision){
        logger.info("heading: " + getLeft());
        logger.info("Facing: " + drone.getFacing());
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", getLeft()));
        cm.updateCoords(decision);
        drone.setFacing(converter.fromString(getLeft()));
    }

    // Turn drone right and update its coordinates and facing direction
    public void turnRight(JSONObject decision){
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", getRight()));
        cm.updateCoords(decision);
        drone.setFacing(converter.fromString(getRight()));
    }

    // Move the drone forward and update its coordinates
    public void moveForward(JSONObject decision){
        decision.put("action", "fly");
        cm.updateCoords(decision);
    }

    // Get the direction when turning right
    public String getRight(){
        return converter.toString(directions[(directionIndex() + 1) % directions.length]);
    }

    // Get the direction when turning left
    public String getLeft(){
        return converter.toString(directions[(directionIndex() - 1 + directions.length) % directions.length]);
    }

    // Get the index of the current facing direction in the directions array
    private int directionIndex(){
        for(int i = 0; i < directions.length; i++){
            if(this.drone.getFacing().equals(directions[i])) return i;
        }
        return -1;
    }
}
