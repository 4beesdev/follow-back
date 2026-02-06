package rs.oris.back.domain.dto.report.driver_relation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverVehicleRelationProjectionData {

    private String imei;
    private String registration;
    private String model;

}
