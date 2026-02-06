package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.FuelCompany;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuelCompanyRepository extends JpaRepository<FuelCompany, Integer> {

    List<FuelCompany> findByFirmFirmId(int firmId);

    @Query("select f from FuelCompany f  WHERE LOWER(REPLACE(f.name,' ','')) = LOWER(REPLACE(:name,' ',''))")
    List<FuelCompany> findByName(@Param("name") String name);
}
