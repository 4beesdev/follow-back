package rs.oris.back.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;
import rs.oris.back.domain.Gs100;

import java.sql.Timestamp;
import java.util.List;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class DTOSpeed {
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private double roadTraveled;
    private double timeOfTravel;
    private double maxSpeed;
    private double avgSpeed;
    private boolean peak;
    private List<Gs100> gpsList;

    private String imei;
    private String registration;
    private String manufacturer;
    private String model;

    public DTOSpeed() {
    }

    public DTOSpeed(Timestamp startTimestamp, Timestamp endTimestamp, double roadTraveled, double timeOfTravel, double maxSpeed, double avgSpeed, boolean peak, List<Gs100> gpsList, String imei, String registration, String manufacturer, String model) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.roadTraveled = roadTraveled;
        this.timeOfTravel = timeOfTravel;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.peak = peak;
        this.gpsList = gpsList;
        this.imei = imei;
        this.registration = registration;
        this.manufacturer = manufacturer;
        this.model = model;
    }

    public DTOSpeed(Timestamp startTimestamp, Timestamp endTimestamp, double roadTraveled, double timeOfTravel, double maxSpeed, double avgSpeed, boolean peak, List<Gs100> gpsList) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.roadTraveled = roadTraveled;
        this.timeOfTravel = timeOfTravel;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.peak = peak;
        this.gpsList = gpsList;
    }

    public DTOSpeed(DTOSpeed sp) {
        this.startTimestamp = sp.getStartTimestamp();
        this.endTimestamp = sp.getEndTimestamp();
        this.roadTraveled = sp.getRoadTraveled();
        this.timeOfTravel = sp.getTimeOfTravel();
        this.maxSpeed = sp.getMaxSpeed();
        this.avgSpeed = sp.getAvgSpeed();
        this.peak = sp.isPeak();
        this.gpsList = sp.getGpsList();
    }


    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Timestamp startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Timestamp getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Timestamp endTimestamp) {
        this.endTimestamp = endTimestamp;
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

    public boolean isPeak() {
        return peak;
    }

    public void setPeak(boolean peak) {
        this.peak = peak;
    }

    public List<Gs100> getGpsList() {
        return gpsList;
    }

    public void setGpsList(List<Gs100> gpsList) {
        this.gpsList = gpsList;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
