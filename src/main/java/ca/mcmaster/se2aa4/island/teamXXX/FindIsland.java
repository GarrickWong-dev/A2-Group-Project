package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class FindIsland implements Search {

    // Current orientation and scan cycle variables
    private String currentHeading; // The current heading of the drone
    private int scanStep = 0; // Tracks the current step in the scanning process
    private boolean waitingForFly = false; // Determines if the drone is waiting to fly
    private boolean firstCycle = true; // Flag to track if this is the first cycle of scanning
    private boolean process = false; // Indicates if the island finding process is complete

    // Directions used in echo scans (forward, right, left)
    private String forwardDir;
    private String rightDir;
    private String leftDir;

    private final Actions actions; // Instance of the Actions class (to control the drone)
    private static FindIsland instance; // Singleton instance of the FindIsland class
    private final EchoProcessor echoProcessor = new EchoProcessor(); // Processor for handling echo responses

    // Private constructor for the Singleton pattern
    private FindIsland(String initHeading, Actions actions) {
        this.currentHeading = initHeading;
        this.actions = actions;
    }

    // Singleton getter method for the FindIsland instance
    public static FindIsland getInstance(String initHeading, Actions actions) {
        if (instance == null) {
            instance = new FindIsland(initHeading, actions);
        }
        return instance;
    }

    // Main method that handles the search logic in a step-by-step manner
    @Override
    public JSONObject search() {
        JSONObject decision = new JSONObject();

        // Step 4 is an additional check to ensure ground is detected in front
        if (scanStep == 4) {
            return DecisionBuilder.createEchoDecision(currentHeading); // Check for echo in front direction
        }

        // If the drone is waiting to fly (after turning), it moves forward
        if (waitingForFly) {
            actions.moveForward(decision);
            waitingForFly = false; // Reset waiting for fly flag
            return decision;
        }

        // Echo scans for different directions
        if (scanStep == 0) { // Forward scan
            forwardDir = currentHeading;
            return DecisionBuilder.createEchoDecision(forwardDir); // Echo scan forward
        } else if (scanStep == 1) { // Right scan
            rightDir = actions.getRight(); // Get the direction to the right of the current heading
            return DecisionBuilder.createEchoDecision(rightDir); // Echo scan right
        } else if (scanStep == 2) { // Left scan
            leftDir = actions.getLeft(); // Get the direction to the left of the current heading
            return DecisionBuilder.createEchoDecision(leftDir); // Echo scan left
        } else if (scanStep == 3) { // Process the results of the echo scans
            if (echoProcessor.hasGroundDetected()) {
                String chosenDir = echoProcessor.chooseBestDirection(); // Choose the direction with the closest ground detected
                if (currentHeading.equals(chosenDir)) {
                    process = true; // Ground detected in front, process complete
                    resetScanState();
                    return decision;
                } else {
                    // Turn towards the direction with the closest ground
                    if (chosenDir.equals(actions.getRight())) {
                        actions.turnRight(decision);
                    } else if (chosenDir.equals(actions.getLeft())) {
                        actions.turnLeft(decision);
                    }
                    currentHeading = chosenDir;
                    scanStep = 4; // Move to the next step to verify ground in the chosen direction
                    return decision;
                }
            } else { // If no ground is detected, choose the direction with the farthest range
                int diagRightScore = echoProcessor.getRightRange();
                int diagLeftScore = echoProcessor.getLeftRange();

                if (diagRightScore >= diagLeftScore) {
                    String newHeading = actions.getRight(); // Choose the right direction if it has a greater range
                    if (currentHeading.equals(newHeading)) {
                        actions.moveForward(decision); // Move forward if already heading right
                    } else {
                        actions.turnRight(decision); // Turn right to face the right direction
                        currentHeading = newHeading;
                        waitingForFly = true; // Wait for fly action
                    }
                } else {
                    String newHeading = actions.getLeft(); // Choose the left direction if it has a greater range
                    if (currentHeading.equals(newHeading)) {
                        actions.moveForward(decision); // Move forward if already heading left
                    } else {
                        actions.turnLeft(decision); // Turn left to face the left direction
                        currentHeading = newHeading;
                        waitingForFly = true; // Wait for fly action
                    }
                }
                resetScanState(); // Reset scan after making a decision
                return decision;
            }
        }
        process = true; // Mark the process as done once all scans are processed
        return decision;
    }

    // Update the state based on the response from the echo scan
    public void updateState(JSONObject response) {
        JSONObject extras = response.getJSONObject("extras");

        // If it's the final step (step 4), check for ground
        if (scanStep == 4) {
            if (extras.has("found") && extras.has("range")) {
                String found = extras.getString("found");
                if ("ground".equals(found)) { // Ground found in front, process done
                    process = true;
                } else {
                    resetScanState(); // No ground, reset scan state
                }
            }
            scanStep = 0; // Reset scan step for next cycle
            return;
        }

        // If no "found" or "range" data, skip
        if (!extras.has("found") || !extras.has("range")) {
            return;
        }

        String found = extras.getString("found");
        int range = extras.getInt("range");

        // Update echo data based on the current scan step
        if (scanStep == 0) {
            echoProcessor.updateEcho(scanStep, forwardDir, found, range);
            scanStep = 1; // Move to the next scan step (right)
        } else if (scanStep == 1) {
            echoProcessor.updateEcho(scanStep, rightDir, found, range);
            scanStep = 2; // Move to the next scan step (left)
        } else if (scanStep == 2) {
            echoProcessor.updateEcho(scanStep, leftDir, found, range);
            scanStep = 3; // Process the results of the scan
        }
    }

    // Reset the scan state to start a fresh scan cycle
    private void resetScanState() {
        scanStep = 0; // Reset to the initial step of scanning
        echoProcessor.reset(); // Reset the echo processor
        firstCycle = false; // Set first cycle flag to false
    }

    // Returns whether the process of finding the island is done
    public boolean processDone() {
        return process;
    }

    // Getter for the current heading of the drone
    public String getCurrentHeading() {
        return currentHeading;
    }
}
