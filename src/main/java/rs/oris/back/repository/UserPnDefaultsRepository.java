package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.UserPnDefaults;

import java.util.Optional;

@Repository
public interface UserPnDefaultsRepository extends JpaRepository<UserPnDefaults, Integer> {

    Optional<UserPnDefaults> findByUserUserId(int userId);
}
