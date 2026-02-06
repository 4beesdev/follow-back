package rs.oris.back.domain;

public class GeozoneSpeed {
    private Geozone geozone;
    private double speed;

    public GeozoneSpeed() {
    }

    public GeozoneSpeed(Geozone geozone, double speed) {
        this.geozone = geozone;
        this.speed = speed;
    }

    public Geozone getGeozone() {
        return geozone;
    }

    public void setGeozone(Geozone geozone) {
        this.geozone = geozone;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
