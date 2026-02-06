package rs.oris.back.domain.reports.driver_relation_fuel.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRoutingInfo {
    private GpsVehicleRoutingInfo gps;
    private IoVehicleRoutingInfo io;
}
