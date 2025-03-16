package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class FindIsland implements Search {

    private String currentHeading;
    private int scanStep = 0;
    private boolean waitingForFly = false;
    // New flag for turning then stopping.
    private boolean waitingForStop = false;
    private boolean firstCycle = true;

    private EchoData forwardEcho = null;
    private EchoData rightEcho = null;
    private EchoData leftEcho = null;

    private String forwardDir = null;
    private String rightDir = null;
    private String leftDir = null;

    private Turning turning = new Turning();

    private Actions actions;

    private boolean process = false;

    private class EchoData {
        String direction;
        String found;
        int range;
        public EchoData(String direction, String found, int range) {
            this.direction = direction;
            this.found = found;
            this.range = range;
        }
    }

    public FindIsland(String initHeading) {
        this.currentHeading = initHeading;
    }
    
    public JSONObject search() {
        JSONObject decision = new JSONObject();
        JSONObject parameters = new JSONObject();
        
        if (waitingForFly) {
            decision.put("action", "fly");
            waitingForFly = false;
            return decision;
        }
        
        // NEW: if we have just changed heading toward the ground,
        // the next cycle should simply stop.
        if (waitingForStop) {
            //decision.put("action", "stop");
            waitingForStop = false;
          //  decision = null;
            process = true;
            return decision;
        }
        
        if (scanStep == 0) {
            forwardDir = currentHeading;
            parameters.put("direction", forwardDir);
            decision.put("action", "echo");
            decision.put("parameters", parameters);
            return decision;
        } 
        else if (scanStep == 1) {
            rightDir = turning.turnRight(currentHeading);
            parameters.put("direction", rightDir);
            decision.put("action", "echo");
            decision.put("parameters", parameters);
            return decision;
        } 
        else if (scanStep == 2) {
            leftDir = turning.turnLeft(currentHeading);
            parameters.put("direction", leftDir);
            decision.put("action", "echo");
            decision.put("parameters", parameters);
            return decision;
        } 
        else if (scanStep == 3) {
            // Check if any echo detected "GROUND"
            if ((forwardEcho != null && "GROUND".equals(forwardEcho.found)) ||
                (rightEcho != null && "GROUND".equals(rightEcho.found)) ||
                (leftEcho != null && "GROUND".equals(leftEcho.found))) {
                
                // Find the echo with the smallest ground range.
                String chosenDir = null;
                int chosenRange = Integer.MAX_VALUE;
                if (forwardEcho != null && "GROUND".equals(forwardEcho.found) && forwardEcho.range < chosenRange) {
                    chosenDir = forwardEcho.direction;
                    chosenRange = forwardEcho.range;
                }
                if (rightEcho != null && "GROUND".equals(rightEcho.found) && rightEcho.range < chosenRange) {
                    chosenDir = rightEcho.direction;
                    chosenRange = rightEcho.range;
                }
                if (leftEcho != null && "GROUND".equals(leftEcho.found) && leftEcho.range < chosenRange) {
                    chosenDir = leftEcho.direction;
                    chosenRange = leftEcho.range;
                }
                
                // If the smallest ground detection is forward, then do nothing and stop.
                if (currentHeading.equals(chosenDir)) {
                   // decision.put("action", "stop");
                    process = true;
                } else {
                    // Otherwise, turn toward the direction with the smallest ground range.
                    parameters.put("direction", chosenDir);
                    decision.put("action", "heading");
                    decision.put("parameters", parameters);
                    currentHeading = chosenDir;
                    // Set flag so that the next cycle will immediately stop.
                    waitingForStop = true;
                }
                // Reset scan state.
                scanStep = 0;
                forwardEcho = null;
                rightEcho = null;
                leftEcho = null;
                firstCycle = false;
                return decision;
            } else {
                // No ground detected – use the combined range score algorithm.
                String chosenDir = null;
                int forwardRange = 0;
                if (forwardEcho != null) {
                    forwardRange = forwardEcho.range;
                }

                int rightRange = 0;
                if (rightEcho != null) {
                    rightRange = rightEcho.range;
                }

                int leftRange = 0;
                if (leftEcho != null) {
                    leftRange = leftEcho.range;
                }

                int diagRightScore = forwardRange + rightRange;
                int diagLeftScore = forwardRange + leftRange;
                
                if (diagRightScore >= diagLeftScore) {
                    chosenDir = turning.turnRight(currentHeading);
                } else {
                    chosenDir = turning.turnLeft(currentHeading);
                }
                
                if (currentHeading.equals(chosenDir)) {
                    decision.put("action", "fly");
                } else {
                    parameters.put("direction", chosenDir);
                    decision.put("action", "heading");
                    decision.put("parameters", parameters);
                    currentHeading = chosenDir;
                    waitingForFly = true;
                }
                // Reset scan state.
                scanStep = 0;
                forwardEcho = null;
                rightEcho = null;
                leftEcho = null;
                return decision;
            }
        }
        
       // decision.put("action", "stop");
        process = true;

        return decision;
    }
    
    public void updateState(JSONObject response) {
        JSONObject extras = response.getJSONObject("extras");
        if (!extras.has("found") || !extras.has("range")) {
            return; 
        }
        String found = extras.getString("found");
        int range = extras.getInt("range");
        
        if (scanStep == 0) {
            forwardEcho = new EchoData(forwardDir, found, range);
            scanStep = 1;
        } 
        else if (scanStep == 1) {
            rightEcho = new EchoData(rightDir, found, range);
            scanStep = 2;
        } 
        else if (scanStep == 2) {
            leftEcho = new EchoData(leftDir, found, range);
            scanStep = 3;
        }
    }

    public boolean processDone()
    {
        return process;
    }

    public String getCurrentHeading() 
    {
        return currentHeading;
    }

}



