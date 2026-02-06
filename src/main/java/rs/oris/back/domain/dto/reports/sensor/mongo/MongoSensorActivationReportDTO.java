package rs.oris.back.domain.dto.reports.sensor.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MongoSensorActivationReportDTO {
    //Broj Registracije,
    private String vehicleRegistrationId;


    //Vreme početka
    private LocalDateTime startTime;

    //Vreme kraja
    private LocalDateTime endTime;

    //Vreme trajanja
    private Long duration;

    //Adresa
    private String address;

}
