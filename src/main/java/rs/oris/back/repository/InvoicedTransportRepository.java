package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.InvoicedTransport;

import java.util.List;

@Repository
public interface InvoicedTransportRepository extends JpaRepository<InvoicedTransport, Integer> {

    List<InvoicedTransport> findByVehicleVehicleId(int vehicleId);

    List<InvoicedTransport> findByVehicleFirmFirmId(int firmId);

    void deleteByVehicleVehicleId(int vehicleId);
}
