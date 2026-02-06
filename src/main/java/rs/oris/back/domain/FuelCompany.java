package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "fuel_company")
public class FuelCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "fuel_company_id")
    private int fuelCompanyId;
    private String name;
    private String place;
    private String address;
    private String phone;
    private String fax;
    private String email;
    private boolean active;
    @Column(name = "contact_person")
    private String contactPerson;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @OneToMany(mappedBy = "fuelCompany", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<FuelStation> fuelStationSet;

    public FuelCompany() {
    }

    public FuelCompany(int fuelCompanyId, String name, String place, String address, String phone, String fax, String email, boolean active, String contactPerson, Firm firm, Set<FuelStation> fuelStationSet) {
        this.fuelCompanyId = fuelCompanyId;
        this.name = name;
        this.place = place;
        this.address = address;
        this.phone = phone;
        this.fax = fax;
        this.email = email;
        this.active = active;
        this.contactPerson = contactPerson;
        this.firm = firm;
        this.fuelStationSet = fuelStationSet;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<FuelStation> getFuelStationSet() {
        return fuelStationSet;
    }

    public void setFuelStationSet(Set<FuelStation> fuelStationSet) {
        this.fuelStationSet = fuelStationSet;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public int getFuelCompanyId() {
        return fuelCompanyId;
    }

    public void setFuelCompanyId(int fuelCompanyId) {
        this.fuelCompanyId = fuelCompanyId;
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
}
