package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Privilege;
import rs.oris.back.service.PrivilegeService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class PrivilegeController {

    @Autowired
    private PrivilegeService privilegeService;
    /**
     * vraca sve privilegije
     */
    @GetMapping("/api/firm/{firm_id}/privilege")
    public Response<Map<String, List<Privilege>>> getAllPrivilege() {
        return privilegeService.getAllPrivilege();
    }
    /**
     * vraca sve privilegije grupe
     */
    @GetMapping("/api/firm/{firm_id}/group/{group_id}/privilege")
    public Response<Map<String, List<Privilege>>> getAllPrivilegeForAGroup(@PathVariable("group_id") int groupId) {
        return privilegeService.getAllForGroup(groupId);
    }
    /**
     * vraca sve privilegije korisnika
     */
    @GetMapping("/api/firm/{firm_id}/user/{user_id}/privilege")
    public Response<Map<String, List<Privilege>>> getAllPrivilegeForAUser(@PathVariable("user_id") int userId) throws Exception {
        return privilegeService.getAllPrivilegeForAUser(userId);
    }



}
