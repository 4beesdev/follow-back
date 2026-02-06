package rs.oris.back.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "technical_inspection")
public class TechnicalInspection {
    @Id
    @GeneratedValue
    @Column(name = "technical_inspection_id")
    private int technicalInspectionId;
    private Date date;
    private int period;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    public TechnicalInspection() {
    }

    public TechnicalInspection(int technicalInspectionId, Date date, int period, Vehicle vehicle) {
        this.technicalInspectionId = technicalInspectionId;
        this.date = date;
        this.period = period;
        this.vehicle = vehicle;
    }

    public int getTechnicalInspectionId() {
        return technicalInspectionId;
    }

    public void setTechnicalInspectionId(int technicalInspectionId) {
        this.technicalInspectionId = technicalInspectionId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
