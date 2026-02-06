package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.GeozoneGroup;
import rs.oris.back.domain.VehicleGeozone;
import rs.oris.back.domain.VehicleVehicleGroup;

import java.util.List;

@Repository
public interface VehicleGeozoneRepository extends JpaRepository<VehicleGeozone, Integer> {

    void deleteByGeozoneGeozoneId(int geozoneId);

    void deleteByVehicleVehicleId(int vehicleId);

    void deleteByGeozoneGeozoneIdAndVehicleVehicleId(int geozoneId, int vehicleId);

    List<VehicleGeozone> findByGeozoneGeozoneId(int geozoneId);

    List<VehicleGeozone> findByVehicleVehicleId(int vehicleId);



}
