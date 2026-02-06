package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.Ticket;
import rs.oris.back.service.TicketService;
import rs.oris.back.controller.wrapper.Response;

import java.util.List;
/**
 * CRUD za kazne
 */
@RestController
public class TicketController {

    @Autowired
    private TicketService ticketService;
    /**
     *
     * sve kazne jednog vozila
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/ticket")
    public Response<List<Ticket>> getAllByVehicle(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return ticketService.getAllByVehicle(vehicleId);
    }
    /**
     *
     * sve kazne jedne firme
     */
    @GetMapping("api/firm/{firm_id}/ticket")
    public Response<List<Ticket>> getAllByFirm(@PathVariable("firm_id") int firmId) throws Exception {
        return ticketService.getAllByFirm(firmId);
    }
    /**
     * vraca kaznu po id-u
     */
    @GetMapping("api/firm/{firm_id}/ticket/{id}")
    public Response<Ticket> getTicketById(@PathVariable("id") int ticketId) throws Exception {
        return ticketService.getSingleTicket(ticketId);
    }
    /**
     *
     * brise kaznu
     */
    @DeleteMapping("/api/firm/{firm_id}/ticket/{ticket_id}")
    public boolean deleteTicket(@PathVariable("ticket_id") int ticketId) throws Exception{
        return ticketService.deleteTicket(ticketId);
    }
    /**
     * azurira kaznu
     */
    @PutMapping("/api/firm/{firm_id}/ticket/{ticket_id}")
    public boolean updateTicket(@PathVariable("ticket_id") int ticketId, @RequestBody Ticket ticket) throws Exception{
        return ticketService.updateTicket(ticketId, ticket);
    }
    /**
     * dodaje novu kaznu
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/driver/{driver_id}/ticket")
    public Response<Ticket> addTicket(@RequestBody Ticket ticket, @PathVariable("driver_id") int driverId, @PathVariable("vehicle_id") int vehicleId) throws Exception{
        return ticketService.createTicket(ticket,driverId,vehicleId);
    }

}
