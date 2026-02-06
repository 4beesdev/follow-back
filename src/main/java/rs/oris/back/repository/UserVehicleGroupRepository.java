package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.UserVehicleGroup;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserVehicleGroupRepository extends JpaRepository<UserVehicleGroup, Integer> {

    Optional<UserVehicleGroup> findByUserUserIdAndVehicleGroupVehicleGroupId(int userId, int vehicleGroupId);

    List<UserVehicleGroup> findByUserUserId(int userId);

    void deleteByUserUserIdAndVehicleGroupVehicleGroupId(int userId, int vehicleGroupId);

    void deleteByUserUserId(int userId);
}
