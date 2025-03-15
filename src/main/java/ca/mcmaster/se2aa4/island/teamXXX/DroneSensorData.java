package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A helper class to extract sensor data from the drone's JSON responses.
 */
public class DroneSensorData {
    private JSONObject extras;

    public DroneSensorData(String response) {
        JSONObject respJson = new JSONObject(new JSONTokener(new StringReader(response)));
        extras = respJson.getJSONObject("extras");
    }
    
    public int getRange(String direction) {
        // Expected JSON structure: 
        // "extras": { "forward": {"range": <int>}, "left": {"range": <int>}, "right": {"range": <int>} }
        return extras.getJSONObject(direction.toLowerCase()).getInt("range");
    }
    
    public boolean isGround(String direction) {
        return extras.getJSONObject(direction.toLowerCase()).getString("found").equals("GROUND");
    }
}