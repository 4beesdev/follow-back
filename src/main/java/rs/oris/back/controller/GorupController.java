package rs.oris.back.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Group;
import rs.oris.back.domain.User;
import rs.oris.back.domain.dto.UpdateGroupDTO;
import rs.oris.back.service.GroupService;
import rs.oris.back.service.UserService;


import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class GorupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;

    /**
     * vraca sve grupe vezane za firmu
     */
    @GetMapping("/api/group")
    public Response<Map<String, List<Group>>> getAllGroups(@RequestHeader("Authorization") String auth, @PathVariable("firm_id") int firmId) throws Exception {
        /**
         * provera korisnika
         */
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);

        return groupService.getAllGroups(user, firmId);
    }
    /**
     * vraca jednu konkretnu grupu
     * @throws Exception ako grupa ne postoji ili ako korisnik nema auth da je vidi
     */
    @GetMapping("/api/firm/{firm_id}/group/{group_id}")
    public Response<Group> getAGroup(@RequestHeader("Authorization") String auth, @PathVariable("group_id") int groupId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);

        return groupService.getAGroup(user, groupId);
    }

    /**
     * provera korisnika
     */
    @GetMapping("/api/firm/{firm_id}/group")
    public Response<Map<String, List<Group>>> getAllGroupsForFirm(@PathVariable("firm_id") int firmId) {
        return groupService.getAllGroupsForFirm(firmId);
    }
    /**
     * vraca sve grupe jedne firme
     */
    @PostMapping("/api/firm/{firm_id}/group")
    public Response<Group> getAllGroupsForFirm(@RequestBody Group group, @RequestHeader("Authorization") String auth, @PathVariable("firm_id") int firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);

        return groupService.addGroupForAFirm(user, group, firmId);
    }
    /**
     * Cuva novu grupu i vezuje je za prosledjenu firmu
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/group/{group_id}/privilege")
    public boolean deleteAllPrivForGroup(@RequestHeader("Authorization") String auth, @PathVariable("group_id") int groupId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return groupService.deletePrivilages(user, groupId);
    }
    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/group/{group_id}")
    public boolean deleteGroup(@RequestHeader("Authorization") String auth, @PathVariable("group_id") int groupId, @PathVariable("firm_id") int firmId) throws Exception {
        /**
         * provera korisnika
         */
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return groupService.deleteGroup(user, groupId,firmId);
    }

    /**
     * update
     */
    @PutMapping("api/firm/{firm_id}/group/{group_id}")
    public Response<Group> updateGroup(@RequestHeader("Authorization") String auth, @PathVariable("group_id") int groupId, @RequestBody UpdateGroupDTO group) throws Exception {
     /**
      * provera korisnika
      */
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return groupService.updateGroup(user, groupId, group);
    }


}
