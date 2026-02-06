package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "vehicle_vehicle_group")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VehicleVehicleGroup {

    @Id
    @GeneratedValue
    @Column(name = "vehicle_vehicle_group_id")
    private int vehicleVehicleGroupId;
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @JsonIgnore
    private Vehicle vehicle;
    @ManyToOne
    @JoinColumn(name = "vehicle_group_id")
    private VehicleGroup vehicleGroup;




}
