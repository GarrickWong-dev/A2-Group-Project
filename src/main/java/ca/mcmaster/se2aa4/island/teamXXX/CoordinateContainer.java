package ca.mcmaster.se2aa4.island.teamXXX;

import java.util.List;

public abstract class CoordinateContainer {
    protected List<Coordinates> container;
    
    public Boolean isEmpty(){
        return container.isEmpty();
    }

    public Integer size(){
        return container.size();
    }

    public void add(Coordinates coords){
        this.container.add(coords);
    }
}
