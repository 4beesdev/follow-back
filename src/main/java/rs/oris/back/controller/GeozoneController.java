package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.AuthUtil;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Geozone;
import rs.oris.back.domain.User;
import rs.oris.back.service.GeozoneService;
import rs.oris.back.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
public class GeozoneController {

    @Autowired
    private GeozoneService geozoneService;
    @Autowired
    private UserService userService;

    /**
     * dodaje novu geozonu
     */
    @PostMapping("/api/firm/{firm_id}/type/{type}")
    public Response<Geozone> addUser(@RequestBody String geozoneString, @PathVariable("firm_id") int firmId, @PathVariable("type") String type,@RequestParam("color") String color) throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return geozoneService.addNewGeozone(geozoneString, user, firmId, type,color);
    }
    /**
     * vraca sve geozone koje pripadaju firmi
     * @param firmId firma
     */
    @GetMapping("/api/firm/{firm_id}/geozone")
    public Response<Map<String, List<Geozone>>> getGeozones(@PathVariable("firm_id") int firmId) throws Exception {
        return geozoneService.getAll(firmId);
    }

    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/geozone/{id}")
    public boolean deleteAGeozone(@PathVariable("id") int id) throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return geozoneService.deleteAGeozone(user, id);
    }

}
