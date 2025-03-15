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
    private final Move moveController = new Move(actions);

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        logger.info("** Initialization info:\n {}",info.toString(2));
        String direction = info.getString("heading");
        Integer batteryLevel = info.getInt("budget");
        logger.info("The drone is facing {}", direction);
        logger.info("Battery level is {}", batteryLevel);
    }

    @Override
    public String takeDecision() {
        Coordinates target = new Coordinates(20, -30);

        // Use the Move class to decide the next action
        JSONObject decision = moveController.move(target);
        logger.info(drone.getCoordinates().getX() + ", " + drone.getCoordinates().getY());
        // If the decision is to stop, switch to searching for landmarks
        // if (decision.has("action") && decision.getString("action").equals("stop")) {
        //     SearchLandmarks sl = SearchLandmarks.getInstance(this.actions);
        //     decision = sl.search();
        // }

        return decision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        JSONObject extras = response.getJSONObject("extras");

        // Update coordinates from server response
        if (extras.has("position")) {
            JSONObject position = extras.getJSONObject("position");
            int x = position.getInt("x");
            int y = position.getInt("y");
            drone.setCoordinates(new Coordinates(x, y));
        }

        // Update direction from server response
        if (extras.has("direction")) {
            String dirStr = extras.getString("direction");
            Direction dir = Direction.valueOf(dirStr);
            drone.setFacing(dir);
        }
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }
}