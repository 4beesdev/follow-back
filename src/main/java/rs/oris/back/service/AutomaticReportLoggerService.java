package rs.oris.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rs.oris.back.domain.logs.AutomaticReportLogger;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.dto.logger.AutomaticReportLoggerDTO;
import rs.oris.back.domain.logs.LoggerFileUtil;
import rs.oris.back.repository.LoggerRepository;
import rs.oris.back.repository.VehicleRepository;

import java.time.LocalDateTime;
//Automatic REPORT logger service

@Service
@RequiredArgsConstructor
public class AutomaticReportLoggerService {

    private final LoggerRepository loggerRepository;
    private final VehicleRepository vehicleRepository;

    //Bezbedan lookup vozila po imei za potrebe logovanja. Vozila bez uredjaja imaju placeholder imei
    //('/', '', '\') koji deli vise stotina vozila - findByImei tada baca NonUniqueResultException
    //i obara i logovanje i slanje izvestaja. Logovanje ne sme da pukne zbog lookup-a registracije.
    private Vehicle findVehicleForLog(String imei) {
        if (imei == null || imei.trim().isEmpty() || "/".equals(imei.trim()) || "\\".equals(imei.trim())) {
            return null;
        }
        try {
            return vehicleRepository.findByImei(imei).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    //Sacuvaj successfully report log
    public void saveSuccessLog(String type,String email, String[] imeis){



        LocalDateTime now = LocalDateTime.now();
        for (String imei : imeis) {

            Vehicle vehicle = findVehicleForLog(imei);
            AutomaticReportLogger automaticReportLogger = AutomaticReportLogger.builder()
                    .email(email)
                    .success(true)
                    .time(now)
                    .imei(imei)
                    .type(type)
                    .registration(vehicle!=null? vehicle.getRegistration():"")
                    .build();
            loggerRepository.save(automaticReportLogger);

        }
    }
    //Sacuvaj fail report log
    public void saveFailLog(String type,String email, String[] imeis,String errorMessage){

        LocalDateTime now = LocalDateTime.now();
        for (String imei : imeis) {
            Vehicle vehicle = findVehicleForLog(imei);

            AutomaticReportLogger automaticReportLogger = AutomaticReportLogger.builder()
                    .email(email)
                    .success(false)
                    .time(now)
                    .imei(imei)
                    .type(type)
                    .registration(vehicle!=null? vehicle.getRegistration():"")
                    .errorMessage(errorMessage)
                    .build();
            loggerRepository.save(automaticReportLogger);

        }
    }

    //Vrati sve logove
    public AutomaticReportLoggerDTO getLogs(int page, int size){
        PageRequest of = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));

        Page<AutomaticReportLogger> all = loggerRepository.findAll(of);

        AutomaticReportLoggerDTO automaticReportLoggerDTO =new AutomaticReportLoggerDTO(all.getContent(),all.getTotalElements());
        return automaticReportLoggerDTO;
    }


    public void saveSuccessLogTXT(String type,String email, String[] imeis){
        LocalDateTime now = LocalDateTime.now();
        for (String imei : imeis) {
            Vehicle vehicle = findVehicleForLog(imei);
            AutomaticReportLogger automaticReportLogger = AutomaticReportLogger.builder()
                    .email(email)
                    .success(true)
                    .time(now)
                    .imei(imei)
                    .type(type)
                    .registration(vehicle != null ? vehicle.getRegistration() : "")
                    .build();
            loggerRepository.save(automaticReportLogger);

            // Zapisivanje u fajl
            LoggerFileUtil.logToFile("SUCCESS - type: " + type + ", email: " + email + ", imei: " + imei);
        }
    }

    //Sacuvaj fail report log
    public void saveFailLogTXT(String type,String email, String[] imeis,String errorMessage) {
        LocalDateTime now = LocalDateTime.now();
        for (String imei : imeis) {
            Vehicle vehicle = findVehicleForLog(imei);
            AutomaticReportLogger automaticReportLogger = AutomaticReportLogger.builder()
                    .email(email)
                    .success(false)
                    .time(now)
                    .imei(imei)
                    .type(type)
                    .registration(vehicle != null ? vehicle.getRegistration() : "")
                    .errorMessage(errorMessage)
                    .build();
            loggerRepository.save(automaticReportLogger);

            // Zapisivanje u fajl
            LoggerFileUtil.logToFile("FAIL - type: " + type + ", email: " + email + ", imei: " + imei + ", error: " + errorMessage);
        }
    }
}
