package ca.mcmaster.se2aa4.island.teamXXX;

public class CoordinateContainer extends Container<Coordinates> {
    public CoordinateContainer() {
        super();
    }

    @Override
    public void add(Coordinates coords) {
        this.container.add(coords);
    }
}