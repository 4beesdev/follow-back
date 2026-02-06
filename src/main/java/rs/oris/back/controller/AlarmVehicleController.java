package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.AuthUtil;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.AlarmBE;
import rs.oris.back.domain.AlarmVehicle;
import rs.oris.back.domain.User;
import rs.oris.back.service.AlarmVehicleService;
import rs.oris.back.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
public class AlarmVehicleController {

    @Autowired
    private AlarmVehicleService alarmVehicleService;
    @Autowired
    private UserService userService;

    /**
     *
     * brise alarm
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/alarm-vehicle/{alarm_vehicle_id}")
    public boolean deleteAlarmById(@PathVariable("alarm_vehicle_id") int alarmVehicleId) {
        return alarmVehicleService.delete(alarmVehicleId);
    }
    /**
     * dodeljuje alarm postojecem vozilu
     */
    @Transactional
    @PostMapping("/api/firm/{firm_id}/alarm/{alarm_id}/vehicle/{vehicle_id}/user/{user_id}")
    public Response<AlarmVehicle> getAllGroupsForFirm(@PathVariable("alarm_id") int alarmId, @PathVariable("vehicle_id") int vehicleId, @PathVariable("user_id") int userId,
                                                      @RequestBody AlarmBE alarmBE) throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);

        return alarmVehicleService.add(user, alarmId, vehicleId, userId, alarmBE);
    }

    /**
     * vraca sve alarme u vozilima jedne firme / korisnika
     */
    @GetMapping("/api/firm/{firm_id}/alarm-vehicle")
    public Response<Map<String, List<AlarmVehicle>>> getAllFirm() throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return alarmVehicleService.getAll(user);
    }

    /**
     * vraca sve alarme korisnika
     */
    @GetMapping("/api/firm/{firm_id}/alarm-vehicle/mine")
    public Response<Map<String, List<AlarmVehicle>>> getMyAlarms() throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return alarmVehicleService.getMine(user);
    }
    /**
     * vraca listu svih alarmBE iz baze
     */
    @GetMapping("/api/alarm/be")
    public List<AlarmBE> getAlarmsForBE() throws Exception {
        return alarmVehicleService.getForBe();
    }
}
