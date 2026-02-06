package rs.oris.back.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ppaparat")
public class PPAparat {
    @Id
    @GeneratedValue
    @Column(name = "ppaparat_id")
    private int ppaparatId;
    private double amount;
    private String number;
    private Date serviced;
    @Column(name = "serviced_to")
    private Date servicedto;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aparat_type_id")
    private AparatType aparatType;

    public PPAparat() {
    }

    public PPAparat(int ppaparatId, double amount, String number, Date serviced, Date servicedto, Vehicle vehicle, AparatType aparatType) {
        this.ppaparatId = ppaparatId;
        this.amount = amount;
        this.number = number;
        this.serviced = serviced;
        this.servicedto = servicedto;
        this.vehicle = vehicle;
        this.aparatType = aparatType;
    }

    public int getPpaparatId() {
        return ppaparatId;
    }

    public void setPpaparatId(int ppaparatId) {
        this.ppaparatId = ppaparatId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getServiced() {
        return serviced;
    }

    public void setServiced(Date serviced) {
        this.serviced = serviced;
    }

    public Date getServicedto() {
        return servicedto;
    }

    public void setServicedto(Date servicedto) {
        this.servicedto = servicedto;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public AparatType getAparatType() {
        return aparatType;
    }

    public void setAparatType(AparatType aparatType) {
        this.aparatType = aparatType;
    }
}
