package rs.oris.back.domain;

public class EventKeyContact {
    //1 - turned on
    //0 - turned off
    int state;
    double lat;
    double lng;
    long timestamp;

    public EventKeyContact() {
    }

    public EventKeyContact(int state, double lat, double lng, long timestamp) {
        this.state = state;
        this.lat = lat;
        this.lng = lng;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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
