package ca.mcmaster.se2aa4.island.teamXXX;

public class TurnContainer extends ListContainer<Coordinates> {

    private Coordinates firstTurning = null;
    private Coordinates lastTurning = null;

    @Override
    public void add(Coordinates coordinate) 
    {
        if (firstTurning == null) {
            firstTurning = coordinate;
        }
        lastTurning = coordinate;
        elements.add(coordinate);
    }

    public Coordinates getFirstTurning() {
        return firstTurning;
    }

    public Coordinates getLastTurning() {
        return lastTurning;
    }
}
