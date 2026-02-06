package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class FuelDistanceRequest {

    private List<String> imeis;
    private int fuelMargin = 20; //TODO: change default values
    private int emptyingMargin = 30;//TODO: change default values
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime from, to;


}
