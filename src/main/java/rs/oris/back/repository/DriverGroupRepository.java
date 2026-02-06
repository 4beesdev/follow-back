package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.DriverGroup;

import java.util.List;

@Repository
public interface DriverGroupRepository extends JpaRepository<DriverGroup, Integer> {

    List<DriverGroup> findByFirmFirmId(int firmId);
}
