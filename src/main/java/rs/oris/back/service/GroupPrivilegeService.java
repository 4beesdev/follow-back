package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.domain.Group;
import rs.oris.back.domain.GroupPrivilege;
import rs.oris.back.domain.Privilege;
import rs.oris.back.repository.GroupPrivilegeRepository;
import rs.oris.back.repository.GroupRepository;
import rs.oris.back.repository.PrivilegeRepository;

import java.util.Optional;

@Service
public class GroupPrivilegeService {

    @Autowired
    private GroupPrivilegeRepository groupPrivilegeRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;

    /**
     * create/save
     * dodeljuje privilegiju grupi
     */
    public boolean addGP(int groupId, int privilegeId) throws Exception {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (!optionalGroup.isPresent()) {
            throw new Exception("Invalid group id "+groupId);
        }
        Optional<Privilege> optionalPrivilege = privilegeRepository.findById(privilegeId);
        if (!optionalPrivilege.isPresent()) {
            throw new Exception("Invalid privialge id "+privilegeId);
        }
        GroupPrivilege gp = new GroupPrivilege();
        gp.setGroup(optionalGroup.get());
        gp.setPrivilege(optionalPrivilege.get());
        GroupPrivilege saved = groupPrivilegeRepository.save(gp);
        if (saved == null) {
            throw new Exception("Failed to save group privilege");
        }
        return true;
    }


}
