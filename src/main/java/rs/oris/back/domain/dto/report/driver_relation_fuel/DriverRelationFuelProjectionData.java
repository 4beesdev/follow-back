package rs.oris.back.domain.dto.report.driver_relation_fuel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverRelationFuelProjectionData {

    private String imei;
    private double engineSize;
    private String registration;
    private String model;
}
