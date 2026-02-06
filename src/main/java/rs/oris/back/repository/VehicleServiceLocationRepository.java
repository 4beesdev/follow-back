package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.VehicleServiceLocation;

import java.util.List;

@Repository
public interface VehicleServiceLocationRepository extends JpaRepository<VehicleServiceLocation, Integer> {

    List<VehicleServiceLocation> findByVehicleVehicleId(int vehicleId);

    List<VehicleServiceLocation> findByServiceLocationServiceLocationId(int serviceLocationId);

    List<VehicleServiceLocation> findByVehicleFirmFirmId(int firmId);
}
