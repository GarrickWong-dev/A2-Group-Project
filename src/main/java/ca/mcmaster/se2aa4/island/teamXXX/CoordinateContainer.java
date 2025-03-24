package ca.mcmaster.se2aa4.island.teamXXX;

public class CoordinateContainer extends ListContainer<Coordinates> {

    // Constructor that initializes the container
    public CoordinateContainer() {
        super();
    }

    // Adds a Coordinates object to the container
    @Override
    public void add(Coordinates coords) {
        this.elements.add(coords);
    }
}
