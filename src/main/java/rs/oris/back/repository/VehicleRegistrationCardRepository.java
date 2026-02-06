package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.VehicleRegistrationCard;

import java.util.List;

@Repository
public interface VehicleRegistrationCardRepository extends JpaRepository<VehicleRegistrationCard, Integer> {

    List<VehicleRegistrationCard> findByVehicleVehicleId(int vehicleId);
}
