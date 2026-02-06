package rs.oris.back.domain;


import lombok.ToString;

import java.sql.Timestamp;

@ToString
public class Gps {
    private Timestamp timestamp;
    private double priority;
    private double lat;
    private double lng;
    private double ang;
    private double satellites;
    private double speed;

    public boolean isDigital_entry_3() {
        return digital_entry_3;
    }

    public void setDigital_entry_3(boolean digital_entry_3) {
        this.digital_entry_3 = digital_entry_3;
    }

    public boolean isDigital_entry_2() {
        return digital_entry_2;
    }

    public void setDigital_entry_2(boolean digital_entry_2) {
        this.digital_entry_2 = digital_entry_2;
    }

    private boolean ignition;
    private boolean sleep_mode;
    private boolean digital_entry_3;
    private boolean digital_entry_2;

    public Gps() {
    }

    public Gps(Timestamp timestamp, double priority, double lat, double lng, double ang, double satellites, double speed, boolean ignition) {
        this.timestamp = timestamp;
        this.priority = priority;
        this.lat = lat;
        this.lng = lng;
        this.ang = ang;
        this.satellites = satellites;
        this.speed = speed;
        this.ignition = ignition;
    }

    public Gps(Timestamp timestamp, double priority, double lat, double lng, double ang, double satellites, double speed, boolean ignition, boolean sleep_mode) {
        this.timestamp = timestamp;
        this.priority = priority;
        this.lat = lat;
        this.lng = lng;
        this.ang = ang;
        this.satellites = satellites;
        this.speed = speed;
        this.ignition = ignition;
        this.sleep_mode = sleep_mode;
    }

    public boolean isSleep_mode() {
        return sleep_mode;
    }

    public void setSleep_mode(boolean sleep_mode) {
        this.sleep_mode = sleep_mode;
    }

    public boolean isIgnition() {
        return ignition;
    }

    public void setIgnition(boolean ignition) {
        this.ignition = ignition;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
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

    public double getAng() {
        return ang;
    }

    public void setAng(double ang) {
        this.ang = ang;
    }

    public double getSatellites() {
        return satellites;
    }

    public void setSatellites(double satellites) {
        this.satellites = satellites;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
