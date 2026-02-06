package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.Gs100;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GsHistoryDTO {
    private Gs100 gs100;
    private String driver;
}
