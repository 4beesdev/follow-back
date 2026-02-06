package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "privilege")
public class Privilege {

    @Id
    @Column(name = "privilege_id")
    private int privilegeId;
    private String name;
    @OneToMany(mappedBy = "privilege")
    @JsonIgnore
    private Set<GroupPrivilege> groupPrivilegeSet;


    public Privilege() {
    }

    public Privilege(int privilegeId, String name, Set<GroupPrivilege> groupPrivilegeSet) {
        this.privilegeId = privilegeId;
        this.name = name;
        this.groupPrivilegeSet = groupPrivilegeSet;
    }

    public Privilege(int privilegeId, String name) {
        this.privilegeId = privilegeId;
        this.name = name;
    }

    public int getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(int privilegeId) {
        this.privilegeId = privilegeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<GroupPrivilege> getGroupPrivilegeSet() {
        return groupPrivilegeSet;
    }

    public void setGroupPrivilegeSet(Set<GroupPrivilege> groupPrivilegeSet) {
        this.groupPrivilegeSet = groupPrivilegeSet;
    }
}
