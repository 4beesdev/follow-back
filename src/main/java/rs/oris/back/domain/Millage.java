package rs.oris.back.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "millage")
public class Millage {
    @Id
    @GeneratedValue
    @Column(name = "millage_id")
    private int millageId;
    private Date date;
    private double amount;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    public Millage() {
    }

    public Millage(int millageId, Date date, double amount, Vehicle vehicle) {
        this.millageId = millageId;
        this.date = date;
        this.amount = amount;
        this.vehicle = vehicle;
    }

    public int getMillageId() {
        return millageId;
    }

    public void setMillageId(int millageId) {
        this.millageId = millageId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
