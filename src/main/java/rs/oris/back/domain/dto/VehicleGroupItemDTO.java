package rs.oris.back.domain.dto;

public class VehicleGroupItemDTO {

    private VehicleGroupBriefDTO vehicleGroup;

    public VehicleGroupItemDTO() {}

    public VehicleGroupItemDTO(VehicleGroupBriefDTO vehicleGroup) {
        this.vehicleGroup = vehicleGroup;
    }

    public VehicleGroupBriefDTO getVehicleGroup() {
        return vehicleGroup;
    }

    public void setVehicleGroup(VehicleGroupBriefDTO vehicleGroup) {
        this.vehicleGroup = vehicleGroup;
    }
}
