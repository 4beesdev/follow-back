package rs.oris.back.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.FuelConsumption;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface FuelConsumptionRepository extends JpaRepository<FuelConsumption, Integer>, PagingAndSortingRepository<FuelConsumption,Integer> {

    List<FuelConsumption> findByDriverDriverId(int driverId);

    List<FuelConsumption> findByVehicleVehicleId(int vehicleId);

    List<FuelConsumption> findByFuelStationFuelStationId(int fstId);

    List<FuelConsumption> findByVehicleVehicleIdAndDateBetween(int vehicleId, Date dateFrom, Date dateTo);
    List<FuelConsumption> findByVehicleRegistrationAndDateBetween(String registration, Date dateFrom, Date dateTo);

    List<FuelConsumption> findByDriverDriverIdAndDateBetween(int driverId, Date dateFrom, Date dateTo);
    Page<FuelConsumption> findAllByVehicle_Firm(Firm firm, Pageable pageable);
    void deleteByVehicleVehicleId(int vehicleId);

    List<FuelConsumption> findByVehicleVehicleIdInAndDateBetween(List<Integer> vehicleIds, Date dateFrom, Date dateTo);
}
