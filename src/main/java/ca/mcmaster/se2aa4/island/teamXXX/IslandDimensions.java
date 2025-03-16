package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class IslandDimensions {

    private int state = 0;
    private final Turning turning = new Turning();
    private String currentHeading;
    

    private int targetForwardSteps = 0;

    private String edgeLeftEcho = null;
    private int edgeLeftRange = 0;
    private String edgeRightEcho = null;
    private int edgeRightRange = 0;
    private boolean waitingForEdgeLeft = false;
    private boolean waitingForEdgeRight = false;

    private int measuredLength = 0;
    private int measuredWidth = 0;
    

    private boolean measureLeft = false;
    

    public IslandDimensions(String initHeading) {
        this.currentHeading = initHeading;
    }
    

    public JSONObject measurer() {
        JSONObject decision = new JSONObject();
        JSONObject parameters = new JSONObject();
        String sensorDirection; // used when issuing echo or heading commands

        switch (state) {
          
            case 0:
                // Echo in front to obtain available range.
                decision.put("action", "echo");
                parameters.put("direction", currentHeading);
                decision.put("parameters", parameters);
                // Do not change state here: wait for the echo response.
                break;
                
            case 1:
                // Fly forward until the echoed range (targetForwardSteps) is exhausted.
                if (targetForwardSteps > 0) {
                    decision.put("action", "fly");
                    targetForwardSteps--;
                } else {
                    // Proceed to edge detection.
                    // Clear any previous edge data.
                    edgeLeftEcho = null;
                    edgeRightEcho = null;
                    state = 2;
                    return measurer();
                }
                break;
                
      
            case 2:
                // Echo to the left.
                waitingForEdgeLeft = true;
                decision.put("action", "echo");
                parameters.put("direction", turning.turnLeft(currentHeading));
                decision.put("parameters", parameters);
                state = 3;
                break;
            case 3:
                // Echo to the right.
                waitingForEdgeRight = true;
                decision.put("action", "echo");
                parameters.put("direction", turning.turnRight(currentHeading));
                decision.put("parameters", parameters);
                state = 4;
                break;
            case 4:
                // Evaluate both edge echoes.
                if (edgeLeftEcho == null || edgeRightEcho == null) {
                    // Still waiting for one or both responses.
                    break;
                } else {
                    // If at least one echo DOES NOT return "OUT_OF_RANGE", then the drone
                    // still sees ground on that side – so fly forward one step and repeat.
                    if (!"OUT_OF_RANGE".equals(edgeLeftEcho) || !"OUT_OF_RANGE".equals(edgeRightEcho)) {
                        edgeLeftEcho = null;
                        edgeRightEcho = null;
                        targetForwardSteps = 1;
                        decision.put("action", "fly");
                        state = 2;  // then repeat left echo (state 2) next.
                    } else {
                        // Both echoes report OUT_OF_RANGE.
                        // Choose the turn based on the lower range.
                        if (edgeLeftRange <= edgeRightRange) {
                            measureLeft = true;  // use left sensor
                        } else {
                            measureLeft = false; // use right sensor
                        }
                        state = 5;
                        return measurer(); // immediately go to state 5.
                    }
                }
                break;
            case 5:
                // Turn according to the chosen sensor.
                if (measureLeft) {
                    String newHeading = turning.turnLeft(currentHeading);
                    decision.put("action", "heading");
                    parameters.put("direction", newHeading);
                    decision.put("parameters", parameters);
                    currentHeading = newHeading;
                } else {
                    String newHeading = turning.turnRight(currentHeading);
                    decision.put("action", "heading");
                    parameters.put("direction", newHeading);
                    decision.put("parameters", parameters);
                    currentHeading = newHeading;
                }
                // After turning from edge detection, proceed to pre‑move.
                state = 6;
                break;
                
        
            case 6:
                // First non‑measured fly.
                targetForwardSteps = 1;
                decision.put("action", "fly");
                state = 7;
                break;
            case 7:
                // Second non‑measured fly.
                targetForwardSteps = 1;
                decision.put("action", "fly");
                state = 8;
                break;
                
            
            case 8:
                // Echo using the sensor selected (if measureLeft true, use left; else use right).
                sensorDirection = measureLeft ? turning.turnLeft(currentHeading)
                                              : turning.turnRight(currentHeading);
                decision.put("action", "echo");
                parameters.put("direction", sensorDirection);
                decision.put("parameters", parameters);
                // Wait in state 8 for updateState to decide the next move.
                break;
            case 9:
                // Based on updateState, if alignment echo did not return OUT_OF_RANGE,
                // fly one step (non‑measured), then repeat alignment.
                if (targetForwardSteps > 0) {
                    decision.put("action", "fly");
                    targetForwardSteps--;
                } else {
                    state = 8;
                    return measurer();
                }
                break;
            case 10:
                // Alignment complete: Turn toward that sensor direction.
                sensorDirection = measureLeft ? turning.turnLeft(currentHeading)
                                              : turning.turnRight(currentHeading);
                decision.put("action", "heading");
                parameters.put("direction", sensorDirection);
                decision.put("parameters", parameters);
                currentHeading = sensorDirection;
                state = 11;
                break;
                
         
            case 11:
                // First pre‑move (non‑counted).
                targetForwardSteps = 1;
                decision.put("action", "fly");
                state = 12;
                break;
            case 12:
                // Second pre‑move (non‑counted).
                targetForwardSteps = 1;
                decision.put("action", "fly");
                state = 13;
                break;
                
       
            case 13:
                // Issue an echo using the same sensor.
                sensorDirection = measureLeft ? turning.turnLeft(currentHeading)
                                              : turning.turnRight(currentHeading);
                decision.put("action", "echo");
                parameters.put("direction", sensorDirection);
                decision.put("parameters", parameters);
                // updateState for state 13 will process the response.
                break;
            case 14:
                // Fly one step that counts toward length.
                if (targetForwardSteps > 0) {
                    decision.put("action", "fly");
                    targetForwardSteps--;
                } else {
                    state = 13;
                    return measurer();
                }
                break;
                
    
            case 15:
                // Turn again in the same relative direction.
                sensorDirection = measureLeft ? turning.turnLeft(currentHeading)
                                              : turning.turnRight(currentHeading);
                decision.put("action", "heading");
                parameters.put("direction", sensorDirection);
                decision.put("parameters", parameters);
                currentHeading = sensorDirection;
                state = 16;
                break;
            case 16:
                // Pre‑move for measurement 2: first fly (non‑counted).
                targetForwardSteps = 1;
                decision.put("action", "fly");
                state = 17;
                break;
            case 17:
                // Pre‑move for measurement 2: second fly.
                targetForwardSteps = 1;
                decision.put("action", "fly");
                state = 18;
                break;
                
           
            case 18:
                // Echo on the same sensor.
                sensorDirection = measureLeft ? turning.turnLeft(currentHeading)
                                              : turning.turnRight(currentHeading);
                decision.put("action", "echo");
                parameters.put("direction", sensorDirection);
                decision.put("parameters", parameters);
                break;
            case 19:
                // Fly one measured step (non‑pre‐move) for width.
                if (targetForwardSteps > 0) {
                    decision.put("action", "fly");
                    targetForwardSteps--;
                } else {
                    state = 18;
                    return measurer();
                }
                break;
                
          
            case 20:
                decision.put("action", "stop");
                break;
            default:
                decision.put("action", "stop");
        }
        
        return decision;
    }
    

    public void updateState(JSONObject response) {
        JSONObject extras = response.getJSONObject("extras");
        if (!extras.has("found") || !extras.has("range"))
            return;
        
        String found = extras.getString("found");
        int range = extras.getInt("range");
        
        // --- Handle waiting flags for edge detection ---
        if (waitingForEdgeLeft) {
            edgeLeftEcho = found;
            edgeLeftRange = range;
            waitingForEdgeLeft = false;
            return;
        } else if (waitingForEdgeRight) {
            edgeRightEcho = found;
            edgeRightRange = range;
            waitingForEdgeRight = false;
            return;
        }
        
        // --- Process echo responses based on current state ---
        switch (state) {
            case 0:
                // Response from the *initial* forward echo.
                targetForwardSteps = range;
                // Now that we've received the echo response, move to state 1.
                state = 1;
                break;
            case 8:
                // Alignment echo for dimension 1.
                if ("OUT_OF_RANGE".equals(found)) {
                    // Alignment complete.
                    state = 10;
                } else {
                    // Still sees ground: schedule a non-measured fly.
                    targetForwardSteps = 1;
                    state = 9;
                }
                break;
            case 13:
                // Measurement cycle for dimension 1.
                if ("OUT_OF_RANGE".equals(found)) {
                    // Measurement complete for dimension 1.
                    state = 15;
                } else {
                    measuredLength++;
                    targetForwardSteps = 1;
                    state = 14;
                }
                break;
            case 18:
                // Measurement cycle for dimension 2.
                if ("OUT_OF_RANGE".equals(found)) {
                    // Measurement complete – finish.
                    state = 20;
                } else {
                    measuredWidth++;
                    targetForwardSteps = 1;
                    state = 19;
                }
                break;
            default:
                break;
            }
    }

    

    public int getMeasuredLength() {
        return measuredLength;
    }
    
    public int getMeasuredWidth() {
        return measuredWidth;
    }
}


