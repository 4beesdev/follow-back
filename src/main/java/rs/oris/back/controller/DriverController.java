package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.Driver;
import rs.oris.back.service.DriverService;
import rs.oris.back.controller.wrapper.Response;


import java.util.List;

@RestController
@CrossOrigin
public class DriverController {

    @Autowired
    private DriverService driverService;

    /**
     * vraca sve vozace jedne firme
     * @param firmId firma
     */
    @GetMapping("api/firm/{firm_id}/driver")
    public Response<List<Driver>> getAll(@PathVariable("firm_id") int firmId) throws Exception {
        return driverService.getAll(firmId);
    }
    /**
     * vraca jednog vozaca po id-u
     */
    @GetMapping("api/firm/{firm_id}/driver/{id}")
    public Response<Driver> getDriverById(@PathVariable("id") int driverId) throws Exception {
        return driverService.getSingleDriver(driverId);
    }
    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/driver/{driver_id}")
    public boolean deleteDriver(@PathVariable("driver_id") int driverId) throws Exception{
        return driverService.deleteDriver(driverId);
    }
    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/driver/{driver_id}")
    public Response<Driver> updateDriver(@PathVariable("driver_id") int driverId, @RequestBody Driver driver) throws Exception{
        return driverService.updateDriver(driverId, driver);
    }
    /**
     * create/save novog vozaca i dodeljuje ga firmi
     */
    @PostMapping("/api/firm/{firm_id}/driver")
    public Response<Driver> addDriver(@RequestBody Driver driver, @PathVariable("firm_id") int firmId) throws Exception{
        return driverService.createDriver(driver,firmId);
    }
    /**
     * dodaje postojeceg vozaca grupi
     * @param firmId firma
     * @param driverId vozac
     * @param driverGroupId grupa
     * @return true/false
     */
    @PostMapping("/api/firm/{firm_id}/driver-group/driver/{driver_id}/{driver_group_id}/driver")
    public boolean addDriver(@PathVariable("firm_id") int firmId,@PathVariable("driver_id") int driverId, @PathVariable("driver_group_id") int driverGroupId) throws Exception{
        return driverService.addDriverToGroup(driverId,driverId);
    }
}