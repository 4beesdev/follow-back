package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.VehicleRegistrationCard;
import rs.oris.back.service.VehicleRegistrationCardService;
import rs.oris.back.controller.wrapper.Response;

import java.util.List;
/**
 * CRUD za vehicle registration card
 */
@RestController
@CrossOrigin
public class VehicleRegistrationCardController {

    @Autowired
    private VehicleRegistrationCardService vehicleRegistrationCardService;


    /**
     *
     * vraca registracije vozila
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/vehicle-registration-card")
    public Response<List<VehicleRegistrationCard>> getByVehicle(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return vehicleRegistrationCardService.getByVehicle(vehicleId);
    }
    /**
     * vraca jedan po id-u
     */
    @GetMapping("api/firm/{firm_id}/vehicle-registration-card/{id}")
    public Response<VehicleRegistrationCard> getVehicleRegistrationCardById(@PathVariable("id") int vehicleRegistrationCardId) throws Exception {
        return vehicleRegistrationCardService.getSingleVehicleRegistrationCard(vehicleRegistrationCardId);
    }
    /**
     *
     * brise entitet po id-u //ukida vezu
     */
    @DeleteMapping("/api/firm/{firm_id}/vehicle-registration-card/{vehicleRegistrationCard_id}")
    public boolean deleteVehicleRegistrationCard(@PathVariable("vehicleRegistrationCard_id") int vehicleRegistrationCardId) throws Exception{
        return vehicleRegistrationCardService.deleteVehicleRegistrationCard(vehicleRegistrationCardId);
    }
    /**
     *
     * azurira entitet
     */
    @PutMapping("/api/firm/{firm_id}/vehicle-registration-card/{vehicleRegistrationCard_id}")
    public Response<VehicleRegistrationCard> updateVehicleRegistrationCard(@PathVariable("vehicleRegistrationCard_id") int vehicleRegistrationCardId, @RequestBody VehicleRegistrationCard vehicleRegistrationCard) throws Exception{
        return vehicleRegistrationCardService.updateVehicleRegistrationCard(vehicleRegistrationCardId, vehicleRegistrationCard);
    }
    /**
     *
     * dodaje entitet //spaja vozilo s registrarskom karticom
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/vehicleRegistrationCard")
    public Response<VehicleRegistrationCard> addVehicleRegistrationCard(@RequestBody VehicleRegistrationCard vehicleRegistrationCard,
                                                                        @PathVariable("vehicle_id") int vehicleId) throws Exception{
        return vehicleRegistrationCardService.createVehicleRegistrationCard(vehicleRegistrationCard,vehicleId);
    }

}
