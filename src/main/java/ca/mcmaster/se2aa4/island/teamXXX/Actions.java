package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class Actions {
    private static Actions instance;
    private final CoordinateManager cm = CoordinateManager.getInstance();
    private final Direction[] directions = Direction.values();
    private final DirectionToString converter = DirectionToString.getInstance();
    private final Drone drone = Drone.getInstance();

    public static Actions getInstance() {
        if (instance == null) {
            instance = new Actions();
        }
        return instance;
    }

    public void turnLeft(JSONObject decision){
        Direction heading = directions[(directionIndex() - 1 + directions.length) % directions.length];
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