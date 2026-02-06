package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "route")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "route_id")
    private int routeId;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String routeString;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @OneToMany(mappedBy = "route")
    private Set<VehicleRoute> vehicleRouteSet;


    public Route() {
    }

    public Route(int routeId, String name, String routeString, Firm firm, Set<VehicleRoute> vehicleRouteSet) {
        this.routeId = routeId;
        this.name = name;
        this.routeString = routeString;
        this.firm = firm;
        this.vehicleRouteSet = vehicleRouteSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteString() {
        return routeString;
    }

    public void setRouteString(String routeString) {
        this.routeString = routeString;
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
