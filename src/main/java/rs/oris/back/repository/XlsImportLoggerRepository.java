package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.logs.XlsImportLogger;

@Repository
public interface XlsImportLoggerRepository extends JpaRepository<XlsImportLogger,Long> {
}
