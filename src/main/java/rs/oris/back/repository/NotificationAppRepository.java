package rs.oris.back.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.domain.NotificationApp;

import java.util.List;


@Repository
public interface NotificationAppRepository extends JpaRepository<NotificationApp, Integer> {

    List<NotificationApp> findByUserIdAndSeen(int userId, boolean b);

    List<NotificationApp> findByUserId(int userId);

    List<NotificationApp> findByUserIdAndSeenOrderByTimestampDesc(int userId, boolean b);

    List<NotificationApp> findByUserIdOrderByTimestampDesc(int userId);
    List<NotificationApp> findByUserIdOrderByTimestampDesc(int userId, Pageable page);

    @Transactional
    @Modifying
    @Query("UPDATE NotificationApp na SET na.seen = true WHERE na.notificationAppId in ?1") //WHERE na.user.id = ?1
     void updateAllNotifsFromUser(List<Integer> niz);

    List<NotificationApp> findTop50ByUserIdAndSeenOrderByTimestampDesc(int userId, boolean b);
    Long countAllByUserId(int userId);

    void deleteByUserId(int userId);
}
