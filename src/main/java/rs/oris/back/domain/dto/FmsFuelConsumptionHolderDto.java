package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.FuelConsumption;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FmsFuelConsumptionHolderDto {
    private String message;
    private List<FmsFuelConsumptionDTO> successData;
    private List<FmsFuelConsumptionDTO> failData;

}
