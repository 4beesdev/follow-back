package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.config.security.AuthUtil;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Firm;

import rs.oris.back.domain.User;
import rs.oris.back.service.FirmService;
import rs.oris.back.service.UserService;


import java.util.List;
import java.util.Map;

@RestController
public class FirmController {

    @Autowired
    private FirmService firmService;
    @Autowired
    private UserService userService;

    /**
     * vraca sve aktivne firme ako je korisnik superadmin
     */
    @GetMapping("/api/firm")
    public Response<Map<String, List<Firm>>> getAllFirm() throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User userCurrent = userService.findByUsername(username);
        return firmService.getAllFirm(userCurrent);
    }
    /**
     * create/save
     */
    @PostMapping("/api/firm")
    public Response<Firm> addFirm(@RequestBody Firm firm) throws Exception{
        return firmService.addFirm(firm);
    }
    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}")
    public Response<Firm> updateFirm(@PathVariable("firm_id") int firmId, @RequestBody Firm firm) throws Exception{
        return firmService.updateFirm(firmId,firm);
    }
    /**
     * delete
     */
    @DeleteMapping("/api/firm/{firm_id}")
    public boolean deleteFirm(@PathVariable("firm_id") int firmId) throws Exception{
        String username = AuthUtil.getCurrentUsername();
        User userCurrent = userService.findByUsername(username);
        return firmService.deleteFirm(firmId,userCurrent);
    }



}
