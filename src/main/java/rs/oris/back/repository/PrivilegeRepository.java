package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Privilege;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {

}
