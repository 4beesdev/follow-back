package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.projection.ReportEngineProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    List<Vehicle> findVehiclesByVehicleIdIn(List<Integer> id);

    List<Vehicle> findByFirmFirmId(int firmId);

    Optional<Vehicle> findByImei(String imei);
    boolean existsByImeiAndDeviceType(String imei, int deviceType);


    List<Vehicle> findByFirmFirmIdAndDeletedDate(int firmId, Object o);

    List<Vehicle> findByFirmFirmIdAndDeletedDateNotNull(int firmId);

    Optional<Vehicle> findByDriverDriverId(int driverId);

    List<ReportEngineProjection> findByImeiIn(List<String> imeis);

    List<Vehicle> findVehiclesByImeiIn(String[] imeis);

    List<Vehicle> findVehiclesByImeiIn(List<String> imeis);


    Optional<Vehicle>  findByRegistration(String registration);
    @Query("SELECT v FROM Vehicle v WHERE LOWER(REPLACE(v.registration,' ','')) = LOWER(REPLACE(:registration,' ',''))")
    List<Vehicle> findByRegistrationIgnoreCaseAndTrim(@Param("registration") String registration);


    List<Vehicle> findAllByImeiIn(List<String> imeis);

}
