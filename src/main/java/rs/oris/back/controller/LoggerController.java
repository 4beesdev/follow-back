package rs.oris.back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.oris.back.domain.dto.XlsImportLoggerDto;
import rs.oris.back.domain.dto.logger.AutomaticNotificaitonLoggerDTO;
import rs.oris.back.domain.dto.logger.AutomaticReportLoggerDTO;
import rs.oris.back.service.AutomaticNotificationLoggerService;
import rs.oris.back.service.AutomaticReportLoggerService;
import rs.oris.back.service.XlsImportLoggerService;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LoggerController {

    private final AutomaticReportLoggerService automaticReportLoggerService;
    private final AutomaticNotificationLoggerService automaticNotificationLoggerService;
    private final XlsImportLoggerService xlsImportLoggerService;
    //Automatski izvestaji
    @GetMapping("/automatic-reports")
    public ResponseEntity<AutomaticReportLoggerDTO> getLogsReports(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ){
        //Jer je stranica 1 u frontu prva a u springu je 0
        page--;
        AutomaticReportLoggerDTO logs = automaticReportLoggerService.getLogs(page, size);

        return ResponseEntity.ok(logs);
    }

    //XLS logovi
    @GetMapping("/xls-logs")
    public ResponseEntity<XlsImportLoggerDto> getXlsLogs(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ){
        //Jer je stranica 1 u frontu prva a u springu je 0
        page--;
        XlsImportLoggerDto logs = xlsImportLoggerService.getLogs(page, size);

        return ResponseEntity.ok(logs);
    }
    //Automatske notifikacije
    @GetMapping("/automatic-notifications")
    public ResponseEntity<AutomaticNotificaitonLoggerDTO> getLogsNotifications(
            @RequestParam("page") int page,
            @RequestParam("size") int size,

            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            @RequestParam("to")
            LocalDateTime to,

            @RequestParam(required = false)
            Integer firmId
    ){
        //Jer je stranica 1 u frontu prva a u springu je 0
        page--;
        AutomaticNotificaitonLoggerDTO logs = automaticNotificationLoggerService.getLogs(page, size,from,to,firmId);

        return ResponseEntity.ok(logs);
    }
}
