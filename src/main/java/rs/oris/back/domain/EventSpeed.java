package rs.oris.back.domain;

public class EventSpeed {
    double speed;
    double lat;
    double lng;
    long timestamp;

    public EventSpeed() {
    }

    public EventSpeed(double speed, double lat, double lng, long timestamp) {
        this.speed = speed;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
