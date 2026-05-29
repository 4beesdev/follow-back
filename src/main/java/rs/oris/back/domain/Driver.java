package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "driver")
public class Driver {
    @Id
    @GeneratedValue
    @Column(name = "driver_id")
    private int driverId;
    private String name;
    private String phone;
    private String jmbg;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "id_number")
    private String idNumber;
    private boolean active;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "licence_expiry")
    private Date licenceExpiry;
    @Column(name = "licence_number")
    private String licenceNumber;
    private String category;
    @Column(name = "number_of_points")
    private int numberOfPoints;
    @Column(name = "identification_number")
    private String identificationNumber;
    private String iButton;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_group_id")
    private DriverGroup driverGroup;
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<PN> pnSet;
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<FuelConsumption> fuelConsumptionSet;
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Ticket> ticketSet;
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<InvoicedTransport> invoicedTransportSet;
    @Column(name = "medical_expiry")
    private Date medicalExpiry;

    public Driver() {
    }

    public Driver(int driverId, String name, String phone, String jmbg, Date dateOfBirth, String idNumber, boolean active, Date licenceExpiry, String licenceNumber, String category, int numberOfPoints, String identificationNumber, String iButton, Firm firm, DriverGroup driverGroup, Set<PN> pnSet, Set<FuelConsumption> fuelConsumptionSet, Set<Ticket> ticketSet, Set<InvoicedTransport> invoicedTransportSet, Date medicalExpiry) {
        this.driverId = driverId;
        this.name = name;
        this.phone = phone;
        this.jmbg = jmbg;
        this.dateOfBirth = dateOfBirth;
        this.idNumber = idNumber;
        this.active = active;
        this.licenceExpiry = licenceExpiry;
        this.licenceNumber = licenceNumber;
        this.category = category;
        this.numberOfPoints = numberOfPoints;
        this.identificationNumber = identificationNumber;
        this.iButton = iButton;
        this.firm = firm;
        this.driverGroup = driverGroup;
        this.pnSet = pnSet;
        this.fuelConsumptionSet = fuelConsumptionSet;
        this.ticketSet = ticketSet;
        this.invoicedTransportSet = invoicedTransportSet;
        this.medicalExpiry = medicalExpiry;
    }

    public Driver(int driverId, String name, String phone, String jmbg, Date dateOfBirth, String idNumber, boolean active, Vehicle vehicle, Date licenceExpiry, String licenceNumber, String category, int numberOfPoints, String identificationNumber, String iButton, Firm firm, DriverGroup driverGroup, Set<PN> pnSet, Set<FuelConsumption> fuelConsumptionSet, Set<Ticket> ticketSet, Set<InvoicedTransport> invoicedTransportSet, Date medicalExpiry) {
        this.driverId = driverId;
        this.name = name;
        this.phone = phone;
        this.jmbg = jmbg;
        this.dateOfBirth = dateOfBirth;
        this.idNumber = idNumber;
        this.active = active;
        this.vehicle = vehicle;
        this.licenceExpiry = licenceExpiry;
        this.licenceNumber = licenceNumber;
        this.category = category;
        this.numberOfPoints = numberOfPoints;
        this.identificationNumber = identificationNumber;
        this.iButton = iButton;
        this.firm = firm;
        this.driverGroup = driverGroup;
        this.pnSet = pnSet;
        this.fuelConsumptionSet = fuelConsumptionSet;
        this.ticketSet = ticketSet;
        this.invoicedTransportSet = invoicedTransportSet;
        this.medicalExpiry = medicalExpiry;
    }

    public Date getMedicalExpiry() {
        return medicalExpiry;
    }

    public void setMedicalExpiry(Date medicalExpiry) {
        this.medicalExpiry = medicalExpiry;
    }

    public Set<InvoicedTransport> getInvoicedTransportSet() {
        return invoicedTransportSet;
    }

    public void setInvoicedTransportSet(Set<InvoicedTransport> invoicedTransportSet) {
        this.invoicedTransportSet = invoicedTransportSet;
    }

    public Set<Ticket> getTicketSet() {
        return ticketSet;
    }

    public void setTicketSet(Set<Ticket> ticketSet) {
        this.ticketSet = ticketSet;
    }

    public Set<PN> getPnSet() {
        return pnSet;
    }

    public void setPnSet(Set<PN> pnSet) {
        this.pnSet = pnSet;
    }

    public Set<FuelConsumption> getFuelConsumptionSet() {
        return fuelConsumptionSet;
    }

    public void setFuelConsumptionSet(Set<FuelConsumption> fuelConsumptionSet) {
        this.fuelConsumptionSet = fuelConsumptionSet;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJmbg() {
        return jmbg;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getLicenceExpiry() {
        return licenceExpiry;
    }

    public void setLicenceExpiry(Date licenceExpiry) {
        this.licenceExpiry = licenceExpiry;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getiButton() {
        return iButton;
    }

    public void setiButton(String iButton) {
        this.iButton = iButton;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public DriverGroup getDriverGroup() {
        return driverGroup;
    }

    public void setDriverGroup(DriverGroup driverGroup) {
        this.driverGroup = driverGroup;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
