package rs.oris.back.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.User;
import rs.oris.back.domain.Vehicle;

import rs.oris.back.service.UserService;
import rs.oris.back.service.VehicleGroupService;
import rs.oris.back.service.VehicleVehicleGroupService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class VehicleVehicleGroupController {

    @Autowired
    private VehicleVehicleGroupService vehicleVehicleGroupService;
    @Autowired
    private UserService userService;
    /**
     * vraca sva vozila iz grupe
     */
    @GetMapping("/api/firm/{firm_id}/vehicle-group/{vehicle_group_id}/vehicle")
    public Response<Map<String, List<Vehicle>>> getAllVehicles(@RequestHeader("Authorization") String auth, @PathVariable("vehicle_group_id") int vehicleGroupId, @PathVariable("firm_id") int firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return vehicleVehicleGroupService.getAllVehiclesForGroup(user,vehicleGroupId, firmId);
    }
    /**
     *
     * dodaje vozilo u grupu
     */
    @PostMapping("api/firm/{firm_id}/assign/vehicle/{vehicle_id}/group/{group_id}")
    public boolean assignAVehicleToAGroup(@PathVariable("vehicle_id") int vehicleId, @PathVariable("group_id") int groupId,@RequestHeader("Authorization") String auth,@PathVariable("firm_id") int firmId) throws Exception{
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return vehicleVehicleGroupService.assign(groupId,vehicleId,user, firmId);
    }
    /**
     *
     * brise vozilo iz grupe //UKIDA VEZU
     */
    @DeleteMapping("api/firm/{firm_id}/assign/vehicle/{vehicle_id}/group/{group_id}")
    public boolean deleteAVehicleFromAGroup(@PathVariable("vehicle_id") int vehicleId, @PathVariable("group_id") int groupId,@RequestHeader("Authorization") String auth, @PathVariable("firm_id") int firmId) throws Exception{
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return vehicleVehicleGroupService.deleteVehicleFromGroup(groupId,vehicleId,user, firmId);
    }



}
