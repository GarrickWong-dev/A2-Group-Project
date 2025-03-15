package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class CoastCases {
    private Turning turning = new Turning();
    
    // Simple inner class for holding echo response data.
    public static class EchoResponse {
        public String direction;
        public String found;
        public int range;
        public EchoResponse(String direction, String found, int range) {
            this.direction = direction;
            this.found = found;
            this.range = range;
        }
    }
    
    /**
     * Decide which case applies based on the echo responses from forward, left, and right.
     * Cases:
     *  - Case 1: Ground is detected in forward, left, and right.
     *  - Case 2: Ground is only detected in forward.
     *  - Case 3: Ground is detected in forward and left.
     *  - Case 4: Ground is detected in forward and right.
     */
    public int decideCase(EchoResponse forward, EchoResponse left, EchoResponse right) {
        if ("GROUND".equals(forward.found) && "GROUND".equals(left.found) && "GROUND".equals(right.found)) {
            return 1;
        } else if ("GROUND".equals(forward.found) && !"GROUND".equals(left.found) && !"GROUND".equals(right.found)) {
            return 2;
        } else if ("GROUND".equals(forward.found) && "GROUND".equals(left.found) && !"GROUND".equals(right.found)) {
            return 3;
        } else if ("GROUND".equals(forward.found) && "GROUND".equals(right.found) && !"GROUND".equals(left.found)) {
            return 4;
        }
        // Default to case 1 if nothing else fits.
        return 1;
    }
    
    /**
     * Based on the chosen case number and the current heading, return a JSON decision.
     * Note: In a multi-cycle implementation you might need to issue multiple commands (for example, turning twice for a 180° turn).
     */
    public JSONObject executeCase(int caseNum, String currentHeading) {
        JSONObject decision = new JSONObject();
        JSONObject parameters = new JSONObject();
        switch(caseNum) {
            case 1:
                // Case 1: Ground on all sides.
                // Instruction: do a 180° by turning right twice.
                // Here we issue one heading command to turn right.
                parameters.put("direction", turning.turnRight(currentHeading));
                decision.put("action", "heading");
                decision.put("parameters", parameters);
                break;
            case 2:
                // Case 2: Ground only in forward.
                // Instruction: simply move forward.
                decision.put("action", "fly");
                break;
            case 3:
                // Case 3: Ground in front and left.
                // Instruction: Turn 180° by turning right twice then move forward.
                parameters.put("direction", turning.turnRight(currentHeading));
                decision.put("action", "heading");
                decision.put("parameters", parameters);
                break;
            case 4:
                // Case 4: Ground in front and right.
                // Instruction: Turn 180° by turning left twice then move forward.
                parameters.put("direction", turning.turnLeft(currentHeading));
                decision.put("action", "heading");
                decision.put("parameters", parameters);
                break;
            default:
                decision.put("action", "fly");
                break;
        }
        return decision;
    }
}