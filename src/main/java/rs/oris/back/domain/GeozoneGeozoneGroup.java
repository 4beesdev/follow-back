package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "geozone_geozone_group")
public class GeozoneGeozoneGroup {
    @Id
    @GeneratedValue
    @Column(name = "geozone_geozone_group_id")
    private int geozoneGeozoneGroupId;
    @ManyToOne
    @JoinColumn(name = "geozone_id")
    private Geozone geozone;
    @ManyToOne
    @JoinColumn(name = "geozone_group_id")
    @JsonIgnore
    private GeozoneGroup geozoneGroup;

    public GeozoneGeozoneGroup() {
    }

    public GeozoneGeozoneGroup(int geozoneGeozoneGroupId, Geozone geozone, GeozoneGroup geozoneGroup) {
        this.geozoneGeozoneGroupId = geozoneGeozoneGroupId;
        this.geozone = geozone;
        this.geozoneGroup = geozoneGroup;
    }

    public int getGeozoneGeozoneGroupId() {
        return geozoneGeozoneGroupId;
    }

    public void setGeozoneGeozoneGroupId(int geozoneGeozoneGroupId) {
        this.geozoneGeozoneGroupId = geozoneGeozoneGroupId;
    }

    public Geozone getGeozone() {
        return geozone;
    }

    public void setGeozone(Geozone geozone) {
        this.geozone = geozone;
    }

    public GeozoneGroup getGeozoneGroup() {
        return geozoneGroup;
    }

    public void setGeozoneGroup(GeozoneGroup geozoneGroup) {
        this.geozoneGroup = geozoneGroup;
    }
}
