package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Izvestaj;


@Repository
public interface IzvestajRepository extends JpaRepository<Izvestaj, Integer> {

}
