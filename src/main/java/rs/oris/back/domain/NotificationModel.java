package rs.oris.back.domain;

import lombok.ToString;

import javax.persistence.Column;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDate;

@ToString
public class NotificationModel {
    int nmid;
    private int userId;
    private String imei;
    private int vehicleId;
    private String manufacturer;
    private String model;
    private String registration;
    private Timestamp lastTriggered;
    private int notificationId;
    private String notificationName;
    private double centerLat;
    private double centerLng;
    private double radius;
    private boolean gs100;
    private Polygon polygon;
    private String geozoneName;
    private String polygonJasonce;
    private boolean sms;
    private boolean email;
    private boolean push=false;


    private int serviceIntervalLength;
    private int serviceIntervalNotif;
    private double vehicleMileage;
    private Double gpsMileage=0.0;
    private Double vehicleMileageReminder=0.0;
    private Double startPointLat;

    public Integer getEmptyingFuelMargine() {
        return emptyingFuelMargine;
    }

    public void setEmptyingFuelMargine(Integer emptyingFuelMargine) {
        this.emptyingFuelMargine = emptyingFuelMargine;
    }

    private Double startPointLng;
    private Boolean isMileage=false;
    private Long startTimestamp;
    private LocalDate vehicleRegistration;
    private Integer notifyDaysBeforeVehicleRegistration;

    //gps
    private Integer serviceIntervalLengthGps;
    private Integer serviceIntervalNotifGps;

    public Integer getServiceIntervalLengthGps() {
        return serviceIntervalLengthGps;
    }

    public void setServiceIntervalLengthGps(Integer serviceIntervalLengthGps) {
        this.serviceIntervalLengthGps = serviceIntervalLengthGps;
    }

    public Integer getServiceIntervalNotifGps() {
        return serviceIntervalNotifGps;
    }

    public void setServiceIntervalNotifGps(Integer serviceIntervalNotifGps) {
        this.serviceIntervalNotifGps = serviceIntervalNotifGps;
    }

    public Double getKilometers() {
        return kilometers;
    }

    public void setKilometers(Double kilometers) {
        this.kilometers = kilometers;
    }

    private Double kilometers;
    //Fuel
    private Boolean fuelDiffFound;

    public Boolean getFuelDiffFound() {
        return fuelDiffFound;
    }

    public void setFuelDiffFound(Boolean fuelDiffFound) {
        this.fuelDiffFound = fuelDiffFound;
    }

    public Integer getServiceIntervalLengthBoard() {
        return serviceIntervalLengthBoard;
    }

    public void setServiceIntervalLengthBoard(Integer serviceIntervalLengthBoard) {
        this.serviceIntervalLengthBoard = serviceIntervalLengthBoard;
    }

    public Integer getServiceIntervalNotifBoard() {
        return serviceIntervalNotifBoard;
    }

    public void setServiceIntervalNotifBoard(Integer serviceIntervalNotifBoard) {
        this.serviceIntervalNotifBoard = serviceIntervalNotifBoard;
    }

    private Integer emptyingFuelMargine;
    private Integer serviceIntervalLengthBoard;
    private Integer serviceIntervalNotifBoard;
    public LocalDate getVehicleRegistration() {
        return vehicleRegistration;
    }

    public void setVehicleRegistration(LocalDate vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }

    public Integer getNotifyDaysBeforeVehicleRegistration() {
        return notifyDaysBeforeVehicleRegistration;
    }

    public void setNotifyDaysBeforeVehicleRegistration(Integer notifyDaysBeforeVehicleRegistration) {
        this.notifyDaysBeforeVehicleRegistration = notifyDaysBeforeVehicleRegistration;
    }

    public Double getGpsMileage() {
        return gpsMileage;
    }

    public void setGpsMileage(Double gpsMileage) {
        this.gpsMileage = gpsMileage;
    }

    public Double getVehicleMileageReminder() {
        return vehicleMileageReminder;
    }

    public void setVehicleMileageReminder(Double vehicleMileageReminder) {
        this.vehicleMileageReminder = vehicleMileageReminder;
    }

    public Double getStartPointLat() {
        return startPointLat;
    }

    public void setStartPointLat(Double startPointLat) {
        this.startPointLat = startPointLat;
    }

    public Double getStartPointLng() {
        return startPointLng;
    }

    public void setStartPointLng(Double startPointLng) {
        this.startPointLng = startPointLng;
    }

    public Boolean getMileage() {
        return isMileage;
    }

