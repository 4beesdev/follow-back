package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Millage;

import java.util.List;

@Repository
public interface MillageRepository extends JpaRepository<Millage, Integer> {


    List<Millage> findByVehicleVehicleId(int vehicleId);

    void deleteByVehicleVehicleId(int vehicleId);
}
