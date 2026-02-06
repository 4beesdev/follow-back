package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.PN;

import java.util.Date;
import java.util.List;

@Repository
public interface PNRepository extends JpaRepository<PN, Integer> {

    List<PN> findByDriverDriverId(int driverId);

    List<PN> findByVehicleVehicleId(int vehicleId);

    List<PN> findByVehicleFirmFirmId(int firmid);

    List<PN> findByDriverDriverIdAndDateBetween(int driverId, Date dateFrom, Date dateTo);

    void deleteByVehicleVehicleId(int vehicleId);
}
