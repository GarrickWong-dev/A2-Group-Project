package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class FindIsland implements Search 
{
    //current orientation and process variables
    private String currentHeading;
    private int scanStep = 0;
    private boolean waitingForFly = false;
    private boolean waitingForStop = false;
    private boolean firstCycle = true;
    private boolean process = false;

    //directions used in echo scans
    private String forwardDir;
    private String rightDir;
    private String leftDir;

    private final Turning turning = new Turning();
    private final Actions actions;

    private final EchoProcessor echoProcessor = new EchoProcessor();

    public FindIsland(String initHeading, Actions actions) {
        this.currentHeading = initHeading;
        this.actions = actions;
    }
    
    @Override
    public JSONObject search() {
        JSONObject decision = new JSONObject();
        
        if (waitingForFly) 
        {
            actions.moveForward(decision);
            waitingForFly = false;
            return decision;
        }
        if (waitingForStop) 
        {
            waitingForStop = false;
            process = true;
            return decision;
        }
        
        //echo forward
        if (scanStep == 0) 
        {
            forwardDir = currentHeading;
            return DecisionBuilder.createEchoDecision(forwardDir);
        } 
        //echo right
        else if (scanStep == 1) 
        {
            rightDir = turning.turnRight(currentHeading);
            return DecisionBuilder.createEchoDecision(rightDir);
        }  
        //echo left
        else if (scanStep == 2) 
        {
            leftDir = turning.turnLeft(currentHeading);
            return DecisionBuilder.createEchoDecision(leftDir);
        } 
        //processing echo responses
        else if (scanStep == 3) 
        {
            if (echoProcessor.hasGroundDetected()) 
            {
                String chosenDir = echoProcessor.chooseBestDirection();
                //if lowest ground range is ahead
                if (currentHeading.equals(chosenDir)) 
                {
                    process = true;
                } 
                else 
                {
                    //turn towards the ground range with lowest range
                    if (chosenDir.equals(turning.turnRight(currentHeading))) 
                    {
                        actions.turnRight(decision);
                    } 
                    else if (chosenDir.equals(turning.turnLeft(currentHeading))) 
                    {
                        actions.turnLeft(decision);
                    }
                    currentHeading = chosenDir;
                    waitingForStop = true;
                }
                resetScanState();
                return decision;
            } 
            //no gorund detected
            else 
            {
                //turn towards direction that has greatest range
                int diagRightScore = echoProcessor.getRightRange();
                int diagLeftScore  = echoProcessor.getLeftRange();
                
                if (diagRightScore >= diagLeftScore) 
                {
                    String newHeading = turning.turnRight(currentHeading);
                    if (currentHeading.equals(newHeading)) 
                    {
                        actions.moveForward(decision);
                    } 
                    else 
                    {
                        actions.turnRight(decision);
                        currentHeading = newHeading;
                        waitingForFly = true;
                    }
                } 
                else 
                {
                    String newHeading = turning.turnLeft(currentHeading);
                    if (currentHeading.equals(newHeading)) 
                    {
                        actions.moveForward(decision);
                    } 
                    else 
                    {
                        actions.turnLeft(decision);
                        currentHeading = newHeading;
                        waitingForFly = true;
                    }
                }
                resetScanState();
                return decision;
            }
        }
        process = true;
        return decision;
    }
     
    public void updateState(JSONObject response) 
    {
        JSONObject extras = response.getJSONObject("extras");
        if (!extras.has("found") || !extras.has("range")) 
        {
            return;
        }
        String found = extras.getString("found");
        int range = extras.getInt("range");
        
        //update echo data based on the current scan step
        if (scanStep == 0) 
        {
            echoProcessor.updateEcho(scanStep, forwardDir, found, range);
            scanStep = 1;
        } 
        else if (scanStep == 1) 
        {
            echoProcessor.updateEcho(scanStep, rightDir, found, range);
            scanStep = 2;
        } 
        else if (scanStep == 2) 
        {
            echoProcessor.updateEcho(scanStep, leftDir, found, range);
            scanStep = 3;
        }
    }

    private void resetScanState() {
        scanStep = 0;
        echoProcessor.reset();
        firstCycle = false;
    }
    
    public boolean processDone() {
        return process;
    }

    public String getCurrentHeading() {
        return currentHeading;
    }
}
