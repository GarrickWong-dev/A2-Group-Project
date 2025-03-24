package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class IslandDimensions implements Search {

    private int state = 0; // State variable to track the current phase of the process
    private String currentHeading; // Current heading/direction the drone is facing
    private final Actions actions; // Instance of the Actions class for controlling the drone
    private String echoHeading;  // Direction where the drone is currently scanning for echoes
    private final Drone drone; // Instance of the Drone class
    private TurnContainer turnContainer = new TurnContainer(); // Container to store turning coordinates

    private int measuredWidth = 0; // Measured width of the island
    private int measuredLength = 0; // Measured length of the island
    private int forwardCount = 0; // Counter to track the number of moves forward

    private boolean waitingForWidthEcho = true; // Flag to check if the width echo is awaited
    private boolean waitingForLengthEcho = true; // Flag to check if the length echo is awaited
    private String lengthEchoDirection = null; // Direction for length echo
    private final String originalHeading; // Original heading of the drone
    private boolean process = false; // Flag to indicate if the process is complete

    // Constructor for initializing the IslandDimensions instance
    public IslandDimensions(String initialHeading, String echoHeading, Actions actions, Drone drone) {
        this.originalHeading = initialHeading; // Store the original heading
        this.currentHeading = initialHeading; // Set current heading to the initial heading
        this.echoHeading = echoHeading; // Set echo heading
        this.actions = actions; // Assign actions instance
        this.drone = drone; // Assign drone instance
    }

    // Main search method that controls the step-by-step process of measuring the island's dimensions
    @Override
    public JSONObject search() {
        JSONObject decision = new JSONObject();
        
        // Loop through the states to perform different actions based on the current state
        while (true) {
            switch (state) {
                case 0:
                    // Turn towards the width direction (based on the echo heading)
                    turnContainer.add(new Coordinates(drone.getCoordinates().getX(), drone.getCoordinates().getY()));
                    if (actions.getRight().equals(echoHeading)) {
                        actions.turnRight(decision);
                        currentHeading = echoHeading;
                        echoHeading = actions.getRight();
                    } else {
                        actions.turnLeft(decision);
                        currentHeading = echoHeading;
                        echoHeading = actions.getLeft();
                    }
                    state = 1;
                    return decision;

                case 1:
                    // Move forward twice before starting width measurement
                    if (forwardCount < 2) {
                        actions.moveForward(decision);
                        forwardCount++;
                        return decision;
                    } else {
                        forwardCount = 0;
                        state = 2;
                        break;
                    }

                case 2:
                    // Measure width by checking the echo
                    if (waitingForWidthEcho) {
                        decision = DecisionBuilder.createEchoDecision(echoHeading); // Request echo measurement
                        waitingForWidthEcho = false; // Set flag to false to avoid repeated echo requests
                        return decision;
                    } else {
                        actions.moveForward(decision); // Move forward to measure width
                        measuredWidth++; // Increment measured width
                        waitingForWidthEcho = true; // Reset flag to wait for the next echo
                        return decision;
                    }

                case 3:
                    // Turn towards the length direction (based on the echo heading)
                    if (actions.getRight().equals(echoHeading)) {
                        actions.turnRight(decision);
                        currentHeading = echoHeading;
                        lengthEchoDirection = actions.getRight();
                    } else if (actions.getLeft().equals(echoHeading)) {
                        actions.turnLeft(decision);
                        currentHeading = echoHeading;
                        lengthEchoDirection = actions.getLeft();
                    } else {
                        // Fallback option if the echo heading doesn't match expected values
                        actions.turnLeft(decision);
                        currentHeading = echoHeading;
                        lengthEchoDirection = actions.getLeft();
                    }
                    state = 4;
                    return decision;

                case 4:
                    // Move forward twice before starting the length measurement
                    if (forwardCount < 2) {
                        actions.moveForward(decision);
                        forwardCount++;
                        return decision;
                    } else {
                        forwardCount = 0;
                        state = 5;
                        break;
                    }

                case 5:
                    // Measure length by checking the echo
                    if (waitingForLengthEcho) {
                        decision = DecisionBuilder.createEchoDecision(lengthEchoDirection); // Request echo measurement
                        waitingForLengthEcho = false; // Set flag to false to avoid repeated echo requests
                        return decision;
                    } else {
                        actions.moveForward(decision); // Move forward to measure length
                        measuredLength++; // Increment measured length
                        waitingForLengthEcho = true; // Reset flag to wait for the next echo
                        return decision;
                    }

                case 6:
                    // End coordinates after completing the measurement process
                    turnContainer.add(new Coordinates(drone.getCoordinates().getX(), drone.getCoordinates().getY()));
                    process = true; // Mark process as done
                    return decision;

                default:
                    turnContainer.add(new Coordinates(drone.getCoordinates().getX(), drone.getCoordinates().getY()));
                    // Finalize the process
                    process = true;
                    return decision;
            }
        }
    }

    // Update the state based on the response received (echo feedback)
    public void updateState(JSONObject response) {
        JSONObject extras = response.getJSONObject("extras");

        // If no "found" or "range" data is available, return without updating
        if (!extras.has("found") || !extras.has("range")) {
            return;
        }

        String found = extras.getString("found");

        // Handling echo data for width measurement
        if (state == 2 && !waitingForWidthEcho) {
            if ("OUT_OF_RANGE".equals(found)) {
                state = 3; // No echo detected, move to length measurement phase
            }
        }
        // Handling echo data for length measurement
        else if (state == 5 && !waitingForLengthEcho) {
            if ("OUT_OF_RANGE".equals(found)) {
                state = 6; // No echo detected, end the measurement process
            }
        }
    }

    // Getter for the measured length of the island
    public int getMeasuredLength() {
        return measuredLength;
    }

    // Getter for the measured width of the island
    public int getMeasuredWidth() {
        return measuredWidth;
    }

    // Check if the process of measuring the island's dimensions is complete
    public boolean processDone() {
        return process;
    }

    // Getter for the first turning coordinates during the measurement process
    public Coordinates getFirstTurning() {
        return turnContainer.getFirstTurning();
    }

    // Getter for the last turning coordinates during the measurement process
    public Coordinates getLastTurning() {
        return turnContainer.getLastTurning();
    }

    // Calculate the mid-point coordinates of the measured island
    public Coordinates getMidCoordinates() {
        int xCoord = (turnContainer.getFirstTurning().getX() + turnContainer.getLastTurning().getX() + 1) / 2;
        int yCoord = (turnContainer.getFirstTurning().getY() + turnContainer.getLastTurning().getY() + 1) / 2;
        return new Coordinates(xCoord, yCoord); // Return the midpoint of the first and last turn coordinates
    }
}
