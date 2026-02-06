package rs.oris.back.domain;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "vehicle_registartion_card")
public class VehicleRegistrationCard {


    @Id
    @GeneratedValue
    @Column(name = "vehicle_registartion_card_id")
    private int vehicleRegistrationCardId;
    private Date extendedDate;
    private Date expieryDate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    public VehicleRegistrationCard() {
    }

    public VehicleRegistrationCard(int vehicleRegistrationCardId, Date extendedDate, Date expieryDate, Vehicle vehicle) {
        this.vehicleRegistrationCardId = vehicleRegistrationCardId;
        this.extendedDate = extendedDate;
        this.expieryDate = expieryDate;
        this.vehicle = vehicle;
    }

    public int getVehicleRegistrationCardId() {
        return vehicleRegistrationCardId;
    }

    public void setVehicleRegistrationCardId(int vehicleRegistrationCardId) {
        this.vehicleRegistrationCardId = vehicleRegistrationCardId;
    }

    public Date getExtendedDate() {
        return extendedDate;
    }

    public void setExtendedDate(Date extendedDate) {
        this.extendedDate = extendedDate;
    }

    public Date getExpieryDate() {
        return expieryDate;
    }

    public void setExpieryDate(Date expieryDate) {
        this.expieryDate = expieryDate;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
