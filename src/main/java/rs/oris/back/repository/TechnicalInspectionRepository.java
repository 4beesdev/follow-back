package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.TechnicalInspection;

import java.util.List;

@Repository
public interface TechnicalInspectionRepository extends JpaRepository<TechnicalInspection, Integer> {

    List<TechnicalInspection> findByVehicleVehicleId(int vehicleId);

    List<TechnicalInspection> findByVehicleFirmFirmId(int firmId);

    void deleteByVehicleVehicleId(int vehicleId);
}
