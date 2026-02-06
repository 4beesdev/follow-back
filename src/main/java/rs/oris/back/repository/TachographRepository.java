package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Tachograph;

import java.util.List;
import java.util.Optional;

@Repository
public interface TachographRepository extends JpaRepository<Tachograph, Integer> {

    Optional<Tachograph> findByTachographId(int tachographId);

    List<Tachograph> findByVehicleVehicleId(int vehicleId);
}
