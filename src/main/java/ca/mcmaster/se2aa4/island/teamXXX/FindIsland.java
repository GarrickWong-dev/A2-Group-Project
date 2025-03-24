package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class FindIsland implements Search 
{
    //current orientation and process variables
    private String currentHeading;
    private int scanStep = 0;
    private boolean waitingForFly = false;
    private boolean firstCycle = true;
    private boolean process = false;

    //directions used in echo scans
    private String forwardDir;
    private String rightDir;
    private String leftDir;

    private final Actions actions;
    private static FindIsland instance;
    private final EchoProcessor echoProcessor = new EchoProcessor();

    private FindIsland(String initHeading, Actions actions) {
        this.currentHeading = initHeading;
        this.actions = actions;
    }

    public static FindIsland getInstance(String initHeading, Actions actions){
        if (instance == null) {
            instance = new FindIsland(initHeading, actions);
        }
        return instance;
    }
    
    @Override
    public JSONObject search() {
        JSONObject decision = new JSONObject();
        
        //extra scan step to make sure ground is infront
        if (scanStep == 4) 
        {
            return DecisionBuilder.createEchoDecision(currentHeading);
        }
        
        if (waitingForFly) 
        {
            actions.moveForward(decision);
            waitingForFly = false;
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
            rightDir = actions.getRight();
            return DecisionBuilder.createEchoDecision(rightDir);
        }  
        //echo left
        else if (scanStep == 2) 
        {
            leftDir = actions.getLeft();
            return DecisionBuilder.createEchoDecision(leftDir);
        } 
        //processing echo response
        else if (scanStep == 3) 
        {
            if (echoProcessor.hasGroundDetected()) 
            {
                String chosenDir = echoProcessor.chooseBestDirection();
                //if the lowest ground range is ahead
                if (currentHeading.equals(chosenDir)) 
                {
                    process = true;
                    resetScanState();
                    return decision;
                } 
                else 
                {
                    //turn towards the ground range with lowest range
                    if (chosenDir.equals(actions.getRight())) 
                    {
                        actions.turnRight(decision);
                    } 
                    else if (chosenDir.equals(actions.getLeft())) 
                    {
                        actions.turnLeft(decision);
                    }
                    currentHeading = chosenDir;
                    scanStep = 4;
                    return decision;
                }
            } 
            //no ground detected
            else 
            {
                //turn towards direction that has greatest range
                int diagRightScore = echoProcessor.getRightRange();
                int diagLeftScore  = echoProcessor.getLeftRange();
                
                if (diagRightScore >= diagLeftScore) 
                {
                    String newHeading = actions.getRight();
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
                    String newHeading = actions.getLeft();
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
        //chekcing if gorund is infront
        if (scanStep == 4) {
            if (extras.has("found") && extras.has("range")) {
                String found = extras.getString("found");
                // If ground is front finish
                if ("ground".equals(found)) {
                    process = true;
                } 
                //no ground restart the search process.
                else 
                {
                    resetScanState();
                }
            }
            scanStep = 0;
            return;
        }
        
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