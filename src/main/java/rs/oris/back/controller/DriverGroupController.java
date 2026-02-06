package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.DriverGroup;
import rs.oris.back.service.DriverGroupService;
import rs.oris.back.controller.wrapper.Response;


import java.util.List;

@RestController
@CrossOrigin
public class DriverGroupController {

    @Autowired
    private DriverGroupService driverGroupService;
    /**
     * vraca sve grupe vozaca jedne firme
     */
    @GetMapping("api/firm/{firm_id}/driver-group")
    public Response<List<DriverGroup>> getAll(@PathVariable("firm_id") int firmId) throws Exception {
        return driverGroupService.getAllByFirmId(firmId);
    }
    /**
     * vraca jednu grupu vozaca po id-u
     * @param driverGroupId id
     */
    @GetMapping("api/firm/{firm_id}/driver-group/{id}")
    public Response<DriverGroup> getDriverGroupById(@PathVariable("id") int driverGroupId) throws Exception {
        return driverGroupService.getSingleDriverGroup(driverGroupId);
    }
    /**
     * delete
     */
    @DeleteMapping("/api/firm/{firm_id}/driverGroup/{driverGroup_id}")
    public boolean deleteDriverGroup(@PathVariable("driverGroup_id") int driverGroupId) throws Exception{
        return driverGroupService.deleteDriverGroup(driverGroupId);
    }

    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/driver-group/{driverGroup_id}")
    public Response<DriverGroup> updateDriverGroup(@PathVariable("driverGroup_id") int driverGroupId, @RequestBody DriverGroup driverGroup) throws Exception{
        return driverGroupService.updateDriverGroup(driverGroupId, driverGroup);
    }
    /**
     * create/save
     */
    @PostMapping("/api/firm/{firm_id}/driverGroup")
    public Response<DriverGroup> addDriverGroup(@RequestBody DriverGroup driverGroup, @PathVariable("firm_id") int firmId) throws Exception{
        return driverGroupService.createDriverGroup(driverGroup, firmId);
    }

}
