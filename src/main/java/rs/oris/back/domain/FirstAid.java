package rs.oris.back.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "first_aid")
public class FirstAid {
    @Id
    @GeneratedValue
    @Column(name = "first_aid_id")
    private int firstAidId;
    private Date expiryDate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    public FirstAid() {
    }

    public FirstAid(int firstAidId, Date expiryDate, Vehicle vehicle) {
        this.firstAidId = firstAidId;
        this.expiryDate = expiryDate;
        this.vehicle = vehicle;
    }

    public int getFirstAidId() {
        return firstAidId;
    }

    public void setFirstAidId(int firstAidId) {
        this.firstAidId = firstAidId;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
