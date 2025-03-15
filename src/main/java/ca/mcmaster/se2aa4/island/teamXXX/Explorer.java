package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import eu.ace_design.island.bot.IExplorerRaid;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private int move = 0; //Decides what action to do
    private Integer totalCost = 0; //Nothing right now

    private final CoordinateManager cm = CoordinateManager.getInstance();
    private final DirectionToString dts = DirectionToString.getInstance();
    private final Drone drone = Drone.getInstance();
    private final Actions actions = Actions.getInstance(cm, drone, dts);
    private String currentHeading;
    private FindIsland findIsland;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));
        currentHeading = info.getString("heading");
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
        findIsland = new FindIsland(currentHeading);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();
        Coordinates coords = drone.getCoordinates();
        decision = findIsland.search();
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n"+response.toString(2));
        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        totalCost += cost;
        logger.info("Total cost is {}", totalCost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
        findIsland.updateState(response);
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }
}
