package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
import eu.ace_design.island.bot.IExplorerRaid;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private Integer totalCost = 0;
    private  CoordinateManager cm; //= CoordinateManager.getInstance();
    private  DirectionToString dts = DirectionToString.getInstance();
    private  Drone drone; //= Drone.getInstance();
    private Actions actions;// = Actions.getInstance(cm, drone, dts);
    private String currentHeading;
    private FindIsland findIsland;
    private IslandDimensions islandDimensions; 
    private boolean islandFind = false;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}", info.toString(2));
        currentHeading = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", currentHeading);
        logger.info("Battery level is {}", batteryLevel);
        //pass the Actions instance to FindIsland.
        cm = CoordinateManager.getInstance(currentHeading);
        drone = Drone.getInstance(currentHeading);
        actions = Actions.getInstance(cm, drone, dts);
        findIsland = new FindIsland(currentHeading, actions);
        islandDimensions = new IslandDimensions(currentHeading);
      //  actions = Actions.getInstance(cm, drone, dts);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();
        if (!islandFind) {
            decision = findIsland.search();
            if (findIsland.processDone()) {
                islandFind = true;
                logger.info("Search process complete. Island found.");
            }
        }
        if (islandFind) {
            decision.put("action", "stop");
        }
        logger.info(drone.getCoordinates().getX() + ", " + drone.getCoordinates().getY());
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n" + response.toString(2));
        Integer cost = response.getInt("cost");
        logger.info("The cost of the action was {}", cost);
        totalCost += cost;
        logger.info("Total cost is {}", totalCost);
        String status = response.getString("status");
        logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        logger.info("Additional information received: {}", extraInfo);
        findIsland.updateState(response);
        logger.info("Dimensions measured: Length = " + islandDimensions.getMeasuredLength() +
                    ", Width = " + islandDimensions.getMeasuredWidth());
        islandDimensions.updateState(response);
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }
}

