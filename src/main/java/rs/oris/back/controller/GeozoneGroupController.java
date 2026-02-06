package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.AuthUtil;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.GeozoneGroup;
import rs.oris.back.domain.User;
import rs.oris.back.service.GeozoneGroupService;
import rs.oris.back.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
public class GeozoneGroupController {

    @Autowired
    private GeozoneGroupService geozoneGroupService;
    @Autowired
    private UserService userService;

    /**
     * dodaje novu geozonu koju vezuje za konkretnu grupu
     */
    @PostMapping("/api/firm/{firm_id}/geozone-group")
    public Response<GeozoneGroup> addUser(@RequestBody GeozoneGroup geozoneGroup, @PathVariable("firm_id") int firmId) throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return geozoneGroupService.addNewGeozone(geozoneGroup, user, firmId);
    }

    /**
     * vraca sve geozone koje su vezane za grupu prosledjene firme
     * @param firmId firma
     */
    @GetMapping("/api/firm/{firm_id}/geozone-group")
    public Response<Map<String, List<GeozoneGroup>>> getGeozones(@PathVariable("firm_id") int firmId) throws Exception {
        return geozoneGroupService.getAll(firmId);
    }

    @DeleteMapping("/api/geozones/{geozone_id}")
    public void deleteGeozoneGroup(@PathVariable("geozone_id") int geozoneId){
         geozoneGroupService.deleteGeozoneGroup(geozoneId);

    }

}
