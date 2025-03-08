package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private int move = 0; //Decides what action to do
    private Integer totalCost = 0; //Nothing right now
    private CoordinateManager coords = CoordinateManager.getInstance(); // Coordinage Manager
    private Actions actions = Actions.getInstance(coords); //Actions class

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
        move ++;
        JSONObject decision = new JSONObject();
        if (move % 2 == 0){
            decision.put("action", "scan");
        }
        if (move == 1){
        actions.forward(decision); //Move Forward in East Direction
        logger.info(coords.getX());
        logger.info(coords.getY());
        }
        if (move == 3){
        actions.forward(decision); //Move forward in east direction
        logger.info(coords.getX());
        logger.info(coords.getY());
        }   
        if (move == 5){
        actions.forward(decision); //Move forward in east direction
        logger.info(coords.getX());
        logger.info(coords.getY());
        }
        if (move == 7){
        actions.south(decision); //south turn
        logger.info(coords.getX());
        logger.info(coords.getY());
        }
        if (move == 9){
        actions.forward(decision); //Forward in south direction
        logger.info(coords.getX());
        logger.info(coords.getY());
        }
        if (move == 11){
        actions.forward(decision); //Forward in south direction
        logger.info(coords.getX());
        logger.info(coords.getY());
        }
        if (move == 13){
        actions.west(decision); //west turn
        logger.info(coords.getX());
        logger.info(coords.getY());
        }
        if (move == 15){
        actions.forward(decision); //Move forward in west direction
        logger.info(coords.getX());
        logger.info(coords.getY());
        }
        if (move == 17){
        actions.north(decision); //North turn
        logger.info(coords.getX());
        logger.info(coords.getY());
        
        }
        if (move == 19){
        actions.forward(decision); //Move forward in North direction
        logger.info(coords.getX());
        logger.info(coords.getY());
        
        }
        if (move == 21){
        actions.east(decision); //east turn
        logger.info(coords.getX());
        logger.info(coords.getY());
        
        }
        if (move == 23){
        actions.forward(decision); //Move forward in east direction
        logger.info(coords.getX());
        logger.info(coords.getY());
        
        }   


        if (move == 25){
        decision.put("action", "stop");
        } // we stop the exploration immediately
        //logger.info("** Decision: {}",decision.toString());
        return decision.toString();
    }



    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        //logger.info("** Response received:\n"+response.toString(2));
        Integer cost = response.getInt("cost");
        //logger.info("The cost of the action was {}", cost);
        totalCost += cost;
        //logger.info("Total cost is {}", totalCost);
        String status = response.getString("status");
        //logger.info("The status of the drone is {}", status);
        JSONObject extraInfo = response.getJSONObject("extras");
        //logger.info("Additional information received: {}", extraInfo);
    }

    @Override
    public String deliverFinalReport() {
        return "no creek found";
    }




}
