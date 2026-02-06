package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.VehicleRoute;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRouteRepository extends JpaRepository<VehicleRoute, Integer> {


    void deleteByRouteRouteId(int routeId);

    void deleteByVehicleVehicleId(int vehicleId);

    void deleteByRouteRouteIdAndVehicleVehicleId(int routeId, int vehicleId);

    List<VehicleRoute> findByRouteRouteId(int routeId);

    List<VehicleRoute> findByVehicleVehicleId(int vehicleId);
}
