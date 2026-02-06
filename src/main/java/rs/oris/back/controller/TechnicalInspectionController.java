package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.TechnicalInspection;
import rs.oris.back.service.TechnicalInspectionService;
import rs.oris.back.controller.wrapper.Response;


import java.util.List;

@RestController
@CrossOrigin
public class TechnicalInspectionController {

    @Autowired
    private TechnicalInspectionService technicalInspectionService;
    /**
     * vraca sve inspekcije vozila
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/technical-inspection")
    public Response<List<TechnicalInspection>> getAll(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return technicalInspectionService.getAll(vehicleId);
    }
    /**
     *
     * vraca sve inspekcije firme
     */
    @GetMapping("api/firm/{firm_id}/technical-inspection")
    public Response<List<TechnicalInspection>> getAllFirm(@PathVariable("firm_id") int firmId) throws Exception {
        return technicalInspectionService.getAllFirm(firmId);
    }

    /**
     * vraca inspekciju po id-u
     */
    @GetMapping("api/firm/{firm_id}/technical-inspection/{id}")
    public Response<TechnicalInspection> getTechnicalInspectionById(@PathVariable("id") int technicalInspectionId) throws Exception {
        return technicalInspectionService.getSingleTechnicalInspection(technicalInspectionId);
    }
    /**
     * brise inspekciju po id-u
     */
    @DeleteMapping("/api/firm/{firm_id}/technical-inspection/{technicalInspection_id}")
    public boolean deleteTechnicalInspection(@PathVariable("technicalInspection_id") int technicalInspectionId) throws Exception{
        return technicalInspectionService.deleteTechnicalInspection(technicalInspectionId);
    }
    /**
     *
     * azurira inspekciju po id-u
     */
    @PutMapping("/api/firm/{firm_id}/technical-inspection/{technicalInspection_id}")
    public Response<TechnicalInspection> updateTechnicalInspection(@PathVariable("technicalInspection_id") int technicalInspectionId, @RequestBody TechnicalInspection technicalInspection) throws Exception{
        return technicalInspectionService.updateTechnicalInspection(technicalInspectionId, technicalInspection);
    }
    /**
     *
     * cuva novu inspekciju
     */
    @PostMapping("/api/firm/{firm_id}/vehicle/{vehicle_id}/technical-inspection")
    public Response<TechnicalInspection> addTechnicalInspection(@RequestBody TechnicalInspection technicalInspection, @PathVariable("vehicle_id") int vehicleId) throws Exception{
        return technicalInspectionService.createTechnicalInspection(technicalInspection,vehicleId);
    }

}
