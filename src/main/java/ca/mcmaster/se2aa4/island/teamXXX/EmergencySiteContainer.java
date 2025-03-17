package ca.mcmaster.se2aa4.island.teamXXX;

public class EmergencySiteContainer extends Container<String> {
    private static EmergencySiteContainer instance;
    private final CoordinateContainer coordContainer;
    
    private EmergencySiteContainer(){
        super();
        this.coordContainer = new CoordinateContainer();
    }

    public static EmergencySiteContainer getInstance(){
        if (instance == null) {
            instance = new EmergencySiteContainer();
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
    public void add(String emergencySite) {
        this.container.add(emergencySite);
    }
}