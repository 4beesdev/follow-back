package rs.oris.back.domain;

public class VGR {
    private Vehicle vehicle;
    private Geozone geozone;
    private long stajanje;
    private long mirovanje;
    private long entryTime;
    private long exitTime;
    private double millage;
    private double fuelStart;
    private double fuelEnd;
    private double fuelSpend;
    private String driverName;

    public VGR() {
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public double getFuelStart() {
        return fuelStart;
    }

    public void setFuelStart(double fuelStart) {
        this.fuelStart = fuelStart;
    }

    public double getFuelEnd() {
        return fuelEnd;
    }

    public void setFuelEnd(double fuelEnd) {
        this.fuelEnd = fuelEnd;
    }

    public double getFuelSpend() {
        return fuelSpend;
    }

    public void setFuelSpend(double fuelSpend) {
        this.fuelSpend = fuelSpend;
    }

    public VGR(Vehicle vehicle, Geozone geozone, long stajanje, long mirovanje, long entryTime, long exitTime, double millage) {
        this.vehicle = vehicle;
        this.geozone = geozone;
        this.stajanje = stajanje;
        this.mirovanje = mirovanje;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.millage = millage;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Geozone getGeozone() {
        return geozone;
    }

    public void setGeozone(Geozone geozone) {
        this.geozone = geozone;
    }

    public long getStajanje() {
        return stajanje;
    }

    public void setStajanje(long stajanje) {
        this.stajanje = stajanje;
    }

    public long getMirovanje() {
        return mirovanje;
    }

    public void setMirovanje(long mirovanje) {
        this.mirovanje = mirovanje;
    }

    public long getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(long entryTime) {
        this.entryTime = entryTime;
    }

    public long getExitTime() {
        return exitTime;
    }

    public void setExitTime(long exitTime) {
        this.exitTime = exitTime;
    }

    public double getMillage() {
        return millage;
    }

    public void setMillage(double millage) {
        this.millage = millage;
    }
}
