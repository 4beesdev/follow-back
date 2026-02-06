package rs.oris.back.domain.dto.fuel_consumtion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.FuelConsumption;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class FuelConsumptionDTO {
    private List<FuelConsumption> content;
    private Long totalElements;

}
