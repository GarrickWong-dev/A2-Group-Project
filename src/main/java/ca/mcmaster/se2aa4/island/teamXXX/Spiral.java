package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Spiral implements Search {
    private final Logger logger = LogManager.getLogger();
    private final Actions actions = Actions.getInstance();
    private int spiralStep = 0; // Initial movement length
    private int movesInCurrentStep = 0;
    private int turnsInCurrentLayer = 0;
    private boolean scanFlag = true;
    private boolean turnState = true;
    private IslandDimensions islandDimensions;
    private static Spiral instance;

    private Spiral(IslandDimensions islandDimensions){
        this.islandDimensions = islandDimensions;
    }

    public static Spiral getInstance(IslandDimensions islandDimensions){
        if (instance == null) {
            instance = new Spiral(islandDimensions);
        }
        return instance;
    }

    @Override
    public JSONObject search(){
        JSONObject decision = new JSONObject();
        Drone drone = Drone.getInstance();
        if(drone.getCoordinates() == islandDimensions.getFirstTurning() || drone.getCoordinates() == islandDimensions.getLastTurning()){
            decision.put("action", "stop");
            return decision;
        }

        // Always scan between actions
        if (scanFlag) {
            decision.put("action", "scan");
            scanFlag = false; // Reset scan flag after scanning
        } else {
            if (turnState || spiralStep == 0) {
                // Perform spiral turn pattern
                actions.turnLeft(decision);
                turnsInCurrentLayer++;

                if(spiralStep == 0 && turnsInCurrentLayer >= 3 || turnsInCurrentLayer >= 2 && spiralStep != 0){
                    spiralStep++; // Increase movement length
                    turnsInCurrentLayer = 0;
                }
                turnState = false;
            } else {
                // Move forward in spiral pattern
                actions.moveForward(decision);
                movesInCurrentStep++;

                // Check if completed current spiral segment
                if (movesInCurrentStep >= spiralStep) {
                    turnState = true;
                    movesInCurrentStep = 0;
                }
            }
            scanFlag = true; // Set scan flag after performing an action
        }
        return decision;
    }
}
