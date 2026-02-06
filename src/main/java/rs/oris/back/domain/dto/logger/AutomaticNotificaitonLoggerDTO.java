package rs.oris.back.domain.dto.logger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.logs.AutomaticNotificationLogger;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutomaticNotificaitonLoggerDTO {

    private List<AutomaticNotificationLogger> data;
    private Long size;
    private Long totalSuccess;
    private Long totalFailed;
}
