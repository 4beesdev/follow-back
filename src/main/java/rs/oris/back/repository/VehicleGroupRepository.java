package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.VehicleGroup;

import java.util.List;

@Repository
public interface VehicleGroupRepository extends JpaRepository<VehicleGroup, Integer> {

    List<VehicleGroup> findByFirmFirmId(int firmId);
}
