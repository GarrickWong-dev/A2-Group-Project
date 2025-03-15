package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class FindIsland implements Search{

    private String currentHeading;
    private int scanStep = 0;
    private boolean waitingForFly = false;
    // New flag to track if we are on the first cycle.
    private boolean firstCycle = true;

    private EchoData forwardEcho = null;
    private EchoData rightEcho = null;
    private EchoData leftEcho = null;

    private String forwardDir = null;
    private String rightDir = null;
    private String leftDir = null;

    private Turning turning = new Turning();

    private Actions actions;

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
            // On cycles after the first, if any echo detects "GROUND", then stop.
            if (!firstCycle && (
                (forwardEcho != null && "GROUND".equals(forwardEcho.found)) ||
                (rightEcho != null && "GROUND".equals(rightEcho.found)) ||
                (leftEcho != null && "GROUND".equals(leftEcho.found))
            )) {
                decision.put("action", "stop");
                // Reset for the next cycle.
                scanStep = 0;
                forwardEcho = null;
                rightEcho = null;
                leftEcho = null;
                return decision;
            }
            
            // If we reach here, either it’s the first cycle or no ground was detected.
            String chosenDir = null;
            int chosenRange = Integer.MAX_VALUE;
            boolean groundFound = false;
            
            // Check for ground detections (this block will normally not trigger on the first cycle,
            // since the echoes should be out-of-range).
            if (forwardEcho != null && "GROUND".equals(forwardEcho.found)) {
                chosenDir = forwardEcho.direction;
                chosenRange = forwardEcho.range;
                groundFound = true;
            }
            if (rightEcho != null && "GROUND".equals(rightEcho.found)) {
                if (!groundFound || rightEcho.range < chosenRange) {
                    chosenDir = rightEcho.direction;
                    chosenRange = rightEcho.range;
                    groundFound = true;
                }
            }
            if (leftEcho != null && "GROUND".equals(leftEcho.found)) {
                if (!groundFound || leftEcho.range < chosenRange) {
                    chosenDir = leftEcho.direction;
                    chosenRange = leftEcho.range;
                    groundFound = true;
                }
            }
            
            if (groundFound) {
                // If the closest ground detection is immediately ahead (range==0) then stop.
                if (chosenRange == 0) {
                    decision.put("action", "stop");
                } 
                else if (currentHeading.equals(chosenDir)) {
                    decision.put("action", "fly");
                } 
                else {
                    parameters.put("direction", chosenDir);
                    decision.put("action", "heading");
                    decision.put("parameters", parameters);
                    currentHeading = chosenDir;
                    waitingForFly = true;
                }
            } else {
                // No ground detected – use the combined range score algorithm.
                int forwardRange = (forwardEcho != null) ? forwardEcho.range : 0;
                int rightRange = (rightEcho != null) ? rightEcho.range : 0;
                int leftRange = (leftEcho != null) ? leftEcho.range : 0;

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
            }
            // Mark that we have completed the first cycle.
            firstCycle = false;
            // Reset scan state for the next cycle.
            scanStep = 0;
            forwardEcho = null;
            rightEcho = null;
            leftEcho = null;
            return decision;
        }
        
        decision.put("action", "stop");
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
}
