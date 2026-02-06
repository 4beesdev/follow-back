package rs.oris.back.domain.dto.daily_movement_consumption;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.reports.daily_movement_consumption.DailyMovementConsumptionReport;
import rs.oris.back.export.annotations.PdfTableElement;
import rs.oris.back.export.annotations.Round;
import rs.oris.back.export.annotations.XlsTableElement;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyMovementConsumptionReportSmall {

    private String registration;

    private String date;


    private String startTime;

    private String endTime;

    @Round(roundTo = 2)
    private Double mileage;

    private Double totalFuelConsumption;


    public DailyMovementConsumptionReportSmall(DailyMovementConsumptionReport dailyMovementConsumptionReport) {
        this.registration = dailyMovementConsumptionReport.getRegistration();
        this.date = dailyMovementConsumptionReport.getDatum();
        this.startTime = dailyMovementConsumptionReport.getStartTime();
        this.endTime = dailyMovementConsumptionReport.getEndTime();
        this.mileage = dailyMovementConsumptionReport.getMileage();
        this.totalFuelConsumption = dailyMovementConsumptionReport.getTotalFuelConsumption();

    }

    public static List<DailyMovementConsumptionReportSmall> fromList(List<DailyMovementConsumptionReport> dailyMovementConsumptionReport) {
        List<DailyMovementConsumptionReportSmall> dailyMovementConsumptionReportSmalls = new ArrayList<>();

        for (DailyMovementConsumptionReport dailyMovementConsumptionReport1 : dailyMovementConsumptionReport) {
            dailyMovementConsumptionReportSmalls.add(new DailyMovementConsumptionReportSmall(dailyMovementConsumptionReport1));
        }

        return dailyMovementConsumptionReportSmalls;
    }
}
