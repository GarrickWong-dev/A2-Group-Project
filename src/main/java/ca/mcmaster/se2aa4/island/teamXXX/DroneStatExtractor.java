package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Extracts drone status information from JSON responses.
 */
public class DroneStatExtractor {
    private JSONObject extras;

    public DroneStatExtractor(String response) {
        JSONObject respJson = new JSONObject(new JSONTokener(new StringReader(response)));
        extras = respJson.getJSONObject("extras");
    }

    public String getLand() {
        return extras.getString("found");
    }

    public int getDistance() {
        return extras.getInt("range");
    }
}