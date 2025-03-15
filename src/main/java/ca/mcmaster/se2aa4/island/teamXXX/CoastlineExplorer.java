package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class CoastlineExplorer {
    private Turning turning = new Turning();
    private String currentHeading;
    private int width = 0;
    private int length = 0;
    private DroneSensorData sensorData;
    
    // State names as string constants
    private static final String INITIAL = "INITIAL";
    private static final String DETERMINE_CASE = "DETERMINE_CASE";
    private static final String CASE1 = "CASE1";
    private static final String CASE1_TURN2 = "CASE1_TURN2";
    private static final String MOVE_FORWARD_CASE1 = "MOVE_FORWARD_CASE1";
    private static final String CASE2 = "CASE2";
    private static final String CASE3 = "CASE3";
    private static final String CASE3_TURN2 = "CASE3_TURN2";
    private static final String MOVE_FORWARD_CASE3 = "MOVE_FORWARD_CASE3";
    private static final String CASE4 = "CASE4";
    private static final String CASE4_TURN2 = "CASE4_TURN2";
    private static final String MOVE_FORWARD_CASE4 = "MOVE_FORWARD_CASE4";
    private static final String NO_GROUND_FORWARD = "NO_GROUND_FORWARD";
    private static final String MEASURE_WIDTH = "MEASURE_WIDTH";
    private static final String PROCESS_WIDTH = "PROCESS_WIDTH";
    private static final String MEASURE_LENGTH = "MEASURE_LENGTH";
    private static final String FINISHED = "FINISHED";
    
    private String state;
    
    public CoastlineExplorer(String initialHeading) {
        this.currentHeading = initialHeading;
        state = INITIAL;
    }
    
    public void updateSensorData(String sensorResponse) {
        sensorData = new DroneSensorData(sensorResponse);
        // Determine case based on echo (ground = range 0)
        if(sensorData.isGround("forward") && sensorData.isGround("left") && sensorData.isGround("right")) {
            state = CASE1;
        } else if(sensorData.isGround("forward") && !sensorData.isGround("left") && !sensorData.isGround("right")) {
            state = CASE2;
        } else if(sensorData.isGround("forward") && sensorData.isGround("left") && !sensorData.isGround("right")) {
            state = CASE3;
        } else if(sensorData.isGround("forward") && sensorData.isGround("right") && !sensorData.isGround("left")) {
            state = CASE4;
        } else if(!sensorData.isGround("forward")) {
            state = NO_GROUND_FORWARD;
        }
    }
    
    public String getNextCommand() {
        JSONObject decision = new JSONObject();
        
        // If no sensor data yet, ask for an echo.
        if(sensorData == null) {
            decision.put("action", "ECHO");
            decision.put("directions", new String[]{"FORWARD", "LEFT", "RIGHT"});
            state = DETERMINE_CASE;
            return decision.toString();
        }
        
        if(state.equals(INITIAL)) {
            decision.put("action", "ECHO");
            decision.put("directions", new String[]{"FORWARD", "LEFT", "RIGHT"});
            state = DETERMINE_CASE;
        }
        else if(state.equals(DETERMINE_CASE)) {
            decision.put("action", "ECHO");
            decision.put("directions", new String[]{"FORWARD", "LEFT", "RIGHT"});
        }
        else if(state.equals(CASE1)) {
            // Case 1: All echoes report ground → turn 180° (turn right twice)
            decision.put("action", "TURN");
            decision.put("direction", "RIGHT");
            currentHeading = turning.turnRight(currentHeading);
            state = CASE1_TURN2;
        }
        else if(state.equals(CASE1_TURN2)) {
            decision.put("action", "TURN");
            decision.put("direction", "RIGHT");
            currentHeading = turning.turnRight(currentHeading);
            state = MOVE_FORWARD_CASE1;
        }
        else if(state.equals(MOVE_FORWARD_CASE1)) {
            decision.put("action", "MOVE");
            decision.put("direction", currentHeading);
            state = MEASURE_WIDTH;
        }
        else if(state.equals(CASE2)) {
            // Case 2: Only forward echo has ground – simply move forward.
            decision.put("action", "MOVE");
            decision.put("direction", currentHeading);
            state = MEASURE_WIDTH;
        }
        else if(state.equals(CASE3)) {
            // Case 3: Ground ahead and on left.
            decision.put("action", "TURN");
            decision.put("direction", "RIGHT");
            currentHeading = turning.turnRight(currentHeading);
            state = CASE3_TURN2;
        }
        else if(state.equals(CASE3_TURN2)) {
            decision.put("action", "TURN");
            decision.put("direction", "RIGHT");
            currentHeading = turning.turnRight(currentHeading);
            state = MOVE_FORWARD_CASE3;
        }
        else if(state.equals(MOVE_FORWARD_CASE3)) {
            decision.put("action", "MOVE");
            decision.put("direction", currentHeading);
            state = MEASURE_WIDTH;
        }
        else if(state.equals(CASE4)) {
            // Case 4: Ground ahead and on right.
            decision.put("action", "TURN");
            decision.put("direction", "LEFT");
            currentHeading = turning.turnLeft(currentHeading);
            state = CASE4_TURN2;
        }
        else if(state.equals(CASE4_TURN2)) {
            decision.put("action", "TURN");
            decision.put("direction", "LEFT");
            currentHeading = turning.turnLeft(currentHeading);
            state = MOVE_FORWARD_CASE4;
        }
        else if(state.equals(MOVE_FORWARD_CASE4)) {
            decision.put("action", "MOVE");
            decision.put("direction", currentHeading);
            state = MEASURE_WIDTH;
        }
        else if(state.equals(NO_GROUND_FORWARD)) {
            // If forward echo is not ground, move forward until a lateral echo shows out-of-range.
            decision.put("action", "MOVE");
            decision.put("direction", currentHeading);
            state = MEASURE_WIDTH;
        }
        else if(state.equals(MEASURE_WIDTH)) {
            // Echo laterally to decide which side to use for measurement.
            int leftRange = sensorData.getRange("left");
            int rightRange = sensorData.getRange("right");
            String measureDirection = (rightRange < leftRange) ? "RIGHT" : "LEFT";
            decision.put("action", "ECHO");
            decision.put("directions", new String[]{measureDirection});
            state = PROCESS_WIDTH;
        }
        else if(state.equals(PROCESS_WIDTH)) {
            // Simulate incrementing width each time the echo is valid.
            width++;
            // For example purposes, measure width for 5 steps.
            if(width < 5) {
                decision.put("action", "MOVE");
                decision.put("direction", currentHeading);
                state = MEASURE_WIDTH;
            } else {
                state = MEASURE_LENGTH;
                decision.put("action", "ECHO");
                decision.put("directions", new String[]{"FORWARD"});
            }
        }
        else if(state.equals(MEASURE_LENGTH)) {
            // Simulate measuring length (again, 5 steps for this example).
            length++;
            if(length < 5) {
                decision.put("action", "MOVE");
                decision.put("direction", currentHeading);
                state = MEASURE_LENGTH;
            } else {
                state = FINISHED;
                decision.put("action", "STOP");
            }
        }
        else if(state.equals(FINISHED)) {
            decision.put("action", "STOP");
        }
        else {
            decision.put("action", "ECHO");
            decision.put("directions", new String[]{"FORWARD", "LEFT", "RIGHT"});
        }
        
        return decision.toString();
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getLength() {
        return length;
    }
}