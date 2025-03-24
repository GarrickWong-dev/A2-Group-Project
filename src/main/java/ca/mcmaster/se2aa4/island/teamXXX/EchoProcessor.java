package ca.mcmaster.se2aa4.island.teamXXX;

public class EchoProcessor {

    // Class to hold the echo reading results
    public static class EchoData {
        public final String direction; // Direction of the echo (e.g., "forward", "right", "left")
        public final String found; // What the echo found ("GROUND", "EMPTY", etc.)
        public final int range; // The range (distance) at which the echo was detected

        // Constructor to initialize EchoData with direction, found status, and range
        public EchoData(String direction, String found, int range) {
            this.direction = direction;
            this.found = found;
            this.range = range;
        }
    }

    // Variables to store the echo readings for each direction
    private EchoData forwardEcho;
    private EchoData rightEcho;
    private EchoData leftEcho;

    // Updates the echo result for a particular step (forward, right, left)
    public void updateEcho(int step, String direction, String found, int range) {
        EchoData echo = new EchoData(direction, found, range);
        if (step == 0) {
            forwardEcho = echo;
        } else if (step == 1) {
            rightEcho = echo;
        } else if (step == 2) {
            leftEcho = echo;
        }
    }

    // Returns true if any echo detects "GROUND"
    public boolean hasGroundDetected() {
        return (forwardEcho != null && "GROUND".equals(forwardEcho.found)) ||
               (rightEcho != null && "GROUND".equals(rightEcho.found)) ||
               (leftEcho != null && "GROUND".equals(leftEcho.found));
    }

    // Chooses the direction with the smallest range where "GROUND" was detected
    public String chooseBestDirection() {
        String chosenDir = null;
        int chosenRange = Integer.MAX_VALUE;

        // Check each direction's echo and select the one with the smallest range
        if (forwardEcho != null && "GROUND".equals(forwardEcho.found) && forwardEcho.range < chosenRange) {
            chosenDir = forwardEcho.direction;
            chosenRange = forwardEcho.range;
        }
        if (rightEcho != null && "GROUND".equals(rightEcho.found) && rightEcho.range < chosenRange) {
            chosenDir = rightEcho.direction;
            chosenRange = rightEcho.range;
        }
        if (leftEcho != null && "GROUND".equals(leftEcho.found) && leftEcho.range < chosenRange) {
            chosenDir = leftEcho.direction;
            chosenRange = leftEcho.range;
        }
        return chosenDir;
    }

    // Gets the range of the right echo, or 0 if not available
    public int getRightRange() {
        if (rightEcho != null) {
            return rightEcho.range;
        } else {
            return 0;
        }
    }

    // Gets the range of the left echo, or 0 if not available
    public int getLeftRange() {
        if (leftEcho != null) {
            return leftEcho.range;
        } else {
            return 0;
        }
    }

    // Resets the echo information
    public void reset() {
        forwardEcho = null;
        rightEcho = null;
        leftEcho = null;
    }
}
