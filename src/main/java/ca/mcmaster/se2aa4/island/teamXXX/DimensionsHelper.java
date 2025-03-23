package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class DimensionsHelper {
    private Actions actions;

    public DimensionsHelper(Actions actions) {
        this.actions = actions;
    }

    public JSONObject buildEcho(String direction) 
    {
        JSONObject decision = new JSONObject();
        JSONObject parameters = new JSONObject();
        parameters.put("direction", direction);
        decision.put("action", "echo");
        decision.put("parameters", parameters);
        return decision;
    }


    public JSONObject buildTurn(String currentHeading, String side) 
    {
        JSONObject decision = new JSONObject();
        if ("right".equals(side)) 
        {
            actions.turnRight(decision);
        } 
        else 
        {
            actions.turnLeft(decision);
        }
        return decision;
    }
    
    public String computeTurnHeading(String currentHeading, String side) 
    {
        if ("right".equals(side)) 
        {
            return actions.getRight();
        } 
        else 
        {
            return actions.getLeft();
        }
    }
    
    public JSONObject buildSideEcho(String currentHeading, String side) 
    {
        return buildEcho(computeTurnHeading(currentHeading, side));
    }
}
