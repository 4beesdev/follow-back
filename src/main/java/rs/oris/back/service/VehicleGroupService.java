package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.User;
import rs.oris.back.domain.UserVehicleGroup;
import rs.oris.back.domain.VehicleGroup;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.UserVehicleGroupRepository;
import rs.oris.back.repository.VehicleGroupRepository;

import java.util.*;

@Service
public class VehicleGroupService {

    @Autowired
    private VehicleGroupRepository vehicleGroupRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private UserVehicleGroupRepository userVehicleGroupRepository;


    /**
     *
     * vraca sve grupe vozila jedne firme ili korisnika
     */
    public Response<Map<String, List<VehicleGroup>>> getAllVehicleGroups(User user, int firmId) throws Exception {
        if (user == null) {
            //System.out.println("Bad token");
        }
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            Optional<Firm> optionalFirm = firmRepository.findById(firmId);
            if (!optionalFirm.isPresent()) {
                throw new Exception("Invalid firm id");
            }
            List<VehicleGroup> list = vehicleGroupRepository.findByFirmFirmId(optionalFirm.get().getFirmId());
            Map<String, List<VehicleGroup>> map = new HashMap<>();
            map.put("vehicleGroups", list);
            return new Response<>(map);
        } else {

            if (user.getAdmin() != null && user.getAdmin() == true) {
                if (user.getFirm() != null) {
                    List<VehicleGroup> list = vehicleGroupRepository.findByFirmFirmId(user.getFirm().getFirmId());
                    Map<String, List<VehicleGroup>> map = new HashMap<>();
                    map.put("vehicleGroups", list);
                    return new Response<>(map);
                } else {
                    throw new Exception("Firm not set");
                }
            } else {

                List<UserVehicleGroup> userVehicleGroupList = userVehicleGroupRepository.findByUserUserId(user.getUserId());
                Set<VehicleGroup> vgrList = new HashSet<>();
                List<VehicleGroup> vehicleGroups = new ArrayList<>();

                for (UserVehicleGroup uvg : userVehicleGroupList) {
                    vgrList.add(uvg.getVehicleGroup());
                }

                for (VehicleGroup vehicleGroup : vgrList) {
                    vehicleGroups.add(vehicleGroup);
                }

                Map<String, List<VehicleGroup>> map = new HashMap<>();
                map.put("vehicleGroups", vehicleGroups);
                return new Response<>(map);

            }


        }
    }
    /**
     *
     * cuva novu grupu vozila u prosledjenoj firmi
     */
    public Response<VehicleGroup> addVehicleGroup(User user, VehicleGroup vehicleGroup, int firmId) throws Exception {
        if (user == null) {
            throw new Exception("Bad token");
        }
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            Optional<Firm> optionalFirm = firmRepository.findById(firmId);
            if (!optionalFirm.isPresent()) {
                throw new Exception("Invalid firm id");
            }
            vehicleGroup.setFirm(optionalFirm.get());
            VehicleGroup saved = vehicleGroupRepository.save(vehicleGroup);
            if (saved == null) {
                throw new Exception("Failed to save vehicle group");
            }
            return new Response<>(saved);
        } else {
            if (user.getFirm() == null) {
                throw new Exception("Users firm is not set!");
            }
        }
        vehicleGroup.setFirm(user.getFirm());
        VehicleGroup saved = vehicleGroupRepository.save(vehicleGroup);
        if (saved == null) {
            throw new Exception("Failed to save vehicle group");
        }
        return new Response<>(saved);
    }

    //Brisanje grupe vozila
    public Response<VehicleGroup> deleteVehicleGroup(User user, int vehicleGroupId,Long firmId) throws Exception {
        if (user == null) {
            throw new Exception("Bad token");
        }
        VehicleGroup byId = vehicleGroupRepository.findById(vehicleGroupId).orElseThrow(() -> new Exception("Vehicle group not found"));
        if(byId.getFirm().getFirmId()!=firmId) throw new Exception("Firm is not allowed to delete this vehicle group");
        vehicleGroupRepository.deleteById(vehicleGroupId);
        return new Response<>(null);
    }
}
