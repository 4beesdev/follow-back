package rs.oris.back.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.NotificationApp;
import rs.oris.back.domain.User;
import rs.oris.back.domain.VehicleGroup;
import rs.oris.back.domain.dto.PaginationDTO;
import rs.oris.back.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     *
     * vraca sve korisnike u firmi
     */
    @GetMapping("/api/user")
    public Response<Map<String, List<User>>> getAllUsers(@RequestHeader("Authorization") String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        user.setPassword(null);
        return userService.getAllUsers(user);
    }


//
//    @GetMapping("/api/firm/{firm_id}/user/admin")
//    public Response<Map<String, List<User>>> getAllAdmins() {
//        return userService.getAllAdmins();
//    }

    /**
     * vraca sve korisnike jedne firme
     */
    @GetMapping("/api/firm/{firm_id}/user")
    public Response<Map<String, List<User>>> getAllUsersForFirm(@PathVariable("firm_id") int firmId) {
        return userService.getAllUsersForFirm(firmId);
    }
    /**
     *
     * vraca sve admine firme
     */
    @GetMapping("/api/firm/{firm_id}/user/admin")
    public Response<Map<String, List<User>>> getAdminsForFirm(@PathVariable("firm_id") int firmId) {
        return userService.getAdminsForFirm(firmId);
    }
    /**
     *
     * vraca sve korisnike iz jedne grupe
     */
    @GetMapping("/api/firm/{firm_id}/group/{group_id}/user")
    public Response<Map<String, List<User>>> getAllUsersForGroup(@PathVariable("group_id") int groupId) {
        return userService.getAllUsersForGroup(groupId);
    }
    /**
     *
     * vraca podatke o trenutnom korisniku koji je pozvao ovu metodu
     */
    @GetMapping("/api/user/current")
    public Response<User> getCurrentUser(@RequestHeader("Authorization") String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());

        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        user.setPassword(null);
        return new Response<>(user);
    }
    /**
     *
     * azurira korisnika
     */
    @PutMapping("/api/firm/{firm_id}/user/{user_id}")
    public boolean updateUser(@PathVariable("user_id") int userId, @RequestBody User user) throws Exception {
        return userService.updateUser(userId, user);
    }
    /**
     *
     * uklanja korisnika iz grupe
     */
    @PutMapping("/api/firm/{firm_id}/user/{user_id}/group/remove")
    public Response<User> removeGroup(@PathVariable("user_id") int userId) throws Exception {
        return userService.removeGroup(userId);
    }
    /**
     * Cuva novog korisnika
     *
     * @param user novi korisnik
     * @throws Exception ako korisnk vec postoji ili ne moze da se sacuva
     */
    @PostMapping("/api/firm/{firm_id}/user/sign-up")
    public boolean signUp(@RequestBody User user) throws Exception {
        return userService.signUp(user);
    }
    /**
     *
     * postavlja korisnika u firmu
     */
    @PostMapping("/api/firm/{firm_id}/user")
    public Response<User> addUser(@RequestBody User user, @RequestHeader("Authorization") String auth, @PathVariable("firm_id") int firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user1 = userService.findByUsername(username);
        return userService.addNewUser(user, user1, firmId);
    }
    /**
     *
     * pravi novog superadmina
     */
    @PostMapping("/api/user/super")
    public Response<User> addSuperadmin(@RequestBody User user, @RequestHeader("Authorization") String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user1 = userService.findByUsername(username);
        return userService.addNewSuperadmin(user, user1);
    }
    /**
     * vraca sve superadmine
     */
    @GetMapping("/api/user/super")
    public Response<Map<String, List<User>>> getSuperadmin(@RequestHeader("Authorization") String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user1 = userService.findByUsername(username);
        return userService.getAllSA(user1);
    }

