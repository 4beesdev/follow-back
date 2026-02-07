package rs.oris.back.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rs.oris.back.config.MongoServerConfig;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.domain.dto.NotifBody;
import rs.oris.back.repository.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.*;

@Service
@Slf4j
public class NotificationVehicleService {

    @Autowired
    private NotificationVehicleRepository notificationVehicleRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GeozoneRepository geozoneRepository;

    @Autowired
    private GroupPrivilegeRepository groupPrivilegeRepository;

    /**
    @Autowired
    private RestTemplate restTemplate;
     * delegira delete
     */
    public boolean delete(int notificationVehicleId) {
        notificationVehicleRepository.deleteById(notificationVehicleId);

        delteSingleNotif(notificationVehicleId);
        return true;
    }
    /**
     * deleete
     */
    private void delteSingleNotif(int notificationVehicleId) {
        String uri = "http://localhost:8080/api/remove/one";
        uri = "http://142.93.164.78:8080/api/notification/remove/one";
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/notification/remove/one";
        } catch (Exception e) {
        }
    }
    /**
     * salje notifikaciju na sms ili mail
     *
     */
    public Response<NotificationVehicle> add(User user, int notificationId, int vehicleId, int userId, NotifBody notifBody, int firmId) throws Exception {

        //Pronadji koristnika
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("User with id " + userId + " not found!");
        }

        //Pronadji i proveri da li notifikacija vec postoji
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (!optionalNotification.isPresent()) {
            throw new Exception("Notification with id " + notificationId + " not found!");
        }

        //Pronadji vozilo
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Vehicle with id " + vehicleId + " not found!");
        }


        //Ako user koji pokrece zahtev nije super admin
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            NotificationVehicle notificationVehicle = new NotificationVehicle();
            //Ako je notifikacija tipa geozone
            if (notificationId == 1) {
                Optional<Geozone> optionalGeozone = geozoneRepository.findById(notifBody.getGeozone().getGeozoneId());
                if (!optionalGeozone.isPresent()) {
                    throw new Exception("Invalid geozone id " + notifBody.getGeozone().getGeozoneId());
                } else {
                    notificationVehicle.setGeozone(optionalGeozone.get());
                }
            }
            //Popuni objekat NotificationVehicle
            notificationVehicle.setNotification(optionalNotification.get());
            notificationVehicle.setVehicle(optionalVehicle.get());
            notificationVehicle.setUser(optionalUser.get());
            notificationVehicle.setSpeed(notifBody.getSpeed());
            notificationVehicle.setFuelMargin(notifBody.getFuelMargin());
            notificationVehicle.setEngine(notificationVehicle.getVehicle().getEngineSize());
            notificationVehicle.setServiceIntervalNotif(notifBody.getServiceIntervalNotif());
            notificationVehicle.setServiceIntervalLength(notifBody.getServiceIntervalLength());
            notificationVehicle.setMail(notifBody.isEmail());
            notificationVehicle.setSms(notifBody.isSms());
            notificationVehicle.setPush(notifBody.isPush());
            notificationVehicle.setFirmId(firmId);
            notificationVehicle.setStartTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
            notificationVehicle.setVehicleRegistration(notifBody.getVehicleRegistration());
            notificationVehicle.setNotifyDaysBeforeVehicleRegistration(notifBody.getNotifyDaysBeforeVehicleRegistration());
            notificationVehicle.setEmptyingFuelMargine(notifBody.getEmptyingFuelMargine());
            notificationVehicle.setServiceIntervalNotifBoard(notifBody.getServiceIntervalNotifBoard());
            notificationVehicle.setServiceIntervalLengthBoard(notifBody.getServiceIntervalLengthBoard());
            notificationVehicle.setFuelDiffFound(false);
            //set data for gps
            notificationVehicle.setServiceIntervalNotifGps(notifBody.getServiceIntervalNotifGps());
            notificationVehicle.setServiceIntervalLengthGps(notifBody.getServiceIntervalLengthGps());
            notificationVehicle.setKilometers(optionalVehicle.get().getMillage());

