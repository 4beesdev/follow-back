package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.AuthUtil;
import rs.oris.back.controller.wrapper.Response;

import rs.oris.back.domain.LatLng;
import rs.oris.back.domain.NotificationModel;
import rs.oris.back.domain.NotificationVehicle;
import rs.oris.back.domain.User;

import rs.oris.back.domain.dto.NotifBody;
import rs.oris.back.service.NotificationVehicleService;
import rs.oris.back.service.UserService;


import java.util.List;
import java.util.Map;

@RestController
public class NotificationVehicleController {

    @Autowired
    private NotificationVehicleService notificationVehicleService;
    @Autowired
    private UserService userService;

    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/notification-vehicle/{notification_vehicle_id}")
    public boolean deleteNotificationById(@PathVariable("notification_vehicle_id") int notificationVehicleId) {
        return notificationVehicleService.delete(notificationVehicleId);
    }

    /*

        private boolean isEmail; -> da lie se radi smanje na email
        private boolean isSms;  -> da lie se radi smanje na sms
        private boolean isPush;  -> da lie se radi smanje na push

        1 povreda geozone
        2 naglo kocenje(sigurna voznja)
        3 uplajeno vozilo
        4 ugaseno vozilo
        5 kontakt poslednje javljanje (2 h)
        6 prekoracenje brzine
        7 promena nivoa goriva
        8 servisni interval
        9 godisnji servis
        10 sensors activation
        11 servis iterval board
        12 servis interval gps
     */

    @PostMapping("/api/firm/{firm_id}/notification/{notification_id}/vehicle/{vehicle_id}/user/{user_id}")
    public Response<NotificationVehicle> getAllGroupsForFirm(
            @PathVariable("notification_id") int notificationId,
            @PathVariable("vehicle_id") int vehicleId,
            @PathVariable("user_id") int userId,
            @PathVariable("firm_id") int firmId,
            @RequestBody NotifBody notifBody) throws Exception {
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);

        //Dodaj notifikaciju
        return notificationVehicleService.add(user, notificationId, vehicleId, userId,notifBody,firmId);
    }
    /**
     *
     * vraca sve notifikacije firme
     */
    @GetMapping("/api/firm/{firm_id}/notification-vehicle")
    public Response<Map<String, List<NotificationVehicle>>> getAllFirm(@PathVariable("firm_id") int firmId) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return notificationVehicleService.getAll(user,firmId);
    }
    /**
     * vraca sve licne notifikacije korisnika koji je poslao zahtev
     */
    @GetMapping("/api/firm/{firm_id}/notification-vehicle/mine")
    public Response<Map<String, List<NotificationVehicle>>> getMyNotifications() throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User user = userService.findByUsername(username);
        return notificationVehicleService.getMine(user);
    }

    /**
     * perkela resava glupu geozonu
     *
     * ostalo je sve intuitivno
     */
    @PostMapping("/api/perkela")
    public boolean getMyNotifications(@RequestBody LatLng latLng) throws Exception{
        return notificationVehicleService.perkela(latLng);
    }

    @GetMapping("/api/notifications/all")
    public ResponseEntity<List<NotificationModel>> getAll(){
    
        return ResponseEntity.ok(notificationVehicleService.updateNotif());
    }

    //{"lat": 44.738433, "lng": 19.675418}
}
