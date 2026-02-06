package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Firm;

import java.util.List;

@Repository
public interface FirmRepository extends JpaRepository<Firm, Integer> {

    List<Firm> findByActive(boolean b);
}
