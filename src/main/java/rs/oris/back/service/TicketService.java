package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Driver;
import rs.oris.back.domain.Ticket;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.DriverRepository;
import rs.oris.back.repository.TicketRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DriverRepository driverRepository;
    /**
     *
     * sve kazne jednog vozila
     */
    public Response<List<Ticket>> getAllByVehicle(int vehicleId) {
        List<Ticket> ticketList = ticketRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(ticketList);
    }

    public List<Ticket> getAllByVehicleAndStartDateAndEndDate(int vehicleId, Date startDate, Date endDate) {
        List<Ticket> ticketList = ticketRepository.findByVehicleVehicleId(vehicleId);
        ticketList=ticketList.stream().filter(ticket -> ticket.getDate().after(startDate) && ticket.getDate().before(endDate)).collect(Collectors.toList());

        return ticketList;
    }
    /**
     *
     * sve kazne jedne firme
     */
    public Response<List<Ticket>> getAllByFirm(int firmId) {
        List<Ticket> ticketList = ticketRepository.findByVehicleFirmFirmId(firmId);
        return new Response<>(ticketList);
    }
    /**
     * dodaje novu kaznu
     */
	public Response<Ticket> createTicket(Ticket ticket, int driverId, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Invalid driver id");
        }

        ticket.setVehicle(optionalVehicle.get());
        ticket.setDriver(optionalDriver.get());

		Ticket obj = ticketRepository.save(ticket);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     * vraca kaznu po id-u
     */
    public Response<Ticket> getSingleTicket(int ticketId) throws Exception {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (!optionalTicket.isPresent()) {
            throw new Exception("Invalid ticket id " + ticketId);
        }
        return new Response<>(optionalTicket.get());
    }
    /**
     * azurira kaznu
     */
    public boolean updateTicket(int ticketId, Ticket ticket) throws Exception {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);
        if (!optionalTicket.isPresent()){
            throw new Exception("Invalid ticket id " +ticketId);
        }

        Ticket old = optionalTicket.get();
  
		// Update fields

        old.setDamage(ticket.getDamage());
        
        old.setAddress(ticket.getAddress());
        
        old.setReason(ticket.getReason());
        
        old.setPoints(ticket.getPoints());
        
        old.setResponsible(ticket.isResponsible());
        
        old.setNote(ticket.getNote());

		// End of update fields

        Ticket savedTicket = ticketRepository.save(old);

        if (savedTicket==null){
            throw new Exception("Failed to save ticket with id "+ticketId);
        }
        return true;
    }
    /**
     *
     * brise kaznu
     */
    public boolean deleteTicket(int ticketId) throws Exception {
        Optional<Ticket> optionalTicket = ticketRepository.findById(ticketId);

        if (!optionalTicket.isPresent()){
            throw new Exception("Invalid ticket id "+ticketId);
        }

        ticketRepository.deleteById(ticketId);

        return true;
    }


}
