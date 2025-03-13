package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DroneStatExtractor {
    private JSONObject extras;

    public DroneStatExtractor(String response) {
        JSONObject respJson = new JSONObject(new JSONTokener(new StringReader(response)));
        extras = respJson.getJSONObject("extras");
    }

    public String getLand()
	{
		String lastFound = extras.getString("found");
        return lastFound;
	}

    public int getDistance()
	{
        int lastRange = extras.getInt("range");
        return lastRange;
	}
}

