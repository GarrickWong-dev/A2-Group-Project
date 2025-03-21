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
    private final Actions actions = Actions.getInstance();
    private Integer batteryLevel;
    private CreekContainer creekContainer = new CreekContainer();
    private EmergencySiteContainer siteContainer = new EmergencySiteContainer();
    private final Integer BATTERY_LIMIT = 150;

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
        logger.info(batteryLevel);
        if(batteryLevel < BATTERY_LIMIT){
            JSONObject decision = new JSONObject();
            decision.put("action", "stop");
            return decision.toString();
        }

        // Use the Move class to decide the next action
        // Move moveController = new Move(actions);
        // JSONObject decision = moveController.move(target);
        // return decision.toString();
        Spiral sl = Spiral.getInstance(this.actions);
        return sl.search().toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        Integer cost = response.getInt("cost");
        JSONObject extras = response.getJSONObject("extras");
        Drone drone = Drone.getInstance();

        batteryLevel -= cost;

        if(extras.has("creeks")) {
            JSONArray creeks = extras.getJSONArray("creeks");
            if(!creeks.isEmpty()){
                creekContainer.put(creeks.getString(0), drone.getCoordinates());
            }
        }
        if(extras.has("sites")) {
            JSONArray sites = extras.getJSONArray("sites");
            if(!sites.isEmpty()){
                siteContainer.put(sites.getString(0), drone.getCoordinates());
            }
        }
    }

    @Override
    public String deliverFinalReport() {
        logger.info(closestCreek());
        return "no creek found";
    }

    private String closestCreek(){
        double shortestDist = -1;
        String closestCreek = "";
        for(String creek: creekContainer){
            for(String site: siteContainer){
                double dist = distance(creekContainer.getValue(creek), siteContainer.getValue(site));
                if(shortestDist == -1 || shortestDist > dist){
                    shortestDist = dist;
                    closestCreek = creek;
                }
            }
        }
        return closestCreek;
    }

    private double distance(Coordinates a, Coordinates b){
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }
}