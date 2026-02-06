package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Geozone;

import java.util.List;

@Repository
public interface GeozoneRepository extends JpaRepository<Geozone, Integer> {

    List<Geozone> findByFirmFirmId(int firmId);
}
