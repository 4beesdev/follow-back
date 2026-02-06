package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.VehicleVehicleGroup;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleVehicleGroupRepository extends JpaRepository<VehicleVehicleGroup, Integer> {

    List<VehicleVehicleGroup> findByVehicleGroupVehicleGroupId(int vehicleGroupId);

    void deleteByVehicleVehicleIdAndVehicleGroupVehicleGroupId(int vehicleId, int groupId);

    Optional<VehicleVehicleGroup> findByVehicleVehicleIdAndVehicleGroupVehicleGroupId(int vehicleId, int vehicleGroupId);

    void deleteByVehicleVehicleId(int vehicleId);
}
