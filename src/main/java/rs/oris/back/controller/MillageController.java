package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Millage;
import rs.oris.back.service.MillageService;

import java.util.List;

@RestController
@CrossOrigin
public class MillageController {

    @Autowired
    private MillageService millageService;

    /**
     * Vraca istoriju kilometrazi jednog vozila
     * @param vehicleId vozilo
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/millage")
    public Response<List<Millage>> getAll(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return millageService.getAllForVehicle(vehicleId);
    }
    /**
     * vraca jedan objekat kilometraze prema id-u
     * @param millageId id
     */
    @GetMapping("api/firm/{firm_id}/millage/{id}")
    public Response<Millage> getMillageById(@PathVariable("id") int millageId) throws Exception {
        return millageService.getSingleMillage(millageId);
    }

    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/millage/{millage_id}")
    public boolean deleteMillage(@PathVariable("millage_id") int millageId) throws Exception{
        return millageService.deleteMillage(millageId);
    }

    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/millage/{millage_id}")
    public Response<Millage> updateMillage(@PathVariable("millage_id") int millageId, @RequestBody Millage millage) throws Exception{
        return millageService.updateMillage(millageId, millage);
    }
    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/millage")
    public Response<Millage> addMillage(@RequestBody Millage millage, @PathVariable("vehicle_id") int vehicleId) throws Exception{
        return millageService.createMillage(millage,vehicleId);
    }

}
