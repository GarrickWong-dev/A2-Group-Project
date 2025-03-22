package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class DecisionBuilder {

    
    
    // Creates an echo decision (action + parameters) for the given direction
    public static JSONObject createEchoDecision(String direction) {
        JSONObject decision = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put("direction", direction);
        decision.put("action", "echo");
        decision.put("parameters", parameters);
        return decision;
    }
    
    // Creates a turn decision (action + parameters) for the given direction
    public static JSONObject createTurnDecision(String direction) {
        JSONObject decision = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put("direction", direction);
        decision.put("action", "turn");
        decision.put("parameters", parameters);
        return decision;
    }
}