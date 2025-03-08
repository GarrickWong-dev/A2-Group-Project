package ca.mcmaster.se2aa4.island.teamXXX;

public class South implements Movement{
    @Override
    public void move(DroneCoordinates location){
        location.setY(location.getY()-1);
        switch (location.getDirection()){
            case EAST:
                location.setX(location.getX()+1);
                break;
            case WEST:
                location.setX(location.getX()-1);
                break;
        }
        location.setFacing(Direction.SOUTH);
    }
}

