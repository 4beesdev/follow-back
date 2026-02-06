package rs.oris.back.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.NotificationModel;
import rs.oris.back.domain.User;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.dto.ImeiDTO;
import rs.oris.back.config.MongoServerConfig;
import rs.oris.back.service.NotificationVehicleService;
import rs.oris.back.service.UserService;
import rs.oris.back.service.VehicleService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationVehicleService notificationVehicleService;
    @Autowired
    private MongoServerConfig mongoServerConfig;
    @Autowired
    private RestTemplate restTemplate;
    /**
     *
     * vraca sva vozila korisnika/firme
     */
    @Transactional
    @GetMapping("/api/firm/{firm_id}/vehicle")
    public Response<Map<String, List<Vehicle>>> getAllVehicles(@RequestHeader("Authorization") String auth, @PathVariable("firm_id") int firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return vehicleService.getAllVehicles(user, firmId);
    }
    /**
     *
     * Vraca sva neobrisana (aktivna) vozila
     */
    @Transactional
    @GetMapping("/api/firm/{firm_id}/vehicle/inactive")
    public Response<Map<String, List<Vehicle>>> getAllVehiclesInactive(@RequestHeader("Authorization") String auth, @PathVariable("firm_id") int firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return vehicleService.getAllVehiclesInactive(user, firmId);
    }

    /**
     *
     * kreira novo vozilo
     */
    @PostMapping("/api/firm/{firm_id}/vehicle")
    public Response<Vehicle> addVehicle(@RequestHeader("Authorization") String auth, @RequestBody Vehicle vehicle, @PathVariable("firm_id") int firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return vehicleService.addVehicle(user, vehicle, firmId);
    }

//    @GetMapping("/api/firm/{firm_id}/group/{group_id}/vehicle")
//    public Response<Map<String, List<Vehicle>>> getAllVehiclesForAGroup(@RequestHeader("Authorization") String auth, @PathVariable("group_id") int groupId, @PathVariable("firm_id") int firmId) throws Exception {
//        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
//        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
//        System.out.println(Arrays.toString(byteArray));
//        String decodedJson = new String(byteArray);
//        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
//        User user = userService.findByUsername(username);
//        return vehicleService.getAllVehiclesForAGroup(user,groupId, firmId);
//    }
    /**
     *
     * brisanje vozila
     */
    @DeleteMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}")
    public boolean deleteVehicle(@RequestHeader("Authorization") String auth, @PathVariable("vehicle_id") int vehicleId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return vehicleService.deleteAVehicle(user, vehicleId);
    }

    /**
     *
     * update vozila
     */
    @PutMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}")
    public Response<Vehicle> updateVehicle(@RequestHeader("Authorization") String auth, @PathVariable("vehicle_id") int vehicleId, @RequestBody Vehicle vehicle, @PathVariable("firm_id") int firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return vehicleService.updateAVehicle(user, vehicleId, vehicle, firmId);
    }
    /**
     *
     * postavlja obrisano vozilo na ne-obrisano stanje, tj
     * UNDO DELETE
     */
    @PatchMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/activate")
    public Response<Vehicle> unInActive(@RequestHeader("Authorization") String auth, @PathVariable("vehicle_id") int vehicleId, @PathVariable("firm_id") int firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return vehicleService.unInActive(user, vehicleId, firmId);
    }
    /**
     *
     * postavlja obrisano vozilo na ne-obrisano stanje, tj
     * UNDO DELETE
     */
    @PutMapping("/api/vehicle/{vehicle_id}/update-mileage")
    @Transactional
    public String updateMileage( @PathVariable("vehicle_id") int vehicleId,@RequestBody NotificationModel notificationModel) throws Exception {

        System.out.println("Radim update "+notificationModel);
         vehicleService.updateMileage( vehicleId, notificationModel);
         notificationVehicleService.updateNotificationVehicle(notificationModel);
         return "te";
    }
    /**
     *
     * promena prirodnog kljuca vozila
     */
    @PatchMapping("/api/change-imei")
    public boolean changeVehicleImei(@RequestHeader("Authorization") String auth, @RequestBody ImeiDTO imeiDTO) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);

        User user = userService.findByUsername(username);

        if(user.getSuperAdmin()==null || user.getSuperAdmin()==false){
            throw new Exception("Only superadmin can use this!");
        }
        //TODO: POSLATI NA MONGO!
        String oldImei = imeiDTO.getOldImei().trim();
        String newImei = imeiDTO.getNewImei().trim();
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/update/imei/" + oldImei + "/to/" + newImei;

        try {
            // Make the GET request to the new endpoint
            String result = restTemplate.getForObject(uri, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }


    }

}
