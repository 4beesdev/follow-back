package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.AddedInsurance;

import java.util.List;

@Repository
public interface AddedInsuranceRepository extends JpaRepository<AddedInsurance, Integer> {

    List<AddedInsurance> findAllByVehicleVehicleId(int vehicleId);

    void deleteByVehicleVehicleId(int vehicleId);
}
