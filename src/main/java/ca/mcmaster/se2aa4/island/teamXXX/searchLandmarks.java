package ca.mcmaster.se2aa4.island.teamXXX;

import org.json.JSONObject;

public class searchLandmarks implements Search {
    Actions actions;
    JSONObject decision;
    Integer state, moveCounter, movesExecuted;

    public searchLandmarks(Actions actions, JSONObject decision){
        this.actions = actions;
        this.decision = decision;
        this.state = 0;
        this.moveCounter = 0;
        this.movesExecuted = 0;
    }

    @Override
    public void search(){
    }
}
