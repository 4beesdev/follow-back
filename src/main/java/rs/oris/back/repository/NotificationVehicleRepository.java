package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Notification;
import rs.oris.back.domain.NotificationVehicle;
import rs.oris.back.domain.Vehicle;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationVehicleRepository extends JpaRepository<NotificationVehicle, Integer> {

    List<NotificationVehicle> findByUserFirmFirmId(int firmId);

    List<NotificationVehicle> findByUserUserId(int userId);

    Optional<NotificationVehicle> findByNotificationAndVehicleAndFirmId(Notification notification, Vehicle vehicle, int firmId);

    void deleteByUserUserId(int userId);
}
