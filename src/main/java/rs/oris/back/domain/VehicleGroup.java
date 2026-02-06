package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "vehicle_group")
public class VehicleGroup {

    @Id
    @GeneratedValue
    @Column(name = "vehicle_group_id")
    private int vehicleGroupId;
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @OneToMany(mappedBy = "vehicleGroup",cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<VehicleVehicleGroup> vehicleVehicleGroupSet;
    @OneToMany(mappedBy = "vehicleGroup", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<UserVehicleGroup> userVehicleGroupSet;

    public VehicleGroup() {
    }

    public VehicleGroup(int vehicleGroupId, String name, Firm firm, Set<VehicleVehicleGroup> vehicleVehicleGroupSet) {
        this.vehicleGroupId = vehicleGroupId;
        this.name = name;
        this.firm = firm;
        this.vehicleVehicleGroupSet = vehicleVehicleGroupSet;
    }

    public int getVehicleGroupId() {
        return vehicleGroupId;
    }

    public void setVehicleGroupId(int vehicleGroupId) {
        this.vehicleGroupId = vehicleGroupId;
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

    public Set<VehicleVehicleGroup> getVehicleVehicleGroupSet() {
        return vehicleVehicleGroupSet;
    }

    public void setVehicleVehicleGroupSet(Set<VehicleVehicleGroup> vehicleVehicleGroupSet) {
        this.vehicleVehicleGroupSet = vehicleVehicleGroupSet;
    }
}
