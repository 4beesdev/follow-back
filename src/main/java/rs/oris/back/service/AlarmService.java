package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.repository.AlarmRepository;
import rs.oris.back.domain.Alarm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlarmService {

    @Autowired
    private AlarmRepository alarmRepository;

    /**
     * vraca sve alarme
     */
    public Response<Map<String, List<Alarm>>> getAll() {
            List<Alarm> list = alarmRepository.findAll();
            Map<String, List<Alarm>> map = new HashMap<>();
            map.put("alarms", list);
            return new Response<>(map);
    }
}
