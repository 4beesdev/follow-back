package rs.oris.back.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KilometersUpdateDTO {

    private Double mileage;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private int vehicleId;
}



//mileage
//registrationDate