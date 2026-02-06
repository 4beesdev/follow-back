package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.Registration;
import rs.oris.back.service.RegistrationService;
import rs.oris.back.controller.wrapper.Response;

import java.util.List;

@RestController
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;
    /**
     *
     * vraca istoriju registracija jednog vozila
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/registration")
    public Response<List<Registration>> getAll(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return registrationService.getAllByVehicle(vehicleId);
    }
    /**
     *
     * vraca sve registracije jedne firme
     */
    @GetMapping("api/firm/{firm_id}/registration")
    public Response<List<Registration>> getAllReg(@PathVariable("firm_id") int firmId) throws Exception {
        return registrationService.getAllByFirm(firmId);
    }
    /**
     *
     * vraca registraciju po id-u
     */
    @GetMapping("api/firm/{firm_id}/registration/{id}")
    public Response<Registration> getRegistrationById(@PathVariable("id") int registrationId) throws Exception {
        return registrationService.getSingleRegistration(registrationId);
    }
    /**
     *
     * delete
     */
    @DeleteMapping("/api/firm/{firm_id}/registration/{registration_id}")
    public boolean deleteRegistration(@PathVariable("registration_id") int registrationId) throws Exception{
        return registrationService.deleteRegistration(registrationId);
    }
    /**
     *
     * update
     */
    @PutMapping("/api/firm/{firm_id}/registration/{registration_id}")
    public Response<Registration> updateRegistration(@PathVariable("registration_id") int registrationId, @RequestBody Registration registration) throws Exception{
        return registrationService.updateRegistration(registrationId, registration);
    }
    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/registration")
    public Response<Registration> addRegistration(@RequestBody Registration registration, @PathVariable("vehicle_id") int vehicleId) throws Exception{
        return registrationService.createRegistration(registration,vehicleId);
    }

}
