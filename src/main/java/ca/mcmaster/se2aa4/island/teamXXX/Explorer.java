package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Explorer is the main entry point for the simulation.
 * It instantiates IslandMeasurements and passes commands/responses.
 */
public class Explorer implements IExplorerRaid {
    private IslandMeasurements measurements;
    private final Logger logger = LogManager.getLogger();

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n{}", info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
        measurements = new IslandMeasurements(direction);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = measurements.move();
        logger.info("** Decision: {}", decision.toString());
        return decision.toString();
    }
    
    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n" + response.toString(2));
        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
        
        measurements.updateState(response);
    }

@Override
    public String deliverFinalReport() {
        String report = measurements.getFinalReport();
        logger.info("** Final Report Generated:\n{}", report);
        return report;
    }

}

