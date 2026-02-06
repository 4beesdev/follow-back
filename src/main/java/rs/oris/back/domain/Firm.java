package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "firm")
public class Firm {

    @Id
    @GeneratedValue
    @Column(name = "firm_id")
    private int firmId;
    private String name;
    private String email;
    private String phone;

    public Boolean getNewReports() {
        return newReports;
    }

    public void setNewReports(Boolean newReports) {
        this.newReports = newReports;
    }

    private Boolean active=true;
    private Boolean newReports;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<User> users;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Group> groupSet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Vehicle> vehicleSet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<VehicleGroup> vehicleGroupSet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Driver> driverSet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<DriverGroup> driverGroupSet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Geozone> geozoneSet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<GeozoneGroup> geozoneGroupSet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Route> routeSet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<ServiceLocation> serviceLocationSet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Intervention> interventionSet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<FuelCompany> fuelCompanySet;
    @OneToMany(mappedBy = "firm", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<AparatType> aparatTypeSet;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<UserReport> userReportSet;
    private Boolean fms;
    private Boolean tracking;


    public Firm() {
    }

    public Firm(int firmId, String name, String email, String phone, Boolean active, Set<User> users, Set<Group> groupSet, Set<Vehicle> vehicleSet, Set<VehicleGroup> vehicleGroupSet, Set<Driver> driverSet, Set<DriverGroup> driverGroupSet, Set<Geozone> geozoneSet, Set<GeozoneGroup> geozoneGroupSet, Set<Route> routeSet, Set<ServiceLocation> serviceLocationSet, Set<Intervention> interventionSet, Set<FuelCompany> fuelCompanySet, Set<AparatType> aparatTypeSet, Set<UserReport> userReportSet) {
        this.firmId = firmId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.active = active;
        this.users = users;
        this.groupSet = groupSet;
        this.vehicleSet = vehicleSet;
        this.vehicleGroupSet = vehicleGroupSet;
        this.driverSet = driverSet;
        this.driverGroupSet = driverGroupSet;
        this.geozoneSet = geozoneSet;
        this.geozoneGroupSet = geozoneGroupSet;
        this.routeSet = routeSet;
        this.serviceLocationSet = serviceLocationSet;
        this.interventionSet = interventionSet;
        this.fuelCompanySet = fuelCompanySet;
        this.aparatTypeSet = aparatTypeSet;
        this.userReportSet = userReportSet;
    }

    public Firm(int firmId, String name, String email, String phone, Boolean active, Set<User> users, Set<Group> groupSet, Set<Vehicle> vehicleSet, Set<VehicleGroup> vehicleGroupSet, Set<Driver> driverSet, Set<DriverGroup> driverGroupSet, Set<Geozone> geozoneSet, Set<GeozoneGroup> geozoneGroupSet, Set<Route> routeSet, Set<ServiceLocation> serviceLocationSet, Set<Intervention> interventionSet, Set<FuelCompany> fuelCompanySet, Set<AparatType> aparatTypeSet, Set<UserReport> userReportSet, Boolean fms, Boolean tracking) {
        this.firmId = firmId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.active = active;
        this.users = users;
        this.groupSet = groupSet;
        this.vehicleSet = vehicleSet;
        this.vehicleGroupSet = vehicleGroupSet;
        this.driverSet = driverSet;
        this.driverGroupSet = driverGroupSet;
        this.geozoneSet = geozoneSet;
        this.geozoneGroupSet = geozoneGroupSet;
        this.routeSet = routeSet;
        this.serviceLocationSet = serviceLocationSet;
        this.interventionSet = interventionSet;
        this.fuelCompanySet = fuelCompanySet;
        this.aparatTypeSet = aparatTypeSet;
        this.userReportSet = userReportSet;
        this.fms = fms;
        this.tracking = tracking;
    }

    public Firm(int firmId, String name, String email, String phone, Boolean active, Set<User> users, Set<Group> groupSet, Set<Vehicle> vehicleSet, Set<VehicleGroup> vehicleGroupSet, Set<Driver> driverSet, Set<DriverGroup> driverGroupSet, Set<Geozone> geozoneSet, Set<GeozoneGroup> geozoneGroupSet, Set<Route> routeSet, Set<ServiceLocation> serviceLocationSet, Set<Intervention> interventionSet, Set<FuelCompany> fuelCompanySet, Set<AparatType> aparatTypeSet) {
        this.firmId = firmId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.active = active;
        this.users = users;
        this.groupSet = groupSet;
        this.vehicleSet = vehicleSet;
        this.vehicleGroupSet = vehicleGroupSet;
        this.driverSet = driverSet;
        this.driverGroupSet = driverGroupSet;
        this.geozoneSet = geozoneSet;
        this.geozoneGroupSet = geozoneGroupSet;
        this.routeSet = routeSet;
        this.serviceLocationSet = serviceLocationSet;
        this.interventionSet = interventionSet;
        this.fuelCompanySet = fuelCompanySet;
        this.aparatTypeSet = aparatTypeSet;
    }

    public Boolean getFms() {
        return fms;
    }

    public void setFms(Boolean fms) {
        this.fms = fms;
    }

    public Boolean getTracking() {
        return tracking;
    }

    public void setTracking(Boolean tracking) {
        this.tracking = tracking;
    }

    public Set<UserReport> getUserReportSet() {
        return userReportSet;
    }

    public void setUserReportSet(Set<UserReport> userReportSet) {
        this.userReportSet = userReportSet;
    }

    public Set<AparatType> getAparatTypeSet() {
        return aparatTypeSet;
    }

    public void setAparatTypeSet(Set<AparatType> aparatTypeSet) {
        this.aparatTypeSet = aparatTypeSet;
    }

    public Set<FuelCompany> getFuelCompanySet() {
        return fuelCompanySet;
    }

    public void setFuelCompanySet(Set<FuelCompany> fuelCompanySet) {
        this.fuelCompanySet = fuelCompanySet;
    }

    public Set<Intervention> getInterventionSet() {
        return interventionSet;
    }

    public void setInterventionSet(Set<Intervention> interventionSet) {
        this.interventionSet = interventionSet;
    }

    public Set<ServiceLocation> getServiceLocationSet() {
        return serviceLocationSet;
    }

    public void setServiceLocationSet(Set<ServiceLocation> serviceLocationSet) {
        this.serviceLocationSet = serviceLocationSet;
    }

    public Set<Driver> getDriverSet() {
        return driverSet;
    }

    public void setDriverSet(Set<Driver> driverSet) {
        this.driverSet = driverSet;
    }

    public Set<DriverGroup> getDriverGroupSet() {
        return driverGroupSet;
    }

    public void setDriverGroupSet(Set<DriverGroup> driverGroupSet) {
        this.driverGroupSet = driverGroupSet;
    }

    public Set<Route> getRouteSet() {
        return routeSet;
    }

    public void setRouteSet(Set<Route> routeSet) {
        this.routeSet = routeSet;
    }

    public Set<GeozoneGroup> getGeozoneGroupSet() {
        return geozoneGroupSet;
    }

    public void setGeozoneGroupSet(Set<GeozoneGroup> geozoneGroupSet) {
        this.geozoneGroupSet = geozoneGroupSet;
    }

    public Set<Geozone> getGeozoneSet() {
        return geozoneSet;
    }

    public void setGeozoneSet(Set<Geozone> geozoneSet) {
        this.geozoneSet = geozoneSet;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Vehicle> getVehicleSet() {
        return vehicleSet;
    }

    public void setVehicleSet(Set<Vehicle> vehicleSet) {
        this.vehicleSet = vehicleSet;
    }

    public Set<VehicleGroup> getVehicleGroupSet() {
        return vehicleGroupSet;
    }

    public void setVehicleGroupSet(Set<VehicleGroup> vehicleGroupSet) {
        this.vehicleGroupSet = vehicleGroupSet;
    }

    public Set<Group> getGroupSet() {
        return groupSet;
    }

    public void setGroupSet(Set<Group> groupSet) {
        this.groupSet = groupSet;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public int getFirmId() {
        return firmId;
    }

    public void setFirmId(int firmId) {
        this.firmId = firmId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
