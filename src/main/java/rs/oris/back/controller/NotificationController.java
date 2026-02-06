package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Notification;
import rs.oris.back.service.NotificationService;

import java.util.List;
import java.util.Map;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Vraca listu svih notifikacija
     */
    //Ovo je util metoda gde vraca sve tipove notifikacija za prikazivanje u opadajucu listu za front
    @GetMapping("/api/firm/{firm_id}/notification")
    public Response<Map<String, List<Notification>>> getAllFirm() throws Exception{
        return notificationService.getAll();
    }

}
