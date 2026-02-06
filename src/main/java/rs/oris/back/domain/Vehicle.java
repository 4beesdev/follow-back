package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue
    @Column(name = "vehicle_id")
    private int vehicleId;
    private String imei;
    private String registration;
    private String manufacturer;
    private String model;
    private String vin;
    private String chassis;
    @Column(name = "manufacturing_year")
    private int manufacturingYear;
    @Column(name = "transmission_type")
    private String transmissionType;
    @Column(name = "millage_date")
    private Date millageDate;
    private double millage;
    @Column(name = "vehicle_Type")
    private String vehicleType;
    @Column(name = "fuel_type")
    private String fuelType;
    @Column(name = "sb")
    private String sb;
    @Column(name = "iccid")
    private String iccid;
    @Column(name = "pb_card")
    private String pbCard;
    @Column(name = "garage_number")
    private String garageNumber;
    @Column(name = "carry_weight")
    private double carryWeight;
    private double weight;
    private int seats;
    private double power;
    @Column(name = "max_weight")
    private double maxWeight;
    @Column(name = "max_speed")
    private double maxSpeed;
    @Column(name = "max_rpm")
    private double maxRPM;
    @Column(name = "consumption_100km")
    private double consumption100km;
    @Column(name = "consumption_1h")
    private double consumption1h;
    @Column(name = "engine_size")
    private double engineSize;
    @Column(name = "installation_date")
    private Date installationDate;
    @Column(name = "device_fw")
    private String deviceFw;
    @Column(name = "modul_fw")
    private String modulFw;
    @Column(name = "device_installed")
    private Boolean deviceInstalled;
    private boolean active;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private Set<VehicleVehicleGroup> vehicleVehicleGroupSet;
    @Column(name = "device_type")
    private Integer deviceType = 0;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<VehicleGeozone> vehicleGeozoneSet;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<VehicleRoute> vehicleRouteSet;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<AlarmVehicle> alarmVehicleSet;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<VehicleServiceLocation> vehicleServiceLocationSet;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @JsonIgnore
    private transient Set<PN> pnSet;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Intervention> interventionSet;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<FuelConsumption> fuelConsumptionSet;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Ticket> ticketSet;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Registration> registrationSet;
    @OneToOne(cascade = CascadeType.ALL)
    private TechnicalInspection technicalInspection;
    @OneToOne(cascade = CascadeType.ALL)
    private Tachograph tachograph;
    @OneToOne(cascade = CascadeType.ALL)
    private PPAparat ppAparat;
    @OneToOne(cascade = CascadeType.ALL)
    private FirstAid firstAid;
    @OneToOne(cascade = CascadeType.ALL)
    private VehicleRegistrationCard vehicleRegistrationCard;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<AddedInsurance> addedInsuranceSet;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Millage> millageSet;
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<InvoicedTransport> invoicedTransportSet;
    private Timestamp deletedDate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id")
    private Driver driver;
    private Integer fuelMargine;


    public Vehicle() {

    }

    public Vehicle(int vehicleId, String imei, String registration, String manufacturer, String model, String vin, String chassis, int manufacturingYear, String transmissionType, Date millageDate, double millage, String vehicleType, String fuelType, String sb, String iccid, String pbCard, String garageNumber, double carryWeight, double weight, int seats, double power, double maxWeight, double maxSpeed, double maxRPM, double consumption100km, double consumption1h, double engineSize, Date installationDate, String deviceFw, String modulFw, Boolean deviceInstalled, boolean active, Firm firm, Set<VehicleVehicleGroup> vehicleVehicleGroupSet, Integer deviceType, Set<VehicleGeozone> vehicleGeozoneSet, Set<VehicleRoute> vehicleRouteSet, Set<AlarmVehicle> alarmVehicleSet, Set<VehicleServiceLocation> vehicleServiceLocationSet, Set<PN> pnSet, Set<Intervention> interventionSet, Set<FuelConsumption> fuelConsumptionSet, Set<Ticket> ticketSet, Set<Registration> registrationSet, TechnicalInspection technicalInspection, Tachograph tachograph, PPAparat ppAparat, FirstAid firstAid, VehicleRegistrationCard vehicleRegistrationCard, Set<AddedInsurance> addedInsuranceSet, Set<Millage> millageSet, Set<InvoicedTransport> invoicedTransportSet, Timestamp deletedDate, Driver driver, Integer fuelMargine) {
        this.vehicleId = vehicleId;
        this.imei = imei;
        this.registration = registration;
        this.manufacturer = manufacturer;
        this.model = model;
        this.vin = vin;
        this.chassis = chassis;
        this.manufacturingYear = manufacturingYear;
        this.transmissionType = transmissionType;
        this.millageDate = millageDate;
        this.millage = millage;
        this.vehicleType = vehicleType;
        this.fuelType = fuelType;
        this.sb = sb;
        this.iccid = iccid;
        this.pbCard = pbCard;
        this.garageNumber = garageNumber;
        this.carryWeight = carryWeight;
        this.weight = weight;
        this.seats = seats;
        this.power = power;
        this.maxWeight = maxWeight;
        this.maxSpeed = maxSpeed;
        this.maxRPM = maxRPM;
        this.consumption100km = consumption100km;
        this.consumption1h = consumption1h;
        this.engineSize = engineSize;
        this.installationDate = installationDate;
        this.deviceFw = deviceFw;
        this.modulFw = modulFw;
        this.deviceInstalled = deviceInstalled;
        this.active = active;
        this.firm = firm;
        this.vehicleVehicleGroupSet = vehicleVehicleGroupSet;
        this.deviceType = deviceType;
        this.vehicleGeozoneSet = vehicleGeozoneSet;
        this.vehicleRouteSet = vehicleRouteSet;
        this.alarmVehicleSet = alarmVehicleSet;
        this.vehicleServiceLocationSet = vehicleServiceLocationSet;
        this.pnSet = pnSet;
        this.interventionSet = interventionSet;
        this.fuelConsumptionSet = fuelConsumptionSet;
        this.ticketSet = ticketSet;
        this.registrationSet = registrationSet;
        this.technicalInspection = technicalInspection;
        this.tachograph = tachograph;
        this.ppAparat = ppAparat;
        this.firstAid = firstAid;
        this.vehicleRegistrationCard = vehicleRegistrationCard;
        this.addedInsuranceSet = addedInsuranceSet;
        this.millageSet = millageSet;
        this.invoicedTransportSet = invoicedTransportSet;
        this.deletedDate = deletedDate;
        this.driver = driver;
        this.fuelMargine = fuelMargine;
    }

    public Integer getFuelMargine() {
        return fuelMargine;
    }

    public void setFuelMargine(Integer fuelMargine) {
        this.fuelMargine = fuelMargine;
    }

    public Timestamp getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Timestamp deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Set<InvoicedTransport> getInvoicedTransportSet() {
        return invoicedTransportSet;
    }

    public void setInvoicedTransportSet(Set<InvoicedTransport> invoicedTransportSet) {
        this.invoicedTransportSet = invoicedTransportSet;
    }

    public FirstAid getFirstAid() {
        return firstAid;
    }

    public void setFirstAid(FirstAid firstAid) {
        this.firstAid = firstAid;
    }

    public VehicleRegistrationCard getVehicleRegistrationCard() {
        return vehicleRegistrationCard;
    }

    public void setVehicleRegistrationCard(VehicleRegistrationCard vehicleRegistrationCard) {
        this.vehicleRegistrationCard = vehicleRegistrationCard;
    }

    public Set<Millage> getMillageSet() {
        return millageSet;
    }

    public void setMillageSet(Set<Millage> millageSet) {
        this.millageSet = millageSet;
    }

    public PPAparat getPpAparat() {
        return ppAparat;
    }

    public void setPpAparat(PPAparat ppAparat) {
        this.ppAparat = ppAparat;
    }

    public Tachograph getTachograph() {
        return tachograph;
    }

    public void setTachograph(Tachograph tachograph) {
        this.tachograph = tachograph;
    }

    public Set<AddedInsurance> getAddedInsuranceSet() {
        return addedInsuranceSet;
    }

    public void setAddedInsuranceSet(Set<AddedInsurance> addedInsuranceSet) {
        this.addedInsuranceSet = addedInsuranceSet;
    }

    public Set<FuelConsumption> getFuelConsumptionSet() {
        return fuelConsumptionSet;
    }

    public void setFuelConsumptionSet(Set<FuelConsumption> fuelConsumptionSet) {
        this.fuelConsumptionSet = fuelConsumptionSet;
    }

    public Set<Ticket> getTicketSet() {
        return ticketSet;
    }

    public void setTicketSet(Set<Ticket> ticketSet) {
        this.ticketSet = ticketSet;
    }

    public Set<Registration> getRegistrationSet() {
        return registrationSet;
    }

    public void setRegistrationSet(Set<Registration> registrationSet) {
        this.registrationSet = registrationSet;
    }

    public TechnicalInspection getTechnicalInspection() {
        return technicalInspection;
    }

    public void setTechnicalInspection(TechnicalInspection technicalInspection) {
        this.technicalInspection = technicalInspection;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Set<Intervention> getInterventionSet() {
        return interventionSet;
    }

    public void setInterventionSet(Set<Intervention> interventionSet) {
        this.interventionSet = interventionSet;
    }

    public Set<PN> getPnSet() {
        return pnSet;
    }

    public void setPnSet(Set<PN> pnSet) {
        this.pnSet = pnSet;
    }

    public Set<VehicleServiceLocation> getVehicleServiceLocationSet() {
        return vehicleServiceLocationSet;
    }

    public void setVehicleServiceLocationSet(Set<VehicleServiceLocation> vehicleServiceLocationSet) {
        this.vehicleServiceLocationSet = vehicleServiceLocationSet;
    }

    public Set<AlarmVehicle> getAlarmVehicleSet() {
        return alarmVehicleSet;
    }

    public void setAlarmVehicleSet(Set<AlarmVehicle> alarmVehicleSet) {
        this.alarmVehicleSet = alarmVehicleSet;
    }

    public Set<VehicleRoute> getVehicleRouteSet() {
        return vehicleRouteSet;
    }

    public void setVehicleRouteSet(Set<VehicleRoute> vehicleRouteSet) {
        this.vehicleRouteSet = vehicleRouteSet;
    }

    public Set<VehicleGeozone> getVehicleGeozoneSet() {
        return vehicleGeozoneSet;
    }

    public void setVehicleGeozoneSet(Set<VehicleGeozone> vehicleGeozoneSet) {
        this.vehicleGeozoneSet = vehicleGeozoneSet;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getChassis() {
        return chassis;
    }

    public void setChassis(String chassis) {
        this.chassis = chassis;
    }

    public int getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(int manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public Date getMillageDate() {
        return millageDate;
    }

    public void setMillageDate(Date millageDate) {
        this.millageDate = millageDate;
    }

    public double getMillage() {
        return millage;
    }

    public void setMillage(double millage) {
        this.millage = millage;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getSb() {
        return sb;
    }

    public void setSb(String sb) {
        this.sb = sb;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getPbCard() {
        return pbCard;
    }

    public void setPbCard(String pbCard) {
        this.pbCard = pbCard;
    }

    public String getGarageNumber() {
        return garageNumber;
    }

    public void setGarageNumber(String garageNumber) {
        this.garageNumber = garageNumber;
    }

    public double getCarryWeight() {
        return carryWeight;
    }

    public void setCarryWeight(double carryWeight) {
        this.carryWeight = carryWeight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getMaxRPM() {
        return maxRPM;
    }

    public void setMaxRPM(double maxRPM) {
        this.maxRPM = maxRPM;
    }

    public double getConsumption100km() {
        return consumption100km;
    }

    public void setConsumption100km(double consumption100km) {
        this.consumption100km = consumption100km;
    }

    public double getConsumption1h() {
        return consumption1h;
    }

    public void setConsumption1h(double consumption1h) {
        this.consumption1h = consumption1h;
    }

    public double getEngineSize() {
        return engineSize;
    }

    public void setEngineSize(double engineSize) {
        this.engineSize = engineSize;
    }

    public Date getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }

    public String getDeviceFw() {
        return deviceFw;
    }

    public void setDeviceFw(String deviceFw) {
        this.deviceFw = deviceFw;
    }

    public String getModulFw() {
        return modulFw;
    }

    public void setModulFw(String modulFw) {
        this.modulFw = modulFw;
    }

    public Boolean getDeviceInstalled() {
        return deviceInstalled;
    }

    public void setDeviceInstalled(Boolean deviceInstalled) {
        this.deviceInstalled = deviceInstalled;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public Set<VehicleVehicleGroup> getVehicleVehicleGroupSet() {
        return vehicleVehicleGroupSet;
    }

    public void setVehicleVehicleGroupSet(Set<VehicleVehicleGroup> vehicleVehicleGroupSet) {
        this.vehicleVehicleGroupSet = vehicleVehicleGroupSet;
    }
}
