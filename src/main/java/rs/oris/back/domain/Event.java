package rs.oris.back.domain;

public class Event {
    private boolean contact;
    private boolean speed;
    private double maxSpeed;
    private boolean location;
    private boolean authorized;

    public Event() {
    }

    public Event(boolean contact, boolean speed, double maxSpeed, boolean location, boolean authorized) {
        this.contact = contact;
        this.speed = speed;
        this.maxSpeed = maxSpeed;
        this.location = location;
        this.authorized = authorized;
    }

    public boolean isContact() {
        return contact;
    }

    public void setContact(boolean contact) {
        this.contact = contact;
    }

    public boolean isSpeed() {
        return speed;
    }

    public void setSpeed(boolean speed) {
        this.speed = speed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
