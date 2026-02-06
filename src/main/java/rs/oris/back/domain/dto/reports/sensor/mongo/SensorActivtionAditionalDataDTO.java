package rs.oris.back.domain.dto.reports.sensor.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorActivtionAditionalDataDTO {
    private String registration;
    private String model;
}
