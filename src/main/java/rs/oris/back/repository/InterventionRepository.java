package rs.oris.back.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.domain.Intervention;
import rs.oris.back.domain.InterventionDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Integer>, PagingAndSortingRepository<Intervention,Integer> {

    @Transactional
    Stream<Intervention> findByVehicleVehicleId(int vehicleId);

    Stream<Intervention> findByVehicleFirmFirmId(int firmId);

    List<Intervention> findByVehicleVehicleIdAndDoneDateBetween(int vehicleId, Date dateFrom, Date dateTo);

    void deleteByVehicleVehicleId(int vehicleId);

    List<Intervention> findByVehicleVehicleIdInAndDoneDateBetween(List<Integer> vehicleIds, Date dateFrom, Date dateTo);
}
