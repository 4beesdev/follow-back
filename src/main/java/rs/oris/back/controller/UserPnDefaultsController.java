package rs.oris.back.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.User;
import rs.oris.back.domain.dto.UserPnDefaultsDTO;
import rs.oris.back.service.UserPnDefaultsService;
import rs.oris.back.service.UserService;

@RestController
@CrossOrigin
public class UserPnDefaultsController {

    @Autowired
    private UserPnDefaultsService userPnDefaultsService;
    @Autowired
    private UserService userService;

    @GetMapping("/api/my/pn-defaults")
    public Response<UserPnDefaultsDTO> getMyPnDefaults(@RequestHeader("Authorization") String auth) throws Exception {
        return userPnDefaultsService.getForUser(getCurrentUser(auth));
    }

    @PutMapping("/api/my/pn-defaults")
    public Response<UserPnDefaultsDTO> updateMyPnDefaults(@RequestHeader("Authorization") String auth, @RequestBody UserPnDefaultsDTO userPnDefaultsDTO) throws Exception {
        return userPnDefaultsService.updateForUser(getCurrentUser(auth), userPnDefaultsDTO);
    }

    private User getCurrentUser(String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        return userService.findByUsername(username);
    }
}
