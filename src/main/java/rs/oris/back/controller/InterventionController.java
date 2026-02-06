package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Intervention;
import rs.oris.back.domain.InterventionDTO;
import rs.oris.back.domain.InterventionUpdateRequest;
import rs.oris.back.service.InterventionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@CrossOrigin
public class InterventionController {

    @Autowired
    private InterventionService interventionService;
    /**
     * vraca sve intervencije vozila
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/intervention")
    @Transactional(readOnly=true)
    public Response<List<InterventionDTO>> getAll(@PathVariable("vehicle_id") int vehicleId


//                                                    @RequestParam("page") int page,
//                                                    @RequestParam("size") int size
                                                    ) throws Exception {

        return interventionService.getAllForVehicle(vehicleId);
    }







    /**
     * vraca sve intervencije firme
     */
    @GetMapping("api/firm/{firm_id}/intervention")
    @Transactional(readOnly=true)
    public Response<ArrayList<InterventionDTO>> getInterventionByFirm(@PathVariable("firm_id") int firmId) throws Exception {
        return interventionService.getIntFirm(firmId);
    }
    /**
     * vraca konkretnu intervenciju
     */
    @GetMapping("api/firm/{firm_id}/intervention/{id}")
    public Response<Intervention> getInterventionById(@PathVariable("id") int interventionId) throws Exception {
        return interventionService.getSingleIntervention(interventionId);
    }
    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/intervention/{intervention_id}")
    public boolean deleteIntervention(@PathVariable("intervention_id") int interventionId) throws Exception{
        return interventionService.deleteIntervention(interventionId);
    }
    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/intervention/{intervention_id}")
    public Response<Intervention> updateIntervention(@PathVariable("intervention_id") int interventionId, @RequestBody Intervention intervention) throws Exception{
        return interventionService.updateIntervention(interventionId, intervention);
    }
    /**
     * create/save files
     */
    @PostMapping("/api/firm/{firm_id}/intervention/{intervention_id}/add/files")
    public Response<Intervention> addFilesToServiceInterval(@PathVariable("intervention_id") int interventionId,   @RequestParam("files") List<MultipartFile> files) throws Exception{
        return interventionService.addFilesToIntervention(interventionId, files);
    }

    //Delete files from intervention
    @DeleteMapping("/api/firm/{firm_id}/intervention/{intervention_id}/delete/files")
    public Response<Intervention> deleteFilesFromServiceInterval(@PathVariable("intervention_id") int interventionId,   @RequestBody List<Long> ids) throws Exception{
        return interventionService.removeFilesToIntervention(interventionId, ids);
    }

    //Create
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/service-location/{slid}/intervention")
    public Response<Intervention> addIntervention(@RequestBody Intervention intervention, @PathVariable("vehicle_id") int vehicleId,
                                                  @PathVariable("slid") int slId) throws Exception {
        return interventionService.createIntervention(intervention, vehicleId, slId);
    }

}
