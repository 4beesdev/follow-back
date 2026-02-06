package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.ServiceLocation;

import java.util.List;

@Repository
public interface ServiceLocationRepository extends JpaRepository<ServiceLocation, Integer> {


    List<ServiceLocation> findByFirmFirmId(int firmId);
}
