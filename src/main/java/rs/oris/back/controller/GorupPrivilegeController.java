package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.oris.back.service.GroupPrivilegeService;

@RestController
public class GorupPrivilegeController {

    @Autowired
    private GroupPrivilegeService groupPrivilegeService;
    /**
     * create/save
     *
     * dodeljuje privilegiju grupi
     * !!! BRISANJE PRIVILEGIJA SE NALAZI U KONTROLERU ZA GRUPE !!!
     */
    @PostMapping("/api/firm/{firm_id}/group/{group_id}/privilege/{privilege_id}")
    public boolean getAllGroups(@PathVariable("group_id") int groupId, @PathVariable("privilege_id") int privilegeId) throws Exception {
        return groupPrivilegeService.addGP(groupId,privilegeId);
    }





}
