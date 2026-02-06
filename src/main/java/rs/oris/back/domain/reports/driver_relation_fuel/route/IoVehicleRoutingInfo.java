package rs.oris.back.domain.reports.driver_relation_fuel.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IoVehicleRoutingInfo {
    private Integer fuelLevel;
    private Integer engineTemperature;
    private Integer engineRpm;
    private Integer vechileDistance;
    private Integer bateryVoltage;
}
