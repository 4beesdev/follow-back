package rs.oris.back.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Firm;

import rs.oris.back.domain.User;
import rs.oris.back.service.FirmService;
import rs.oris.back.service.UserService;


import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class FirmController {

    @Autowired
    private FirmService firmService;
    @Autowired
    private UserService userService;

    /**
     * vraca sve aktivne firme ako je korisnik superadmin
     */
    @GetMapping("/api/firm")
    public Response<Map<String, List<Firm>>> getAllFirm(@RequestHeader("Authorization") String auth) throws Exception{
        /**
         * provera korisnika
         */
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
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
    public boolean deleteFirm(@PathVariable("firm_id") int firmId,@RequestHeader("Authorization") String auth) throws Exception{
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User userCurrent = userService.findByUsername(username);
        return firmService.deleteFirm(firmId,userCurrent);
    }



}
