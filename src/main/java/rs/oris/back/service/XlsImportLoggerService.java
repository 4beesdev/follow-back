package rs.oris.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rs.oris.back.domain.dto.XlsImportLoggerDto;
import rs.oris.back.domain.logs.XlsImportLogger;
import rs.oris.back.repository.XlsImportLoggerRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class XlsImportLoggerService {

    private final XlsImportLoggerRepository loggerRepository;

    //Sacuvaj uspesan log
    public void saveSuccessLog(String vehicleRegistration,String email){



        LocalDateTime now = LocalDateTime.now();

//            Vehicle vehicle = vehicleRepository.findByImei(imei).orElse(null);
            XlsImportLogger automaticReportLogger = XlsImportLogger.builder()
                    .success(true)
                    .time(now)
                    .email(email)
                    .registration(vehicleRegistration)
                    .build();
            loggerRepository.save(automaticReportLogger);


    }
    //Sacuvaj neuspesan log

    public void saveFailLog(String errorMessage, String email){

        LocalDateTime now = LocalDateTime.now();

            XlsImportLogger automaticReportLogger = XlsImportLogger.builder()
                    .success(false)
                    .time(now)
                    .email(email)
                    .errorMessage(errorMessage)
                    .build();
            loggerRepository.save(automaticReportLogger);


    }

    //Vrati sve logove po strani i velicini
    public XlsImportLoggerDto getLogs(int page, int size){
        PageRequest of = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));

        Page<XlsImportLogger> all = loggerRepository.findAll(of);

        XlsImportLoggerDto xlsImportLoggerDto =new XlsImportLoggerDto(all.getContent(),all.getTotalElements());
        return xlsImportLoggerDto;
    }

}
