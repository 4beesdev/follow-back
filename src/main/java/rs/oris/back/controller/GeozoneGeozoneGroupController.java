package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.AuthUtil;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Geozone;
import rs.oris.back.domain.User;
import rs.oris.back.service.GeozoneGeozoneGroupService;
import rs.oris.back.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
public class GeozoneGeozoneGroupController {

    @Autowired
    private GeozoneGeozoneGroupService geozoneGeozoneGroupService;
    @Autowired
    private UserService userService;

    /**
     * vraca sve geozone koje pripadaju grupi
     */
    @GetMapping("/api/firm/{firm_id}/geozone-group/{geozone_group_id}/geozone")
    public Response<Map<String, List<Geozone>>> getAllVehicles(@PathVariable("geozone_group_id") int geozoneGroupId, @PathVariable("firm_id") int firmId) throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return geozoneGeozoneGroupService.getAllGeozonesForAGroup(user, geozoneGroupId, firmId);
    }
    /**
     * dodaje postojecu geozonu u grupu
     */
    @PostMapping("api/firm/{firm_id}/assign/geozone/{geozone_id}/group/{group_id}")
    public boolean assignAVehicleToAGroup(@PathVariable("geozone_id") int geozoneId, @PathVariable("group_id") int groupId) throws Exception {
        return geozoneGeozoneGroupService.assign(groupId, geozoneId);
    }

    /**
     * delete
     */
    @Transactional
    @DeleteMapping("api/firm/{firm_id}/assign/geozone/{geozone_id}/group/{group_id}")
    public boolean deleteAGeozone(@PathVariable("geozone_id") int geozoneId, @PathVariable("group_id") int groupId) throws Exception {
        return geozoneGeozoneGroupService.deleteGeozone(groupId, geozoneId);
    }


    /**
     * brise sve geozone iz grupe
     */
    @Transactional
    @DeleteMapping("api/firm/{firm_id}/group/{group_id}/geozone")
    public boolean deleteAllGezonesForAGroup(@PathVariable("group_id") int groupId) throws Exception {
        return geozoneGeozoneGroupService.deleteGeozoneByGroup(groupId);
    }


}
