package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.VehicleGroupRepository;
import rs.oris.back.repository.VehicleRepository;
import rs.oris.back.repository.VehicleVehicleGroupRepository;

import java.util.*;

@Service
public class VehicleVehicleGroupService {

    @Autowired
    private VehicleVehicleGroupRepository vehicleVehicleGroupRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleGroupRepository vehicleGroupRepository;
    @Autowired
    private FirmRepository firmRepository;
    /**
     *
     * vraca vozila grupe
     */
    public Response<Map<String, List<Vehicle>>> getAllVehiclesForGroup(User user, int vehicleGroupId, int firmId) throws Exception {
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if(!optionalFirm.isPresent()){
            throw new Exception("Firm not set");
        }

        List<VehicleVehicleGroup> listVG = vehicleVehicleGroupRepository.findByVehicleGroupVehicleGroupId(vehicleGroupId);
        List<Vehicle> list = new ArrayList<>();
        for (VehicleVehicleGroup vvg : listVG) {
            if (vvg.getVehicle() == null || vvg.getVehicle().getFirm() == null) {
                continue;
            }
            if (vvg.getVehicle().getFirm().getFirmId() != optionalFirm.get().getFirmId()) {
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
     * dodaje vozilo u grupu
     */
    public boolean assign(int groupId, int vehicleId, User user, int firmId) throws Exception {
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if(!optionalFirm.isPresent()){
            throw new Exception("Firm not set");
        }
        Optional<VehicleGroup> optionalVehicleGroup = vehicleGroupRepository.findById(groupId);
        if (!optionalVehicleGroup.isPresent()) {
            throw new Exception("Invalid vehicle group");
        }
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle");
        }
        Vehicle vehicle = optionalVehicle.get();
        VehicleGroup vehicleGroup = optionalVehicleGroup.get();

        if (vehicle.getFirm() == null || vehicleGroup.getFirm() == null) {
            throw new Exception("Firm not set for vehicle or vehicle group");
        }
        if (vehicle.getFirm().getFirmId() != optionalFirm.get().getFirmId() || vehicleGroup.getFirm().getFirmId() != optionalFirm.get().getFirmId()) {
            throw new Exception("Firms do not match!");
        }
        Optional<VehicleVehicleGroup> optionalVehicleVehicleGroup = vehicleVehicleGroupRepository.findByVehicleVehicleIdAndVehicleGroupVehicleGroupId(optionalVehicle.get().getVehicleId(), optionalVehicleGroup.get().getVehicleGroupId());
        if (optionalVehicleVehicleGroup.isPresent()) {
            throw new Exception("Vehicle is already in the group");
        }
        VehicleVehicleGroup vvh = new VehicleVehicleGroup();
        vvh.setVehicle(vehicle);
        vvh.setVehicleGroup(vehicleGroup);
        VehicleVehicleGroup saved = vehicleVehicleGroupRepository.save(vvh);
        if (saved == null) {
            throw new Exception("Failed to assign vehicle to a group");
        }
        return true;
    }

    /**
     *
     * brise vozilo iz grupe
     */
    @Transactional
    public boolean deleteVehicleFromGroup(int groupId, int vehicleId, User user, int firmId) throws Exception {
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if(!optionalFirm.isPresent()){
            throw new Exception("Firm not set");
        }
        Optional<VehicleGroup> optionalVehicleGroup = vehicleGroupRepository.findById(groupId);
        if (!optionalVehicleGroup.isPresent()) {
            throw new Exception("Invalid vehicle group");
        }
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle");
        }
        Vehicle vehicle = optionalVehicle.get();
        VehicleGroup vehicleGroup = optionalVehicleGroup.get();

        if (vehicle.getFirm() == null || vehicleGroup.getFirm() == null) {
            throw new Exception("Firm not set for vehicle or vehicle group");
        }
        if (vehicle.getFirm().getFirmId() != optionalFirm.get().getFirmId() || vehicleGroup.getFirm().getFirmId() != optionalFirm.get().getFirmId()) {
            throw new Exception("Firms do not match!");
        }

        vehicleVehicleGroupRepository.deleteByVehicleVehicleIdAndVehicleGroupVehicleGroupId(vehicleId, groupId);
        return true;
    }
}
