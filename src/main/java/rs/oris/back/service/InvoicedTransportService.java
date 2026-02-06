package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Driver;
import rs.oris.back.domain.InvoicedTransport;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.DriverRepository;
import rs.oris.back.repository.InvoicedTransportRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class InvoicedTransportService {

    @Autowired
    private InvoicedTransportRepository invoicedTransportRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DriverRepository driverRepository;
    /**
     * vraca sve intervencije vozila
     */
    public Response<List<InvoicedTransport>> getAllForVehicle(int vehicleId) {
        List<InvoicedTransport> invoicedTransportList = invoicedTransportRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(invoicedTransportList);
    }

    /**
     * vraca sve intervencije firme
     */
    public Response<List<InvoicedTransport>> getAllForFrm(int firmId) {
        List<InvoicedTransport> invoicedTransportList = invoicedTransportRepository.findByVehicleFirmFirmId(firmId);
        return new Response<>(invoicedTransportList);
    }


    /**
     * create/save
     */

	public Response<InvoicedTransport> createInvoicedTransport(InvoicedTransport invoicedTransport, int vehicleId, int driverId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }

        invoicedTransport.setDriver(optionalDriver.get());
        invoicedTransport.setVehicle(optionalVehicle.get());
		InvoicedTransport obj = invoicedTransportRepository.save(invoicedTransport);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     * vraca konkretnu intervenciju
     */
    public Response<InvoicedTransport> getSingleInvoicedTransport(int invoicedTransportId) throws Exception {
        Optional<InvoicedTransport> optionalInvoicedTransport = invoicedTransportRepository.findById(invoicedTransportId);
        if (!optionalInvoicedTransport.isPresent()) {
            throw new Exception("Invalid invoicedTransport id " + invoicedTransportId);
        }
        return new Response<>(optionalInvoicedTransport.get());
    }
    /**
     * update
     */
    public Response<InvoicedTransport> updateInvoicedTransport(int invoicedTransportId, InvoicedTransport invoicedTransport) throws Exception {
        Optional<InvoicedTransport> optionalInvoicedTransport = invoicedTransportRepository.findById(invoicedTransportId);
        if (!optionalInvoicedTransport.isPresent()){
            throw new Exception("Invalid invoicedTransport id " +invoicedTransportId);
        }

        InvoicedTransport old = optionalInvoicedTransport.get();

		// Update fields
        
     
        old.setInvoiceNumber(invoicedTransport.getInvoiceNumber());
        
        old.setRoute(invoicedTransport.getRoute());
        
        old.setExpenses(invoicedTransport.getExpenses());
        
        old.setDriversExpense(invoicedTransport.getDriversExpense());
        

		// End of update fields

        InvoicedTransport savedInvoicedTransport = invoicedTransportRepository.save(old);

        if (savedInvoicedTransport==null){
            throw new Exception("Failed to save invoicedTransport with id "+invoicedTransportId);
        }
        return new Response<>(invoicedTransport);
    }
    /**
     * delete
     */
    public boolean deleteInvoicedTransport(int invoicedTransportId) throws Exception {
        Optional<InvoicedTransport> optionalInvoicedTransport = invoicedTransportRepository.findById(invoicedTransportId);

        if (!optionalInvoicedTransport.isPresent()){
            throw new Exception("Invalid invoicedTransport id "+invoicedTransportId);
        }

        invoicedTransportRepository.deleteById(invoicedTransportId);

        return true;
    }



}
