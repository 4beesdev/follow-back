package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.PPAparat;

import java.util.List;

@Repository
public interface PPAparatRepository extends JpaRepository<PPAparat, Integer> {

    List<PPAparat> findByVehicleVehicleId(int vehicleId);
}
