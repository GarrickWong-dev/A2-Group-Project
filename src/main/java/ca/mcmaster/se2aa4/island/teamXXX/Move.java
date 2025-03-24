package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Move implements Movement{
    // Indicates if we have already performed the turning for the y phase.
    private static Move instance;
    private boolean hasTurned = false, completed = false;
    private final Actions actions = Actions.getInstance();
    private final Drone drone = Drone.getInstance();
    private final Logger logger = LogManager.getLogger();

    private Move() {
    }

    public static Move getInstance(){
        if (instance == null) {
            instance = new Move();
        }
        return instance;
    }

    @Override
    public JSONObject move(Coordinates target) {
        JSONObject decision = new JSONObject();
        Coordinates current = drone.getCoordinates();

        int dx = target.getX() - current.getX();
        int dy = target.getY() - current.getY();

        // Determine target directions.
        Direction targetXDir = (dx > 0) ? Direction.EAST : Direction.WEST;
        Direction targetYDir = (dy > 0) ? Direction.NORTH : Direction.SOUTH;

        // Phase 1: Move in the x direction until within one unit of the target x.
        if (Math.abs(dx) > 1) {
            if (!drone.getFacing().equals(targetXDir)) {
                turnToDirection(decision, targetXDir);
            } else {
                actions.moveForward(decision);
            }
        } 
        // Phase 2: Once near the target x, turn (diagonally) to face the target y.
        else {
            if (!hasTurned) {
                logger.info("TURNED");
                turnToDirection(decision, targetYDir);
                hasTurned = true;
            } else {
                // Phase 3: Move forward in y direction until reaching the target.
                actions.moveForward(decision);
            }
        }

        // If already at the target, stop and reset state.
        if (current.equals(target)) {
            completed = true;
        }

        return decision;
    }

    private void turnToDirection(JSONObject decision, Direction targetDir) {
        // Determine which turn to take.
        Direction currentDir = drone.getFacing();
        int currentOrdinal = currentDir.ordinal();
        int targetOrdinal = targetDir.ordinal();

        int clockwiseTurns = (targetOrdinal - currentOrdinal + 4) % 4;
        if (clockwiseTurns <= 2) {
            actions.turnRight(decision);
        } else {
            actions.turnLeft(decision);
        }
    }

    public boolean isCompleted(){
        return completed;
    }
}
