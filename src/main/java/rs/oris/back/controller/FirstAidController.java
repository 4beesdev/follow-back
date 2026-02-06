package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.FirstAid;
import rs.oris.back.service.FirstAidService;

import java.util.List;

@RestController
public class FirstAidController {

    @Autowired
    private FirstAidService firstAidService;

    /**
     * vraca sve jedinice prve pomoci jedne firme
     */
    @GetMapping("api/firm/{firm_id}/first-aid")
    public Response<List<FirstAid>> getAll(@PathVariable("firm_id") int firmId) throws Exception {
        return firstAidService.getAllForFirm(firmId);
    }
    /**
     * vraca sve jedinice prve pomoci u vozilu
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/first-aid")
    public Response<List<FirstAid>> getAll(@PathVariable("firm_id") int firmId, @PathVariable("vehicle_id") int vehicleId) throws Exception {
        return firstAidService.getAllForVeh(vehicleId);
    }

    /**
     * vraca jednu jedinicu prve pomoci po id-u
     */
    @GetMapping("api/firm/{firm_id}/first-aid/{id}")
    public Response<FirstAid> getFirstAidById(@PathVariable("id") int firstAidId) throws Exception {
        return firstAidService.getSingleFirstAid(firstAidId);
    }
    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/first-aid/{firstAid_id}")
    public boolean deleteFirstAid(@PathVariable("firstAid_id") int firstAidId) throws Exception {
        return firstAidService.deleteFirstAid(firstAidId);
    }

    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/first-aid/{firstAid_id}")
    public Response<FirstAid> updateFirstAid(@PathVariable("firstAid_id") int firstAidId, @RequestBody FirstAid firstAid) throws Exception {
        return firstAidService.updateFirstAid(firstAidId, firstAid);
    }
    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/first-aid")
    public Response<FirstAid> addFirstAid(@RequestBody FirstAid firstAid, @PathVariable("vehicle_id") int vehicleId) throws Exception {
        return firstAidService.createFirstAid(firstAid, vehicleId);
    }

}
