package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class DimensionsSetUp implements Search {
    private int state = 0; // Current state in the search process
    private final Logger logger = LogManager.getLogger(); // Logger for logging actions
    
    // Variables related to the echo and edge statuses
    private String currentHeading;
    private int targetForwardSteps = 0;
    private String rightEdgeStatus = null;
    private int rightEdgeRange = 0;
    private String leftEdgeStatus = null;
    private int leftEdgeRange = 0;
    private String chosenSide = null;
    private String sideEchoStatus = null;
    private boolean process = false; // Flag to indicate if the process is done

    // Variables for tracking the last echo and edge direction
    private String lastEchoType = null;
    private String lastEdgeDirection = null;
    private boolean chaseEchoPending = true; // Flag to track if an echo is pending
    private String lastEchoDirection = "N"; // Default direction for echo

    private Actions actions; // Actions object to execute movement and other actions
    private DimensionsHelper helper; // Helper class to manage echo and turn actions

    // Constructor to initialize the state, actions, and helper
    public DimensionsSetUp(String initHeading, Actions actions) {
        this.currentHeading = initHeading;
        this.actions = actions;
        this.helper = new DimensionsHelper(actions);
    }
    
    // Main search method that decides the next action based on the current state
    public JSONObject search() {
        JSONObject decision = new JSONObject();
        switch(state) {
            case 0:
                // Echo forward to detect surroundings
                decision = helper.buildEcho(currentHeading);
                lastEchoType = "forward"; // Track echo type as forward
                break;
            case 1:
                // Fly forward the target number of steps
                if (targetForwardSteps > 0) {
                    actions.moveForward(decision);
                    targetForwardSteps--;
                } else {
                    rightEdgeStatus = null;
                    leftEdgeStatus = null;
                    state = 2; // Move to next state (echo right)
                    return search();
                }
                break;
            case 2:
                // Echo right to check right edge
                decision = helper.buildEcho(helper.computeTurnHeading(currentHeading, "right"));
                lastEchoType = "edge";
                lastEdgeDirection = "right";
                state = 3; // Move to echo left state
                break;
            case 3:
                // Echo left to check left edge
                decision = helper.buildEcho(helper.computeTurnHeading(currentHeading, "left"));
                lastEchoType = "edge";
                lastEdgeDirection = "left";
                state = 4; // Evaluate edge echoes next
                break;
            case 4:
                // Evaluate echo results from right and left edges
                if ("OUT_OF_RANGE".equals(rightEdgeStatus) && "OUT_OF_RANGE".equals(leftEdgeStatus)) {
                    // Choose side based on which edge is closer
                    chosenSide = (rightEdgeRange < leftEdgeRange) ? "right" : "left";
                    state = 5; // Move to turn to chosen side
                    return search();
                } else {
                    actions.moveForward(decision); // Move forward if edges are in range
                    state = 2; // Echo right again
                }
                break;
            case 5:
                // Turn to the chosen side (right or left)
                decision = helper.buildTurn(currentHeading, chosenSide);
                currentHeading = helper.computeTurnHeading(currentHeading, chosenSide);
                logger.info("current Heading " + currentHeading);
                state = 6; // Move to echo chosen side
                break;
            case 6:
                // Echo the chosen side to check range
                decision = helper.buildSideEcho(currentHeading, chosenSide);
                lastEchoType = "side";
                state = 7;
                chaseEchoPending = true; // Flag to track pending echo
                break;
            case 7:
                // Fly until the echo status returns out of range
                if (chaseEchoPending) {
                    decision = helper.buildSideEcho(currentHeading, chosenSide);
                    lastEchoType = "side";
                    chaseEchoPending = false;
                    return decision;
                } else {
                    if (sideEchoStatus != null && "OUT_OF_RANGE".equals(sideEchoStatus)) {
                        state = 8; // Finish search when echo is out of range
                        return search();
                    } else {
                        actions.moveForward(decision); // Continue moving forward
                        chaseEchoPending = true; // Reset the pending echo flag
                    }
                }
                break;
            case 8:
                // Stop command when search is completed
                lastEchoDirection = helper.computeTurnHeading(currentHeading, chosenSide);
                decision = new JSONObject();
                process = true; // Mark process as done
                break;
            default:
                decision = new JSONObject();
                process = true;
                break;
        }
        return decision;
    }
    
    // Update the state based on the response from the environment
    public void updateState(JSONObject response) {
        JSONObject extras = response.getJSONObject("extras");
        if (!extras.has("found") || !extras.has("range")) {
            return; // Return if the response doesn't contain necessary data
        }
        String found = extras.getString("found");
        int range = extras.getInt("range");
        if ("forward".equals(lastEchoType)) {
            targetForwardSteps = range + 1;
            state = 1; // Move to state 1 for moving forward
            lastEchoType = null;
        } else if ("edge".equals(lastEchoType)) {
            // Update edge statuses for right or left edges
            if ("right".equals(lastEdgeDirection)) {
                rightEdgeStatus = found;
                rightEdgeRange = range;
            } else if ("left".equals(lastEdgeDirection)) {
                leftEdgeStatus = found;
                leftEdgeRange = range;
            }
            lastEchoType = null;
        } else if ("side".equals(lastEchoType)) {
            // Update side echo status
            sideEchoStatus = found;
            lastEchoType = null;
        }
    }

    // Get the heading of the last echo
    public String getEchoHeading() {
        return lastEchoDirection;
    }

    // Get the current heading
    public String getCurrentHeading() {
        return currentHeading;
    }

    // Check if the search process is complete
    public boolean processDone() {
        return process;
    }
}
