package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ipp {
    private String registraiton;
    private String model;
    private String type;
    private String manufacturer;
    private double roadTraveled;
    private double timeSleeping;
    private double timeIdle;
    private double timeOfTravel;
    private double accuracy;
    private double avgSpeed;


    private String startTime;  // ili LocalDateTime
    private String endTime;

    public Ipp() {
    }

    public Ipp(String registraiton, String model, String type, String manufacturer,
               double roadTraveled, double timeSleeping, double timeIdle,
               double timeOfTravel, double accuracy, double avgSpeed,
               String startTime, String endTime) {
        this.registraiton = registraiton;
        this.model = model;
        this.type = type;
        this.manufacturer = manufacturer;
        this.roadTraveled = roadTraveled;
        this.timeSleeping = timeSleeping;
        this.timeIdle = timeIdle;
        this.timeOfTravel = timeOfTravel;
        this.accuracy = accuracy;
        this.avgSpeed = avgSpeed;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Ipp(String registraiton, String model, String type, String manufacturer, double roadTraveled, double timeSleeping, double timeIdle, double timeOfTravel, double accuracy, double avgSpeed) {
        this.registraiton = registraiton;
        this.model = model;
        this.type = type;
        this.manufacturer = manufacturer;
        this.roadTraveled = roadTraveled;
        this.timeSleeping = timeSleeping;
        this.timeIdle = timeIdle;
        this.timeOfTravel = timeOfTravel;
        this.accuracy = accuracy;
        this.avgSpeed = avgSpeed;
    }

    public Ipp(double roadTraveled, double timeSleeping, double timeIdle, double timeOfTravel, double accuracy, double avgSpeed) {
        this.roadTraveled = roadTraveled;
        this.timeSleeping = timeSleeping;
        this.timeIdle = timeIdle;
        this.timeOfTravel = timeOfTravel;
        this.accuracy = accuracy;
        this.avgSpeed = avgSpeed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getRoadTraveled() {
        return roadTraveled;
    }

    public void setRoadTraveled(double roadTraveled) {
        this.roadTraveled = roadTraveled;
    }

    public double getTimeSleeping() {
        return timeSleeping;
    }

    public void setTimeSleeping(double timeSleeping) {
        this.timeSleeping = timeSleeping;
    }

    public double getTimeIdle() {
        return timeIdle;
    }

    public void setTimeIdle(double timeIdle) {
        this.timeIdle = timeIdle;
    }

    public double getTimeOfTravel() {
        return timeOfTravel;
    }

    public void setTimeOfTravel(double timeOfTravel) {
        this.timeOfTravel = timeOfTravel;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
