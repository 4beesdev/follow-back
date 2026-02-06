package rs.oris.back.domain.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FirebasePushNotificationService {

    private final FirebaseMessaging fcm;

    public void sendPushNotification(String token, String title, String content) throws Exception {
        Message message = Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(content)
                                .build()
                )
                .setToken(token)
                .build();


        String response = fcm.send(message);
        }
}
