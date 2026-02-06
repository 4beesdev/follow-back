package rs.oris.back.domain;

import lombok.ToString;

import java.util.HashMap;

public class Gs100 {
    private HashMap<String,Object> id;
    private String imei;
    private Gps gps;
    private HashMap<String,Double> io;
    private String rfid;

    private String driverName;

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Gs100() {
        io = new HashMap<>();
    }


    public Gs100(HashMap<String, Object> id, String imei, Gps gps, HashMap<String, Double> io) {
        this.id = id;
        this.imei = imei;
        this.gps = gps;
        this.io = io;
    }

    public Gs100(HashMap<String, Object> id, String imei, Gps gps, HashMap<String, Double> io, String rfid) {
        this.id = id;
        this.imei = imei;
        this.gps = gps;
        this.io = io;
        this.rfid = rfid;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public HashMap<String, Object> getId() {
        return id;
    }

    public void setId(HashMap<String, Object> id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }

    public HashMap<String, Double> getIo() {
        return io;
    }

    public void setIo(HashMap<String, Double> io) {
        this.io = io;
    }

    @Override
    public String toString() {
        return "Gs100{" +
                "id=" + id +
                ", imei='" + imei + '\'' +
                ", gps=" + gps +
                ", io=" + io +
                ", rfid='" + rfid + '\'' +
                ", driverName='" + driverName + '\'' +
                '}';
    }
}
