package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Actions {
    private final Logger logger = LogManager.getLogger();
    private static Actions instance;
    private final CoordinateManager cm;
    private final Direction[] directions;
    private final DirectionToString converter;
    private final Drone drone;

    private Actions(CoordinateManager cm, Drone drone, DirectionToString converter){
        this.directions = Direction.values();
        this.cm = cm;
        this.drone = drone;
        this.converter = converter;
    }

    public static Actions getInstance(CoordinateManager cm, Drone drone, DirectionToString converter) {
        if (instance == null) {
            instance = new Actions(cm, drone, converter);
        }
        return instance;
    }

    public void turnLeft(JSONObject decision){
        Direction heading = directions[(directionIndex() - 1 + directions.length) % directions.length];
        logger.info("heading: " + heading );
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", converter.toString(heading)));
        cm.updateCoords(decision);
        drone.setFacing(heading);
    }

    public void turnRight(JSONObject decision){
        Direction heading = directions[(directionIndex() + 1) % directions.length];
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", converter.toString(heading)));
        cm.updateCoords(decision);
        drone.setFacing(heading);
    }

    public void moveForward(JSONObject decision){
        decision.put("action", "fly");
        cm.updateCoords(decision);
    }

    private int directionIndex(){
        for(int i = 0; i < directions.length; i++){
            if(this.drone.getFacing().equals(directions[i])) return i;
        }
        return -1;
    }
}