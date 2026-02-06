package rs.oris.back.domain.dto.reports.sensor;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.dto.reports.sensor.mongo.MongoSensorActivationReportDTO;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorActivationReportDTO {

    //Broj Registracije,
    private String vehicleRegistrationId;

    //Proizvođač/Model, da li je to manufacturer ili model?
    private String model;

    //Vreme početka
    private LocalDateTime startTime;

    //Vreme kraja
    private LocalDateTime endTime;

    //Vreme trajanja
    private Long duration;

    //Adresa
    private String address;

    // Kontakt brava da li je to polje active u drugom modulu
    private Boolean active;

    public SensorActivationReportDTO(MongoSensorActivationReportDTO mongoSensorActivationReportDTO){
        this.vehicleRegistrationId=mongoSensorActivationReportDTO.getVehicleRegistrationId();
        this.startTime=mongoSensorActivationReportDTO.getStartTime();
        this.endTime=mongoSensorActivationReportDTO.getEndTime();
        this.duration=mongoSensorActivationReportDTO.getDuration();
        this.address=mongoSensorActivationReportDTO.getAddress();
    }
}
