package rs.oris.back.domain;

import javax.persistence.*;

@Entity
@Table(name="alarm_vehicle")
public class AlarmVehicle {
    @Id
    @GeneratedValue
    @Column(name = "alarm_vehicle_id")
    private int alarmVehicleId;
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @ManyToOne
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private long timestampTriggered;
    private boolean contact;
    private boolean speed;
    private double maxSpeed;
    private boolean authorized;
    private long timestamp;

    public AlarmVehicle() {
    }

    public AlarmVehicle(int alarmVehicleId, Vehicle vehicle, Alarm alarm, User user, long timestampTriggered, boolean contact, boolean speed, double maxSpeed, boolean authorized, long timestamp) {
        this.alarmVehicleId = alarmVehicleId;
        this.vehicle = vehicle;
        this.alarm = alarm;
        this.user = user;
        this.timestampTriggered = timestampTriggered;
        this.contact = contact;
        this.speed = speed;
        this.maxSpeed = maxSpeed;
        this.authorized = authorized;
        this.timestamp = timestamp;
    }

    public long getTimestampTriggered() {
        return timestampTriggered;
    }

    public void setTimestampTriggered(long timestampTriggered) {
        this.timestampTriggered = timestampTriggered;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getAlarmVehicleId() {
        return alarmVehicleId;
    }

    public void setAlarmVehicleId(int alarmVehicleId) {
        this.alarmVehicleId = alarmVehicleId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}
