package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class IslandMeasurements {
    private enum State { INIT, SCAN, DONE }
    private State state;
    private Turning turning;
    private String currentHeading;
    

    private int initTurnCount = 0;

    private int scanSubState = 0;

    private int flySteps = 0;

    private int lastLeftRange = 0;
    private int lastRightRange = 0;

    private boolean leftOut = false;
    private boolean rightOut = false;

    private String expectedEcho = "";
    
    public IslandMeasurements(String initHeading) {
        this.currentHeading = initHeading;
        this.turning = new Turning();
        this.state = State.INIT;
    }
    
	public JSONObject move() {
   		JSONObject command = new JSONObject();
    	JSONObject parameters = new JSONObject();

	    switch (state) {
	        case INIT:
	            if (initTurnCount < 2) {
	                // Turn right twice (180 degrees)
	                String newHeading = turning.turnRight(currentHeading);
	                parameters.put("direction", newHeading);
	                command.put("action", "heading");
	                command.put("parameters", parameters);
	                currentHeading = newHeading;
	                initTurnCount++;
	                return command;
	            } else {
	                // Move to SCAN state
	                state = State.SCAN;
	                scanSubState = 0;
	            }
	            // fall-through to SCAN

	        case SCAN:
	            if (scanSubState == 0) {
	                // Fly forward
	                command.put("action", "fly");
	                flySteps++;
	                scanSubState = 1;
	                return command;
	            } else if (scanSubState == 1) {
	                // Echo left
	                expectedEcho = "left";
	                String echoDir = turning.turnLeft(currentHeading);
	                parameters.put("direction", echoDir);
	                command.put("action", "echo");
	                command.put("parameters", parameters);
	                scanSubState = 2;
	                return command;
	            } else if (scanSubState == 2) {
	                // Fly forward
	                command.put("action", "fly");
	                flySteps++;
	                scanSubState = 3;
	                return command;
	            } else if (scanSubState == 3) {
	                // Echo right
	                expectedEcho = "right";
	                String echoDir = turning.turnRight(currentHeading);
	                parameters.put("direction", echoDir);
	                command.put("action", "echo");
	                command.put("parameters", parameters);
	                scanSubState = 4;
	                return command;
	            } else if (scanSubState == 4) {
	                // Check if both sides are out-of-range
	                if (leftOut && rightOut) {
	                    state = State.DONE;
	                    command.put("action", "stop");
	                    return command;
	                } else {
	                    // Continue scanning
	                    scanSubState = 0;
	                    return move();
	                }
	            }
	            break;

	        case DONE:
	            command.put("action", "stop");
	            return command;
    }

    command.put("action", "stop");
    return command;
}
 
    public void updateState(JSONObject response) {
	    JSONObject extras = response.getJSONObject("extras");
	    if (!extras.has("found") || !extras.has("range"))
	        return;
	    String found = extras.getString("found");
	    int range = extras.getInt("range");

	    if (expectedEcho.equals("left")) {
	        if (found.equals("GROUND")) {
	            lastLeftRange = range;
	        } else {
	            lastLeftRange = range;  // Store OUT_OF_RANGE value instead of keeping old data
	            leftOut = true;
	        }
	        expectedEcho = "";
	    } else if (expectedEcho.equals("right")) {
	        if (found.equals("GROUND")) {
	            lastRightRange = range;
	        } else {
	            lastRightRange = range; // Store OUT_OF_RANGE value
	            rightOut = true;
	        }
	        expectedEcho = "";
	    }
	}

    

    public String getFinalReport() {
        int approxWidth = lastLeftRange + lastRightRange;
        return "Island dimensions (approx): leftRange = " + lastLeftRange + ", rightRange = " + lastRightRange;
    }
}
