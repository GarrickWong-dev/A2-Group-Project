package ca.mcmaster.se2aa4.island.teamXXX;

public class EchoProcessor {

    //echo reading.
    public static class EchoData 
    {
        public final String direction;
        public final String found;
        public final int range;

        public EchoData(String direction, String found, int range) 
        {
            this.direction = direction;
            this.found = found;
            this.range = range;
        }
    }

    private EchoData forwardEcho;
    private EchoData rightEcho;
    private EchoData leftEcho;
    
    //updates the echo result for one of the scan steps
    public void updateEcho(int step, String direction, String found, int range) 
    {
        EchoData echo = new EchoData(direction, found, range);
        if (step == 0) 
        {
            forwardEcho = echo;
        } 
        else if (step == 1) 
        {
            rightEcho = echo;
        } 
        else if (step == 2) 
        {
            leftEcho = echo;
        }
    }
    
    //returns true if any echo detects "GROUND"
    public boolean hasGroundDetected() 
    {
        return (forwardEcho != null && "GROUND".equals(forwardEcho.found)) || (rightEcho != null && "GROUND".equals(rightEcho.found)) || (leftEcho != null && "GROUND".equals(leftEcho.found));
    }
    
    //chooses the direction of the echo reporting "GROUND" with the smallest range
    public String chooseBestDirection() 
    {
        String chosenDir = null;
        int chosenRange = Integer.MAX_VALUE;
        
        if (forwardEcho != null && "GROUND".equals(forwardEcho.found) && forwardEcho.range < chosenRange) 
        {
            chosenDir = forwardEcho.direction;
            chosenRange = forwardEcho.range;
        }
        if (rightEcho != null && "GROUND".equals(rightEcho.found) && rightEcho.range < chosenRange) 
        {
            chosenDir = rightEcho.direction;
            chosenRange = rightEcho.range;
        }
        if (leftEcho != null && "GROUND".equals(leftEcho.found) && leftEcho.range < chosenRange) 
        {
            chosenDir = leftEcho.direction;
            chosenRange = leftEcho.range;
        }
        return chosenDir;
    }
    
    public int getRightRange() 
    {
        if (rightEcho != null) 
        {
            return rightEcho.range;
        } 
        else 
        {
        return 0;
        }
    }

    public int getLeftRange() 
    {
        if (leftEcho != null) 
        {
            return leftEcho.range;
        } 
        else 
        {
            return 0;
        }
    }
    
    //resets the echo information
    public void reset() {
        forwardEcho = null;
        rightEcho = null;
        leftEcho = null;
    }
}
