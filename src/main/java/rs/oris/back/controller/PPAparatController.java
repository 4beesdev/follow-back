package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.PPAparat;
import rs.oris.back.service.PPAparatService;
import rs.oris.back.controller.wrapper.Response;

import java.util.List;

@RestController
public class PPAparatController {

    @Autowired
    private PPAparatService pPAparatService;

    /**
     * vraca sve aparate prosledjenog vozila
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/ppaparat")
    public Response<List<PPAparat>> getAllForVehicle(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return pPAparatService.getAllByVehicle(vehicleId);
    }
    /**
     * vraca jedan aparat po id-u
     */
    @GetMapping("api/firm/{firm_id}/pPAparat/{id}")
    public Response<PPAparat> getPPAparatById(@PathVariable("id") int pPAparatId) throws Exception {
        return pPAparatService.getSinglePPAparat(pPAparatId);
    }
    /**
     * delete
     */
    @DeleteMapping("/api/firm/{firm_id}/pPAparat/{pPAparat_id}")
    public boolean deletePPAparat(@PathVariable("pPAparat_id") int pPAparatId) throws Exception{
        return pPAparatService.deletePPAparat(pPAparatId);
    }

    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/pPAparat/{pPAparat_id}")
    public Response<PPAparat> updatePPAparat(@PathVariable("pPAparat_id") int pPAparatId, @RequestBody PPAparat pPAparat) throws Exception{
        return pPAparatService.updatePPAparat(pPAparatId, pPAparat);
    }
    /**
     * create/save
     * @param pPAparat aparat za cuvanje
     * @param vehicleId vozilo u kome ce biti aparat
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/pPAparat")
    public Response<PPAparat> addPPAparat(@RequestBody PPAparat pPAparat, @PathVariable("vehicle_id") int vehicleId) throws Exception{
        return pPAparatService.createPPAparat(pPAparat,vehicleId);
    }

}
