package rs.oris.back.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Geozone;
import rs.oris.back.domain.User;
import rs.oris.back.service.GeozoneService;
import rs.oris.back.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class GeozoneController {

    @Autowired
    private GeozoneService geozoneService;
    @Autowired
    private UserService userService;

    /**
     * dodaje novu geozonu
     */
    @PostMapping("/api/firm/{firm_id}/type/{type}")
    public Response<Geozone> addUser(@RequestBody String geozoneString, @RequestHeader("Authorization") String auth, @PathVariable("firm_id") int firmId, @PathVariable("type") String type,@RequestParam("color") String color) throws Exception {
        /**
         * provera korisnika
         */
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
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
    public boolean deleteAGeozone(@RequestHeader("Authorization") String auth, @PathVariable("id") int id) throws Exception {
        /**
         * provera korisnika
         */
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return geozoneService.deleteAGeozone(user, id);
    }

}
