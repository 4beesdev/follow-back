package rs.oris.back.domain.dto.reports.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorActivtionVehicleDTO {
    private String imei;
    private String registration;
    private String model;
}
