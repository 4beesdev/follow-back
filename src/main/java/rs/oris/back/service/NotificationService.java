package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Notification;
import rs.oris.back.repository.NotificationRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    /**
     * Vraca listu svih notifikacija
     */
    public Response<Map<String, List<Notification>>> getAll() {
            List<Notification> list = notificationRepository.findAll();
            Map<String, List<Notification>> map = new HashMap<>();
            map.put("notifications", list);
            return new Response<>(map);
    }
}
