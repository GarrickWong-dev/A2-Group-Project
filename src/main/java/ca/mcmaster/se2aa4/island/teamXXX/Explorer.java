package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import eu.ace_design.island.bot.IExplorerRaid;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private final CoordinateManager cm = CoordinateManager.getInstance();
    private final Drone drone = Drone.getInstance();
    private final Actions actions = Actions.getInstance(cm, drone);
    private final Move moveController = new Move(actions);
    private Integer batteryLevel;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));
        String direction = info.getString("heading");
        batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        Coordinates target = new Coordinates(20, -30);
        if(batteryLevel < 150){
            JSONObject decision = new JSONObject();
            decision.put("action", "stop");
            return decision.toString();
        }

        // Use the Move class to decide the next action
        logger.info(drone.getCoordinates().getX() + ", " + drone.getCoordinates().getY());
        // JSONObject decision = moveController.move(target);
        // return decision.toString();
        Spiral sl = Spiral.getInstance(this.actions);
        return sl.search().toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        JSONObject extras = response.getJSONObject("extras");

        if(extras.has("creeks")) {
            JSONArray creeks = extras.getJSONArray("creeks");
            if(!creeks.isEmpty()){
                CreekContainer creekContainer = CreekContainer.getInstance();
                creekContainer.addCoordinate(drone.getCoordinates());
                creekContainer.add(creeks.getString(0));
            }
        }
        if(extras.has("sites")) {
            JSONArray sites = extras.getJSONArray("sites");
            if(!sites.isEmpty()){
                EmergencySiteContainer siteContainer = EmergencySiteContainer.getInstance();
                siteContainer.addCoordinate(drone.getCoordinates());
                siteContainer.add(sites.getString(0));
            }
        }
    }

    @Override
    public String deliverFinalReport() {
        logger.info(CreekContainer.getInstance().isEmpty());
        logger.info(EmergencySiteContainer.getInstance().isEmpty());
        return "no creek found";
    }
}