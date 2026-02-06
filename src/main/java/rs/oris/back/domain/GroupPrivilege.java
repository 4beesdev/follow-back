package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "group_privilege")
public class GroupPrivilege {

    @Id
    @GeneratedValue
    @Column(name = "group_privilege_id")
    private int groupPrivilegeId;
    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private Group group;
    @ManyToOne
    @JoinColumn(name = "privilege_id")
    private Privilege privilege;

    public GroupPrivilege() {
    }

    public GroupPrivilege(int groupPrivilegeId, Group group, Privilege privilege) {
        this.groupPrivilegeId = groupPrivilegeId;
        this.group = group;
        this.privilege = privilege;
    }


    public int getGroupPrivilegeId() {
        return groupPrivilegeId;
    }

    public void setGroupPrivilegeId(int groupPrivilegeId) {
        this.groupPrivilegeId = groupPrivilegeId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }
}
