package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.AuthUtil;
import rs.oris.back.controller.wrapper.Response;

import rs.oris.back.domain.Route;
import rs.oris.back.domain.User;
import rs.oris.back.domain.dto.DTORoute;
import rs.oris.back.domain.dto.DTORouteBack;

import rs.oris.back.service.RouteService;
import rs.oris.back.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
public class RouteController {

    @Autowired
    private RouteService routeService;
    @Autowired
    private UserService userService;
    /**
     *
     * cuva novu rutu
     */
    @Transactional
    @PostMapping("/api/firm/{firm_id}/route")
    public Response<Route> addUser(@RequestBody DTORoute dtoRoute, @PathVariable("firm_id") int firmId) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return routeService.addNewRoute(dtoRoute,user,firmId);
    }

    /**
     * vraca sve rute firme
     */
    @Transactional
    @GetMapping("/api/firm/{firm_id}/route")
    public Response<Map<String, List<DTORouteBack>>> getRoutes(@PathVariable("firm_id") int firmId) throws Exception{
        return routeService.getAll(firmId);
    }
    /**
     * vraca jednu rutu po id-u kao entitet
     */
    @Transactional
    @GetMapping("/api/firm/{firm_id}/route/{route_id}")
    public Response<Route> getSingleRoute(@PathVariable("firm_id") int firmId, @PathVariable("route_id") int routeId) throws Exception{
        return routeService.getSingleRoute(routeId,firmId);
    }

    /**
     * vraca jednu rutu po id-u kao string
     */
    @GetMapping("/api/firm/{firm_id}/route/{route_id}/string")
    public Response<String> getSingleRouteString(@PathVariable("firm_id") int firmId, @PathVariable("route_id") int routeId) throws Exception{
        return routeService.getRouteString(routeId,firmId);
    }
    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/route/{route_id}")
    public Response<Boolean> deleteRoute(@PathVariable("firm_id") int firmId, @PathVariable("route_id") int routeId) throws Exception{
        return routeService.deleteRoute(routeId,firmId);
    }
}
