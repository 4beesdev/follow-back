package rs.oris.back.domain.dto.daily_movement_consumption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyMovementConsumptionReportAddutionalDataDTO {
    private String registration;
    private String manufacturer;
    private String model;
    private double engineSize;
    private String imei;

}
