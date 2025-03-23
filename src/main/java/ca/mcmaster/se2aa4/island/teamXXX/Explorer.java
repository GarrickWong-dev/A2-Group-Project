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
    private final DirectionToString dts = DirectionToString.getInstance();
    private Drone drone;

    private String currentHeading;
    private FindIsland findIsland;
    private DimensionsSetUp dimensionsSetUp;     
    private IslandDimensions islandDimensions;

    private boolean islandFind = false;
    private boolean islandDimensionSetup = false;
    private boolean islandDimensionSetupIntialized = false;
    private boolean islandDimensionsInitialized = false;
    private boolean islandMeasured = false;

    private Integer batteryLevel;
    private CreekContainer creekContainer = new CreekContainer();
    private EmergencySiteContainer siteContainer = new EmergencySiteContainer();
    private final Integer BATTERY_LIMIT = 150;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}", info.toString(2));
        currentHeading = info.getString("heading");
        logger.info("The drone is facing {}", currentHeading);
        batteryLevel = info.getInt("budget");
        logger.info("Battery level is {}", batteryLevel);
        //pass the Actions instance to FindIsland.
        drone = Drone.getInstance(dts.fromString(currentHeading));
        findIsland = FindIsland.getInstance(currentHeading, actions);
        dimensionsSetUp = new DimensionsSetUp(currentHeading, actions);
        islandDimensions = new IslandDimensions(currentHeading,dimensionsSetUp.getEchoHeading(), actions, drone);
    }

    @Override
    public String takeDecision() {
        JSONObject decision = new JSONObject();
        Coordinates coords = drone.getCoordinates();
        logger.info(batteryLevel);
        if(batteryLevel < BATTERY_LIMIT){
            decision.put("action", "stop");
            return decision.toString();
        }

        if (!islandFind) 
        {
            decision = findIsland.search();
            if (findIsland.processDone()) {
                islandFind = true;
                logger.info("Search process complete. Island found.");
            }
        }
        if (!islandDimensionSetup && islandFind)
        {
             if (!islandDimensionSetupIntialized) 
             {
                dimensionsSetUp = new DimensionsSetUp(findIsland.getCurrentHeading(), actions);
                islandDimensionSetupIntialized = true;
            }
            
            decision = dimensionsSetUp.setupDimensions();

            if (dimensionsSetUp.processDone())
            {
                islandDimensionSetup = true;
            }
        }
        //getting dimensions
        if(!islandMeasured && islandDimensionSetup)
        {
            
             if(islandDimensionsInitialized == false)
             {
                
                logger.info("current heading " + dimensionsSetUp.getCurrentHeading());
                logger.info("echo direction " + dimensionsSetUp.getEchoHeading());
                islandDimensions = new IslandDimensions(dimensionsSetUp.getCurrentHeading(),dimensionsSetUp.getEchoHeading(), actions, drone);
                islandDimensionsInitialized = true;
             }

            decision = islandDimensions.measurer();

            if (islandDimensions.processDone())
            {
                islandMeasured = true;
            }
         }
         if (islandMeasured)
         {
            logger.info("First turning coordinates " + islandDimensions.getFirstTurning().getX() + ", " + islandDimensions.getFirstTurning().getY());
            logger.info("End turning coordinates " + islandDimensions.getLastTurning().getX() + ", " + islandDimensions.getLastTurning().getY());
            logger.info(islandDimensions.getMidCoordinates().getX() + ", " + islandDimensions.getMidCoordinates().getY());
            //move to location 
            decision.put("action", "stop");
         }

        // Use the Move class to decide the next action
        // Move moveController = new Move(actions);
        // JSONObject decision = moveController.move(target);
        // return decision.toString();
        Spiral sl = Spiral.getInstance(this.actions);
    
        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Response received:\n"+response.toString(2));
        Integer cost = response.getInt("cost");
        JSONObject extras = response.getJSONObject("extras");
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

        findIsland.updateState(response);
        dimensionsSetUp.updateState(response);
        islandDimensions.updateState(response);
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


