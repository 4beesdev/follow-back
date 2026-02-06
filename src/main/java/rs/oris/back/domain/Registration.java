package rs.oris.back.domain;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "registration")
public class Registration {

    @Id
    @GeneratedValue
    @Column(name = "registration_id")
    private int registrationId;
    @Column(name = "registration_date")
    private Date registrationDate;
    @Column(name = "registration_expiry_date")
    private Date registrationExpiryDate;
    private String responsible;
    private String note;
    private double amount;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    public Registration() {
    }

    public Registration(int registrationId, Date registrationDate, String responsible, String note, double amount, Vehicle vehicle) {
        this.registrationId = registrationId;
        this.registrationDate = registrationDate;
        this.responsible = responsible;
        this.note = note;
        this.amount = amount;
        this.vehicle = vehicle;
    }


    public Registration(int registrationId, Date registrationDate, Date registrationExpiryDate, String responsible, String note, double amount, Vehicle vehicle) {
        this.registrationId = registrationId;
        this.registrationDate = registrationDate;
        this.registrationExpiryDate = registrationExpiryDate;
        this.responsible = responsible;
        this.note = note;
        this.amount = amount;
        this.vehicle = vehicle;
    }

    public Date getRegistrationExpiryDate() {
        return registrationExpiryDate;
    }

    public void setRegistrationExpiryDate(Date registrationExpiryDate) {
        this.registrationExpiryDate = registrationExpiryDate;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
