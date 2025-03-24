package ca.mcmaster.se2aa4.island.teamXXX;

// TurnContainer extends ListContainer and manages a list of Coordinates
// It tracks the first and last turning points in a journey.
public class TurnContainer extends ListContainer<Coordinates> {

    private Coordinates firstTurning = null; // The first turning point
    private Coordinates lastTurning = null;  // The most recent (last) turning point

    // Override the add method to track the first and last turning coordinates
    @Override
    public void add(Coordinates coordinate) {
        if (firstTurning == null) {
            firstTurning = coordinate; // If first turning is null, this is the first coordinate
        }
        lastTurning = coordinate; // Update the last turning point each time a new coordinate is added
        elements.add(coordinate); // Add the coordinate to the list of elements
    }

    // Getter for the first turning coordinate
    public Coordinates getFirstTurning() {
        return firstTurning;
    }

    // Getter for the last turning coordinate
    public Coordinates getLastTurning() {
        return lastTurning;
    }
}
