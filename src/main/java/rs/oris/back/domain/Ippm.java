package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ippm {
    private String registration;
    private String manufacturer;
    private String model;
    private double ukupno;
    private Map<Integer, Double> map;

    public Ippm() {
    }

    public Ippm(String registration, String manufacturer, String model, double ukupno, Map<Integer, Double> map) {
        this.registration = registration;
        this.manufacturer = manufacturer;
        this.model = model;
        this.ukupno = ukupno;
        this.map = map;
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

    public double getUkupno() {
        return ukupno;
    }

    public void setUkupno(double ukupno) {
        this.ukupno = ukupno;
    }

    public Map<Integer, Double> getMap() {
        return map;
    }

    public void setMap(Map<Integer, Double> map) {
        this.map = map;
    }
}
