package rs.oris.back.domain;

import java.sql.Timestamp;

public class SpeedReport {
    private String registraiton;
    private String model;
    private Timestamp startTime;
    private Timestamp endTime;
    private double roadTraveled;
    private double timeOfTravel;
    private double maxSpeed;
    private double avgSpeed;

    public SpeedReport() {
    }

    public SpeedReport(String registraiton, String model, Timestamp startTime, Timestamp endTime, double roadTraveled, double timeOfTravel, double maxSpeed, double avgSpeed) {
        this.registraiton = registraiton;
        this.model = model;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roadTraveled = roadTraveled;
        this.timeOfTravel = timeOfTravel;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getRegistraiton() {
        return registraiton;
    }

    public void setRegistraiton(String registraiton) {
        this.registraiton = registraiton;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
}
