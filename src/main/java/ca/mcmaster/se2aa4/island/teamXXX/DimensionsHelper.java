package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class DimensionsHelper {
    private Actions actions; // Actions instance for performing movements and decisions

    // Constructor to initialize with an Actions object
    public DimensionsHelper(Actions actions) {
        this.actions = actions;
    }

    // Builds an "echo" decision with a given direction
    public JSONObject buildEcho(String direction) {
        JSONObject decision = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put("direction", direction);
        decision.put("action", "echo");
        decision.put("parameters", parameters);
        return decision;
    }

    // Builds a "turn" decision based on current heading and side (right or left)
    public JSONObject buildTurn(String currentHeading, String side) {
        JSONObject decision = new JSONObject();
        // Turn right or left based on the side parameter
        if ("right".equals(side)) {
            actions.turnRight(decision);
        } else {
            actions.turnLeft(decision);
        }
        return decision;
    }

    // Computes the new heading (direction) after turning right or left
    public String computeTurnHeading(String currentHeading, String side) {
        // Return the new heading after a right or left turn
        if ("right".equals(side)) {
            return actions.getRight();
        } else {
            return actions.getLeft();
        }
    }

    // Builds a "side echo" by determining the new heading and calling buildEcho
    public JSONObject buildSideEcho(String currentHeading, String side) {
        return buildEcho(computeTurnHeading(currentHeading, side));
    }
}
