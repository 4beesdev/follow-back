package rs.oris.back.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.GeozoneGeozoneGroupRepository;
import rs.oris.back.repository.GeozoneRepository;
import rs.oris.back.repository.VehicleGeozoneRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GeozoneService {

    @Autowired
    private GeozoneRepository geozoneRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private GeozoneGeozoneGroupRepository geozoneGeozoneGroupRepository;
    @Autowired
    private VehicleGeozoneRepository vehicleGeozoneRepository;
    @Autowired
    private NotificationVehicleService notificationVehicleService;

    /**
     * dodaje novu geozonu
     */
    public Response<Geozone> addNewGeozone(String geozoneString, User user, int firmId, String type, String color) throws Exception {
        if (user.getSuperAdmin() != true && user.getFirm().getFirmId() != firmId) {
            throw new Exception("You dont have permission for this operation.");
        }
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id " + firmId);
        }
        Firm firm = optionalFirm.get();


        Map<String, Object> retMap = new Gson().fromJson(
                geozoneString, new TypeToken<HashMap<String, Object>>() {
                }.getType()
        );

        Geozone geozone = new Geozone();
        String name = (String) retMap.get("name");
        geozone.setName(name);
        geozone.setFirm(firm);
        geozone.setJson(geozoneString);
        geozone.setType(type);
        geozone.setColor(color);

        switch (type) {
            case "circ":
                Double radius = (Double) retMap.get("radius");
                Coordinates coord = new Gson().fromJson(
                        retMap.get("center").toString(), new TypeToken<Coordinates>() {
                        }.getType()
                );


                geozone.setLat(coord.getLat());
                geozone.setLng(coord.getLng());
                geozone.setRadius(radius);
                break;
            case "rect":

                Bounds bounds = new Gson().fromJson(
                        retMap.get("bounds").toString(), new TypeToken<Bounds>() {
                        }.getType()
                );

                geozone.setLatTL(bounds.getEast());
                geozone.setLngTL(bounds.getNorth());
                geozone.setLatBR(bounds.getWest());
                geozone.setLngBR(bounds.getSouth());
                break;
            case "poly":


                break;
            default:
                throw new Exception("Type is not set well");
        }

        Geozone saved = geozoneRepository.save(geozone);
        if (saved == null) {
            throw new Exception("Failed to save geozone");
        }
        return new Response<>(saved);
    }
    /**
     * vraca sve geozone koje pripadaju firmi
     * @param firmId firma
     */
    public Response<Map<String, List<Geozone>>> getAll(int firmId) {
        List<Geozone> list = geozoneRepository.findByFirmFirmId(firmId);

        Map<String, List<Geozone>> map = new HashMap<>();
        map.put("geozones", list);
        return new Response<>(map);
    }
    /**
     * delete
     */
    @Transactional
    public boolean deleteAGeozone(User user, int id) throws Exception {
        Optional<Geozone> optionalGeozone = geozoneRepository.findById(id);
        if (!optionalGeozone.isPresent()) {
            throw new Exception("Invalid geozone id");
        }
        Geozone geozone = optionalGeozone.get();
        if (user.getSuperAdmin()) {


            vehicleGeozoneRepository.deleteByGeozoneGeozoneId(id);
            geozoneGeozoneGroupRepository.deleteByGeozoneGeozoneId(id);

            for (NotificationVehicle notificationVehicle : geozone.getNotificationVehicleSet()) {
                notificationVehicleService.delete(notificationVehicle.getNotificationVehicleId());
            }

            geozoneRepository.deleteById(id);

            return true;
        } else {
            if (user.getAdmin() && user.getFirm().getFirmId() == optionalGeozone.get().getFirm().getFirmId()) {

                vehicleGeozoneRepository.deleteByGeozoneGeozoneId(id);
                geozoneGeozoneGroupRepository.deleteByGeozoneGeozoneId(id);
                for (NotificationVehicle notificationVehicle : geozone.getNotificationVehicleSet()) {
                    notificationVehicleService.delete(notificationVehicle.getNotificationVehicleId());
                }

                geozoneRepository.deleteById(id);
                return true;
            } else {
                throw new Exception("User is not admin or the firms do not match.");
            }
        }


    }
}
