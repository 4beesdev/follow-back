package rs.oris.back.domain;

public class Temperature {
    private long timestamp;
    private Double lng;
    private Double lat;
    private Double cabinTemp;
    private Double engineTemp;
    private String imei;
    private String registraiton;

    public Temperature() {
    }

    public Temperature(long timestamp, Double lng, Double lat, Double cabinTemp, Double engineTemp, String imei, String registraiton) {
        this.timestamp = timestamp;
        this.lng = lng;
        this.lat = lat;
        this.cabinTemp = cabinTemp;
        this.engineTemp = engineTemp;
        this.imei = imei;
        this.registraiton = registraiton;
    }

    public String getRegistraiton() {
        return registraiton;
    }

    public void setRegistraiton(String registraiton) {
        this.registraiton = registraiton;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getCabinTemp() {
        return cabinTemp;
    }

    public void setCabinTemp(Double cabinTemp) {
        this.cabinTemp = cabinTemp;
    }

    public Double getEngineTemp() {
        return engineTemp;
    }

    public void setEngineTemp(Double engineTemp) {
        this.engineTemp = engineTemp;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}

