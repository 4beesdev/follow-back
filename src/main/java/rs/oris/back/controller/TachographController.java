package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.Tachograph;
import rs.oris.back.service.TachographService;
import rs.oris.back.controller.wrapper.Response;

import java.util.List;
/**
 * Tachograph CRUD
 */
@RestController
@CrossOrigin
public class TachographController {

    @Autowired
    private TachographService tachographService;
    /**
     * vraca sve tachograph-e proslednjenog vozila
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/tachograph")
    public Response<List<Tachograph>> getAll(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return tachographService.getAll(vehicleId);
    }
    /**
     *
     * cita jedan tachograph po id-u
     */
    @GetMapping("api/firm/{firm_id}/tachograph/{id}")
    public Response<Tachograph> getTachographById(@PathVariable("id") int tachographId) throws Exception {
        return tachographService.getSingleTachograph(tachographId);
    }
    /**
     * delete
     */
    @DeleteMapping("/api/firm/{firm_id}/tachograph/{tachograph_id}")
    public boolean deleteTachograph(@PathVariable("tachograph_id") int tachographId) throws Exception{
        return tachographService.deleteTachograph(tachographId);
    }
    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/tachograph/{tachograph_id}")
    public Response<Tachograph> updateTachograph(@PathVariable("tachograph_id") int tachographId, @RequestBody Tachograph tachograph) throws Exception{
        return tachographService.updateTachograph(tachographId, tachograph);
    }
    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/tachograph")
    public Response<Tachograph> addTachograph(@RequestBody Tachograph tachograph,@PathVariable("vehicle_id") int vehicleId) throws Exception{
        return tachographService.createTachograph(tachograph,vehicleId);
    }

}
