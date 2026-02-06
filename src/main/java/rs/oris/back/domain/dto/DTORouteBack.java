package rs.oris.back.domain.dto;

import rs.oris.back.domain.Firm;
import rs.oris.back.domain.Route;
import rs.oris.back.domain.VehicleRoute;

import java.util.Set;

public class DTORouteBack {
    private int routeId;
    private String name;
    private Firm firm;
    private Set<VehicleRoute> vehicleRouteSet;

    public DTORouteBack() {
    }

    public DTORouteBack(Route route) {
        this.routeId = route.getRouteId();
        this.setName(route.getName());
        this.setFirm(route.getFirm());
        this.setVehicleRouteSet(route.getVehicleRouteSet());
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public Set<VehicleRoute> getVehicleRouteSet() {
        return vehicleRouteSet;
    }

    public void setVehicleRouteSet(Set<VehicleRoute> vehicleRouteSet) {
        this.vehicleRouteSet = vehicleRouteSet;
    }
}
