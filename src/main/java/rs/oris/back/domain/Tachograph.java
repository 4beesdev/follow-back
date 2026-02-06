package rs.oris.back.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tachograph")
public class Tachograph {

    @Id
    @GeneratedValue
    @Column(name = "tachograph_id")
    private int tachographId;
    private boolean digital;
    private Date dateFrom;
    private Date dateTo;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    public Tachograph() {
    }

    public Tachograph(int tachographId, boolean digital, Date dateFrom, Date dateTo, Vehicle vehicle) {
        this.tachographId = tachographId;
        this.digital = digital;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.vehicle = vehicle;
    }

    public int getTachographId() {
        return tachographId;
    }

    public void setTachographId(int tachographId) {
        this.tachographId = tachographId;
    }

    public boolean isDigital() {
        return digital;
    }

    public void setDigital(boolean digital) {
        this.digital = digital;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
