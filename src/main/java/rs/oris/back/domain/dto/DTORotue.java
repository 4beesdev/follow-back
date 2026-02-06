package rs.oris.back.domain.dto;

import rs.oris.back.domain.Gs100;
import rs.oris.back.domain.Vehicle;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DTORotue {
    private Vehicle vehicle;
    private List<Gs100> latLngList = new ArrayList<>();
    private double roadTraveled;
    private double timeOfTravel;
    private double idleTime;
    private double stoppedTime;
    private String routeName;
    private Timestamp startTime;
    private Timestamp endTime;

    public DTORotue() {
    }

    public DTORotue(DTORotue rut) {
        this.vehicle = rut.getVehicle();
        this.latLngList = rut.getLatLngList();
        this.roadTraveled = rut.getRoadTraveled();
        this.timeOfTravel = rut.getTimeOfTravel();
        this.idleTime = rut.getIdleTime();
        this.stoppedTime = rut.getStoppedTime();
        this.routeName = rut.getRouteName();
        this.startTime = rut.getStartTime();
        this.endTime = rut.getEndTime();

    }

    public DTORotue(Vehicle vehicle, List<Gs100> latLngList, double roadTraveled, double timeOfTravel, double idleTime, double stoppedTime, String routeName, Timestamp startTime, Timestamp endTime) {
        this.vehicle = vehicle;
        this.latLngList = latLngList;
        this.roadTraveled = roadTraveled;
        this.timeOfTravel = timeOfTravel;
        this.idleTime = idleTime;
        this.stoppedTime = stoppedTime;
        this.routeName = routeName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<Gs100> getLatLngList() {
        return latLngList;
    }

    public void setLatLngList(List<Gs100> latLngList) {
        this.latLngList = latLngList;
    }

    public double getRoadTraveled() {
        return roadTraveled;
    }

    public void setRoadTraveled(double roadTraveled) {
        this.roadTraveled = roadTraveled;
    }

    public double getTimeOfTravel() {
        return timeOfTravel;
    }

    public void setTimeOfTravel(double timeOfTravel) {
        this.timeOfTravel = timeOfTravel;
    }

    public double getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(double idleTime) {
        this.idleTime = idleTime;
    }

    public double getStoppedTime() {
        return stoppedTime;
    }

    public void setStoppedTime(double stoppedTime) {
        this.stoppedTime = stoppedTime;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}
