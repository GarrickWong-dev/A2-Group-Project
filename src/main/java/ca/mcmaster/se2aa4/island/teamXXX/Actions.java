package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Actions {
    private static Actions instance;
    private CoordinateManager coords;

    private Actions(CoordinateManager coords){
        this.coords = coords;
    }

    public static Actions getInstance(CoordinateManager coords) {
        if (instance == null) {
            instance = new Actions(coords);
        }
        return instance;
    }


    public void forward(JSONObject decision){
        decision.put("action", "fly");
        this.coords.forward();
    }

    public void south(JSONObject decision){
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "S"));
        this.coords.south();
    }    

    public void east(JSONObject decision){
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "E"));
        this.coords.east();
    }    

    public void west(JSONObject decision){
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "W"));
        this.coords.west();
    }    

    public void north(JSONObject decision){
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", "N"));
        this.coords.north();
    }    
    
}