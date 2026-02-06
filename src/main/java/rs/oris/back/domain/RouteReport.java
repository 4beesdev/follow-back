package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteReport {

    private long timestampStart;
    private double latStart;
    private double lngStart;
    private long timestampEnd;
    private double latEnd;
    private double lngEnd;
    private double roadTraveled;
    private String startAddress;
    private String endAddress;
    private List<Gps> gpsList;

    private double time;
    private double idleTime;
    private double averageSpeed;
    private double maxSpeed;
    private Double boardKmDiffStartEnd=0.0;
    private Double startBoardInKm =0.0;
    private Double endBoardInKm =0.0;
    private Double startEngineWorkTimeInHours =0.0;
    private Double endEngineWorkTimeInHours =0.0;
    private Double diffEngineWorkTimeInHours=0.0;

    private String imei;

    public Double getBoardKmDiffStartEnd() {
        return boardKmDiffStartEnd;
    }

    public void setBoardKmDiffStartEnd(Double boardKmDiffStartEnd) {
        this.boardKmDiffStartEnd = boardKmDiffStartEnd;
    }

    public Double getStartBoardInKm() {
        return startBoardInKm;
    }

    public void setStartBoardInKm(Double startBoardInKm) {
        this.startBoardInKm = startBoardInKm;
    }

    public Double getEndBoardInKm() {
        return endBoardInKm;
    }

    public void setEndBoardInKm(Double endBoardInKm) {
        this.endBoardInKm = endBoardInKm;
    }

    public Double getStartEngineWorkTimeInHours() {
        return startEngineWorkTimeInHours;
    }

    public void setStartEngineWorkTimeInHours(Double startEngineWorkTimeInHours) {
        this.startEngineWorkTimeInHours = startEngineWorkTimeInHours;
    }

    public Double getEndEngineWorkTimeInHours() {
        return endEngineWorkTimeInHours;
    }

    public void setEndEngineWorkTimeInHours(Double endEngineWorkTimeInHours) {
        this.endEngineWorkTimeInHours = endEngineWorkTimeInHours;
    }

    public Double getDiffEngineWorkTimeInHours() {
        return diffEngineWorkTimeInHours;
    }

    public void setDiffEngineWorkTimeInHours(Double diffEngineWorkTimeInHours) {
        this.diffEngineWorkTimeInHours = diffEngineWorkTimeInHours;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public RouteReport() {
    }

    public RouteReport(long timestampStart, double latStart, double lngStart, long timestampEnd, double latEnd, double lngEnd, double roadTraveled,
                       String startAddress, String endAddress, List<Gps> gpsList, double time, double idleTime, double averageSpeed, double maxSpeed,
                       Double boardKmDiffStartEnd, Double startBoardInKm, Double endBoardInKm, Double startEngineWorkTimeInHours, Double endEngineWorkTimeInHours,
                       Double diffEngineWorkTimeInHours, String imei) {
        this.timestampStart = timestampStart;
        this.latStart = latStart;
        this.lngStart = lngStart;
        this.timestampEnd = timestampEnd;
        this.latEnd = latEnd;
        this.lngEnd = lngEnd;
        this.roadTraveled = roadTraveled;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.gpsList = gpsList;
        this.time = time;
        this.idleTime = idleTime;
        this.averageSpeed = averageSpeed;
        this.maxSpeed = maxSpeed;
        this.boardKmDiffStartEnd = boardKmDiffStartEnd;
        this.startBoardInKm = startBoardInKm;
        this.endBoardInKm = endBoardInKm;
        this.startEngineWorkTimeInHours = startEngineWorkTimeInHours;
        this.endEngineWorkTimeInHours = endEngineWorkTimeInHours;
        this.diffEngineWorkTimeInHours = diffEngineWorkTimeInHours;
        this.imei = imei;
    }

    public RouteReport(long timestampStart, double latStart, double lngStart, long timestampEnd, double latEnd, double lngEnd, double roadTraveled, String startAddress, String endAddress) {
        this.timestampStart = timestampStart;
        this.latStart = latStart;
        this.lngStart = lngStart;
        this.timestampEnd = timestampEnd;
        this.latEnd = latEnd;
        this.lngEnd = lngEnd;
        this.roadTraveled = roadTraveled;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }

    public RouteReport(long timestampStart, double latStart, double lngStart, long timestampEnd, double latEnd, double lngEnd, double roadTraveled, String startAddress, String endAddress, List<Gps> gpsList) {
        this.timestampStart = timestampStart;
        this.latStart = latStart;
        this.lngStart = lngStart;
        this.timestampEnd = timestampEnd;
        this.latEnd = latEnd;
        this.lngEnd = lngEnd;
        this.roadTraveled = roadTraveled;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.gpsList = gpsList;
    }

    public RouteReport(long timestampStart, double latStart, double lngStart, long timestampEnd, double latEnd, double lngEnd, double roadTraveled, String startAddress, String endAddress, List<Gps> gpsList, double time, double idleTime, double averageSpeed, double maxSpeed) {
        this.timestampStart = timestampStart;
        this.latStart = latStart;
        this.lngStart = lngStart;
        this.timestampEnd = timestampEnd;
        this.latEnd = latEnd;
        this.lngEnd = lngEnd;
        this.roadTraveled = roadTraveled;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.gpsList = gpsList;
        this.time = time;
        this.idleTime = idleTime;
        this.averageSpeed = averageSpeed;
        this.maxSpeed = maxSpeed;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(double idleTime) {
        this.idleTime = idleTime;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public List<Gps> getGpsList() {
        return gpsList;
    }

    public void setGpsList(List<Gps> gpsList) {
        this.gpsList = gpsList;
    }

    public long getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(long timestampStart) {
        this.timestampStart = timestampStart;
    }

    public double getLatStart() {
        return latStart;
    }

    public void setLatStart(double latStart) {
        this.latStart = latStart;
    }

    public double getLngStart() {
        return lngStart;
    }

    public void setLngStart(double lngStart) {
        this.lngStart = lngStart;
    }

    public long getTimestampEnd() {
        return timestampEnd;
    }

    public void setTimestampEnd(long timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    public double getLatEnd() {
        return latEnd;
    }

    public void setLatEnd(double latEnd) {
        this.latEnd = latEnd;
    }

    public double getLngEnd() {
        return lngEnd;
    }

    public void setLngEnd(double lngEnd) {
        this.lngEnd = lngEnd;
    }

    public double getRoadTraveled() {
        return roadTraveled;
    }

    public void setRoadTraveled(double roadTraveled) {
        this.roadTraveled = roadTraveled;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    @Override
    public String toString() {
        return "RouteReport{" +
                "timestampStart=" + timestampStart +
                ", latStart=" + latStart +
                ", lngStart=" + lngStart +
                ", timestampEnd=" + timestampEnd +
                ", latEnd=" + latEnd +
                ", lngEnd=" + lngEnd +
                ", roadTraveled=" + roadTraveled +
                ", startAddress='" + startAddress + '\'' +
                ", endAddress='" + endAddress + '\'' +
                ", gpsList=" + gpsList +
                ", time=" + time +
                ", idleTime=" + idleTime +
                ", averageSpeed=" + averageSpeed +
                ", maxSpeed=" + maxSpeed +
                ", boardKmDiffStartEnd=" + boardKmDiffStartEnd +
                ", startBoardInKm=" + startBoardInKm +
                ", endBoardInKm=" + endBoardInKm +
                ", startEngineWorkTimeInHours=" + startEngineWorkTimeInHours +
                ", endEngineWorkTimeInHours=" + endEngineWorkTimeInHours +
                ", diffEngineWorkTimeInHours=" + diffEngineWorkTimeInHours +
                ", imei='" + imei + '\'' +
                '}';
    }
}
