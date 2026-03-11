package rs.oris.back.domain.dto;

public class VehicleGroupBriefDTO {

    private int vehicleGroupId;
    private String name;

    public VehicleGroupBriefDTO() {}

    public VehicleGroupBriefDTO(int vehicleGroupId, String name) {
        this.vehicleGroupId = vehicleGroupId;
        this.name = name;
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
}
