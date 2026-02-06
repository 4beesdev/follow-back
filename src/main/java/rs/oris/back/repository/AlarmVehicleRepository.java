package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.AlarmVehicle;

import java.util.List;

@Repository
public interface AlarmVehicleRepository extends JpaRepository<AlarmVehicle, Integer> {

    List<AlarmVehicle> findByUserFirmFirmId(int firmId);

    List<AlarmVehicle> findByUserUserId(int userId);
}
