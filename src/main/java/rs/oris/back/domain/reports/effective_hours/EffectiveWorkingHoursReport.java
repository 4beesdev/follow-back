package rs.oris.back.domain.reports.effective_hours;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class EffectiveWorkingHoursReport {
    private Long totalRpmInTime;
    private Long totalRpmOutTime;
    private List<EffectiveWorkingHoursReportData> reports;

}
