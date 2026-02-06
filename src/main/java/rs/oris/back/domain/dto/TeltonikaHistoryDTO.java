package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.Teltonika;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeltonikaHistoryDTO {

    private Teltonika teltonika;
    private String driver;
}
