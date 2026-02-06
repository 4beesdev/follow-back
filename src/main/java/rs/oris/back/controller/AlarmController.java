package rs.oris.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Alarm;
import rs.oris.back.service.AlarmService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class AlarmController {


    @Autowired
    private AlarmService alarmService;

    /**
     * vraca sve alarme
     */
    @GetMapping("/api/firm/{firm_id}/alarm")
    public Response<Map<String, List<Alarm>>> getAllFirm() throws Exception {
        return alarmService.getAll();
    }

}
