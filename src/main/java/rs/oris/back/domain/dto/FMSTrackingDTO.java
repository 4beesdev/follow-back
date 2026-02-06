package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.Intervention;
import rs.oris.back.domain.InterventionDTO;
import rs.oris.back.domain.TechnicalInspection;

import java.util.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class FMSTrackingDTO {

    Integer interventionNumber;
    Date lastInterventionDate;
    Double interventionSum;

    Double distanceTraveled;

    Date lastRegistration;
    Integer numberOfPenaltis;

    Date lastPenaltyDate;

    Double fuelConsumtionTotalAmount;
    List<InterventionDTO> lastInterventions;

    List<TechnicalInspection> technicalInspections;




}
