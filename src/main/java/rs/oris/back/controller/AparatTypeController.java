package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.AparatType;
import rs.oris.back.service.AparatTypeService;

import java.util.List;

@RestController
public class AparatTypeController {

    @Autowired
    private AparatTypeService aparatTypeService;

    /**
     * generise random izvestaj, vrv za testiranje koriscen
     */
    @PostMapping("/api/test-izvestaj/{broj}")
    public Response<String> izgenerisiRandomIzvestaj(@PathVariable("broj") int broj){
        return aparatTypeService.izvestaj(broj);
    }
    /**
     * vraca sve tipove protivpozarnih aparata jedne firme
     */

    @GetMapping("api/firm/{firm_id}/aparat-type")
    public Response<List<AparatType>> getAll(@PathVariable("firm_id") int firmId) throws Exception {
        return aparatTypeService.getAll(firmId);
    }
    /**
     * vraca jedan tip pp aparata po id-u
     */
    @GetMapping("api/firm/{firm_id}/aparat-type/{id}")
    public Response<AparatType> getAparatTypeById(@PathVariable("id") int aparatTypeId) throws Exception {
        return aparatTypeService.getSingleAparatType(aparatTypeId);
    }
    /**
     * create/save i dodeljuje ga firmi
     */
    @PostMapping("/api/firm/{firm_id}/aparat-type")
    public Response<AparatType> addAparatType(@RequestBody AparatType aparatType, @PathVariable("firm_id") int firmId) throws Exception{
        return aparatTypeService.createAparatType(aparatType,firmId);
    }
    /**
     * delete
     */
    @Transactional
    @DeleteMapping("api/firm/{firm_id}/aparat-type/{id}")
    public boolean deleteById(@PathVariable("id") int aparatTypeId) throws Exception {
        return aparatTypeService.delete(aparatTypeId);
    }


}
