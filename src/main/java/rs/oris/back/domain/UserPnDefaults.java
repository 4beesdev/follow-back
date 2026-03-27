package rs.oris.back.domain;

import javax.persistence.*;

@Entity
@Table(name = "user_pn_defaults")
public class UserPnDefaults {

    @Id
    @GeneratedValue
    @Column(name = "user_pn_defaults_id")
    private int userPnDefaultsId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "company_owner")
    private String companyOwner;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "place_of_issue")
    private String placeOfIssue;

    @Column(name = "transport_type")
    private String transportType;

    @Column(name = "garage_address")
    private String garageAddress;

    public UserPnDefaults() {
    }

    public int getUserPnDefaultsId() {
        return userPnDefaultsId;
    }

    public void setUserPnDefaultsId(int userPnDefaultsId) {
        this.userPnDefaultsId = userPnDefaultsId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCompanyOwner() {
        return companyOwner;
    }

    public void setCompanyOwner(String companyOwner) {
        this.companyOwner = companyOwner;
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

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getGarageAddress() {
        return garageAddress;
    }

    public void setGarageAddress(String garageAddress) {
        this.garageAddress = garageAddress;
    }
}
