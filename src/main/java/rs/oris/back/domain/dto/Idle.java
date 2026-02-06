package rs.oris.back.domain.dto;

import rs.oris.back.domain.Gps;
import rs.oris.back.domain.Vehicle;

import java.util.List;

public class Idle {

    private long start;
    private long end;
    private long time;
    private List<Gps> gpsList;
    private String location;
    private Vehicle vehicle;

    private Double kilometers;
    private boolean contactLock;

    public Double getKilometers() {
        return kilometers;
    }

    public void setKilometers(Double kilometers) {
        this.kilometers = kilometers;
    }

    public boolean isContactLock() {
        return contactLock;
    }

    public void setContactLock(boolean contactLock) {
        this.contactLock = contactLock;
    }

    public Idle() {
    }

    public Idle(long start, long end, long time, List<Gps> gpsList, String location) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.gpsList = gpsList;
        this.location = location;
    }

    public Idle(long start, long end, long time, List<Gps> gpsList) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.gpsList = gpsList;
    }

    public Idle(long start, long end, long time, List<Gps> gpsList, String location, Vehicle vehicle) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.gpsList = gpsList;
        this.location = location;
        this.vehicle = vehicle;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Gps> getGpsList() {
        return gpsList;
    }

    public void setGpsList(List<Gps> gpsList) {
        this.gpsList = gpsList;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
