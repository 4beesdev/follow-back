package rs.oris.back.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="pn")
public class PN {

    @Id
    @GeneratedValue
    @Column(name = "pn_id")
    private int pnId;
    @Column(name = "company_owner")
    private String companyOwner;
    private boolean trailer;
    @Column(name = "company_address")
    private String companyAddress;
    @Column(name = "palce_of_issue")
    private String placeOfIssue;
    private Date date;
    @Column(name = "name_of_user")
    private String nameOfUser;
    @Column(name = "registration_field")
    private String registrationField;
    @Column(name = "transport_type")
    private String transportType;
    private String route;
    private String technical;
    @Column(name = "techincal_date")
    private Date technicalDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;
    private String others;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @Column(name = "trailer_registration")
    private String trailerRegistration;
    @Column(name = "trailer_mark")
    private String trailerMark;
    @Column(name = "trailer_registration_field")
    private String trailerRegistrationField;
    @Column(name = "garage_address")
    private String garageAddress;

    public PN() {
    }

    public PN(int pnId, String companyOwner, boolean trailer, String companyAddress, String placeOfIssue, Date date, String nameOfUser, String registrationField, String transportType, String route, String technical, Date technicalDate, Driver driver, String others, Vehicle vehicle, String trailerRegistration, String trailerMark, String trailerRegistrationField, String garageAddress) {
        this.pnId = pnId;
        this.companyOwner = companyOwner;
        this.trailer = trailer;
        this.companyAddress = companyAddress;
        this.placeOfIssue = placeOfIssue;
        this.date = date;
        this.nameOfUser = nameOfUser;
        this.registrationField = registrationField;
        this.transportType = transportType;
        this.route = route;
        this.technical = technical;
        this.technicalDate = technicalDate;
        this.driver = driver;
        this.others = others;
        this.vehicle = vehicle;
        this.trailerRegistration = trailerRegistration;
        this.trailerMark = trailerMark;
        this.trailerRegistrationField = trailerRegistrationField;
        this.garageAddress = garageAddress;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getGarageAddress() {
        return garageAddress;
    }

    public void setGarageAddress(String garageAddress) {
        this.garageAddress = garageAddress;
    }

    public int getPnId() {
        return pnId;
    }

    public void setPnId(int pnId) {
        this.pnId = pnId;
    }

    public String getCompanyOwner() {
        return companyOwner;
    }

    public void setCompanyOwner(String companyOwner) {
        this.companyOwner = companyOwner;
    }

    public boolean isTrailer() {
        return trailer;
    }

    public void setTrailer(boolean trailer) {
        this.trailer = trailer;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getPlaceOfIssue() {
        return placeOfIssue;
    }

    public void setPlaceOfIssue(String placeOfIssue) {
        this.placeOfIssue = placeOfIssue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String getNameOfUser() {
        return nameOfUser;
    }

    public void setNameOfUser(String nameOfUser) {
        this.nameOfUser = nameOfUser;
    }

    public String getRegistrationField() {
        return registrationField;
    }

    public void setRegistrationField(String registrationField) {
        this.registrationField = registrationField;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTechnical() {
        return technical;
    }

    public void setTechnical(String technical) {
        this.technical = technical;
    }

    public Date getTechnicalDate() {
        return technicalDate;
    }

    public void setTechnicalDate(Date technicalDate) {
        this.technicalDate = technicalDate;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getTrailerRegistration() {
        return trailerRegistration;
    }

    public void setTrailerRegistration(String trailerRegistration) {
        this.trailerRegistration = trailerRegistration;
    }

    public String getTrailerMark() {
        return trailerMark;
    }

    public void setTrailerMark(String trailerMark) {
        this.trailerMark = trailerMark;
    }

    public String getTrailerRegistrationField() {
        return trailerRegistrationField;
    }

    public void setTrailerRegistrationField(String trailerRegistrationField) {
        this.trailerRegistrationField = trailerRegistrationField;
    }
}
