package ca.mcmaster.se2aa4.island.teamXXX;

public class Forward implements Movement{
    @Override
    public void move(DroneCoordinates location){
        switch (location.getDirection()){
            case EAST:
                location.setX(location.getX()+1);
                break;
            case NORTH:
                location.setY(location.getY()+1);
                break;
            case WEST:
                location.setX(location.getX()-1);
                break;
            case SOUTH:
                location.setY(location.getY()-1);
                break;
        }
    }
}