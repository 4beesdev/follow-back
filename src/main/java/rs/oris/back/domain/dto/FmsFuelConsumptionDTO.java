package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FmsFuelConsumptionDTO {
    private String message;
    private Boolean isSuccess;
    private int rowNum;
}
