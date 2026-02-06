package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.domain.dto.PrivilegeUpdateDTO;
import rs.oris.back.domain.dto.UpdateGroupDTO;
import rs.oris.back.repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private GroupPrivilegeRepository groupPrivilegeRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private UserReportRepository userReportRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * vraca sve grupe vezane za firmu
     */
    public Response<Map<String, List<Group>>> getAllGroups(User user, int firmId) throws Exception {
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            Optional<Firm> optionalFirm = firmRepository.findById(firmId);
            if (!optionalFirm.isPresent()) {
                throw new Exception("Invalid firm id");
            }
            List<Group> groupList = groupRepository.findByFirmFirmId(optionalFirm.get().getFirmId());
            Map<String, List<Group>> map = new HashMap<>();
            map.put("groups", groupList);
            return new Response<>(map);
        } else {
            if (user.getFirm() == null) {
                throw new Exception("Users firm is not set");
            }
            if (!user.getAdmin()) {
                throw new Exception("User is not an admin");
            }
            List<Group> groupList = groupRepository.findByFirmFirmId(user.getFirm().getFirmId());
            Map<String, List<Group>> map = new HashMap<>();
            map.put("groups", groupList);
            return new Response<>(map);
        }


    }

    /**
     * vraca sve grupe vezane za jednu firmu
     */
    public Response<Map<String, List<Group>>> getAllGroupsForFirm(int firmId) {
        List<Group> groupList = groupRepository.findByFirmFirmId(firmId);
        Map<String, List<Group>> map = new HashMap<>();
        map.put("groups", groupList);
        return new Response<>(map);
    }
    /**
     * Cuva novu grupu i vezuje je za prosledjenu firmu
     */
    @Transactional
    public Response<Group> addGroupForAFirm(User user, Group group, int firmId) throws Exception {
        if (user == null)
            throw new Exception("Bad token!");
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            Optional<Firm> optionalFirm = firmRepository.findById(firmId);
            if (!optionalFirm.isPresent()) {
                throw new Exception("Invalid firm id");
            }
            group.setFirm(optionalFirm.get());
            Group saved = groupRepository.save(group);
            for (GroupPrivilege groupPrivilege : group.getGroupPrivilegeSet()) {
                groupPrivilege.setGroup(group);
                groupPrivilegeRepository.save(groupPrivilege);
                System.out.println("UBACUJEM "+ groupPrivilege);
            }
            if (saved == null) {
                throw new Exception("Failed to save group");
            }
            return new Response<>(saved);
        } else {
            if (user.getFirm() == null)
                throw new Exception("Firm not set!");
            Optional<Firm> optionalFirm = firmRepository.findById(user.getFirm().getFirmId());
            if (!optionalFirm.isPresent()) {
                throw new Exception("Invalid firm id " + user.getFirm().getFirmId());
            }
            group.setFirm(optionalFirm.get());

            Group saved = groupRepository.save(group);
            for (GroupPrivilege groupPrivilege : group.getGroupPrivilegeSet()) {
                groupPrivilege.setGroup(group);
             GroupPrivilege g=   groupPrivilegeRepository.save(groupPrivilege);
                System.out.println(g.getGroupPrivilegeId());

            }

            if (saved == null) {
                throw new Exception("Failed to save group");
            }
            return new Response<>(saved);
        }
    }
    /**
     * vraca jednu konkretnu grupu
     * @throws Exception ako grupa ne postoji ili ako korisnik nema auth da je vidi
     */
    public Response<Group> getAGroup(User user, int groupId) throws Exception {
        if (user.getSuperAdmin()) {
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (!optionalGroup.isPresent()) {
                throw new Exception("Invalid group id " + groupId);
            }
            return new Response<>(optionalGroup.get());
        } else {
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (!optionalGroup.isPresent()) {
                throw new Exception("Invalid group id " + groupId);
            }
            Group g = optionalGroup.get();
            if (g.getFirm().getFirmId() != user.getFirm().getFirmId()) {
                throw new Exception("You are not authorized to access this group");
            } else {
                return new Response<>(g);
            }
        }
    }
    /**
     * brise sve privilegije jedne grupe
     */
    public boolean deletePrivilages(User user, int groupId) throws Exception {
        if (user.getSuperAdmin()) {
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (!optionalGroup.isPresent()) {
                throw new Exception("Invalid group id " + groupId);
            }
            groupPrivilegeRepository.deleteByGroupGroupId(groupId);
        } else {
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (!optionalGroup.isPresent()) {
                throw new Exception("Invalid group id " + groupId);
            }
            Group g = optionalGroup.get();
            if (g.getFirm().getFirmId() != user.getFirm().getFirmId()) {
                throw new Exception("Firms do not match!");
            }
            groupPrivilegeRepository.deleteByGroupGroupId(groupId);
        }
        return true;
    }
    /**
     * update
     */
    @Transactional
    public Response<Group> updateGroup(User user, int groupId, UpdateGroupDTO dto) throws Exception {
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (!optionalGroup.isPresent()) {
                throw new Exception("Invalid group id " + groupId);
            }
            Group g = optionalGroup.get();
            g.setName(dto.getName());
            for (PrivilegeUpdateDTO privilegeUpdateDTO : dto.getPrivilegeUpdateDTOList()) {
                if(privilegeUpdateDTO.isToDelete() && privilegeUpdateDTO.getGroupPrivilegeId()==null) continue;
                if(privilegeUpdateDTO.isToDelete()){
                    GroupPrivilege groupPrivilege = groupPrivilegeRepository.findById(privilegeUpdateDTO.getGroupPrivilegeId()).orElseThrow(() -> new Exception("Invalid privilege id"));
                    groupPrivilegeRepository.deleteById(groupPrivilege.getGroupPrivilegeId());
                    g.getGroupPrivilegeSet().remove(groupPrivilege);
                }else{
                    Privilege privilege = privilegeRepository.findById(privilegeUpdateDTO.getPrivilegeId()).orElseThrow(() -> new Exception("Invalid privilege id"));
                    GroupPrivilege groupPrivilege = groupPrivilegeRepository.save(new GroupPrivilege(0, g, privilege));
                    g.getGroupPrivilegeSet().add(groupPrivilege);
                }
            }
            if(dto.getName()!=null)
                g.setName(dto.getName());
            Group saved = groupRepository.save(g);
            if (saved == null) {
                throw new Exception("Failed to save group.");
            }
            return new Response<>(saved);
        } else {
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (!optionalGroup.isPresent()) {
                throw new Exception("Invalid group id " + groupId);
            }

            Group g = optionalGroup.get();
            if (g.getFirm().getFirmId() != user.getFirm().getFirmId()) {
                throw new Exception("Firms do not match!");
            }

            for (PrivilegeUpdateDTO privilegeUpdateDTO : dto.getPrivilegeUpdateDTOList()) {
                if(privilegeUpdateDTO.isToDelete() && privilegeUpdateDTO.getGroupPrivilegeId()==null) continue;

                if(privilegeUpdateDTO.isToDelete()){
                    Optional<GroupPrivilege> groupPrivilegeOptional = groupPrivilegeRepository.findById(privilegeUpdateDTO.getGroupPrivilegeId());
                    if(!groupPrivilegeOptional.isPresent()) continue;;
                    GroupPrivilege groupPrivilege = groupPrivilegeOptional.get();
                    groupPrivilegeRepository.deleteById(groupPrivilege.getGroupPrivilegeId());
                    g.getGroupPrivilegeSet().remove(groupPrivilege);
                }else{
                    if(privilegeUpdateDTO.getGroupPrivilegeId()==null) {
                        Privilege privilege = privilegeRepository.findById(privilegeUpdateDTO.getPrivilegeId()).orElseThrow(() -> new Exception("Invalid privilege id"));
                        GroupPrivilege groupPrivilege = groupPrivilegeRepository.save(new GroupPrivilege(0, g, privilege));
                        g.getGroupPrivilegeSet().add(groupPrivilege);
                    }
                }
            }
            if(dto.getName()!=null)
                g.setName(dto.getName());



            Group saved = groupRepository.save(g);
            if (saved == null) {
                throw new Exception("Failed to save group.");
            }
            return new Response<>(saved);
        }
    }

    /**
     * delete
     */
    @Transactional
    public boolean deleteGroup(User user, int groupId, int firmId) throws Exception {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (!optionalGroup.isPresent()){
            throw new RuntimeException("Invalid group id "+groupId);
        }
        Group group = optionalGroup.get();
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            List<User> userList = userRepository.findByGroupGroupId(groupId);
            for(User u : userList) {
                u.setGroup(null);
                User saved = userRepository.save(u);
                if (saved==null)
                    throw new RuntimeException("Failed to remove group.");
            }
            groupRepository.deleteById(groupId);
            return true;
        } else {
            if (user.getAdmin()!=null && user.getAdmin() && user.getFirm().getFirmId()==group.getFirm().getFirmId()){
                List<User> userList = userRepository.findByGroupGroupId(groupId);
                for(User u : userList) {
                    u.setGroup(null);
                    User saved = userRepository.save(u);
                    if (saved==null)
                        throw new RuntimeException("Failed to remove group.");
                }
                groupRepository.deleteById(groupId);
                return true;
            }else{
                throw new Exception("You don't have permission to do this operation");
            }
        }
    }
}
