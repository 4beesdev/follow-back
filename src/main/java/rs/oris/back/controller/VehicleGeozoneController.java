package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.AuthUtil;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Geozone;
import rs.oris.back.domain.User;
import rs.oris.back.domain.Vehicle;

import rs.oris.back.service.UserService;
import rs.oris.back.service.VehicleGeozoneService;


import java.util.List;
import java.util.Map;

@RestController
public class VehicleGeozoneController {

    @Autowired
    private VehicleGeozoneService vehicleGeozoneService;
    @Autowired
    private UserService userService;
    /**
     *
     * sva vozila iz geozone
     */
    @GetMapping("/api/firm/{firm_id}/geozone/{geozone_id}/vehicle")
    public Response<Map<String, List<Vehicle>>> getAllVehiclesForGeozone(@PathVariable("geozone_id") int geozoneId) throws Exception {
        return vehicleGeozoneService.getAllVehiclesForGeozone(geozoneId);
    }
    /**
     *
     * sve geozone kojima pripada vozilo
     */
    @GetMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/geozone")
    public Response<Map<String, List<Geozone>>> getAllGeozoneForAVehicle(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return vehicleGeozoneService.getAllGeozonesForAVehicle(vehicleId);
    }
    /**
     *
     * dodeljuje vozilo geozoni
     */
    @PostMapping("api/firm/{firm_id}/assign/vehicle/{vehicle_id}/geozone/{geozone_id}")
    public boolean assignVehicleToGezone(@PathVariable("vehicle_id") int vehicleId, @PathVariable("geozone_id") int geozoneId,@PathVariable("firm_id") int firmId) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleGeozoneService.assign(geozoneId,vehicleId, user, firmId);
    }
    /**
     *
     * brise vozilo iz geozone //ukida vezu
     */
    @Transactional
    @DeleteMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/geozone/{geozone_id}")
    public boolean deleteAVehicleFromAGroup(@PathVariable("vehicle_id") int vehicleId, @PathVariable("geozone_id") int geozoneId, @PathVariable("firm_id") int firmId) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleGeozoneService.deleteByVehicleGeozone(geozoneId, vehicleId, user, firmId);
    }
    /**
     *
     * uklanja sva vozila iz geozone
     */
    @Transactional
    @DeleteMapping("api/firm/{firm_id}/geozone/{geozone_id}/vehicle")
    public boolean deleteByGeozone(@PathVariable("geozone_id") int geozoneId, @PathVariable("firm_id") int firmId) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleGeozoneService.deleteByGeozone(geozoneId, user, firmId);
    }
    /**
     *
     * uklanja vozilo iz svih geozona
     */
    @Transactional
    @DeleteMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/geozone")
    public boolean deleteByVehicle(@PathVariable("vehicle_id") int vehicleId, @PathVariable("firm_id") int firmId) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleGeozoneService.deleteByVehicle(vehicleId, user, firmId);
    }

}
