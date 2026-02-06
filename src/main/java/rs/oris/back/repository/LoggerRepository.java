package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.logs.AutomaticReportLogger;

@Repository
public interface LoggerRepository extends JpaRepository<AutomaticReportLogger, Long>{
}
