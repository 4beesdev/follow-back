package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "driver_group")
public class DriverGroup {
    @Id
    @GeneratedValue
    @Column(name = "driver_group_id")
    private int driverGroupId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @OneToMany(mappedBy = "driverGroup", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<Driver> driverSet;
    private String name;

    public DriverGroup() {
    }

    public DriverGroup(int driverGroupId, Firm firm, Set<Driver> driverSet, String name) {
        this.driverGroupId = driverGroupId;
        this.firm = firm;
        this.driverSet = driverSet;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDriverGroupId() {
        return driverGroupId;
    }

    public void setDriverGroupId(int driverGroupId) {
        this.driverGroupId = driverGroupId;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public Set<Driver> getDriverSet() {
        return driverSet;
    }

    public void setDriverSet(Set<Driver> driverSet) {
        this.driverSet = driverSet;
    }
}
