package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "aparat_type")
public class AparatType {
    @Id
    @GeneratedValue
    @Column(name = "aparat_type_id")
    private int aparatTypeId;
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @OneToMany(mappedBy = "aparatType", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<PPAparat> ppAparatSet;

    public AparatType() {
    }

    public AparatType(int aparatTypeId, String name, Firm firm, Set<PPAparat> ppAparatSet) {
        this.aparatTypeId = aparatTypeId;
        this.name = name;
        this.firm = firm;
        this.ppAparatSet = ppAparatSet;
    }

    public int getAparatTypeId() {
        return aparatTypeId;
    }

    public void setAparatTypeId(int aparatTypeId) {
        this.aparatTypeId = aparatTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public Set<PPAparat> getPpAparatSet() {
        return ppAparatSet;
    }

    public void setPpAparatSet(Set<PPAparat> ppAparatSet) {
        this.ppAparatSet = ppAparatSet;
    }
}
