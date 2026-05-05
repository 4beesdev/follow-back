package rs.oris.back.domain.dto.report.monthly;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthFuelReportEngineDTO {
    private String imei;
    private Double fuelMargine;
    private String registration;
    private String model;
}
