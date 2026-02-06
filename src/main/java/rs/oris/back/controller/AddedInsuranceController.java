package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.AddedInsurance;
import rs.oris.back.service.AddedInsuranceService;

import java.util.List;



/**
 * CRUD za AddedInsurance
 */
@RestController
@CrossOrigin
public class AddedInsuranceController {


    @Autowired
    private AddedInsuranceService addedInsuranceService;


    /**
     * vraca istoriju osiguranja vozila
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/added-insurance")
    public Response<List<AddedInsurance>> getAllForVehicle(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return addedInsuranceService.getAllForVehicle(vehicleId);
    }


    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/added-insurance")
    public Response<AddedInsurance> addAddedInsurance(@RequestBody AddedInsurance addedInsurance, @PathVariable("vehicle_id") int vehicleId) throws Exception{
        return addedInsuranceService.createAddedInsurance(addedInsurance,vehicleId);
    }

    /**
     * vraca konkretno osiguranje preko id-a
     * @param addedInsuranceId id
     */
    @GetMapping("api/firm/{firm_id}/addedInsurance/{id}")
    public Response<AddedInsurance> getAddedInsuranceById(@PathVariable("id") int addedInsuranceId) throws Exception {
        return addedInsuranceService.getSingleAddedInsurance(addedInsuranceId);
    }

    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/addedInsurance/{addedInsurance_id}")
    public boolean deleteAddedInsurance(@PathVariable("addedInsurance_id") int addedInsuranceId) throws Exception{
        return addedInsuranceService.deleteAddedInsurance(addedInsuranceId);
    }

    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/addedInsurance/{addedInsurance_id}")
    public Response<AddedInsurance> updateAddedInsurance(@PathVariable("addedInsurance_id") int addedInsuranceId, @RequestBody AddedInsurance addedInsurance) throws Exception{
        return addedInsuranceService.updateAddedInsurance(addedInsuranceId, addedInsurance);
    }



}
