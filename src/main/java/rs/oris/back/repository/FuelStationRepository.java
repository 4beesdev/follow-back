package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.FuelStation;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuelStationRepository extends JpaRepository<FuelStation, Integer> {

    List<FuelStation> findByFuelCompanyFirmFirmId(int firmId);

    @Query("SELECT fs FROM FuelStation fs WHERE LOWER(REPLACE(fs.name,' ','')) = LOWER(REPLACE(:name,' ',''))")
    List<FuelStation> findByName(@Param("name") String name);

}
