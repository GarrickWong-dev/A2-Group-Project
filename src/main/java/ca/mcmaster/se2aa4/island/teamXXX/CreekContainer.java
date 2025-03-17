package ca.mcmaster.se2aa4.island.teamXXX;

public class CreekContainer extends Container<String> {
    private static CreekContainer instance;
    private final CoordinateContainer coordContainer;
    
    private CreekContainer(){
        super();
        this.coordContainer = new CoordinateContainer();
    }

    public static CreekContainer getInstance(){
        if (instance == null) {
            instance = new CreekContainer();
        }
        return instance;
    }

    public void addCoordinate(Coordinates coord){
        this.coordContainer.add(coord);
    }

    public CoordinateContainer getCoordinates(){
        return this.coordContainer;
    }

    @Override
    public void add(String creek) {
        this.container.add(creek);
    }
}
