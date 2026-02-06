package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.FuelCompany;
import rs.oris.back.service.FuelCompanyService;

import java.util.List;

@RestController
public class FuelCompanyController {

    @Autowired
    private FuelCompanyService fuelCompanyService;


    /**
     * vraca sve lance benziskih pumpi vezanih za jednu firmu
     */
    @GetMapping("api/firm/{firm_id}/fuel-company")
    public Response<List<FuelCompany>> getAll(@PathVariable("firm_id") int firmId) throws Exception {
        return fuelCompanyService.getAllByFirmFirmId(firmId);
    }
    /**
     * vraca jedan laanac benz pump po id-u
     */
    @GetMapping("api/firm/{firm_id}/fuel-company/{id}")
    public Response<FuelCompany> getFuelCompanyById(@PathVariable("id") int fuelCompanyId) throws Exception {
        return fuelCompanyService.getSingleFuelCompany(fuelCompanyId);
    }
    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/fuel-company/{fuelCompany_id}")
    public boolean deleteFuelCompany(@PathVariable("fuelCompany_id") int fuelCompanyId) throws Exception{
        return fuelCompanyService.deleteFuelCompany(fuelCompanyId);
    }
    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/fuel-company/{fuelCompany_id}")
    public boolean updateFuelCompany(@PathVariable("fuelCompany_id") int fuelCompanyId, @RequestBody FuelCompany fuelCompany) throws Exception{
        return fuelCompanyService.updateFuelCompany(fuelCompanyId, fuelCompany);
    }

    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/fuel-company")
    public Response<FuelCompany> addFuelCompany(@RequestBody FuelCompany fuelCompany, @PathVariable("firm_id") int firmId) throws Exception{
        return fuelCompanyService.createFuelCompany(fuelCompany, firmId);
    }

}
