package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "vehicle_route")
public class VehicleRoute {

    @Id
    @GeneratedValue
    @Column(name = "vehicle_route_id")
    private int vehicleRouteId;
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @ManyToOne
    @JoinColumn(name = "route_id")
    @JsonIgnore
    private Route route;
    private long fromRoute;
    private long toRoute;


    public VehicleRoute() {
    }

    public VehicleRoute(int vehicleRouteId, Vehicle vehicle, Route route, long fromRoute, long toRoute) {
        this.vehicleRouteId = vehicleRouteId;
        this.vehicle = vehicle;
        this.route = route;
        this.fromRoute = fromRoute;
        this.toRoute = toRoute;
    }

    public int getVehicleRouteId() {
        return vehicleRouteId;
    }

    public void setVehicleRouteId(int vehicleRouteId) {
        this.vehicleRouteId = vehicleRouteId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public long getFromRoute() {
        return fromRoute;
    }

    public void setFromRoute(long fromRoute) {
        this.fromRoute = fromRoute;
    }

    public long getToRoute() {
        return toRoute;
    }

    public void setToRoute(long toRoute) {
        this.toRoute = toRoute;
    }
}
