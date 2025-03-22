package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class DimensionsSetUp {
    private int state = 0;
    
    private String currentHeading;
    private int targetForwardSteps = 0;
    private String rightEdgeStatus = null;
    private int rightEdgeRange = 0;
    private String leftEdgeStatus = null;
    private int leftEdgeRange = 0;
    private String chosenSide = null;
    private String sideEchoStatus = null;
    private boolean process = false;
    
    private String lastEchoType = null;
    private String lastEdgeDirection = null;
    private boolean chaseEchoPending = true;
    private String lastEchoDirection = "N";
    
    private Actions actions;
    private final Turning turning = new Turning();
    private DimensionsHelper helper;

    public DimensionsSetUp(String initHeading, Actions actions) {
        this.currentHeading = initHeading;
        this.actions = actions;
        this.helper = new DimensionsHelper(turning, actions);
    }
    
    public JSONObject setupDimensions() {
        JSONObject decision = new JSONObject();
        switch(state) 
        {
            case 0:
                //echo forward
                decision = helper.buildEcho(currentHeading);
                lastEchoType = "forward";
                break;
            case 1:
                //fly forward targetForwardSteps times 
                if (targetForwardSteps > 0) 
                {
                    actions.moveForward(decision);
                    targetForwardSteps--;
                } 
                else 
                {
                    rightEdgeStatus = null;
                    leftEdgeStatus = null;
                    state = 2;
                    return setupDimensions();
                }
                break;
            case 2:
                //echo right
                decision = helper.buildEcho(helper.computeTurnHeading(currentHeading, "right"));
                lastEchoType = "edge";
                lastEdgeDirection = "right";
                state = 3;
                break;
            case 3:
                //echo left
                decision = helper.buildEcho(helper.computeTurnHeading(currentHeading, "left"));
                lastEchoType = "edge";
                lastEdgeDirection = "left";
                state = 4;
                break;
            case 4:
                //evaluate edge echoes
                if ("OUT_OF_RANGE".equals(rightEdgeStatus) && "OUT_OF_RANGE".equals(leftEdgeStatus)) 
                {
                    chosenSide = (rightEdgeRange < leftEdgeRange) ? "right" : "left";
                    state = 5;
                    return setupDimensions();
                } 
                else 
                {
                    actions.moveForward(decision);
                    state = 2; 
                }
                break;
            case 5:
                //turn to chosen side
                decision = helper.buildTurn(currentHeading, chosenSide);
                currentHeading = helper.computeTurnHeading(currentHeading, chosenSide);
                state = 6;
                break;
            case 6:
                //echo chosen side
                decision = helper.buildSideEcho(currentHeading, chosenSide);
                lastEchoType = "side";
                state = 7;
                chaseEchoPending = true;
                break;
            case 7:
                //fly until the echo returns out of range
                if (chaseEchoPending) 
                {
                    decision = helper.buildSideEcho(currentHeading, chosenSide);
                    lastEchoType = "side";
                    chaseEchoPending = false;
                    return decision;
                } 
                else 
                {
                    if (sideEchoStatus != null && "OUT_OF_RANGE".equals(sideEchoStatus)) 
                    {
                        state = 8;
                        return setupDimensions();
                    } 
                    else 
                    {
                        actions.moveForward(decision);
                        chaseEchoPending = true;
                    }
                }
                break;
            case 8:
                //turn final time in chosen direction
                decision = helper.buildTurn(currentHeading, chosenSide);
                currentHeading = helper.computeTurnHeading(currentHeading, chosenSide);
                lastEchoDirection = helper.computeTurnHeading(currentHeading, chosenSide);
                state = 9;
                break;
            case 9:
                // Stop command.
                decision = new JSONObject();
                process = true;
                break;
            default:
                decision = new JSONObject();
                process = true;
                break;
        }
        return decision;
    }
    
    public void updateState(JSONObject response) {
        JSONObject extras = response.getJSONObject("extras");
        if (!extras.has("found") || !extras.has("range")) 
        {
            return;
        }
        String found = extras.getString("found");
        int range = extras.getInt("range");
        if ("forward".equals(lastEchoType)) 
        {
            targetForwardSteps = range + 1;
            state = 1;
            lastEchoType = null;
        } else if ("edge".equals(lastEchoType)) 
        {
            if ("right".equals(lastEdgeDirection)) 
            {
                rightEdgeStatus = found;
                rightEdgeRange = range;
            } 
            else if ("left".equals(lastEdgeDirection)) 
            {
                leftEdgeStatus = found;
                leftEdgeRange = range;
            }
            lastEchoType = null;
        } 
        else if ("side".equals(lastEchoType)) 
        {
            sideEchoStatus = found;
            lastEchoType = null;
        }
    }

    public String getEchoHeading() {
        return lastEchoDirection;
    }

    public String getCurrentHeading() {
        return currentHeading;
    }

    public boolean processDone() {
        return process;
    }
}
