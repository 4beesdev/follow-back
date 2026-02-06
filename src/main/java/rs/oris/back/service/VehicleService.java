package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import rs.oris.back.config.MongoServerConfig;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.repository.*;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleVehicleGroupRepository vehicleVehicleGroupRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private AddedInsuranceRepository addedInsuranceRepository;
    @Autowired
    private VehicleGeozoneRepository vehicleGeozoneRepository;
    @Autowired
    private VehicleRouteRepository vehicleRouteRepository;

    @Autowired
    private PNRepository pnRepository;
    @Autowired
    private InterventionRepository interventionRepository;
    @Autowired
    private FuelConsumptionRepository fuelConsumptionRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private TechnicalInspectionRepository technicalInspectionRepository;
    @Autowired
    private MillageRepository millageRepository;
    @Autowired
    private InvoicedTransportRepository invoicedTransportRepository;


    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationVehicleRepository notificationVehicleRepository;
    @Autowired
    private MongoServerConfig mongoServerConfig;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Vraca sva neobrisana (aktivna) vozila
     *
     * @param user korisnik koji zahteva
     * @param firmId firma korisnika
     * @return mapu aktivnih vozila
     * @throws Exception firma ne postoji/ los token
     */
    public Response<Map<String, List<Vehicle>>> getAllVehiclesInactive(User user, int firmId) throws Exception {
        if (user == null)
            throw new Exception("Bad token");
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            Optional<Firm> optionalFirm = firmRepository.findById(firmId);
            if (!optionalFirm.isPresent()) {
                throw new Exception("Invalid firm id");
            }
            List<Vehicle> list = vehicleRepository.findByFirmFirmIdAndDeletedDateNotNull(optionalFirm.get().getFirmId());


            Map<String, List<Vehicle>> map = new HashMap<>();
            map.put("vehicles", list);
            return new Response<>(map);
        } else {
            if (user.getFirm() == null)
                throw new Exception("Firm not set!");
            List<Vehicle> list = vehicleRepository.findByFirmFirmIdAndDeletedDateNotNull(user.getFirm().getFirmId());

            Map<String, List<Vehicle>> map = new HashMap<>();
            map.put("vehicles", list);
            return new Response<>(map);
        }
    }

    /**
     * Vraca sva vozila firme korisnika koji zahteva
     *
     * @param user korisnik koji zahteva
     * @param firmId firma u pitanju
     * @return HashMapa sa vozilima firme
     * @throws Exception
     */
    public Response<Map<String, List<Vehicle>>> getAllVehicles(User user, int firmId) throws Exception {
        if (user == null)
            throw new Exception("Bad token");
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            Optional<Firm> optionalFirm = firmRepository.findById(firmId);
            if (!optionalFirm.isPresent()) {
                throw new Exception("Invalid firm id");
            }
            List<Vehicle> list = vehicleRepository.findByFirmFirmIdAndDeletedDate(optionalFirm.get().getFirmId(), null);

            Map<String, List<Vehicle>> map = new HashMap<>();
            map.put("vehicles", list);
            return new Response<>(map);
        } else {
            if (user.getFirm() == null)
                throw new Exception("Firm not set!");
            List<Vehicle> list = new ArrayList<>();
            if (user.getAdmin() != null && user.getAdmin() == true) {
                list = vehicleRepository.findByFirmFirmIdAndDeletedDate(user.getFirm().getFirmId(), null);
            } else {
                Set<Vehicle> setce = new HashSet<>();
                for (UserVehicleGroup userVehicleGroup : user.getUserVehicleGroupSet()) {
                    for (VehicleVehicleGroup v : userVehicleGroup.getVehicleGroup().getVehicleVehicleGroupSet()) {
                        Vehicle vehicle = v.getVehicle();


                        setce.add(vehicle);
                    }
                }

                list.addAll(setce);


            }
            DecimalFormat decimalFormat = new DecimalFormat("#.00");


            for (Vehicle vehicle : list) {
                vehicle.setMillage(Double.parseDouble(decimalFormat.format(vehicle.getMillage())));
                System.out.println(vehicle.getMillage());
            }
            Map<String, List<Vehicle>> map = new HashMap<>();
            map.put("vehicles", list);
            return new Response<>(map);
        }
    }
    /**
     * Kreira novo vozilo ako je korisnik admin
     *
     * @return
     * @throws Exception ako ime vozila vec postoji / ne moze da se sacuva / firma ne postoji
     */
    public Response<Vehicle> addVehicle(User user, Vehicle vehicle, int firmId) throws Exception {
        if (user == null)
            throw new Exception("Bad token");
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            Optional<Firm> optionalFirm = firmRepository.findById(firmId);
            if (!optionalFirm.isPresent()) {
                throw new Exception("Invalid firm id");
            }
            vehicle.setFirm(optionalFirm.get());

            if (vehicle.getImei() != null && vehicle.getImei().length() > 2) {
                Vehicle v = findByImeiInternal(vehicle.getImei());
                if (v != null)
                    throw new Exception("Vehicle with imei already exists! imei: " + v.getImei());
            }

            Vehicle saved = vehicleRepository.save(vehicle);
            if (saved == null) {
                throw new Exception("Failed to set vehicle");
            }
            return new Response<>(saved);
        } else {
            if (user.getFirm() == null)
                throw new Exception("Firm not set!");
            vehicle.setFirm(user.getFirm());

            if (vehicle.getImei() != null && vehicle.getImei().length() > 2) {
                Vehicle v = findByImeiInternal(vehicle.getImei());
                if (v != null)
                    throw new Exception("Vehicle with imei already exists! imei: " + v.getImei());
            }


            Vehicle saved = vehicleRepository.save(vehicle);
            if (saved == null) {
                throw new Exception("Failed to set vehicle");
            }
            return new Response<>(saved);
        }
    }

