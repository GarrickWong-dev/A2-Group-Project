package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Move implements Movement {
    // Singleton pattern to ensure only one instance of Move exists
    private static Move instance;

    // Track if a turn has been completed and if the movement is completed
    private boolean hasTurned = false, completed = false;

    // Accessor objects for actions and drone
    private final Actions actions = Actions.getInstance();
    private final Drone drone = Drone.getInstance();
    private final Logger logger = LogManager.getLogger();

    // Private constructor to prevent external instantiation
    private Move() {}

    // Get the single instance of Move
    public static Move getInstance(){
        if (instance == null) {
            instance = new Move();
        }
        return instance;
    }

    // Method to move the drone towards the target coordinates
    @Override
    public JSONObject move(Coordinates target) {
        JSONObject decision = new JSONObject();
        Coordinates current = drone.getCoordinates();

        // Calculate the difference in x and y coordinates
        int dx = target.getX() - current.getX();
        int dy = target.getY() - current.getY();

        // Determine the target directions based on the differences in coordinates
        Direction targetXDir = (dx > 0) ? Direction.EAST : Direction.WEST;
        Direction targetYDir = (dy > 0) ? Direction.NORTH : Direction.SOUTH;

        // Phase 1: Move in the x direction until we are within one unit of the target x
        if (Math.abs(dx) > 1) {
            if (!drone.getFacing().equals(targetXDir)) {
                turnToDirection(decision, targetXDir);  // Turn to face the x direction
            } else {
                actions.moveForward(decision);  // Move forward in the x direction
            }
        } 
        // Phase 2: Once near the target x, turn towards the target y
        else {
            if (!hasTurned) {
                logger.info("TURNED");
                turnToDirection(decision, targetYDir);  // Turn to face the y direction
                hasTurned = true;  // Mark the turn as done
            } else {
                // Phase 3: Move forward in the y direction until the target is reached
                actions.moveForward(decision);
            }
        }

        // If the drone has reached the target coordinates, mark the movement as completed
        if (current.equals(target)) {
            completed = true;
        }

        return decision;
    }

    // Helper method to turn the drone towards the desired direction
    private void turnToDirection(JSONObject decision, Direction targetDir) {
        // Get the current and target directions
        Direction currentDir = drone.getFacing();
        int currentOrdinal = currentDir.ordinal();
        int targetOrdinal = targetDir.ordinal();

        // Calculate the number of clockwise turns required
        int clockwiseTurns = (targetOrdinal - currentOrdinal + 4) % 4;

        // Turn right or left based on the shortest path
        if (clockwiseTurns <= 2) {
            actions.turnRight(decision);  // Turn right if it's faster
        } else {
            actions.turnLeft(decision);   // Turn left otherwise
        }
    }

    // Check if the movement is completed
    public boolean isCompleted(){
        return completed;
    }
}
