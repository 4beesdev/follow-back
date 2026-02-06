package rs.oris.back.domain.dto.logger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.logs.AutomaticReportLogger;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutomaticReportLoggerDTO {


    private List<AutomaticReportLogger> data;
    private Long size;
}