//    @PostMapping("/api/firm/{firm_id}/super")
//    public Response<User> addSuperadmin(@RequestBody User user) throws Exception{
//        return userService.addSuperadmin(user);
//    }
    /**
     *
     * dodaje postojeceg korisnika firmi
     */
    @PostMapping("/api/firm/{firm_id}/assign/user/{user_id}/group/{group_id}")
    public Response<User> addUserFormFirm(@PathVariable("user_id") int userId, @PathVariable("group_id") int groupId) throws Exception {
        return userService.addUserToFirm(userId, groupId);
    }
    /**
     *
     * uklanja korisnika iz grupe
     */
    @DeleteMapping("/api/firm/{firm_id}/user/{user_id}/group")
    public boolean deleteGroup(@PathVariable("user_id") int userId) throws Exception {
        return userService.deleteGroup(userId);
    }
    /**
     * azurira superadmina
     */
    @PutMapping("/api/firm/{firm_id}/user/{user_id}/super")
    public Response<User> updateSuperadmon(@PathVariable("user_id") int userId, @RequestHeader("Authorization") String auth, @RequestBody User usernew) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User usercurrent = userService.findByUsername(username);
        return userService.updateSuperadmin(userId, usercurrent, usernew);
    }


    /////////////////
    /**
     *
     * vraca korisnika po id-u
     */

    @GetMapping("/api/firm/{firm_id}/user/{user_id}")
    public Response<User> SuperAdminGetUser(@PathVariable("user_id") int userId) throws Exception {
        return userService.findById(userId);
    }
    /**
     * brise superadmina
     */
    @DeleteMapping("/api/user/{user_id}/super")
    public boolean deleteSuperadming(@PathVariable("user_id") int userId, @RequestHeader("Authorization") String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user1 = userService.findByUsername(username);
        return userService.deleteSuperAdmin(userId, user1);
    }
    /**
     *
     * brise korisnika iz baze ako je korisnik koji poziva operaciju ili superadmin
     * ili admin iz firme
     */
    @DeleteMapping("/api/firm/{firm_id}/user/{user_id}")
    public boolean deleteUser(@PathVariable("user_id") int userId, @RequestHeader("Authorization") String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User userCurrent = userService.findByUsername(username);
        return userService.deleteUser(userId, userCurrent);
    }

    /**
     *
     * vraca poslednjih 50 nepregledanih notifikacija korisnika
     */
    @GetMapping("/api/my/notif")
    public Response<List<NotificationApp>> getNotifisis(@RequestHeader("Authorization") String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User usercurrent = userService.findByUsername(username);
        return userService.getNotifs(usercurrent);
    }

    //NOTIFIKACIJE
    //Brise sve notifikacije za usera
    //Ovo su notifikacije  u onom delu navbar-a
    /**
     *
     * brise sve notifikacije korisnika
     */
    @Transactional
    @DeleteMapping("/api/my/notif/clear")
    public boolean clearMyNotifs(@RequestHeader("Authorization") String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User usercurrent = userService.findByUsername(username);
        return userService.clearNotifs(usercurrent);
    }

    //NOTIFIKACIJE
    //Na frontu je ovaj deo koda zakomentarisan,ovde se radi polling za notifikacije
    @GetMapping("/api/my/notif/history")
    public Response<List<NotificationApp>> getNotifisisHistory(@RequestHeader("Authorization") String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User usercurrent = userService.findByUsername(username);
        return userService.getNotifsHistory(usercurrent);
    }

    /**
     *
     * vraca sve notifikacije korisnika, sortirane obrnuto hronoloski
     */
    @GetMapping("/api/my/notif/history/top/{topNumber}")
    public Response<List<NotificationApp>> getNotifisisHistoryTopN(
            @RequestHeader("Authorization") String auth,
            @PathVariable("topNumber") Integer topNumber

    ) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User usercurrent = userService.findByUsername(username);
        return userService.getNotifsHistoryTopN(usercurrent,topNumber);
    }

    /**
     *
     * vraca sve notifikacije korisnika koje nije jos pregledao, sortirane obrnuto hronoloski
     */
    @GetMapping("/api/my/notif/history/{page}/{size}")
    public Response<PaginationDTO> getNotifisisHistoryPagination(
            @RequestHeader("Authorization") String auth,
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size

    ) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User usercurrent = userService.findByUsername(username);
        return userService.getNotifsHistoryPagination(usercurrent,page,size);
    }


    /**
     *
     * dodeljuje vehicle group korisniku
     */
    @PostMapping("/api/my/notif/seen")
    public Response<Boolean> getNotifisisSeen(@RequestHeader("Authorization") String auth) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User usercurrent = userService.findByUsername(username);
        return userService.getNotifsSeen(usercurrent);
    }
    /////////////////

    /**
     *
     * brisanje korisnika iz grupe vozila
     */
    @PostMapping("/api/firm/{firm_id}/user/{uid}/vehicle-group/{vid}/user-vehicle-group")
    public Response<Boolean> setVehicleGroupToUser(@RequestHeader("Authorization") String auth, @PathVariable("uid") int userId, @PathVariable("vid") int vehicleGroupId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User usercurrent = userService.findByUsername(username);
        return userService.setVehicleGroupToUser(usercurrent, userId, vehicleGroupId);
    }
    /**
     *
     * brisanje korisnika iz grupe vozila
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/user/{uid}/user-vehicle-group")
    public Response<Boolean> deleteVehGroupId(@RequestHeader("Authorization") String auth, @PathVariable("uid") int userId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User usercurrent = userService.findByUsername(username);
        return userService.deleteUVG(usercurrent, userId);
    }

    /**
     *
     * vraca vehicle groupe od korisnika
     */
    @GetMapping("/api/firm/{firm_id}/user/{user_id}/user-vehicle-group")
    public Response<Set<VehicleGroup>> getGroups(@RequestHeader("Authorization") String auth, @PathVariable("user_id") int userId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User usercurrent = userService.findByUsername(username);
        return userService.getVehGroupForUser(usercurrent, userId);
    }



}
