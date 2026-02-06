package rs.oris.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rs.oris.back.domain.logs.AutomaticNotificationLogger;
import rs.oris.back.domain.NotificationType;
import rs.oris.back.domain.dto.logger.AutomaticNotificaitonLoggerDTO;
import rs.oris.back.repository.AutomaticNotificationLoggerRepository;

import java.time.LocalDateTime;
import java.util.List;

//Automatic NOTIFICATION logger service
@Service
@RequiredArgsConstructor
public class AutomaticNotificationLoggerService {

    private final AutomaticNotificationLoggerRepository automaticNotificationLoggerRepository;

    //Sacuvaj successfully notification log
    public void saveSuccessNotificationLog(int firmId, String userName, NotificationType notificationType, String phoneNumber, String subject) {
        AutomaticNotificationLogger log = AutomaticNotificationLogger.builder()
                .phoneNumber(phoneNumber)
                .time(LocalDateTime.now())
                .notificationType(notificationType)
                .subject(subject)
                .provider(firmId)
                .userName(userName)
                .success(true)
                .build();
        saveLog(log);
    }

    private void saveLog(AutomaticNotificationLogger log) {
        automaticNotificationLoggerRepository.save(log);
    }
    //Sacuvaj fail notification log

    public void saveFailedNotificationLog(int firmId, String userName, NotificationType notificationType, String phoneNumber, String subject, String errorMessage) {
        AutomaticNotificationLogger log = AutomaticNotificationLogger.builder()
                .phoneNumber(phoneNumber)
                .time(LocalDateTime.now())
                .notificationType(notificationType)
                .subject(subject)
                .provider(firmId)
                .userName(userName)
                .success(false)
                .errorMessage(errorMessage)
                .build();
        saveLog(log);
    }

    //Vrati sve logove
    public AutomaticNotificaitonLoggerDTO getLogs(int page, int size, LocalDateTime from, LocalDateTime to, Integer firmId) {
        PageRequest of = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));

        List<AutomaticNotificationLogger> all;
        if (firmId != null)
            all = automaticNotificationLoggerRepository.findAllByTimeAfterAndTimeBeforeAndProvider(from, to, firmId.intValue(),of);
        else
            all = automaticNotificationLoggerRepository.findAllByTimeAfterAndTimeBefore(from, to, of);

        if(firmId!=null){
            Long count = automaticNotificationLoggerRepository.countAllByTimeAfterAndTimeBeforeAndProvider(from, to, firmId.intValue());
            Long successCount = automaticNotificationLoggerRepository.countAllByTimeAfterAndTimeBeforeAndProviderAndSuccess(from, to, firmId.intValue(), true);
            Long failedCount = count - successCount;
            AutomaticNotificaitonLoggerDTO automaticReportLoggerDTO = new AutomaticNotificaitonLoggerDTO(all, count, successCount, failedCount);
            return automaticReportLoggerDTO;
        }else {
            Long count = automaticNotificationLoggerRepository.countAllByTimeAfterAndTimeBefore(from, to);
            Long successCount = automaticNotificationLoggerRepository.countAllByTimeAfterAndTimeBeforeAndAndSuccess(from, to, true);
            Long failedCount = count - successCount;

            AutomaticNotificaitonLoggerDTO automaticReportLoggerDTO = new AutomaticNotificaitonLoggerDTO(all, count, successCount, failedCount);
            return automaticReportLoggerDTO;
        }

    }
}
