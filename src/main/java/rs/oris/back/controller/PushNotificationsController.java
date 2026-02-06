package rs.oris.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.dto.TokenDTO;
import rs.oris.back.service.PushNotificationService;

import java.net.URLDecoder;

@RequestMapping("/api/push-notifications")
@RestController
@RequiredArgsConstructor
public class PushNotificationsController {

    private final PushNotificationService pushNotificationService;

// da li je push notification token setovan  za user-a
    @GetMapping("/is-token-set/{userId}")
    public boolean isTokenSet(
            @PathVariable int userId
    ) {
        return pushNotificationService.isTokenSet(userId);
    }


    //Setovanje tokena za push notifikacije
    @PostMapping("/set-token/{userId}")
    public void setToken(
            @PathVariable int userId,
            @RequestBody String token
    ) throws Exception {

        String decodedToken = URLDecoder.decode(token, "UTF-8");
        pushNotificationService.saveToken(decodedToken, userId);
    }

    //slanje notifikacije za user-a
    @PostMapping("/send-notification/{userId}")
    public void testSendNotificationViaPostman(
            @PathVariable Integer userId
    ) throws Exception {
        pushNotificationService.sendPushNotification(userId,"Test","Test");
    }
}
