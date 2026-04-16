package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Route;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {

    List<Route> findByFirmFirmId(int firmId);

    @Query("SELECT DISTINCT r FROM Route r "
            + "LEFT JOIN FETCH r.vehicleRouteSet vr "
            + "LEFT JOIN FETCH vr.vehicle "
            + "WHERE r.firm.firmId = :firmId")
    List<Route> findAllByFirmIdWithVehicleRoutes(@Param("firmId") int firmId);
}
