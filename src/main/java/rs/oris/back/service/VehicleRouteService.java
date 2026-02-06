package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.RouteRepository;
import rs.oris.back.repository.VehicleRepository;
import rs.oris.back.repository.VehicleRouteRepository;

import java.util.*;

@Service
public class VehicleRouteService {

    @Autowired
    private VehicleRouteRepository vehicleRouteRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    /**
     *
     * brise sva vozila iz rute //UKIDA VEZU
     */
    @Transactional
    public boolean deleteByRoute(int routeId, User user, int firmId) throws Exception {
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            vehicleRouteRepository.deleteByRouteRouteId(routeId);
        } else {
            Optional<Route> optionalRoute = routeRepository.findById(routeId);
            if (!optionalRoute.isPresent()) {
                throw new Exception("Invalid route id " + routeId);
            }
            if (optionalRoute.get().getFirm().getFirmId() != user.getFirm().getFirmId()) {
                throw new Exception("No permission!");
            } else {
                vehicleRouteRepository.deleteByRouteRouteId(routeId);
            }

        }
        return true;
    }

    /**
     *
     * brise sve rute iz vozila //UKIDA VEZU
     */
    @Transactional
    public boolean deleteByVehicle(int vehicleId, User user, int firmId) throws Exception {
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            vehicleRouteRepository.deleteByVehicleVehicleId(vehicleId);
        } else {
            Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
            if (!optionalVehicle.isPresent()) {
                throw new Exception("Invalid vehicle id " + vehicleId);
            }
            if (optionalVehicle.get().getFirm().getFirmId() != user.getFirm().getFirmId()) {
                throw new Exception("No permission!");
            } else {
                vehicleRouteRepository.deleteByVehicleVehicleId(vehicleId);
            }
        }
        return true;
    }
    /**
     *
     * brise jedno vozilo iz jedne rute //UKIDA VEZU
     */
    @Transactional
    public boolean deleteByVehicleRoute(int routeId, int vehicleId, User user, int firmId) throws Exception {
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            vehicleRouteRepository.deleteByRouteRouteIdAndVehicleVehicleId(routeId, vehicleId);
            return true;
        }
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id " + vehicleId);
        }
        Vehicle v = optionalVehicle.get();
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (!optionalRoute.isPresent()) {
            throw new Exception("Invalid route id " + routeId);
        }
        Route g = optionalRoute.get();
        if (v.getFirm().getFirmId() != user.getFirm().getFirmId() || g.getFirm().getFirmId() != user.getFirm().getFirmId()) {
            throw new Exception("No permission!");
        }
        return true;
    }
    /**
     *
     * spaja vozilo i rutu, tj dodaje vozilo u rutu i rutu u vozilo
     *
     */
    public boolean assign(int routeId, int vehicleId, User user, int firmId, long from, long to) throws Exception {
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Firm not set");
        }
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (!optionalRoute.isPresent()) {
            throw new Exception("Invalid route");
        }
        Route route = optionalRoute.get();
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle");
        }
        Vehicle vehicle = optionalVehicle.get();
        if (user.getSuperAdmin() != null && user.getSuperAdmin() == true) {
            if (vehicle.getFirm().getFirmId() != route.getFirm().getFirmId()) {
                throw new Exception("Invalid combination");
            }
            VehicleRoute vehicleRoute = new VehicleRoute();
            vehicleRoute.setVehicle(vehicle);
            vehicleRoute.setRoute(route);
            vehicleRoute.setFromRoute(from);
            vehicleRoute.setToRoute(to);
            VehicleRoute saved = vehicleRouteRepository.save(vehicleRoute);
            if (saved == null) {
                throw new Exception("Failed to save vehicle route");
            }
            return true;
        } else {
            if (vehicle.getFirm().getFirmId() != user.getFirm().getFirmId() || route.getFirm().getFirmId() != user.getFirm().getFirmId()) {
                throw new Exception("No permission");
            }
            if (vehicle.getFirm().getFirmId() != route.getFirm().getFirmId()) {
                throw new Exception("Invalid combination");
            }

            VehicleRoute vehicleRoute = new VehicleRoute();
            vehicleRoute.setVehicle(vehicle);
            vehicleRoute.setRoute(route);
            VehicleRoute saved = vehicleRouteRepository.save(vehicleRoute);
            if (saved == null) {
                throw new Exception("Failed to save vehicle route");
            }
            return true;
        }
    }
    /**
     * sva vozila iz rute
     */
    public Response<Map<String, List<Vehicle>>> getAllVehiclesForRoute(int routeId) {
        List<VehicleRoute> listVG = vehicleRouteRepository.findByRouteRouteId(routeId);
        List<Vehicle> list = new ArrayList<>();
        for (VehicleRoute vvg : listVG) {
            if (vvg.getVehicle() == null || vvg.getVehicle().getFirm() == null) {
                continue;
            }
            list.add(vvg.getVehicle());
        }
        Map<String, List<Vehicle>> map = new HashMap<>();
        map.put("vehicles", list);
        return new Response<>(map);
    }
    /**
     * sve rute jednog vozila
     */
    public Response<Map<String, List<Route>>> getAllRoutesForAVehicle(int vehicleId) {
        List<VehicleRoute> listVG = vehicleRouteRepository.findByVehicleVehicleId(vehicleId);
        List<Route> list = new ArrayList<>();
        for (VehicleRoute vvg : listVG) {
            if (vvg.getRoute() == null || vvg.getRoute().getFirm() == null) {
                continue;
            }
            list.add(vvg.getRoute());
        }
        Map<String, List<Route>> map = new HashMap<>();
        map.put("routes", list);
        return new Response<>(map);
    }
}
