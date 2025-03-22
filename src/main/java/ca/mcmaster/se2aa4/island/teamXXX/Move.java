package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class Move {
    // Indicates if we have already performed the turning for the y phase.
    private boolean hasTurned = false;
    private final Actions actions;
    private final Drone drone;

    public Move(Actions actions) {
        this.actions = actions;
        this.drone = Drone.getInstance();
    }

    public JSONObject move(Coordinates target) {
        JSONObject decision = new JSONObject();
        Coordinates current = drone.getCoordinates();

        // If already at the target, stop and reset state.
        if (current.equals(target)) {
            decision.put("action", "stop");
            hasTurned = false;
            return decision;
        }

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
                turnToDirection(decision, targetYDir);
                hasTurned = true;
            } else {
                // Phase 3: Move forward in y direction until reaching the target.
                actions.moveForward(decision);
            }
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
        // Removed the extra moveForward to avoid applying a double move.
    }
}
