package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.ServiceLocation;
import rs.oris.back.service.ServiceLocationService;

import java.util.List;
/**
 * CRUd service location
 */
@RestController
public class ServiceLocationController {

    @Autowired
    private ServiceLocationService serviceLocationService;
    /**
     * sve servise jedne firme
     */
    @GetMapping("api/firm/{firm_id}/service-location")
    public Response<List<ServiceLocation>> getAll(@PathVariable("firm_id") int firmId) throws Exception {
        return serviceLocationService.getAllForFirm(firmId);
    }
    /**
     *
     * vraca jedan servis po id-u
     */
    @GetMapping("api/firm/{firm_id}/service-location/{id}")
    public Response<ServiceLocation> getServiceLocationById(@PathVariable("id") int serviceLocationId) throws Exception {
        return serviceLocationService.getSingleServiceLocation(serviceLocationId);
    }
    /**
     *
     * delete
     */
    @DeleteMapping("/api/firm/{firm_id}/service-location/{serviceLocation_id}")
    public boolean deleteServiceLocation(@PathVariable("serviceLocation_id") int serviceLocationId) throws Exception{
        return serviceLocationService.deleteServiceLocation(serviceLocationId);
    }
    /**
     *
     * update
     */
    @PutMapping("/api/firm/{firm_id}/service-location/{serviceLocation_id}")
    public Response<ServiceLocation> updateServiceLocation(@PathVariable("serviceLocation_id") int serviceLocationId, @RequestBody ServiceLocation serviceLocation) throws Exception{
        return serviceLocationService.updateServiceLocation(serviceLocationId, serviceLocation);
    }
    /**
     *
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/service-location")
    public Response<ServiceLocation> addServiceLocation(@RequestBody ServiceLocation serviceLocation, @PathVariable("firm_id") int firmId) throws Exception{
        return serviceLocationService.createServiceLocation(serviceLocation,firmId);
    }

}