//            if(geozoneSpeed.getGeozone()!=null){
//                Optional<Geozone> optionalGeozone = geozoneRepository.findById(geozoneSpeed.getGeozone().getGeozoneId());
//                if(!optionalGeozone.isPresent()){
//                    throw new Exception("Geozone in but not present!");
//                }else{
//                    notificationVehicle.setGeozone(optionalGeozone.get());
//                }
//            }
//            if(geozoneSpeed.getSpeed()!=0){
//                notificationVehicle.setSpeed(geozoneSpeed.getSpeed());
//            }


            //Save newlly created notification vehicle
            NotificationVehicle saved = notificationVehicleRepository.save(notificationVehicle);
            log.info("SACUVANA NOTIFIKACIJA : Saved notification vehicle: {}", saved);
            if (saved == null) {
                throw new Exception("Failed to save notification vehicle");
            }
            try {
                //Send to mongo server
                updateSingleNotif(saved);
            } catch (Exception e) {
                e.printStackTrace();

            }
            return new Response<>(saved);
        }
        //If its not super admin
        else {
            //If firm does not exist
            if (optionalVehicle.get().getFirm().getFirmId() != user.getFirm().getFirmId()) {
                throw new Exception("You dont have permission for this operation");
            }

            //Check if notification already exists for this vehicle
            Optional<NotificationVehicle> byNotificationAndVehicleAndFirmId = notificationVehicleRepository.findByNotificationAndVehicleAndFirmId(optionalNotification.get(), optionalVehicle.get(), firmId);
            if(byNotificationAndVehicleAndFirmId.isPresent()){
                throw new RuntimeException("Notification already exists for this vehicle");
            }

            //Create new notification vehicle
            NotificationVehicle notificationVehicle = new NotificationVehicle();
            notificationVehicle.setNotification(optionalNotification.get());
            notificationVehicle.setVehicle(optionalVehicle.get());
            notificationVehicle.setUser(optionalUser.get());
            notificationVehicle.setSpeed(notifBody.getSpeed());
            notificationVehicle.setFuelMargin(notifBody.getFuelMargin());
            notificationVehicle.setEngine(notificationVehicle.getVehicle().getEngineSize());
            notificationVehicle.setServiceIntervalNotif(notifBody.getServiceIntervalNotif());
            notificationVehicle.setServiceIntervalLength(notifBody.getServiceIntervalLength());
            notificationVehicle.setMail(notifBody.isEmail());
            notificationVehicle.setSms(notifBody.isSms());
            notificationVehicle.setPush(notifBody.isPush());
            notificationVehicle.setFirmId(firmId);
            notificationVehicle.setStartTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
            notificationVehicle.setVehicleRegistration(notifBody.getVehicleRegistration());
            notificationVehicle.setNotifyDaysBeforeVehicleRegistration(notifBody.getNotifyDaysBeforeVehicleRegistration());
            notificationVehicle.setEmptyingFuelMargine(notifBody.getEmptyingFuelMargine());
            notificationVehicle.setServiceIntervalNotifBoard(notifBody.getServiceIntervalNotifBoard());
            notificationVehicle.setServiceIntervalLengthBoard(notifBody.getServiceIntervalLengthBoard());
            notificationVehicle.setFuelDiffFound(false);

            //Populate data for gps
            notificationVehicle.setServiceIntervalNotifGps(notifBody.getServiceIntervalNotifGps());
            notificationVehicle.setServiceIntervalLengthGps(notifBody.getServiceIntervalLengthGps());
            notificationVehicle.setKilometers(optionalVehicle.get().getMillage());

//            if(geozoneSpeed.getGeozone()!=null){
//                Optional<Geozone> optionalGeozone = geozoneRepository.findById(geozoneSpeed.getGeozone().getGeozoneId());
//                if(!optionalGeozone.isPresent()){
//                    throw new Exception("Geozone in but not present!");
//                }else{
//                    notificationVehicle.setGeozone(optionalGeozone.get());
//                }
//            }
//            if(geozoneSpeed.getSpeed()!=0){
//                notificationVehicle.setSpeed(geozoneSpeed.getSpeed());
//            }


            //Save newlly created notification vehicle
            NotificationVehicle saved = notificationVehicleRepository.save(notificationVehicle);
            if (saved == null) {
                throw new Exception("Failed to save notification vehicle");
            }
            try {
                //Send to mongo server
                updateSingleNotif(saved);
            } catch (Exception e) {
                e.printStackTrace();

            }
            return new Response<>(saved);
        }
    }
    /**
     * azurira jednu notifikaciju
     */
    private void updateSingleNotif(NotificationVehicle notificationVehicle) {


        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setNotificationId(notificationVehicle.getNotification().getNotificationId());

        //Check device type
        if (notificationVehicle.getVehicle().getDeviceType() == 1) {
            notificationModel.setGs100(true);
        } else {
            notificationModel.setGs100(false);
        }


        //If geozone do some calculations
        if (notificationVehicle.getNotification().getNotificationId() == 1) {
            if (notificationVehicle.getGeozone() != null) {
                notificationModel.setGeozoneName(notificationVehicle.getGeozone().getName());
                //If shape is circle
                if (notificationVehicle.getGeozone().getType().equals("circ")) {
                    notificationModel.setRadius(notificationVehicle.getGeozone().getRadius());
                    notificationModel.setCenterLat(notificationVehicle.getGeozone().getLat());
                    notificationModel.setCenterLng(notificationVehicle.getGeozone().getLng());
                }
                //If its other shape
                else {
                    String geozoneString = notificationVehicle.getGeozone().getJson();
                    int x = geozoneString.indexOf("\"bounds\"");
                    int y = geozoneString.length() - 1;
                    String s2 = geozoneString.substring(x + 4, y - 1);

                    ObjectMapper mapper = new ObjectMapper();
                    try {
//                            ArrayList<LatLng> latLngList = mapper.readValue(s2, new TypeReference<List<LatLng>>() {
//                            });
//                            Polygon polygon = new Polygon();
//                            for (LatLng latLng : latLngList) {
//                                polygon.addPoint((int) (latLng.getLat() * 100000000), (int) (latLng.getLng() * 100000000));
//                            }
                        notificationModel.setPolygonJasonce(notificationVehicle.getGeozone().getJson());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //Set other data
        notificationModel.setImei(notificationVehicle.getVehicle().getImei());
        notificationModel.setNotificationName(notificationVehicle.getNotification().getName());
        notificationModel.setVehicleId(notificationVehicle.getVehicle().getVehicleId());
        notificationModel.setVehicleMileage(notificationVehicle.getVehicle().getMillage());
        notificationModel.setManufacturer(notificationVehicle.getVehicle().getManufacturer());
        notificationModel.setModel(notificationVehicle.getVehicle().getModel());
        notificationModel.setUserId(notificationVehicle.getUser() != null ? notificationVehicle.getUser().getUserId() : 0);
        notificationModel.setRegistration(notificationVehicle.getVehicle().getRegistration());
        notificationModel.setNmid(notificationVehicle.getNotificationVehicleId());
        notificationModel.setSms(notificationVehicle.isSms());
        notificationModel.setEmail(notificationVehicle.isMail());
        notificationModel.setPush(notificationVehicle.getPush());
        notificationModel.setSpeed(notificationVehicle.getSpeed());
        notificationModel.setFuelMargin(notificationVehicle.getFuelMargin());
        notificationModel.setEngine(notificationVehicle.getEngine());
        notificationModel.setFirmId(notificationVehicle.getFirmId());
        notificationModel.setServiceIntervalNotif(notificationVehicle.getServiceIntervalNotif());
        notificationModel.setServiceIntervalLength(notificationVehicle.getServiceIntervalLength());
        notificationModel.setVehicleRegistration(notificationVehicle.getVehicleRegistration());
        notificationModel.setNotifyDaysBeforeVehicleRegistration(notificationVehicle.getNotifyDaysBeforeVehicleRegistration());
        notificationModel.setEmptyingFuelMargine(notificationVehicle.getEmptyingFuelMargine());
        notificationModel.setServiceIntervalNotifBoard(notificationVehicle.getServiceIntervalNotifBoard());
        notificationModel.setServiceIntervalLengthBoard(notificationVehicle.getServiceIntervalLengthBoard());
        notificationModel.setFuelDiffFound(false);

        //Set data for gps
        notificationModel.setServiceIntervalNotifGps(notificationVehicle.getServiceIntervalNotifGps());
        notificationModel.setServiceIntervalLengthGps(notificationVehicle.getServiceIntervalLengthGps());
        notificationModel.setKilometers(notificationVehicle.getKilometers());



        String uri = "http://localhost:8080/api/notification/add/one";
        uri = "http://142.93.164.78:8080/api/notification/add/one";
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/notification/add/one";
        try {
            ResponseEntity<String> responseEntityStr = restTemplate.postForEntity(uri, notificationModel, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *
     * vraca sve notifikacije firme
     */
    public Response<Map<String, List<NotificationVehicle>>> getAll(User user, int firmId) throws Exception {
        Map<String, List<NotificationVehicle>> map = new HashMap<>();


//        List<GroupPrivilege> groupPrivileges = grouppr

//        if ((user.getSuperAdmin() != null && user.getSuperAdmin()) || (user.getAdmin() != null && user.getAdmin() == true)) {
            List<NotificationVehicle> list = notificationVehicleRepository.findByUserFirmFirmId(firmId);
            map.put("notifications", list);
            return new Response<>(map);
//        }
       // return new Response<>(map);

//
//        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
//            List<NotificationVehicle> list = notificationVehicleRepository.findAll();
//            Map<String, List<NotificationVehicle>> map = new HashMap<>();
//            map.put("notifications", list);
//            return new Response<>(map);
//        } else {
//            if (user.getFirm() == null) {
//                throw new Exception("Firm not set");
//            } else {
//                if (user.getAdmin() != null && user.getAdmin()) {
//                    List<NotificationVehicle> list = notificationVehicleRepository.findByUserUserId(user.getFirm().getFirmId());
//                    Map<String, List<NotificationVehicle>> map = new HashMap<>();
//                    map.put("notifications", list);
//                    return new Response<>(map);
//                } else {
//                    List<NotificationVehicle> list = notificationVehicleRepository.findByUserFirmFirmId(user.getUserId());
//                    Map<String, List<NotificationVehicle>> map = new HashMap<>();
//                    map.put("notifications", list);
//                    return new Response<>(map);
//                }
//            }
        //   }

    }

    /**
     * vraca sve licne notifikacije korisnika koji je poslao zahtev
     */
    public Response<Map<String, List<NotificationVehicle>>> getMine(User user) {
        List<NotificationVehicle> list = notificationVehicleRepository.findByUserUserId(user.getUserId());
        Map<String, List<NotificationVehicle>> map = new HashMap<>();
        map.put("notifications", list);
        return new Response<>(map);
    }
    /**
     * azurira notifikacije
     */
//    public List<NotificationModel> updateNotif() {
//        List<NotificationVehicle> notificationVehicleList = notificationVehicleRepository.findAll();
//        log.info("IZ REPOZITORIJUMA NOTIFIKACIJE: Found notification vehicles to update: {}", notificationVehicleList.size());
//        List<NotificationModel> notificationModelList = new ArrayList<>();
//        for (NotificationVehicle notificationVehicle : notificationVehicleList) {
//            NotificationModel notificationModel = new NotificationModel();
//            notificationModel.setNotificationId(notificationVehicle.getNotification().getNotificationId());
//            if (notificationVehicle.getEngine() == null) {
//                notificationModel.setEngine(0.0);
//            } else {
//                notificationModel.setEngine(notificationVehicle.getEngine());
//            }
//            if (notificationVehicle.getVehicle().getDeviceType() == 1) {
//                notificationModel.setGs100(true);
//            } else {
//                notificationModel.setGs100(false);
//            }
//
//            if (notificationVehicle.getNotification().getNotificationId() == 1) {
//
//                if (notificationVehicle.getGeozone() != null) {
//                    notificationModel.setGeozoneName(notificationVehicle.getGeozone().getName());
//                    if (notificationVehicle.getGeozone().getType().equals("circ")) {
//                        notificationModel.setRadius(notificationVehicle.getGeozone().getRadius());
//                        notificationModel.setCenterLat(notificationVehicle.getGeozone().getLat());
//                        notificationModel.setCenterLng(notificationVehicle.getGeozone().getLng());
//                    } else {
//                        String geozoneString = notificationVehicle.getGeozone().getJson();
//                        int x = geozoneString.indexOf("\"bounds\"");
//                        int y = geozoneString.length() - 1;
//                        String s2 = geozoneString.substring(x + 4, y - 1);
//
//                        ObjectMapper mapper = new ObjectMapper();
//                        try {
////                            ArrayList<LatLng> latLngList = mapper.readValue(s2, new TypeReference<List<LatLng>>() {
////                            });
////                            Polygon polygon = new Polygon();
////                            for (LatLng latLng : latLngList) {
////                                polygon.addPoint((int) (latLng.getLat() * 100000000), (int) (latLng.getLng() * 100000000));
////                            }
//                            notificationModel.setPolygonJasonce(notificationVehicle.getGeozone().getJson());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//
//            notificationModel.setImei(notificationVehicle.getVehicle().getImei());
//            notificationModel.setNotificationName(notificationVehicle.getNotification().getName());
//            notificationModel.setVehicleId(notificationVehicle.getVehicle().getVehicleId());
//            notificationModel.setManufacturer(notificationVehicle.getVehicle().getManufacturer());
//            notificationModel.setModel(notificationVehicle.getVehicle().getModel());
//            notificationModel.setUserId(notificationVehicle.getUser() != null ? notificationVehicle.getUser().getUserId() : 0);
//            notificationModel.setRegistration(notificationVehicle.getVehicle().getRegistration());
//            notificationModel.setNmid(notificationVehicle.getNotificationVehicleId());
//            notificationModel.setSpeed(notificationVehicle.getSpeed());
//            notificationModel.setSms(notificationVehicle.isSms());
//            notificationModel.setEmail(notificationVehicle.isMail());
//            notificationModel.setFuelMargin(notificationVehicle.getFuelMargin());
//            notificationModel.setStartTimestamp(notificationVehicle.getStartTimestamp());
//            notificationModel.setStartPointLat(notificationVehicle.getStartPointLat());
//            notificationModel.setStartPointLng(notificationVehicle.getStartPointLng());
//            notificationModel.setMileage(notificationVehicle.getIsMileage());
//            notificationModel.setServiceIntervalNotif(notificationVehicle.getServiceIntervalNotif()!=null? notificationVehicle.getServiceIntervalNotif() : 0);
//            notificationModel.setServiceIntervalLength(notificationVehicle.getServiceIntervalLength()!=null? notificationVehicle.getServiceIntervalLength() : 0);
//            notificationModel.setServiceIntervalNotifBoard(notificationVehicle.getServiceIntervalNotifBoard()!=null? notificationVehicle.getServiceIntervalNotifBoard() : 0);
//            notificationModel.setServiceIntervalLengthBoard(notificationVehicle.getServiceIntervalLengthBoard()!=null? notificationVehicle.getServiceIntervalLengthBoard() : 0);
//            notificationModel.setNmid(notificationVehicle.getNotificationVehicleId());
//            notificationModel.setVehicleRegistration(notificationVehicle.getVehicleRegistration());
//            notificationModel.setVehicleMileage(notificationVehicle.getVehicleMileage() !=null ? notificationVehicle.getVehicleMileage() : 0);
//            notificationModel.setNotifyDaysBeforeVehicleRegistration(notificationVehicle.getNotifyDaysBeforeVehicleRegistration()!=null? notificationVehicle.getNotifyDaysBeforeVehicleRegistration() : 0);
//            notificationModel.setServiceIntervalNotifGps(notificationVehicle.getServiceIntervalNotifGps()!=null? notificationVehicle.getServiceIntervalNotifGps() : 0);
//            notificationModel.setServiceIntervalLengthGps(notificationVehicle.getServiceIntervalLengthGps()!=null? notificationVehicle.getServiceIntervalLengthGps() : 0);
//            notificationModel.setKilometers(notificationVehicle.getKilometers()!=null? notificationVehicle.getKilometers() : 0);
//            notificationModelList.add(notificationModel);
//        }
//        log.info("AZURIRANE NOTIFIKACIJE: Updated notification vehicles: {}", notificationModelList.size());
//       return notificationModelList;
//    }


    public List<NotificationModel> updateNotif() {
        List<NotificationVehicle> notificationVehicleList = notificationVehicleRepository.findAll();
        log.info("IZ REPOZITORIJUMA NOTIFIKACIJE: Found notification vehicles to update: {}", notificationVehicleList.size());

        List<NotificationModel> notificationModelList = new ArrayList<>();

        for (NotificationVehicle notificationVehicle : notificationVehicleList) {
            try {
                // hard guard da endpoint nikad ne padne zbog jednog loseg reda
                if (notificationVehicle == null) continue;
                if (notificationVehicle.getNotification() == null || notificationVehicle.getVehicle() == null) {
                    log.warn("SKIP notificationVehicleId={} (notification or vehicle is null)",
                            notificationVehicle.getNotificationVehicleId());
                    continue;
                }

                NotificationModel notificationModel = new NotificationModel();

                // notificationId
                notificationModel.setNotificationId(notificationVehicle.getNotification().getNotificationId());

                // engine
                if (notificationVehicle.getEngine() == null) {
                    notificationModel.setEngine(0.0);
                } else {
                    notificationModel.setEngine(notificationVehicle.getEngine());
                }

                // gs100 (FIX za NPE: deviceType je Integer)
                Integer deviceType = notificationVehicle.getVehicle().getDeviceType();
                notificationModel.setGs100(deviceType != null && deviceType == 1);

                // geozone data samo za notifId=1
                if (notificationVehicle.getNotification().getNotificationId() == 1 && notificationVehicle.getGeozone() != null) {
                    notificationModel.setGeozoneName(notificationVehicle.getGeozone().getName());

                    if ("circ".equals(notificationVehicle.getGeozone().getType())) {
                        notificationModel.setRadius(notificationVehicle.getGeozone().getRadius());
                        notificationModel.setCenterLat(notificationVehicle.getGeozone().getLat());
                        notificationModel.setCenterLng(notificationVehicle.getGeozone().getLng());
                    } else {
                        // FIX: ne radi indexOf/substring koji lako puca; dovoljno je proslediti ceo json
                        String json = notificationVehicle.getGeozone().getJson();
                        if (json != null) {
                            notificationModel.setPolygonJasonce(json);
                        }
                    }
                }

                // ostala polja
                notificationModel.setImei(notificationVehicle.getVehicle().getImei());
                notificationModel.setNotificationName(notificationVehicle.getNotification().getName());
                notificationModel.setVehicleId(notificationVehicle.getVehicle().getVehicleId());
                notificationModel.setManufacturer(notificationVehicle.getVehicle().getManufacturer());
                notificationModel.setModel(notificationVehicle.getVehicle().getModel());
                notificationModel.setUserId(notificationVehicle.getUser() != null ? notificationVehicle.getUser().getUserId() : 0);
                notificationModel.setRegistration(notificationVehicle.getVehicle().getRegistration());
                notificationModel.setNmid(notificationVehicle.getNotificationVehicleId());
                notificationModel.setSpeed(notificationVehicle.getSpeed());
                notificationModel.setSms(notificationVehicle.isSms());
                notificationModel.setEmail(notificationVehicle.isMail());
                notificationModel.setPush(notificationVehicle.getPush() != null ? notificationVehicle.getPush() : false);
                notificationModel.setFuelMargin(notificationVehicle.getFuelMargin());
                notificationModel.setStartTimestamp(notificationVehicle.getStartTimestamp());
                notificationModel.setStartPointLat(notificationVehicle.getStartPointLat());
                notificationModel.setStartPointLng(notificationVehicle.getStartPointLng());
                notificationModel.setMileage(notificationVehicle.getIsMileage());

                notificationModel.setServiceIntervalNotif(notificationVehicle.getServiceIntervalNotif() != null ? notificationVehicle.getServiceIntervalNotif() : 0);
                notificationModel.setServiceIntervalLength(notificationVehicle.getServiceIntervalLength() != null ? notificationVehicle.getServiceIntervalLength() : 0);

                notificationModel.setServiceIntervalNotifBoard(notificationVehicle.getServiceIntervalNotifBoard() != null ? notificationVehicle.getServiceIntervalNotifBoard() : 0);
                notificationModel.setServiceIntervalLengthBoard(notificationVehicle.getServiceIntervalLengthBoard() != null ? notificationVehicle.getServiceIntervalLengthBoard() : 0);

                notificationModel.setVehicleRegistration(notificationVehicle.getVehicleRegistration());
                notificationModel.setVehicleMileage(notificationVehicle.getVehicleMileage() != null ? notificationVehicle.getVehicleMileage() : 0);

                notificationModel.setNotifyDaysBeforeVehicleRegistration(
                        notificationVehicle.getNotifyDaysBeforeVehicleRegistration() != null ? notificationVehicle.getNotifyDaysBeforeVehicleRegistration() : 0
                );

                notificationModel.setServiceIntervalNotifGps(notificationVehicle.getServiceIntervalNotifGps() != null ? notificationVehicle.getServiceIntervalNotifGps() : 0);
                notificationModel.setServiceIntervalLengthGps(notificationVehicle.getServiceIntervalLengthGps() != null ? notificationVehicle.getServiceIntervalLengthGps() : 0);
                notificationModel.setKilometers(notificationVehicle.getKilometers() != null ? notificationVehicle.getKilometers() : 0);

                notificationModelList.add(notificationModel);

            } catch (Exception e) {
                log.error("SKIP notificationVehicleId={} due to error",
                        notificationVehicle != null ? notificationVehicle.getNotificationVehicleId() : -1,
                        e);
            }
        }

        return notificationModelList;
    }

    /**
     * sve je poprilicno intuitivno, nema potrebe za komentarisanjem
     */

    /**
     *

     */
    public boolean perkela(LatLng latLngSent) {
        List<NotificationVehicle> notificationVehicleList = notificationVehicleRepository.findAll();
        for (NotificationVehicle notificationVehicle : notificationVehicleList) {
            if (notificationVehicle.getNotification().getNotificationId() == 1) {
                if (notificationVehicle.getGeozone() != null) {
                    if (notificationVehicle.getGeozone().getType().equals("circ")) {
                        double length = distance1((double) latLngSent.getLat(), (double) latLngSent.getLng(),
                                (double) notificationVehicle.getGeozone().getLat(), (double) notificationVehicle.getGeozone().getLng());

                        System.out.println("XXXX: " + length + " m");

                        System.out.println(length * 1000);
                        System.out.println(notificationVehicle.getGeozone().getRadius());
                        if ((length * 1000) < notificationVehicle.getGeozone().getRadius()) {
                            System.out.println("RESI BRE GLUPU GEOZONU");
                        }


                    } else {
                        System.out.println("EVO ME");
                        String geozoneString = notificationVehicle.getGeozone().getJson();
                        int x = geozoneString.indexOf("\"bounds\"");
                        int y = geozoneString.length() - 1;
                        String s2 = geozoneString.substring(x + 4, y - 1);

                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            ArrayList<LatLng> latLngList = mapper.readValue(s2, new TypeReference<List<LatLng>>() {
                            });
                            Polygon polygon = new Polygon();
                            for (LatLng latLng : latLngList) {
                                polygon.addPoint((int) (latLng.getLat() * 100000), (int) (latLng.getLng() * 100000));
                            }

                            if (polygon.contains((int) (latLngSent.getLat() * 100000), (int) (latLngSent.getLng() * 100000))) {
                                System.out.println("OLAA");
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }

                }
            }
        }
        return true;
    }
    /**
     *
     * @param deg Stepeni
     * @return Radijani
     */

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    /**
     *
     * @param lat1 geog duzina 1
     * @param lon1 geog sirina 1
     * @param lat2 geog duzina 2
     * @param lon2 geog sirina 2
     * @return udaljenost dve tacke
     */

    private double distance1(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = (double) Math.sin(deg2rad(lat1)) * (double) Math.sin(deg2rad(lat2)) + (double) Math.cos(deg2rad(lat1)) * (double) Math.cos(deg2rad(lat2)) * (double) Math.cos(deg2rad(theta));
        dist = (double) Math.acos(dist);
        dist = (double) rad2deg(dist);
        dist = (double) dist * 60.0 * 1.1515 * 1.609344;
        return (double) (dist);
    }

    public void updateNotificationVehicle(NotificationModel notificationModel) {
        Optional<NotificationVehicle> optionalNotificationVehicle = notificationVehicleRepository.findById(notificationModel.getNmid());
        if(!optionalNotificationVehicle.isPresent()) throw new RuntimeException("Notification vehicle not found");
        NotificationVehicle notificationVehicle = optionalNotificationVehicle.get();
        notificationVehicle.setVehicleMileage(notificationModel.getVehicleMileage());
        notificationVehicle.setStartPointLng(notificationModel.getStartPointLng());
        notificationVehicle.setStartPointLat(notificationModel.getStartPointLat());
        notificationVehicle.setStartTimestamp(notificationModel.getStartTimestamp());
        notificationVehicleRepository.save(notificationVehicle);
    }
}
