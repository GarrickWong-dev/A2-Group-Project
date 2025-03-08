package ca.mcmaster.se2aa4.island.teamXXX;

public class West implements Movement{
    @Override
    public void move(DroneCoordinates location){
        location.setX(location.getX()-1);
        switch (location.getDirection()){
            case NORTH:
                location.setY(location.getY()+1);
                break;
            case SOUTH:
                location.setY(location.getY()-1);
                break;
        }
        location.setFacing(Direction.WEST);
    }
}