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

}