    public void setMileage(Boolean mileage) {
        isMileage = mileage;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public double getVehicleMileage() {
        return vehicleMileage;
    }

    public void setVehicleMileage(double vehicleMileage) {
        this.vehicleMileage = vehicleMileage;
    }

    public int getServiceIntervalLength() {
        return serviceIntervalLength;
    }

    public void setServiceIntervalLength(int serviceIntervalLength) {
        this.serviceIntervalLength = serviceIntervalLength;
    }

    public int getServiceIntervalNotif() {
        return serviceIntervalNotif;
    }

    public void setServiceIntervalNotif(int serviceIntervalNotif) {
        this.serviceIntervalNotif = serviceIntervalNotif;
    }

    public int getFirmId() {
        return firmId;
    }

    public void setFirmId(int firmId) {
        this.firmId = firmId;
    }

    private int firmId;

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }

    private int speed;
    private int fuelMargin;
    private Double engine;


    public NotificationModel() {
    }

    public NotificationModel(int nmid, int userId, String imei, int vehicleId, String manufacturer, String model, String registration, Timestamp lastTriggered, int notificationId, String notificationName, double centerLat, double centerLng, double radius, boolean gs100, Polygon polygon, String geozoneName, String polygonJasonce, boolean sms, boolean email,boolean push) {
        this.nmid = nmid;
        this.userId = userId;
        this.imei = imei;
        this.vehicleId = vehicleId;
        this.manufacturer = manufacturer;
        this.model = model;
        this.registration = registration;
        this.lastTriggered = lastTriggered;
        this.notificationId = notificationId;
        this.notificationName = notificationName;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.radius = radius;
        this.gs100 = gs100;
        this.polygon = polygon;
        this.geozoneName = geozoneName;
        this.polygonJasonce = polygonJasonce;
        this.sms = sms;
        this.email = email;
    }

    public NotificationModel(int nmid, int userId, String imei, int vehicleId, String manufacturer, String model, String registration, Timestamp lastTriggered, int notificationId, String notificationName, double centerLat, double centerLng, double radius, boolean gs100, Polygon polygon, String geozoneName, String polygonJasonce, boolean sms, boolean email,boolean push, int speed) {
        this.nmid = nmid;
        this.userId = userId;
        this.imei = imei;
        this.vehicleId = vehicleId;
        this.manufacturer = manufacturer;
        this.model = model;
        this.registration = registration;
        this.lastTriggered = lastTriggered;
        this.notificationId = notificationId;
        this.notificationName = notificationName;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.radius = radius;
        this.gs100 = gs100;
        this.polygon = polygon;
        this.geozoneName = geozoneName;
        this.polygonJasonce = polygonJasonce;
        this.sms = sms;
        this.email = email;
        this.speed = speed;
    }

    public NotificationModel(int nmid, int userId, String imei, int vehicleId, String manufacturer, String model, String registration, Timestamp lastTriggered, int notificationId, String notificationName, double centerLat, double centerLng, double radius, boolean gs100, Polygon polygon, String geozoneName, String polygonJasonce, boolean sms, boolean email,boolean push,int speed, int fuelMargin) {
        this.nmid = nmid;
        this.userId = userId;
        this.imei = imei;
        this.vehicleId = vehicleId;
        this.manufacturer = manufacturer;
        this.model = model;
        this.registration = registration;
        this.lastTriggered = lastTriggered;
        this.notificationId = notificationId;
        this.notificationName = notificationName;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.radius = radius;
        this.gs100 = gs100;
        this.polygon = polygon;
        this.geozoneName = geozoneName;
        this.polygonJasonce = polygonJasonce;
        this.sms = sms;
        this.email = email;
        this.speed = speed;
        this.fuelMargin = fuelMargin;
    }

    public Double getEngine() {
        return engine;
    }

    public void setEngine(Double engine) {
        this.engine = engine;
    }

    public int getFuelMargin() {
        return fuelMargin;
    }

    public void setFuelMargin(int fuelMargin) {
        this.fuelMargin = fuelMargin;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isSms() {
        return sms;
    }

    public void setSms(boolean sms) {
        this.sms = sms;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public int getNmid() {
        return nmid;
    }

    public void setNmid(int nmid) {
        this.nmid = nmid;
    }

    public String getPolygonJasonce() {
        return polygonJasonce;
    }

    public void setPolygonJasonce(String polygonJasonce) {
        this.polygonJasonce = polygonJasonce;
    }

    public String getGeozoneName() {
        return geozoneName;
    }

    public void setGeozoneName(String geozoneName) {
        this.geozoneName = geozoneName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public Timestamp getLastTriggered() {
        return lastTriggered;
    }

    public void setLastTriggered(Timestamp lastTriggered) {
        this.lastTriggered = lastTriggered;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationName() {
        return notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(double centerLat) {
        this.centerLat = centerLat;
    }

    public double getCenterLng() {
        return centerLng;
    }

    public void setCenterLng(double centerLng) {
        this.centerLng = centerLng;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isGs100() {
        return gs100;
    }

    public void setGs100(boolean gs100) {
        this.gs100 = gs100;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }


}
