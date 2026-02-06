package rs.oris.back.domain.dto.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverRelationVehicleRoutingInfo {
    private double engineSize;
    private String registration;
}
