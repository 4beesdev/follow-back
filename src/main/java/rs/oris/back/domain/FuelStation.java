package rs.oris.back.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name ="fuel_station")
public class FuelStation {
    @Id
    @GeneratedValue
    @Column(name = "fuel_station_id")
    private int fuelStationId;
    private String name;
    private String place;
    private String address;
    private String phone;
    private String fax;
    private String email;
    @Column(name = "contact_person")
    private String contactPerson;
    private boolean active;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fuel_company_id")
    private FuelCompany fuelCompany;
    @OneToMany(mappedBy = "fuelStation", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<FuelConsumption> fuelConsumptionSet;

    public FuelStation() {
    }

    public FuelStation(int fuelStationId, String name, String place, String address, String phone, String fax, String email, String contactPerson, boolean active, FuelCompany fuelCompany, Set<FuelConsumption> fuelConsumptionSet) {
        this.fuelStationId = fuelStationId;
        this.name = name;
        this.place = place;
        this.address = address;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.contactPerson = contactPerson;
        this.active = active;
        this.fuelCompany = fuelCompany;
        this.fuelConsumptionSet = fuelConsumptionSet;
    }

    public Set<FuelConsumption> getFuelConsumptionSet() {
        return fuelConsumptionSet;
    }

    public void setFuelConsumptionSet(Set<FuelConsumption> fuelConsumptionSet) {
        this.fuelConsumptionSet = fuelConsumptionSet;
    }

    public int getFuelStationId() {
        return fuelStationId;
    }

    public void setFuelStationId(int fuelStationId) {
        this.fuelStationId = fuelStationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public FuelCompany getFuelCompany() {
        return fuelCompany;
    }

    public void setFuelCompany(FuelCompany fuelCompany) {
        this.fuelCompany = fuelCompany;
    }
}
