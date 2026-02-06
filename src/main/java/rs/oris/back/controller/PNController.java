package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.PN;
import rs.oris.back.service.PNService;
import rs.oris.back.controller.wrapper.Response;


import java.util.List;

@RestController
@CrossOrigin
public class PNController {

    @Autowired
    private PNService pNService;

    /**
     *
     * vraca sve putne naloge jednog vozaca
     */
    @GetMapping("api/firm/{firm_id}/driver/{driver_id}/pn")
    public Response<List<PN>> getAllForDrivur(@PathVariable("driver_id") int driverId) throws Exception {
        return pNService.getAllForDrivur(driverId);
    }

    /**
     *
     * vraca sve putne naloge jedne firme
     */
    @GetMapping("api/firm/{firm_id}/pn")
    public Response<List<PN>> getAllForFirm(@PathVariable("firm_id") int firmid) throws Exception {
        return pNService.getAllForFirm(firmid);
    }

    /**
     * vraca sve putne naloge vezane za prosledjeno vozilo
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/pn")
    public Response<List<PN>> getAllForVehicle(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return pNService.getAllForVehicle(vehicleId);
    }
    /**
     *
     * vraca jedan putni nalog preko id-a
     */
    @GetMapping("api/firm/{firm_id}/pn/{id}")
    public Response<PN> getPNById(@PathVariable("id") int pNId) throws Exception {
        return pNService.getSinglePN(pNId);
    }

    /**
     * delete
     */
    @DeleteMapping("/api/firm/{firm_id}/pn/{pN_id}")
    public boolean deletePN(@PathVariable("pN_id") int pNId) throws Exception{
        return pNService.deletePN(pNId);
    }
    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/pn/{pN_id}")
    public Response<PN> updatePN(@PathVariable("pN_id") int pNId, @RequestBody PN pN) throws Exception{
        return pNService.updatePN(pNId, pN);
    }
    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/driver/{driver_id}/pn")
    public Response<PN> addPN(@RequestBody PN pN,@PathVariable("driver_id") int driverId,@PathVariable("vehicle_id") int vehicleId) throws Exception{
        return pNService.createPN(pN,vehicleId,driverId);
    }

}
