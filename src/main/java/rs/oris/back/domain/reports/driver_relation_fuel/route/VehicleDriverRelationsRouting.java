package rs.oris.back.domain.reports.driver_relation_fuel.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDriverRelationsRouting {
    private String imei;
    private List<VehicleRoutingInfo> data;
}
