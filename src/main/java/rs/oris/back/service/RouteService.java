package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.Route;
import rs.oris.back.domain.User;
import rs.oris.back.domain.dto.DTORoute;
import rs.oris.back.domain.dto.DTORouteBack;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.RouteRepository;
import rs.oris.back.repository.VehicleRouteRepository;

import java.util.*;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private VehicleRouteRepository vehicleRouteRepository;
    /**
     *
     * cuva novu rutu
     */
    public Response<Route> addNewRoute(DTORoute dtoRoute, User user, int firmId) throws Exception {
        if (user.getSuperAdmin() != true && user.getFirm().getFirmId() != firmId) {
            throw new Exception("You dont have permission for this operation.");
        }
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id " + firmId);
        }
        Firm firm = optionalFirm.get();

        Route route = new Route();
        route.setFirm(firm);
        route.setName(dtoRoute.getName());
        route.setRouteString(dtoRoute.getRouteString());
        Route saved = routeRepository.save(route);
        return new Response<>(saved);
    }
    /**
     * vraca sve rute firme
     */
    public Response<Map<String, List<DTORouteBack>>> getAll(int firmId) {
        List<Route> list = routeRepository.findByFirmFirmId(firmId);
        List<DTORouteBack> dtoRouteBacks = new ArrayList<>();

        for (Route route : list){
            dtoRouteBacks.add(new DTORouteBack(route));
        }

        Map<String, List<DTORouteBack>> map = new HashMap<>();
        map.put("routes", dtoRouteBacks);
        return new Response<>(map);
    }
    /**
     * vraca jednu rutu po id-u kao entitet
     */
    public Response<Route> getSingleRoute(int routeId, int firmId) throws Exception {
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (!optionalRoute.isPresent()) {
            throw new Exception("Route does not exist");
        }
        return new Response<>(optionalRoute.get());
    }
    /**
     * delete
     */
    @Transactional
    public Response<Boolean> deleteRoute(int routeId, int firmId) throws Exception {
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (!optionalRoute.isPresent()) {
            throw new RuntimeException("Route does not exist");
        }
        vehicleRouteRepository.deleteByRouteRouteId(routeId);
        routeRepository.deleteById(routeId);

        return new Response<>(true);

    }
    /**
     * vraca jednu rutu po id-u kao string
     */
    public Response<String> getRouteString(int routeId, int firmId) throws Exception {
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (!optionalRoute.isPresent()) {
            throw new Exception("Route does not exist");
        }
        Route route = optionalRoute.get();

        return new Response<>(route.getRouteString());
    }
}
