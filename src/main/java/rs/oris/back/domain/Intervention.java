package rs.oris.back.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name ="intervention")
public class Intervention {
    @Id
    @GeneratedValue
    @Column(name = "intervention_id")
    private int interventionId;
    @Column(name = "done_date")
    private Date doneDate;
    @Column(name = "doneTime")
    private String doneTime;
    @Column(name = "needed_date")
    private Date neededDate;
    private String neededTime;
    private String description;
    private double price;
    private String note;
    boolean done;
    boolean needed;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_location_id")
    private ServiceLocation serviceLocation;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InterventionFiles> interventionFiles;

    public List<InterventionFiles> getInterventionFiles() {
        return interventionFiles;
    }

    public void setInterventionFiles(List<InterventionFiles> interventionFiles) {
        this.interventionFiles = interventionFiles;
    }

    public Intervention() {
    }

    public Intervention(int interventionId, Date doneDate, String doneTime, Date neededDate, String neededTime, String description, double price, String note, boolean done, boolean needed, Vehicle vehicle, ServiceLocation serviceLocation) {
        this.interventionId = interventionId;
        this.doneDate = doneDate;
        this.doneTime = doneTime;
        this.neededDate = neededDate;
        this.neededTime = neededTime;
        this.description = description;
        this.price = price;
        this.note = note;
        this.done = done;
        this.needed = needed;
        this.vehicle = vehicle;
        this.serviceLocation = serviceLocation;
    }

    public String getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(String doneTime) {
        this.doneTime = doneTime;
    }

    public String getNeededTime() {
        return neededTime;
    }

    public void setNeededTime(String neededTime) {
        this.neededTime = neededTime;
    }

    public ServiceLocation getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(ServiceLocation serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public int getInterventionId() {
        return interventionId;
    }

    public void setInterventionId(int interventionId) {
        this.interventionId = interventionId;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public Date getNeededDate() {
        return neededDate;
    }

    public void setNeededDate(Date neededDate) {
        this.neededDate = neededDate;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isNeeded() {
        return needed;
    }

    public void setNeeded(boolean needed) {
        this.needed = needed;
    }



    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
