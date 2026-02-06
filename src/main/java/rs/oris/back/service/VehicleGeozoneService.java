package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.GeozoneRepository;
import rs.oris.back.repository.VehicleGeozoneRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.*;

@Service
public class VehicleGeozoneService {

    @Autowired
    private VehicleGeozoneRepository vehicleGeozoneRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private GeozoneRepository geozoneRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     *
     * uklanja sva vozila iz geozone
     */
    @Transactional
    public boolean deleteByGeozone(int geozoneId, User user, int firmId) throws Exception {
        if (user.getSuperAdmin()!=null && user.getSuperAdmin()){
            vehicleGeozoneRepository.deleteByGeozoneGeozoneId(geozoneId);
        }else{
            Optional<Geozone> optionalGeozone = geozoneRepository.findById(geozoneId);
            if (!optionalGeozone.isPresent()) {
                throw new Exception("Invalid geozone id "+geozoneId);
            }
            if (optionalGeozone.get().getFirm().getFirmId()!=user.getFirm().getFirmId()){
                throw new Exception("No permission!");
            }else{
                vehicleGeozoneRepository.deleteByGeozoneGeozoneId(geozoneId);
            }

        }
        return true;
    }





    /**
     *
     * uklanja vozilo iz svih geozona
     */
    @Transactional
    public boolean deleteByVehicle(int vehicleId, User user, int firmId) throws Exception {
        if (user.getSuperAdmin()!=null && user.getSuperAdmin()){
            vehicleGeozoneRepository.deleteByVehicleVehicleId(vehicleId);
        }else{
            Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
            if (!optionalVehicle.isPresent()) {
                throw new Exception("Invalid vehicle id "+vehicleId);
            }
            if (optionalVehicle.get().getFirm().getFirmId()!=user.getFirm().getFirmId()){
                throw new Exception("No permission!");
            }else{
                vehicleGeozoneRepository.deleteByVehicleVehicleId(vehicleId);
            }
        }
        return true;
    }
    /**
     *
     * brise vozilo iz geozone //ukida vezu
     */
    @Transactional
    public boolean deleteByVehicleGeozone(int geozoneId, int vehicleId, User user, int firmId) throws Exception {
        if(user.getSuperAdmin()!=null && user.getSuperAdmin()){
            vehicleGeozoneRepository.deleteByGeozoneGeozoneIdAndVehicleVehicleId(geozoneId,vehicleId);
            return true;
        }
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id "+vehicleId);
        }
        Vehicle v = optionalVehicle.get();
        Optional<Geozone> optionalGeozone = geozoneRepository.findById(geozoneId);
        if (!optionalGeozone.isPresent()) {
            throw new Exception("Invalid geozone id "+geozoneId);
        }
        Geozone g = optionalGeozone.get();
        if (v.getFirm().getFirmId()!=user.getFirm().getFirmId() || g.getFirm().getFirmId()!=user.getFirm().getFirmId()  ){
            throw new Exception("No permission!");
        }
        return true;
    }

    /**
     *
     * dodeljuje vozilo geozoni
     */
    public boolean assign(int geozoneId, int vehicleId, User user, int firmId) throws Exception {
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if(!optionalFirm.isPresent()){
            throw new Exception("Firm not set");
        }
        Optional<Geozone> optionalGeozone = geozoneRepository.findById(geozoneId);
        if (!optionalGeozone.isPresent()) {
            throw new Exception("Invalid geozone");
        }
        Geozone geozone = optionalGeozone.get();
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle");
        }
        Vehicle vehicle = optionalVehicle.get();
        if(user.getSuperAdmin()!=null && user.getSuperAdmin()==true){
            if (vehicle.getFirm().getFirmId()!=geozone.getFirm().getFirmId()){
                throw new Exception("Invalid combination");
            }
             VehicleGeozone vehicleGeozone = new VehicleGeozone();
            vehicleGeozone.setVehicle(vehicle);
            vehicleGeozone.setGeozone(geozone);
            VehicleGeozone saved = vehicleGeozoneRepository.save(vehicleGeozone);
            if (saved == null) {
                throw new Exception("Failed to save vehicle geozone");
            }
            return true;
        }else{
            if(vehicle.getFirm().getFirmId()!=user.getFirm().getFirmId() || geozone.getFirm().getFirmId()!=user.getFirm().getFirmId()){
                throw new Exception("No permission");
            }
            if (vehicle.getFirm().getFirmId()!=geozone.getFirm().getFirmId()){
                throw new Exception("Invalid combination");
            }

            VehicleGeozone vehicleGeozone = new VehicleGeozone();
            vehicleGeozone.setVehicle(vehicle);
            vehicleGeozone.setGeozone(geozone);
            VehicleGeozone saved = vehicleGeozoneRepository.save(vehicleGeozone);
            if (saved == null) {
                throw new Exception("Failed to save vehicle geozone");
            }
            return true;
        }
    }
    /**
     *
     * vraca sva vozila iz geozone
     */
    public Response<Map<String, List<Vehicle>>> getAllVehiclesForGeozone(int geozoneId) {
        List<VehicleGeozone> listVG = vehicleGeozoneRepository.findByGeozoneGeozoneId(geozoneId);
        List<Vehicle> list = new ArrayList<>();
        for (VehicleGeozone vvg : listVG) {
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
     *
     * vraca sve geozone kojima pripada vozilo
     */
    public Response<Map<String, List<Geozone>>> getAllGeozonesForAVehicle(int vehicleId) {
        List<VehicleGeozone> listVG = vehicleGeozoneRepository.findByVehicleVehicleId(vehicleId);
        List<Geozone> list = new ArrayList<>();
        for (VehicleGeozone vvg : listVG) {
            if (vvg.getGeozone() == null || vvg.getGeozone().getFirm() == null) {
                continue;
            }
            list.add(vvg.getGeozone());
        }
        Map<String, List<Geozone>> map = new HashMap<>();
        map.put("geozones", list);
        return new Response<>(map);
    }
}
