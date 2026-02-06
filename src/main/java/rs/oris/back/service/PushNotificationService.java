package rs.oris.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.oris.back.domain.User;
import rs.oris.back.domain.UserPushNotification;
import rs.oris.back.domain.firebase.FirebasePushNotificationService;
import rs.oris.back.repository.PushNotificationRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PushNotificationService {
    private final UserService userService;
    private final PushNotificationRepository pushNotificationRepository;
    private final FirebasePushNotificationService firebasePushNotificationService;

    //Cuvanje token za user-a
    public void saveToken(String token, int userId) throws Exception {
        User user = userService.findUserById(userId);
        UserPushNotification userPushNotification = new UserPushNotification(user,token);
        pushNotificationRepository.save(userPushNotification);

    }

    //Slanje push notifikacije

    public void sendPushNotification(int userId,String title,String content) throws Exception {
        User userById = userService.findUserById(userId);
        UserPushNotification pushNotification = pushNotificationRepository.findById(userId).orElseThrow(() -> new Exception("Notification entity not found"));

        firebasePushNotificationService.sendPushNotification(pushNotification.getToken(),title,content);


    }

    //Da li je token prisutan za user-a
    public boolean isTokenSet(int userId) {
        return pushNotificationRepository.findById(userId).isPresent();
    }
}
