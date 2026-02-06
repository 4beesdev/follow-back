package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Registration;

import java.util.Date;
import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

    List<Registration> findByVehicleVehicleId(int vehicleId);

    List<Registration> findByVehicleFirmFirmId(int firmId);

    List<Registration> findByVehicleVehicleIdAndRegistrationDateBetween(int vehicleId, Date dateFrom, Date dateTo);

    void deleteByVehicleVehicleId(int vehicleId);

    List<Registration> findByVehicleVehicleIdInAndRegistrationDateBetween(List<Integer> vehicleIds, Date dateFrom, Date dateTo);
}
