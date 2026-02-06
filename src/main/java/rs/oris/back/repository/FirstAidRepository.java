package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.FirstAid;

import java.util.List;

@Repository
public interface FirstAidRepository extends JpaRepository<FirstAid, Integer> {

    List<FirstAid> findByVehicleFirmFirmId(int firmId);

    List<FirstAid> findByVehicleVehicleId(int vehicleId);
}
