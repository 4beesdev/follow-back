package rs.oris.back.domain;

public class Bounds {
    double east;
    double north;
    double south;
    double west;

    public Bounds() {
    }

    public Bounds(double east, double north, double south, double west) {
        this.east = east;
        this.north = north;
        this.south = south;
        this.west = west;
    }

    public double getEast() {
        return east;
    }

    public void setEast(double east) {
        this.east = east;
    }

    public double getNorth() {
        return north;
    }

    public void setNorth(double north) {
        this.north = north;
    }

    public double getSouth() {
        return south;
    }

    public void setSouth(double south) {
        this.south = south;
    }

    public double getWest() {
        return west;
    }

    public void setWest(double west) {
        this.west = west;
    }
}