//    public Response<Map<String, List<Vehicle>>> getAllVehiclesForAGroup(User user, int groupId, int firmId) {
//        return null;
//    }
    /**
     * Brise voilo (POSTAVLJA DATUM I VREME NA POLJE DELETED_DATE
     * ako je korisnik admin i ako je firma odgovarajuca
     *
     * @param user korisnik
     * @param vehicleId id vozila
     * @return true ako je uspesno "brisanje"
     * @throws Exception ako nije uspesno brisanje
     */
    @Transactional
    public boolean deleteAVehicle(User user, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        Vehicle vehicle = optionalVehicle.get();
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }
        if (user.getSuperAdmin()) {
//            vehicleVehicleGroupRepository.deleteByVehicleVehicleId(vehicleId);
            addedInsuranceRepository.deleteByVehicleVehicleId(vehicleId);
            vehicleGeozoneRepository.deleteByVehicleVehicleId(vehicleId);
            vehicle.setDeletedDate(new Timestamp(new Date().getTime()));
//            vehicleRouteRepository.deleteByVehicleVehicleId(vehicleId);
//            pnRepository.deleteByVehicleVehicleId(vehicleId);
//            interventionRepository.deleteByVehicleVehicleId(vehicleId);
//            fuelConsumptionRepository.deleteByVehicleVehicleId(vehicleId);
//            ticketRepository.deleteByVehicleVehicleId(vehicleId);
//            registrationRepository.deleteByVehicleVehicleId(vehicleId);
//            technicalInspectionRepository.deleteByVehicleVehicleId(vehicleId);
//            millageRepository.deleteByVehicleVehicleId(vehicleId);
//            invoicedTransportRepository.deleteByVehicleVehicleId(vehicleId);
//            vehicleRepository.deleteById(vehicleId);
            vehicleRepository.save(vehicle);

            return true;
        } else {
            if (user.getAdmin() && user.getFirm().getFirmId() == optionalVehicle.get().getFirm().getFirmId()) {
//            vehicleVehicleGroupRepository.deleteByVehicleVehicleId(vehicleId);
                addedInsuranceRepository.deleteByVehicleVehicleId(vehicleId);
                vehicleGeozoneRepository.deleteByVehicleVehicleId(vehicleId);
                vehicle.setDeletedDate(new Timestamp(new Date().getTime()));
//            vehicleRouteRepository.deleteByVehicleVehicleId(vehicleId);
//            pnRepository.deleteByVehicleVehicleId(vehicleId);
//            interventionRepository.deleteByVehicleVehicleId(vehicleId);
//            fuelConsumptionRepository.deleteByVehicleVehicleId(vehicleId);
//            ticketRepository.deleteByVehicleVehicleId(vehicleId);
//            registrationRepository.deleteByVehicleVehicleId(vehicleId);
//            technicalInspectionRepository.deleteByVehicleVehicleId(vehicleId);
//            millageRepository.deleteByVehicleVehicleId(vehicleId);
//            invoicedTransportRepository.deleteByVehicleVehicleId(vehicleId);
//            vehicleRepository.deleteById(vehicleId);
                vehicleRepository.save(vehicle);

                return true;
            } else {
                throw new Exception("User is not admin or the firms do not match.");
            }
        }
    }
    /**
     * Vraca vozilo po IMEI broju - sto predstavlja prirodni kljuc
     * @param imei
     * @return vozilo, ako postooji
     * @throws Exception ako vozilo ne postoji
     */
    public Vehicle findByImei(String imei) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByImei(imei);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Vehicle not found!");
        }
        return optionalVehicle.get();
    }

    /**
     * Ista funkcija kao ova iznad samo vraca null umesto exception-a ako vozilo ne postoji
     */    public Vehicle findByImeiInternal(String imei) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByImei(imei);
        if (!optionalVehicle.isPresent())
            return null;
        return optionalVehicle.get();
    }
    /**
     * Menja podatke o vozilu
     *
     * @param user korisnik
     * @param vehicleId id vozila
     * @param firmId firma
     * @return izmenjeno vozilo ako je uspesno
     * @throws Exception
     */
    public Response<Vehicle> updateAVehicle(User user, int vehicleId, Vehicle vehicle, int firmId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }


        if (vehicle.getImei() != null) {
            if (!vehicle.getImei().equals("/")) {
                Vehicle v = findByImeiInternal(vehicle.getImei());
                if (v != null) {
                    if (v.getVehicleId() != vehicleId) {
                        throw new Exception("Vehicle with that imei already exists!");
                    }
                }
            }
        }

        Vehicle old = optionalVehicle.get();
        vehicle.setFirm(old.getFirm());
        vehicle.setVehicleId(old.getVehicleId());
        vehicle.setVehicleVehicleGroupSet(old.getVehicleVehicleGroupSet());

        if(vehicle.getMillage()!=old.getMillage()){
            old.setMillage(vehicle.getMillage());
            Optional<Notification> optionalNotification = notificationRepository.findById(12);
            if(optionalNotification.isPresent()){
                Optional<NotificationVehicle> optionalNotificationVehicle = notificationVehicleRepository.findByNotificationAndVehicleAndFirmId(optionalNotification.get(), old, firmId);
                optionalNotificationVehicle.ifPresent(x->{
                    x.setKilometers(vehicle.getMillage());
                    notificationVehicleRepository.save(x);
                });
            }
            sendToMongoForUpdateNotif(old.getVehicleId(), vehicle.getMillage());
        }
        Vehicle saved = vehicleRepository.save(vehicle);
        if (saved == null) {
            throw new Exception("Failed to update vehicle!");
        }
        return new Response<>(saved);
    }


    //Salje update na mongo servis
    private void sendToMongoForUpdateNotif(int vehicleId, double millage) {
        String base = mongoServerConfig.getMongoBaseUrl() + "/api/notification/update";
        String uri = UriComponentsBuilder.fromHttpUrl(base)
                .queryParam("vehicle_id", vehicleId)
                .queryParam("mileage", millage)
                .toUriString();
        try {
           restTemplate.put(uri, null, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Postavlja kolonu deleted date vozila na null,
     * bukv UNDO-DELETE
     *
     *
     *
     * @param user
     * @param vehicleId
     * @param firmId
     * @return
     * @throws Exception
     */
    public Response<Vehicle> unInActive(User user, int vehicleId, int firmId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }

        if (user.getSuperAdmin() || (user.getAdmin() && user.getFirm().getFirmId() == optionalVehicle.get().getVehicleId())) {
            Vehicle v = optionalVehicle.get();
            v.setDeletedDate(null);
            Vehicle veh = vehicleRepository.save(v);
            return new Response<>(veh);
        } else {
            throw new Exception("User is not admin or the firms do not match.");
        }
    }

    public void updateMileage(int vehicleId, NotificationModel notificationModel) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new RuntimeException("Vehicle not found");
        }
        Vehicle v = optionalVehicle.get();
        v.setMillage(notificationModel.getKilometers());
        Vehicle saved = vehicleRepository.save(v);
    }

    public List<Vehicle> findAllByImeiIn(List<String> imeis) {
        return vehicleRepository.findAllByImeiIn(imeis);
    }
}
