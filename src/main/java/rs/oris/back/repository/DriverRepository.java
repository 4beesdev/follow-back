package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Driver;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.Vehicle;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer> {

    List<Driver> findByFirmFirmId(int firmId);

    List<Driver> findAllByDriverIdIn(List<Integer> driverIds);

    List<Driver> findByVehicle(Vehicle vehicle);
    @Nullable
    List<Driver> findAllByIdNumberIn(List<String> rfid);
    @Nullable
    List<Driver> findAllByIdentificationNumberIn(List<String> rfid);
    @Nullable
    Optional<Driver> findFirstByIdentificationNumber(String rfid);

    @Query("SELECT d FROM Driver d WHERE LOWER(REPLACE(d.name,' ','')) = LOWER(REPLACE(:name,' ','')) AND d.firm = :firm")
    Optional<Driver> findByNameAndFirm(@Param("name") String name,@Param("firm") Firm firm);
}
