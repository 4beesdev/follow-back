package rs.oris.back.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.NotificationModel;
import rs.oris.back.domain.User;
import rs.oris.back.domain.UserReport;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.dto.DTOUserReport;
import rs.oris.back.service.ReportService;
import rs.oris.back.service.UserReportService;
import rs.oris.back.service.UserService;

import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
public class UserReportController {

    @Autowired
    private UserReportService userReportService;
    @Autowired
    private UserService userService;
    /**
     *
     * vraca sve user-report-ove firme
     */
    @GetMapping("/api/firm/{firm_id}/user-report")
    public Response<Map<String, Set<DTOUserReport>>> getAllReports(@RequestHeader("Authorization") String auth, @PathVariable("firm_id") int firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return userReportService.getAll(user,firmId);
    }

    @PostMapping("/api/notification-modal/update")
    public ResponseEntity<NotificationModel> updateReport(@RequestBody NotificationModel notificationModel) throws Exception {
        return ResponseEntity.ok(userReportService.updateNotificationModel(notificationModel));
    }
    /**
     *
     * cuva novi userReport
     */
    @PostMapping("/api/firm/{firm_id}/user-report")
    public Response<UserReport> addReport(@RequestHeader("Authorization") String auth, @RequestBody UserReport userReport, @PathVariable("firm_id") int firmId) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return userReportService.add(user, userReport,firmId);
    }
    /**
     *
     * azurira userReport
     */
    @PutMapping("/api/firm/{firm_id}/user-report/{id}")
    public Response<UserReport> updateReport(@RequestHeader("Authorization") String auth, @RequestBody UserReport userReport, @PathVariable("id") int id) throws Exception {
        String payload = auth.substring(auth.indexOf(".") + 1, auth.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user = userService.findByUsername(username);
        return userReportService.update(user, userReport, id);
    }
    /**
     *
     * brise jedan userReport
     */
    @DeleteMapping("api/firm/{firm_id}/user-report/{id}")
    public boolean deleteReport(@PathVariable("id") int id) throws Exception {
        return userReportService.delete(id);
    }
    /**
     * slanje mejla, verujem za testiranje
     */
    @GetMapping("/api/sendmejl")
    public boolean sendMail() throws Exception {
        return userReportService.sendMail();
    }



}
