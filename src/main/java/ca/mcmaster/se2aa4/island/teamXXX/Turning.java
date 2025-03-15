package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.StringReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ace_design.island.bot.IExplorerRaid;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Turning
{
	public String turnRight(String direction) 
	{
	    String newDirection;
	    switch (direction.toUpperCase()) 
	    { 
	        case "N":
	            newDirection = "E";
	            break;
	        case "E":
	            newDirection = "S";
	            break;
	        case "S":
	            newDirection = "W";
	            break;
	        case "W":
	            newDirection = "N";
	            break;
	        default:
	            newDirection = "N";
	    }
	    return newDirection;
	}

	public String turnLeft(String direction) 
	{
	    String newDirection;
	    switch (direction.toUpperCase()) 
	    { 
	        case "N":
	            newDirection = "W";
	            break;
	        case "W":
	            newDirection = "S";
	            break;
	        case "S":
	            newDirection = "E";
	            break;
	        case "E":
	            newDirection = "N";
	            break;
	        default:
	            newDirection = "N";
	    }
	    return newDirection;
	}

	public String turnAround(String direction) 
	{
	    String newDirection;
	    switch (direction.toUpperCase()) 
	    { 
	        case "N":
	            newDirection = "S";
	            break;
	        case "S":
	            newDirection = "N";
	            break;
	        case "E":
	            newDirection = "W";
	            break;
	        case "W":
	            newDirection = "E";
	            break;
	        default:
	            newDirection = "N";
	    }
	    return newDirection;
	}
	
}