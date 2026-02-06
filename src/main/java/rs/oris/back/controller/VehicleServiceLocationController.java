package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.VehicleServiceLocation;
import rs.oris.back.service.VehicleServiceLocationService;

import java.util.List;

@RestController
@CrossOrigin
public class VehicleServiceLocationController {

    @Autowired
    private VehicleServiceLocationService vehicleServiceLocationService;
    /**
     * vraca listu svih servisiranja u jednoj firmi
     */
    @GetMapping("api/firm/{firm_id}/vehicle-service-location")
    public Response<List<VehicleServiceLocation>> getAll(@PathVariable("firm_id") int firmId) throws Exception {
        return vehicleServiceLocationService.getAllByFirm(firmId);
    }
    /**
     * vraca listu svih servisiranja vozila
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/vehicle-service-location")
    public Response<List<VehicleServiceLocation>> getAllForVehicle(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return vehicleServiceLocationService.getAllByVehicle(vehicleId);
    }
    /**
     * vraca listu svih servisiranja jednog servisa (majstora)
     */
    @GetMapping("api/firm/{firm_id}/service-location/{service_location_id}/vehicle-service-location")
    public Response<List<VehicleServiceLocation>> getAllForSl(@PathVariable("service_location_id") int slId) throws Exception {
        return vehicleServiceLocationService.getAllBySL(slId);
    }

    /**
     * vraca jedno konkretno servisiranje po id-u
     */
    @GetMapping("api/firm/{firm_id}/vehicle-service-location/{id}")
    public Response<VehicleServiceLocation> getVehicleServiceLocationById(@PathVariable("id") int vehicleServiceLocationId) throws Exception {
        return vehicleServiceLocationService.getSingleVehicleServiceLocation(vehicleServiceLocationId);
    }
    /**
     * delete
     */
    @DeleteMapping("/api/firm/{firm_id}/vehicle-service-location/{vehicleServiceLocation_id}")
    public boolean deleteVehicleServiceLocation(@PathVariable("vehicleServiceLocation_id") int vehicleServiceLocationId) throws Exception{
        return vehicleServiceLocationService.deleteVehicleServiceLocation(vehicleServiceLocationId);
    }
    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/vehicle-service-location/{vehicleServiceLocation_id}")
    public Response<VehicleServiceLocation> updateVehicleServiceLocation(@PathVariable("vehicleServiceLocation_id") int vehicleServiceLocationId, @RequestBody VehicleServiceLocation vehicleServiceLocation) throws Exception{
        return vehicleServiceLocationService.updateVehicleServiceLocation(vehicleServiceLocationId, vehicleServiceLocation);
    }
    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}//vehicle/{vehicle_id}/service-location/{service_location_id}/vehicle-service-location")
    public Response<VehicleServiceLocation> addVehicleServiceLocation(@RequestBody VehicleServiceLocation vehicleServiceLocation, @PathVariable("service_location_id") int slId,@PathVariable("vehicle_id") int vehicleId) throws Exception{
        return vehicleServiceLocationService.createVehicleServiceLocation(vehicleServiceLocation,slId,vehicleId);
    }

}
