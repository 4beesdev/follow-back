package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "service_locations")
public class ServiceLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "service_location_id")
    private int serviceLocationId;
    private String name;
    private String address;
    private String type;
    private String phone;
    @Column(name = "mobile_phone")
    private String mobilePhone;
    private String email;
    @Column(name = "contact_person")
    private String contactPerson;
    @OneToMany(mappedBy = "serviceLocation")
    @JsonIgnore
    private Set<VehicleServiceLocation> vehicleServiceLocationSet;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @OneToMany(mappedBy = "serviceLocation", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Intervention> interventionSet;

    private String serviceCode;
    public ServiceLocation() {
    }

    public ServiceLocation(int serviceLocationId, String name, String address, String type, String phone, String mobilePhone, String email, String contactPerson, Set<VehicleServiceLocation> vehicleServiceLocationSet, Firm firm, Set<Intervention> interventionSet) {
        this.serviceLocationId = serviceLocationId;
        this.name = name;
        this.address = address;
        this.type = type;
        this.phone = phone;
        this.mobilePhone = mobilePhone;
        this.email = email;
        this.contactPerson = contactPerson;
        this.vehicleServiceLocationSet = vehicleServiceLocationSet;
        this.firm = firm;
        this.interventionSet = interventionSet;
    }

    public Set<Intervention> getInterventionSet() {
        return interventionSet;
    }

    public void setInterventionSet(Set<Intervention> interventionSet) {
        this.interventionSet = interventionSet;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public Set<VehicleServiceLocation> getVehicleServiceLocationSet() {
        return vehicleServiceLocationSet;
    }

    public void setVehicleServiceLocationSet(Set<VehicleServiceLocation> vehicleServiceLocationSet) {
        this.vehicleServiceLocationSet = vehicleServiceLocationSet;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public int getServiceLocationId() {
        return serviceLocationId;
    }

    public void setServiceLocationId(int serviceLocationId) {
        this.serviceLocationId = serviceLocationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
}
