package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private String currentHeading;
    private FindIsland findIsland; // persistent island finder

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}", info.toString(2));
        // Store the heading in our field (avoid shadowing)
        currentHeading = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", currentHeading);
        logger.info("Battery level is {}", batteryLevel);
        // Create our persistent island finder with the initial heading.
        findIsland = new FindIsland(currentHeading);
    }

    @Override
    public String takeDecision() {
        // Delegate decision making to our island finder.
        JSONObject decision = findIsland.move();
        logger.info("** Decision: {}", decision.toString());
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n" + response.toString(2));
        int cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
        
        // Let our island finder update its state based on this response.
        findIsland.updateState(response);
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }
}

