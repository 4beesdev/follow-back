package rs.oris.back.domain;

public class EventUnauthorized {
    long timestamp;
    double lat;
    double lng;

    public EventUnauthorized() {
    }

    public EventUnauthorized(long timestamp, double lat, double lng) {
        this.timestamp = timestamp;
        this.lat = lat;
        this.lng = lng;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
}
