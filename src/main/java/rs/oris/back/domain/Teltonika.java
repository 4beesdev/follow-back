package rs.oris.back.domain;



import java.util.HashMap;


public class Teltonika {

    private HashMap<Object,Object> id;
    private String imei;
    private Gps gps;
    private HashMap<String,Double> io;
    private String rfid;

    public Teltonika() {
        io = new HashMap<>();
    }


    public Teltonika(HashMap<Object, Object> id, String imei, Gps gps, HashMap<String, Double> io) {
        this.id = id;
        this.imei = imei;
        this.gps = gps;
        this.io = io;
    }

    public Teltonika(HashMap<Object, Object> id, String imei, Gps gps, HashMap<String, Double> io, String rfid) {
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

    public HashMap<Object, Object> getId() {
        return id;
    }

    public void setId(HashMap<Object, Object> id) {
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
}
