package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "group_oris")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue
    @Column(name = "group_id")
    private int groupId;
    private String name;
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<User> users;
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Set<GroupPrivilege> groupPrivilegeSet;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;


}
