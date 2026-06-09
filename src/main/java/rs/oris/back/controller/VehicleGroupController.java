package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.AuthUtil;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.User;

import rs.oris.back.domain.VehicleGroup;
import rs.oris.back.service.UserService;
import rs.oris.back.service.VehicleGroupService;

import java.util.List;
import java.util.Map;

@RestController
public class VehicleGroupController {

    @Autowired
    private VehicleGroupService vehicleGroupService;
    @Autowired
    private UserService userService;
    /**
     * vraca sve grupe vozila u firmi
     * @param firmId firma
     */
    @GetMapping("/api/firm/{firm_id}/vehicle-group")
    public Response<Map<String, List<VehicleGroup>>> getAllVehicles(@PathVariable("firm_id") int firmId) throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleGroupService.getAllVehicleGroups(user, firmId);
    }
    /**
     *
     * cuva novu grupu vozila u prosledjenoj firmi
     */
    @PostMapping("/api/firm/{firm_id}/vehicle-group")
    public Response<VehicleGroup > addVehicle(@RequestBody VehicleGroup vehicleGroup, @PathVariable("firm_id") int firmId) throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleGroupService.addVehicleGroup(user, vehicleGroup, firmId);
    }

    @DeleteMapping("/api/firm/{firm_id}/vehicle-group/{vehicle_group_id}")
    public Response<VehicleGroup> deleteVehicle(@PathVariable("vehicle_group_id") int vehicleGroupId,@PathVariable("firm_id") Long firmId) throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return vehicleGroupService.deleteVehicleGroup(user, vehicleGroupId,firmId);
    }

    /**
     * menja ime postojece grupe vozila
     */
    @PutMapping("/api/firm/{firm_id}/vehicle-group/{vehicle_group_id}")
    public Response<VehicleGroup> updateVehicle(@RequestHeader("Authorization") String auth, @RequestBody VehicleGroup vehicleGroup, @PathVariable("vehicle_group_id") int vehicleGroupId, @PathVariable("firm_id") Long firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return vehicleGroupService.updateVehicleGroup(user, vehicleGroupId, vehicleGroup, firmId);
    }


}
