package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {


    User findByUsername(String username);

    Optional<User> findByUserId(int chiefId);

    List<User> findByFirmFirmId(int firmId);

    List<User> findByGroupGroupId(int groupId);

    List<User> findByFirmFirmIdAndAdmin(int firmId, boolean b);

    List<User> findByAdmin(boolean b);

    List<User> findBySuperAdmin(boolean b);
}
