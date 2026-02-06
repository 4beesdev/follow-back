package rs.oris.back.controller;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Alarm;
import rs.oris.back.domain.dto.FuelDistanceRequest;
import rs.oris.back.domain.dto.daily_movement_consumption.DailyMovementConsumptionReportSmall;
import rs.oris.back.domain.reports.daily_movement_consumption.DailyMovementConsumptionReport;
import rs.oris.back.domain.reports.monthly_fuel.MonthlyFuelConsumptionReport;
import rs.oris.back.service.AlarmService;
import rs.oris.back.service.ReportService;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@CrossOrigin
public class IntegrationController {


    @Autowired
    private ReportService reportService;
    private String apiKey = "0802efcfeac23490c9a0c71972";
    List<String> imeiList = Arrays.asList(
            "862462034676733",
            "864547035760698",
            "862430053885113",
            "862430058895745",
            "860906046969199",
            "869867037858703",
            "357544378560425",
            "862430053887580",
            "350317178961714",
            "862430053882045",
            "862843044470052",
            "357454073648634",
            "860906046968209",
            "862430058885738",
            "353201355993129"
    );


    @PostMapping("/api/integration/fuel-and-distance")
    public ResponseEntity<?> getLatestVehiclesLocation(@RequestBody FuelDistanceRequest fuelDistanceRequest, @RequestHeader("api-key") String apiKey) {
        System.out.println("HIHIIHI");
        if (!apiKey.equals(this.apiKey))
            return ResponseEntity.badRequest().body("Invalid API key.");

        List<String> imeis = fuelDistanceRequest.getImeis();

        //if any of the imeis is not on the list, return bad request
        for (String imei : imeis) {
            if (!imeiList.contains(imei))
                return ResponseEntity.badRequest().body("Invalid imei: " + imei);
        }

        System.out.println(fuelDistanceRequest);

        LocalDateTime from = fuelDistanceRequest.getFrom();
        LocalDateTime to = fuelDistanceRequest.getTo();
        int fuelMargin = fuelDistanceRequest.getFuelMargin();
        int emptyingMargin = fuelDistanceRequest.getEmptyingMargin();

//        List<MonthlyFuelConsumptionReport> monthFuelReport = reportService.getMonthFuelReport(from, to, imeis, fuelMargin,emptyingMargin);
        List<DailyMovementConsumptionReport> dailyMovementConsumptionReport = reportService.getDailyMovementConsumptionReport(from, to, imeis, emptyingMargin, fuelMargin);



        List<DailyMovementConsumptionReportSmall> dailyMovementConsumptionReportSmalls = DailyMovementConsumptionReportSmall.fromList(dailyMovementConsumptionReport);

        // Sort the list by registration first and then by date
        dailyMovementConsumptionReportSmalls.sort(Comparator
                .comparing(DailyMovementConsumptionReportSmall::getRegistration)
                .thenComparing(DailyMovementConsumptionReportSmall::getDate));


        return ResponseEntity.ok(dailyMovementConsumptionReportSmalls);
    }


}
