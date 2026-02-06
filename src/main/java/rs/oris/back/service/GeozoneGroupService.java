package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.GeozoneGroup;
import rs.oris.back.domain.User;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.GeozoneGeozoneGroupRepository;
import rs.oris.back.repository.GeozoneGroupRepository;
import rs.oris.back.repository.VehicleGeozoneRepository;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GeozoneGroupService {

    @Autowired
    private GeozoneGroupRepository geozoneGroupRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private GeozoneGeozoneGroupRepository geozoneGeozoneGroupRepository;

    @Autowired
    private VehicleGeozoneRepository vehicleGeozoneRepository;

    /**
     * dodaje novu geozonu koju vezuje za konkretnu grupu
     */
    public Response<GeozoneGroup> addNewGeozone(GeozoneGroup geozoneGroup, User user, int firmId) throws Exception {
        if (user.getSuperAdmin() != true && user.getFirm().getFirmId() != firmId) {
            throw new Exception("You dont have permission for this operation.");
        }
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id " + firmId);
        }
        Firm firm = optionalFirm.get();
        geozoneGroup.setFirm(firm);
        GeozoneGroup saved = geozoneGroupRepository.save(geozoneGroup);
        if (saved==null)
            throw new Exception("Failed to save geozone");
        return new Response<>(saved);
    }

    /**
     * vraca sve geozone koje su vezane za grupu prosledjene firme
     * @param firmId firma
     */
    public Response<Map<String, List<GeozoneGroup>>> getAll(int firmId) {
        List<GeozoneGroup> list = geozoneGroupRepository.findByFirmFirmId(firmId);
        Map<String, List<GeozoneGroup>> map = new HashMap<>();
        map.put("geozoneGroups", list);
        return new Response<>(map);
    }

    //Brise geozon grupu
    @Transactional
    public void deleteGeozoneGroup(int groupId) {
        Optional<GeozoneGroup> geozoneGroupOptional = geozoneGroupRepository.findById(groupId);
        if(!geozoneGroupOptional.isPresent()) throw new RuntimeException("Geozone group not found");
        GeozoneGroup geozoneGroup = geozoneGroupOptional.get();
        //add deletion for this
//        vehicleGeozoneRepository.deleteByGeozoneGroup(geozoneGroup);

        geozoneGeozoneGroupRepository.deleteAllByGeozoneGroup(geozoneGroup);
        geozoneGroupRepository.deleteById(groupId);
    }
}
