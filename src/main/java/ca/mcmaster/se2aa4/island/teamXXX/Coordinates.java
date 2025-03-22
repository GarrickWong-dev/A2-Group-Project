package ca.mcmaster.se2aa4.island.teamXXX;

public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates getCoordinates() {
        return this;
    }

    public Integer getX() {
        return this.x;
    }

    public Integer getY() {
        return this.y;
    }

    public void increaseX(int amount) {
        this.x += amount;
    }

    public void increaseY(int amount) {
        this.y += amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return this.x == that.x && this.y == that.y;
    }
}
