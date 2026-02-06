package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "geozone")
public class Geozone {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "geozone_id")
    private int geozoneId;
    private String name;
    @Column(length = 10)
    private String type;
    private Double lat;
    private Double lng;
    private double radius;
    private Double latTL;
    private Double lngTL;
    private Double latBR;
    private Double lngBR;
    @Column(length = 65535)
    private String json;
    private boolean active;

    private String color;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @OneToMany(mappedBy = "geozone")
    @JsonIgnore
    private Set<GeozoneGeozoneGroup> geozoneGeozoneGroupSet;
    @OneToMany(mappedBy = "geozone")
    private Set<VehicleGeozone> vehicleGeozoneSet;
    @OneToMany(mappedBy = "notification")
    @JsonIgnore
    private Set<NotificationVehicle> notificationVehicleSet;


    public Geozone() {
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Geozone(int geozoneId, String name, String type, Double lat, Double lng, double radius, Double latTL, Double lngTL, Double latBR, Double lngBR, String json, boolean active, Firm firm, Set<GeozoneGeozoneGroup> geozoneGeozoneGroupSet, Set<VehicleGeozone> vehicleGeozoneSet, Set<NotificationVehicle> notificationVehicleSet) {
        this.geozoneId = geozoneId;
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
        this.latTL = latTL;
        this.lngTL = lngTL;
        this.latBR = latBR;
        this.lngBR = lngBR;
        this.json = json;
        this.active = active;
        this.firm = firm;
        this.geozoneGeozoneGroupSet = geozoneGeozoneGroupSet;
        this.vehicleGeozoneSet = vehicleGeozoneSet;
        this.notificationVehicleSet = notificationVehicleSet;
    }

    public Set<NotificationVehicle> getNotificationVehicleSet() {
        return notificationVehicleSet;
    }

    public void setNotificationVehicleSet(Set<NotificationVehicle> notificationVehicleSet) {
        this.notificationVehicleSet = notificationVehicleSet;
    }

    public Set<VehicleGeozone> getVehicleGeozoneSet() {
        return vehicleGeozoneSet;
    }

    public void setVehicleGeozoneSet(Set<VehicleGeozone> vehicleGeozoneSet) {
        this.vehicleGeozoneSet = vehicleGeozoneSet;
    }

    public int getGeozoneId() {
        return geozoneId;
    }

    public void setGeozoneId(int geozoneId) {
        this.geozoneId = geozoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Double getLatTL() {
        return latTL;
    }

    public void setLatTL(Double latTL) {
        this.latTL = latTL;
    }

    public Double getLngTL() {
        return lngTL;
    }

    public void setLngTL(Double lngTL) {
        this.lngTL = lngTL;
    }

    public Double getLatBR() {
        return latBR;
    }

    public void setLatBR(Double latBR) {
        this.latBR = latBR;
    }

    public Double getLngBR() {
        return lngBR;
    }

    public void setLngBR(Double lngBR) {
        this.lngBR = lngBR;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public Set<GeozoneGeozoneGroup> getGeozoneGeozoneGroupSet() {
        return geozoneGeozoneGroupSet;
    }

    public void setGeozoneGeozoneGroupSet(Set<GeozoneGeozoneGroup> geozoneGeozoneGroupSet) {
        this.geozoneGeozoneGroupSet = geozoneGeozoneGroupSet;
    }
}
