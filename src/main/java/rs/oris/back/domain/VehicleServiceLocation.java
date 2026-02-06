package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "vehicle_service_location")
public class VehicleServiceLocation {
    @Id
    @GeneratedValue
    @Column(name = "vehicle_service_location_id")
    private int vehicleServiceLocationId;
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @JsonIgnore
    private Vehicle vehicle;
    @ManyToOne
    @JoinColumn(name = "service_location_id")
    private ServiceLocation serviceLocation;
    private Date date;
    private double expenses;
    private String explained;


    public VehicleServiceLocation() {
    }

    public VehicleServiceLocation(int vehicleServiceLocationId, Vehicle vehicle, ServiceLocation serviceLocation, Date date, double expenses, String explained) {
        this.vehicleServiceLocationId = vehicleServiceLocationId;
        this.vehicle = vehicle;
        this.serviceLocation = serviceLocation;
        this.date = date;
        this.expenses = expenses;
        this.explained = explained;
    }

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public String getExplained() {
        return explained;
    }

    public void setExplained(String explained) {
        this.explained = explained;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getVehicleServiceLocationId() {
        return vehicleServiceLocationId;
    }

    public void setVehicleServiceLocationId(int vehicleServiceLocationId) {
        this.vehicleServiceLocationId = vehicleServiceLocationId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public ServiceLocation getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(ServiceLocation serviceLocation) {
        this.serviceLocation = serviceLocation;
    }
}
