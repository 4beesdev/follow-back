package rs.oris.back.domain;

public class AlarmBE {
    private String imei;
    private int userId;
    private boolean contact;
    private boolean speed;
    private double maxSpeed;
    private boolean authorized;
    private long timestamp;

    public AlarmBE() {
    }

    public AlarmBE(String imei, int userId, boolean contact, boolean speed, double maxSpeed, boolean authorized, long timestamp) {
        this.imei = imei;
        this.userId = userId;
        this.contact = contact;
        this.speed = speed;
        this.maxSpeed = maxSpeed;
        this.authorized = authorized;
        this.timestamp = timestamp;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
