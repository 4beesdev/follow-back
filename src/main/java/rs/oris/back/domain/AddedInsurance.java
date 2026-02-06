package rs.oris.back.domain;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "added_insurance")
public class AddedInsurance {
    @Id
    @GeneratedValue
    @Column(name = "added_insurance_id")
    private int addedInsuranceId;
    private int type;
    private String name;
    private Date dateFrom;
    private Date dateTo;
    private double expenses;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    public AddedInsurance() {
    }

    public AddedInsurance(int addedInsuranceId, int type, String name, Date dateFrom, Date dateTo, double expenses, Vehicle vehicle) {
        this.addedInsuranceId = addedInsuranceId;
        this.type = type;
        this.name = name;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.expenses = expenses;
        this.vehicle = vehicle;
    }

    public int getAddedInsuranceId() {
        return addedInsuranceId;
    }

    public void setAddedInsuranceId(int addedInsuranceId) {
        this.addedInsuranceId = addedInsuranceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
