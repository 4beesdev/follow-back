package rs.oris.back.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Data
public class InterventionDTO {
    private int interventionId;
    private Date doneDate;
    private String doneTime;
    private Date neededDate;
    private String neededTime;
    private String description;
    private double price;
    private String note;
    boolean done;
    boolean needed;
    private Vehicle vehicle;
    private ServiceLocation serviceLocation;

    /**
     * Red izvestaja bez priloga. Intervention.interventionFiles je EAGER @Lob
     * (Postgres large object) i ne sme da izadje iz servisnog sloja - citanje
     * takvog polja van transakcije baca "Large Objects may not be used in
     * auto-commit mode", a izvestaju prilozi ni ne trebaju.
     */
    public static InterventionDTO from(Intervention intervention) {
        return new InterventionDTO(
                intervention.getInterventionId(),
                intervention.getDoneDate(),
                intervention.getDoneTime(),
                intervention.getNeededDate(),
                intervention.getNeededTime(),
                intervention.getDescription(),
                intervention.getPrice(),
                intervention.getNote(),
                intervention.isDone(),
                intervention.isNeeded(),
                intervention.getVehicle(),
                intervention.getServiceLocation());
    }
}
