package rs.oris.back.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "geozone_group")
public class GeozoneGroup {
    @Id
    @GeneratedValue
    @Column(name = "geozoneGroup_id")
    private int geozoneGroupId;
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @OneToMany(mappedBy = "geozoneGroup")
    private Set<GeozoneGeozoneGroup> geozoneGeozoneGroupSet;

    public GeozoneGroup() {
    }

    public GeozoneGroup(int geozoneGroupId, String name, Firm firm, Set<GeozoneGeozoneGroup> geozoneGeozoneGroupSet) {
        this.geozoneGroupId = geozoneGroupId;
        this.name = name;
        this.firm = firm;
        this.geozoneGeozoneGroupSet = geozoneGeozoneGroupSet;
    }


    public int getGeozoneGroupId() {
        return geozoneGroupId;
    }

    public void setGeozoneGroupId(int geozoneGroupId) {
        this.geozoneGroupId = geozoneGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<GeozoneGeozoneGroup> getGeozoneGeozoneGroupSet() {
        return geozoneGeozoneGroupSet;
    }

    public void setGeozoneGeozoneGroupSet(Set<GeozoneGeozoneGroup> geozoneGeozoneGroupSet) {
        this.geozoneGeozoneGroupSet = geozoneGeozoneGroupSet;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }
}
