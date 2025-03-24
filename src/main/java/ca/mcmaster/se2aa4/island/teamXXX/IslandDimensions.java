package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class IslandDimensions implements Search{

    private int state = 0;
    private String currentHeading;
    private final Actions actions;
    private String echoHeading;  
    private final Drone drone;
    private TurnContainer turnContainer = new TurnContainer();

    private int measuredWidth = 0;
    private int measuredLength = 0;
    private int forwardCount = 0;

    private boolean waitingForWidthEcho = true;
    private boolean waitingForLengthEcho = true;
    private String lengthEchoDirection = null;
    private final String originalHeading;
    private boolean process = false;

    public IslandDimensions(String initialHeading, String echoHeading, Actions actions, Drone drone) {
        this.originalHeading = initialHeading;
        this.currentHeading = initialHeading;
        this.echoHeading = echoHeading;
        this.actions = actions;
        this.drone = drone;
    }

    @Override
    public JSONObject search() 
    {
        JSONObject decision = new JSONObject();
        while (true) 
        {
            switch (state) 
            {
                 //turn towards width
                 case 0:
                    turnContainer.add(new Coordinates(drone.getCoordinates().getX(), drone.getCoordinates().getY()));
                    if (actions.getRight().equals(echoHeading)) 
                    {
                        actions.turnRight(decision);
                        currentHeading = echoHeading;
                        echoHeading = actions.getRight();
                    } 
                    else
                    {
                        actions.turnLeft(decision);
                        currentHeading = echoHeading;
                        echoHeading = actions.getLeft();
                    } 
                    state = 1;
                    return decision;
                case 1:
                    //move forward twice before measuring width
                    if (forwardCount < 2) 
                    {
                        actions.moveForward(decision);
                        forwardCount++;
                        return decision;
                    } 
                    else 
                    {
                        forwardCount = 0;
                        state = 2; 
                        break;
                    }
                case 2:
                    //measre width
                    if (waitingForWidthEcho) 
                    {
                        decision = DecisionBuilder.createEchoDecision(echoHeading);
                        waitingForWidthEcho = false;
                        return decision;
                    } 
                    else 
                    {
                        actions.moveForward(decision);
                        measuredWidth++;
                        waitingForWidthEcho = true;
                        return decision;
                    }

                case 3:
                    if (actions.getRight().equals(echoHeading)) 
                    {
                        actions.turnRight(decision);
                        currentHeading = echoHeading;
                        lengthEchoDirection = actions.getRight();
                    } 
                    else if (actions.getLeft().equals(echoHeading)) 
                    {
                        actions.turnLeft(decision);
                        currentHeading = echoHeading;
                        lengthEchoDirection = actions.getLeft();
                    } 
                    else 
                    {
                        //fallback
                        actions.turnLeft(decision);
                        currentHeading = echoHeading;
                        lengthEchoDirection = actions.getLeft();
                    }
                    state = 4;
                    return decision;
                case 4:
                    //move forward twice before measuring length
                    if (forwardCount < 2) 
                    {
                        actions.moveForward(decision);
                        forwardCount++;
                        return decision;
                    } 
                    else 
                    {
                        forwardCount = 0;
                        state = 5; 
                        break; 
                    }
                case 5:
                    //measure length
                    if (waitingForLengthEcho) 
                    {
                        decision = DecisionBuilder.createEchoDecision(lengthEchoDirection);
                        waitingForLengthEcho = false;
                        return decision;
                    } 
                    else 
                    {
                        actions.moveForward(decision);
                        measuredLength++;
                        waitingForLengthEcho = true;
                        return decision;
                    }
                case 6:
                    //end coords here
                    turnContainer.add(new Coordinates(drone.getCoordinates().getX(), drone.getCoordinates().getY()));
                    process = true;
                    return decision;
                default:
                    turnContainer.add(new Coordinates(drone.getCoordinates().getX(), drone.getCoordinates().getY()));
                    //end coords here
                    process = true;
                    return decision;
            }
        }
    }

    public void updateState(JSONObject response) {
        JSONObject extras = response.getJSONObject("extras");
        if (!extras.has("found") || !extras.has("range")) 
        {
            return;
        }
        String found = extras.getString("found");
        //for width measurement phase
        if (state == 2 && !waitingForWidthEcho) 
        {
            if ("OUT_OF_RANGE".equals(found)) 
            {
                state = 3; 
            }
        }
        //for length measurement phase
        else if (state == 5 && !waitingForLengthEcho) 
        {
            if ("OUT_OF_RANGE".equals(found)) 
            {
                state = 6; 
            }
        }
    }

    public int getMeasuredLength() {
        return measuredLength;
    }

    public int getMeasuredWidth() {
        return measuredWidth;
    }

    public boolean processDone() {
        return process;
    }

    public Coordinates getFirstTurning() {
        return turnContainer.getFirstTurning();
    }

    public Coordinates getLastTurning() {
        return turnContainer.getLastTurning();
    }

    public Coordinates getMidCoordinates()
    {
        int xCoord = (turnContainer.getFirstTurning().getX() + turnContainer.getLastTurning().getX() + 1) / 2;
        int yCoord = (turnContainer.getFirstTurning().getY() + turnContainer.getLastTurning().getY() + 1) / 2;
        return new Coordinates(xCoord, yCoord);
    }
}