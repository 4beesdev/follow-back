package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.FirmPnCounter;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface FirmPnCounterRepository extends JpaRepository<FirmPnCounter, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from FirmPnCounter c where c.firmId = :firmId")
    Optional<FirmPnCounter> findForUpdate(@Param("firmId") int firmId);
}

