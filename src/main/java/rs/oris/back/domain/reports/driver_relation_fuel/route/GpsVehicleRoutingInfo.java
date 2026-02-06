package rs.oris.back.domain.reports.driver_relation_fuel.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GpsVehicleRoutingInfo {
    private Long timestsamp;
    private Double latitude;
    private Double longitude;
    private Integer speed;
}
