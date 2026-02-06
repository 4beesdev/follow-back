package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KilometersAdministrationDTO {

    private Long id;

    private int vehicleId;
    private String vehicleRegistration;

    private LocalDate date;

    private Double value;
}
