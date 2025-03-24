package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

// Interface for defining movement behavior of a drone or object
public interface Movement {
    // Method to move the drone or object to the specified coordinates
    // Returns a JSONObject with the decision or action taken during movement
    public JSONObject move(Coordinates coords);
}
