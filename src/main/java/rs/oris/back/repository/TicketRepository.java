package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.Ticket;

import java.util.Date;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {


    List<Ticket> findByVehicleVehicleId(int vehicleId);

    List<Ticket> findByVehicleFirmFirmId(int firmId);

    List<Ticket> findByDriverDriverIdAndDateBetween(int driverId, Date dateFrom, Date dateTo);

    void deleteByVehicleVehicleId(int vehicleId);
}
