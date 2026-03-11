package rs.oris.back.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.VehicleGroup;
import rs.oris.back.domain.VehicleVehicleGroup;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VehicleWithGroupsDTO {

    @JsonUnwrapped
    private Vehicle vehicle;

    private List<VehicleGroupItemDTO> vehicleVehicleGroupSet;

    public VehicleWithGroupsDTO() {}

    public VehicleWithGroupsDTO(Vehicle vehicle, List<VehicleGroupItemDTO> vehicleVehicleGroupSet) {
        this.vehicle = vehicle;
        this.vehicleVehicleGroupSet = vehicleVehicleGroupSet;
    }

    public static VehicleWithGroupsDTO from(Vehicle vehicle, Set<VehicleVehicleGroup> groups) {
        List<VehicleGroupItemDTO> groupDTOs;
        if (groups == null || groups.isEmpty()) {
            groupDTOs = Collections.emptyList();
        } else {
            groupDTOs = groups.stream()
                    .map(vvg -> {
                        VehicleGroup vg = vvg.getVehicleGroup();
                        return new VehicleGroupItemDTO(
                                new VehicleGroupBriefDTO(vg.getVehicleGroupId(), vg.getName())
                        );
                    })
                    .collect(Collectors.toList());
        }
        return new VehicleWithGroupsDTO(vehicle, groupDTOs);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<VehicleGroupItemDTO> getVehicleVehicleGroupSet() {
        return vehicleVehicleGroupSet;
    }

    public void setVehicleVehicleGroupSet(List<VehicleGroupItemDTO> vehicleVehicleGroupSet) {
        this.vehicleVehicleGroupSet = vehicleVehicleGroupSet;
    }
}
