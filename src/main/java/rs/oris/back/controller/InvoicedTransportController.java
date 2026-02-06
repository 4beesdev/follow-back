package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.InvoicedTransport;
import rs.oris.back.service.InvoicedTransportService;

import java.util.List;

@RestController
public class InvoicedTransportController {

    @Autowired
    private InvoicedTransportService invoicedTransportService;
    /**
     * vraca listu invoiced transport objekata vezanih za prosledjeno vozilo
     * @param vehicleId vozilo
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/invoiced-transport")
    public Response<List<InvoicedTransport>> getAll(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return invoicedTransportService.getAllForVehicle(vehicleId);
    }
    /**
     * vraca listu invoiced transportt objekata vezanih za prosledjenu firmu
     * @param firmId firma
     */
    @GetMapping("api/firm/{firm_id}/invoiced-transport")
    public Response<List<InvoicedTransport>> getAllForFRM(@PathVariable("firm_id") int firmId) throws Exception {
        return invoicedTransportService.getAllForFrm(firmId);
    }
    /**
     * vraca jedan konkretan entitet preko id-a
     * @param invoicedTransportId id
     */
    @GetMapping("api/invoiced-transport/{id}")
    public Response<InvoicedTransport> getInvoicedTransportById(@PathVariable("id") int invoicedTransportId) throws Exception {
        return invoicedTransportService.getSingleInvoicedTransport(invoicedTransportId);
    }
    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/invoiced-transport/{invoicedTransport_id}")
    public boolean deleteInvoicedTransport(@PathVariable("invoicedTransport_id") int invoicedTransportId) throws Exception{
        return invoicedTransportService.deleteInvoicedTransport(invoicedTransportId);
    }
    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/invoiced-transport/{invoicedTransport_id}")
    public Response<InvoicedTransport> updateInvoicedTransport(@PathVariable("invoicedTransport_id") int invoicedTransportId, @RequestBody InvoicedTransport invoicedTransport) throws Exception{
        return invoicedTransportService.updateInvoicedTransport(invoicedTransportId, invoicedTransport);
    }

    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/driver/{driver_id}/invoiced-transport")
    public Response<InvoicedTransport> addInvoicedTransport(@RequestBody InvoicedTransport invoicedTransport, @PathVariable("vehicle_id") int vehicleId,
                                                            @PathVariable("driver_id") int driverId) throws Exception{
        return invoicedTransportService.createInvoicedTransport(invoicedTransport,vehicleId,driverId);
    }

}
