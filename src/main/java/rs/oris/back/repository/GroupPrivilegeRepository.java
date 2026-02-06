package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.GroupPrivilege;

import java.util.List;

@Repository
public interface GroupPrivilegeRepository extends JpaRepository<GroupPrivilege, Integer> {

    List<GroupPrivilege> findByGroupGroupId(int groupId);

    void deleteByGroupGroupId(int groupId);

}
