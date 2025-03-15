package ca.mcmaster.se2aa4.island.teamXXX;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class SearchLandmarks implements Search {
    private final Logger logger = LogManager.getLogger();
    private final Actions actions;
    private int spiralStep = 1; // Initial movement length
    private int movesInCurrentStep = 0;
    private int turnsInCurrentLayer = 0;
    private boolean scanFlag = true;
    private boolean turnState = false;
    private static SearchLandmarks instance;

    private SearchLandmarks(Actions actions){
        this.actions = actions;
    }

    public static SearchLandmarks getInstance(Actions actions){
        if (instance == null) {
            instance = new SearchLandmarks(actions);
        }
        return instance;
    }

    @Override
    public JSONObject search(){
        JSONObject decision = new JSONObject();
        // Always scan between actions
        if (scanFlag) {
            decision.put("action", "scan");
            scanFlag = false; // Reset scan flag after scanning
        } else {
            if (turnState) {
                // Perform spiral turn pattern
                actions.turnLeft(decision);
                turnsInCurrentLayer++;

                // Spiral pattern logic: 2 turns per layer
                if (turnsInCurrentLayer >= 2) {
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
