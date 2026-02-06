package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.FuelStation;
import rs.oris.back.service.FuelStationService;

import java.util.List;

@RestController
@CrossOrigin
public class FuelStationController {

    @Autowired
    private FuelStationService fuelStationService;

    /**
     * vraca sve pumpe jedne firme
     */
    @GetMapping("api/firm/{firm_id}/fuel-station")
    public Response<List<FuelStation>> getAll(@PathVariable("firm_id") int firmId) throws Exception {
        return fuelStationService.getAllByFirm(firmId);
    }


    /**
     * vraca jednu pumpu po id-u
     */
    @GetMapping("api/firm/{firm_id}/fuel-station/{id}")
    public Response<FuelStation> getFuelStationById(@PathVariable("id") int fuelStationId) throws Exception {
        return fuelStationService.getSingleFuelStation(fuelStationId);
    }
    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/fuel-station/{fuelStation_id}")
    public boolean deleteFuelStation(@PathVariable("fuelStation_id") int fuelStationId) throws Exception{
        return fuelStationService.deleteFuelStation(fuelStationId);
    }
    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/fuel-station/{fuelStation_id}")
    public Response<FuelStation> updateFuelStation(@PathVariable("fuelStation_id") int fuelStationId, @RequestBody FuelStation fuelStation, @PathVariable("firm_id") int firmId) throws Exception{
        return fuelStationService.updateFuelStation(fuelStationId, fuelStation);
    }
    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/fuel-company/{fuel_company_id}/fuel-station")
    public Response<FuelStation> addFuelStation(@RequestBody FuelStation fuelStation, @PathVariable("fuel_company_id") int fuelCompanyId) throws Exception{
        return fuelStationService.createFuelStation(fuelStation, fuelCompanyId);
    }

}
