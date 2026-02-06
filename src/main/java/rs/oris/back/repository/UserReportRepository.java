package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.UserReport;

import java.util.Set;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Integer> {

    Set<UserReport> findByFirmFirmId(int firmId);
}
