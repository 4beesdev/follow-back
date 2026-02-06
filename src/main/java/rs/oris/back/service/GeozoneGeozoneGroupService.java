package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.GeozoneGeozoneGroupRepository;
import rs.oris.back.repository.GeozoneGroupRepository;
import rs.oris.back.repository.GeozoneRepository;

import java.util.*;

@Service
public class GeozoneGeozoneGroupService {

    @Autowired
    private GeozoneGeozoneGroupRepository geozoneGeozoneGroupRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private GeozoneRepository geozoneRepository;
    @Autowired
    private GeozoneGroupRepository geozoneGroupRepository;

    /**
     * dodaje postojecu geozonu u grupu
     */
    public boolean assign(int groupId, int geozoneId) throws Exception {

        Optional<GeozoneGroup> optionalGeozoneGroup = geozoneGroupRepository.findById(groupId);
        if (!optionalGeozoneGroup.isPresent()) {
            throw new Exception("Invalid geozone group id");
        }

        Optional<Geozone> optionalGeozone = geozoneRepository.findById(geozoneId);
        if (!optionalGeozone.isPresent()) {
            throw new Exception("Invalid geozone id");
        }
        if (optionalGeozone.get().getFirm().getFirmId() != optionalGeozoneGroup.get().getFirm().getFirmId()) {
            throw new Exception("Firms DO NOT MATCH");
        }

        GeozoneGeozoneGroup geozoneGeozoneGroup = new GeozoneGeozoneGroup();
        geozoneGeozoneGroup.setGeozone(optionalGeozone.get());
        geozoneGeozoneGroup.setGeozoneGroup(optionalGeozoneGroup.get());

        GeozoneGeozoneGroup saved = geozoneGeozoneGroupRepository.save(geozoneGeozoneGroup);
        if (saved == null) {
            throw new Exception("Failed to save ggg");
        }
        return true;

    }

    /**
     * delete
     */
    @Transactional
    public boolean deleteGeozone(int groupId, int geozoneId) {
        geozoneGeozoneGroupRepository.deleteByGeozoneGeozoneIdAndGeozoneGroupGeozoneGroupId(geozoneId, groupId);
        return true;
    }
    /**
     * vraca sve geozone koje pripadaju grupi
     */
    public Response<Map<String, List<Geozone>>> getAllGeozonesForAGroup(User user, int geozoneGroupId, int firmId) throws Exception {
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Firm not set");
        }

        List<GeozoneGeozoneGroup> listVG = geozoneGeozoneGroupRepository.findByGeozoneGeozoneGroupId(geozoneGroupId);
        List<Geozone> list = new ArrayList<>();
        for (GeozoneGeozoneGroup vvg : listVG) {
            if (vvg.getGeozone() == null || vvg.getGeozone().getFirm() == null) {
                continue;
            }
            if (vvg.getGeozone().getFirm().getFirmId() != optionalFirm.get().getFirmId()) {
                continue;
            }
            list.add(vvg.getGeozone());
        }
        Map<String, List<Geozone>> map = new HashMap<>();
        map.put("geozones", list);
        return new Response<>(map);
    }
    /**
     * brise sve geozone iz grupe
     */
    @Transactional
    public boolean deleteGeozoneByGroup(int groupId) {
        geozoneGeozoneGroupRepository.deleteByGeozoneGroupGeozoneGroupId(groupId);
        return true;
    }
}
