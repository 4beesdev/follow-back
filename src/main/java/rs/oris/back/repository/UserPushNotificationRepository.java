package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.User;
import rs.oris.back.domain.UserPushNotification;

import java.util.List;

@Repository
public interface UserPushNotificationRepository extends JpaRepository<UserPushNotification, Long>
{

    List<UserPushNotification> findAllByUser(User usre);
}
