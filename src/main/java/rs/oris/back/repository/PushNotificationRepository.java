package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.User;
import rs.oris.back.domain.UserPushNotification;

@Repository
public interface PushNotificationRepository extends JpaRepository<UserPushNotification, Integer> {

    void deleteByUserUserId(int userId);
}
