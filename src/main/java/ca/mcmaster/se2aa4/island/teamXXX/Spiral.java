package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

// The Spiral class implements the Search interface and is responsible for performing a spiral search pattern.
public class Spiral implements Search {
    private final Logger logger = LogManager.getLogger(); // Logger for debugging and tracking
    private final Actions actions = Actions.getInstance(); // Actions instance for controlling movement
    private int spiralStep = 0; // Initial movement length in the spiral pattern
    private int movesInCurrentStep = 0; // Tracks moves in the current spiral segment
    private int turnsInCurrentLayer = 0; // Tracks turns in the current layer of the spiral
    private boolean scanFlag = true; // Flag to determine when to scan
    private boolean turnState = true; // Flag to determine if a turn or move is needed
    private IslandDimensions islandDimensions; // Reference to the IslandDimensions object
    private static Spiral instance; // Singleton instance of Spiral class

    // Private constructor for the Spiral class that takes an IslandDimensions object
    private Spiral(IslandDimensions islandDimensions) {
        this.islandDimensions = islandDimensions;
    }

    // Singleton pattern: ensures only one instance of Spiral is created
    public static Spiral getInstance(IslandDimensions islandDimensions) {
        if (instance == null) {
            instance = new Spiral(islandDimensions);
        }
        return instance;
    }

    // The search method performs the spiral search pattern
    @Override
    public JSONObject search() {
        JSONObject decision = new JSONObject();
        Drone drone = Drone.getInstance(); // Get the current drone instance

        // Check if the drone is at the first or last turning point, and stop if so
        if (drone.getCoordinates() == islandDimensions.getFirstTurning() || drone.getCoordinates() == islandDimensions.getLastTurning()) {
            decision.put("action", "stop"); // Stop the drone if at a boundary
            return decision;
        }

        // Perform a scan between movements
        if (scanFlag) {
            decision.put("action", "scan"); // Initiate scan action
            scanFlag = false; // Reset the scan flag after scanning
        } else {
            if (turnState || spiralStep == 0) {
                // Perform a left turn in the spiral pattern
                actions.turnLeft(decision);
                turnsInCurrentLayer++; // Increment the number of turns in the current layer

                // Increase movement length after certain conditions are met
                if (spiralStep == 0 && turnsInCurrentLayer >= 3 || turnsInCurrentLayer >= 2 && spiralStep != 0) {
                    spiralStep++; // Increase the movement length for the next segment of the spiral
                    turnsInCurrentLayer = 0; // Reset the turn counter for the next layer
                }
                turnState = false; // Indicate that the turn is complete
            } else {
                // Move forward in the spiral pattern
                actions.moveForward(decision);
                movesInCurrentStep++; // Track the number of moves made in the current segment

                // Check if the current spiral segment is complete
                if (movesInCurrentStep >= spiralStep) {
                    turnState = true; // Indicate that it's time for the next turn
                    movesInCurrentStep = 0; // Reset the move counter for the next segment
                }
            }
            scanFlag = true; // Reset the scan flag after performing a move or turn
        }

        return decision; // Return the decision containing the current action
    }
}
