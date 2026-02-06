package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.AuthUtil;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Route;
import rs.oris.back.domain.User;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.service.UserService;
import rs.oris.back.service.VehicleRouteService;


import java.util.List;
import java.util.Map;
/**
 * CRUD operacije za VehicleRoute
 */
@RestController
public class VehicleRouteController {

    @Autowired
    private VehicleRouteService vehicleRouteService;
    @Autowired
    private UserService userService;

    /**
     *
     * sva vozila iz rute
     */
    @GetMapping("/api/firm/{firm_id}/route/{route_id}/vehicle")
    public Response<Map<String, List<Vehicle>>> getAllVehiclesForRoute(@PathVariable("route_id") int routeId) throws Exception {
        return vehicleRouteService.getAllVehiclesForRoute(routeId);
    }
    /**
     *
     * sve rute vozila
     */
    @GetMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/route")
    public Response<Map<String, List<Route>>> getAllRouteForAVehicle(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return vehicleRouteService.getAllRoutesForAVehicle(vehicleId);
    }
    /**
     *
     * dodaje vozilo ruti, iako po imenu funkcije neko ce pomisliti geozoni
     */
    @PostMapping("api/firm/{firm_id}/assign/vehicle/{vehicle_id}/route/{route_id}/from/{from}/to/{to}")
    public boolean assignVehicleToGezone(@PathVariable("vehicle_id") int vehicleId, @PathVariable("route_id") int routeId,
                                         @PathVariable("firm_id") int firmId, @PathVariable("from") long from, @PathVariable("to") long to) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleRouteService.assign(routeId,vehicleId, user, firmId, from, to);
    }
    /**
     *
     * brise vozilo iz grupe
     */
    @Transactional
    @DeleteMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/route/{route_id}")
    public boolean deleteAVehicleFromAGroup(@PathVariable("vehicle_id") int vehicleId, @PathVariable("route_id") int routeId, @PathVariable("firm_id") int firmId) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleRouteService.deleteByVehicleRoute(routeId, vehicleId, user, firmId);
    }
    /**
     *
     * brise sva vozila iz rute
     */
    @Transactional
    @DeleteMapping("api/firm/{firm_id}/route/{route_id}/vehicle")
    public boolean deleteByRoute(@PathVariable("route_id") int routeId, @PathVariable("firm_id") int firmId) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleRouteService.deleteByRoute(routeId, user, firmId);
    }

    @Transactional
    @DeleteMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/route")
    public boolean deleteByVehicle(@PathVariable("vehicle_id") int vehicleId, @PathVariable("firm_id") int firmId) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleRouteService.deleteByVehicle(vehicleId, user, firmId);
    }

}
