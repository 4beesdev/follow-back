package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "vehicle_geozone")
public class VehicleGeozone {

    @Id
    @GeneratedValue
    @Column(name = "vehicle_geozone_id")
    private int vehicleGeozoneId;
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @JsonIgnore
    private Vehicle vehicle;
    @ManyToOne
    @JoinColumn(name = "geozone_id")
    @JsonIgnore
    private Geozone geozone;


    public VehicleGeozone() {
    }

    public VehicleGeozone(int vehicleGeozoneId, Vehicle vehicle, Geozone geozone) {
        this.vehicleGeozoneId = vehicleGeozoneId;
        this.vehicle = vehicle;
        this.geozone = geozone;
    }

    public int getVehicleGeozoneId() {
        return vehicleGeozoneId;
    }

    public void setVehicleGeozoneId(int vehicleGeozoneId) {
        this.vehicleGeozoneId = vehicleGeozoneId;
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
}
