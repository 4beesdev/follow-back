package rs.oris.back.domain.dto.report.driver_relation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverRelationProjectionData {
    private Integer driverId;
    private String driverNumber;
    private String driverName;
}
