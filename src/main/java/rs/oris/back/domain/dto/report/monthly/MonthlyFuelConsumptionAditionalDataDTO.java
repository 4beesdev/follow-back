package rs.oris.back.domain.dto.report.monthly;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyFuelConsumptionAditionalDataDTO {
    private double engineSize;
    private String registration;
    private String model;
}
