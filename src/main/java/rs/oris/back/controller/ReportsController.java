package rs.oris.back.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.oris.back.domain.reports.daily_movement_consumption.DailyMovementConsumptionReport;
import rs.oris.back.domain.reports.driver_relation.DriverRelationReport;
import rs.oris.back.domain.reports.driver_relation.DriverRelationReportData;
import rs.oris.back.domain.reports.driver_relation.DriverVehicleRelationReport;
import rs.oris.back.domain.reports.driver_relation.DriverVehicleRelationReportData;
import rs.oris.back.domain.reports.driver_relation_fuel.DriverRelationsFuelReport;
import rs.oris.back.domain.reports.effective_hours.EffectiveWorkingHoursReport;
import rs.oris.back.domain.reports.effective_hours.EffectiveWorkingHoursReportData;
import rs.oris.back.domain.reports.monthly_fuel.MonthlyFuelConsumptionReport;
import rs.oris.back.domain.reports.route.DriverRelationVehicleRoutingAllInfo;
import rs.oris.back.domain.reports.sensor_activation.SensorActivationReport;
import rs.oris.back.export.pdf.PdfExporter;
import rs.oris.back.export.pdf.impl.*;
import rs.oris.back.export.xml.XlsExporter;
import rs.oris.back.export.xml.impl.*;
import rs.oris.back.service.ReportService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportsController {

    private final ReportService reportsService;


    //Endpoint koji obradjuje zahtev za izvestaj o relacijama vozaca po vozilu za zadati period i za data vozila
    @GetMapping("/vehicle/driver-relations")
    public ResponseEntity<DriverVehicleRelationReport> getDriverRelationsReportByVehicle(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            @RequestParam("to") LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis,
            @RequestParam(name="minDistance")
            Double minDistance

    ) {
        //Pozovi servis metodu
        DriverVehicleRelationReport driverVehicleRelationReport = reportsService.getDriverRelationReportByVehicle(from, to, imeis,minDistance);
        return ResponseEntity.ok(driverVehicleRelationReport);
    }


    //Endpoint koji obradjuje zahtev za izvestaj o relacijama vozaca po vozacu za zadati period i za date vozace
    @GetMapping("/driver/driver-relations")
    public ResponseEntity<DriverRelationReport> getDriverRelationsReportByDriver(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            @RequestParam("to") LocalDateTime to,

            @RequestParam(name = "drivers")
            List<Integer> drivers,
            @RequestParam(name="minDistance")
            Double minDistance

    ) {

        //Pozovi servis metodu
        DriverRelationReport driverVehicleRelationReport = reportsService.getDriverRelationReportByDriver(from, to, drivers,minDistance);
        return ResponseEntity.ok(driverVehicleRelationReport);
    }


    //Endpoint koji obradjuje zahtev za izvestaj o relacijama vozaca po vozacu u xls formatu za zadati period i za date vozace
    @GetMapping("/driver/driver-relations/xls")
    public ResponseEntity<ByteArrayResource> exportDriverRelationsReportByDriverInXls(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            @RequestParam("to") LocalDateTime to,

            @RequestParam(name = "drivers")
            List<Integer> drivers,
            @RequestParam(name="minDistance")
            Double minDistance

    ) {

        //Pozovi servisnu metodu
        DriverRelationReport driverVehicleRelationReport = reportsService.getDriverRelationReportByDriver(from, to, drivers,minDistance);

        //Proveri da li postoji ime vozaca
        Optional<String> optionalDriverName = driverVehicleRelationReport.getReports().stream().filter(x -> x.getDrivernames().size() > 0).findFirst().map(x -> x.getDrivernames().get(0));
       //Ako postoji ime vozaca uzmi ga, ako ne uzmi prazan string
        String driverName = optionalDriverName.isPresent() ? optionalDriverName.get() : "";

        //Kreiraj hedere za xls fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Izvestaj o relacijama vozaca " + driverName + " " + from.toString() + " " + to.toString() + ".xls");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        //Kreiraj xls exporter i obradi podatke
        XlsExporter xlsExporter = new DriverDriverRelationReportXlsExporter(from, to);
        byte[] pdf = xlsExporter.export(driverVehicleRelationReport.getReports(), DriverRelationReportData.class, "Izveštaj o relacijama vozača");

        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    //Endpoint koji obradjuje zahtev za izvestaj o relacijama vozaca po vozacu u pdf formatu za zadati period i za date vozace
    @GetMapping("/driver/driver-relations/pdf")
    public ResponseEntity<ByteArrayResource> exportDriverRelationsReportByDriverInPdf(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            @RequestParam("to") LocalDateTime to,

            @RequestParam(name = "drivers")
            List<Integer> drivers,
            @RequestParam(name="minDistance")
            Double minDistance

    ) {


        //Pozovi servisnu metodu
        DriverRelationReport driverVehicleRelationReport = reportsService.getDriverRelationReportByDriver(from, to, drivers,minDistance);

        //Proveri da li postoji ime vozaca
        Optional<String> optionalDriverName = driverVehicleRelationReport.getReports().stream().filter(x -> x.getDrivernames().size() > 0).findFirst().map(x -> x.getDrivernames().get(0));
       //Ako postoji ime vozaca uzmi ga, ako ne uzmi prazan string
        String driverName = optionalDriverName.isPresent() ? optionalDriverName.get() : "";
      //Kreiraj hedere za pdf fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Izvestaj o relacijama vozaca " + driverName + " " + from.toString() + " " + to.toString() + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        //Kreiraj pdf exporter i obradi podatke
        PdfExporter pdfExporter = new PdfExporter(to, from);
        byte[] pdf = pdfExporter.export(driverVehicleRelationReport.getReports(), DriverRelationReportData.class, "Izveštaj o relacijama vozača");

        ByteArrayResource resource = new ByteArrayResource(pdf);


        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    //Endpoint koji obradjuje zahtev za izvestaj o relacijama vozaca po vozilu u pdf formatu za zadati period i za data vozila
    @GetMapping("/vehicle/driver-relations/pdf")
    public ResponseEntity<ByteArrayResource> exportDriverVehicleRelationsReportInPdf(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            @RequestParam("to") LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis,
            @RequestParam(name="minDistance")
            Double minDistance

    ) {

        //Pozovi servisnu metodu
        DriverVehicleRelationReport driverVehicleRelationReport = reportsService.getDriverRelationReportByVehicle(from, to, imeis,minDistance);
        //Proveri da li postoji ime vozila
        Optional<String> optionalRegistration = driverVehicleRelationReport.getReports().stream().filter(x -> x.getRegistration() != null && !x.getRegistration().isEmpty()).findFirst().map(x -> x.getRegistration());
        //Ako postoji ime vozila uzmi ga, ako ne uzmi prazan string
        String registration = optionalRegistration.isPresent() ? optionalRegistration.get() : "";
        //Kreiraj hedere za pdf fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Izvetaj o vozilima vozaca " + registration + " od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);


        //Kreiraj pdf exporter i obradi podatke
        PdfExporter pdfExporter = new DriverVehicleRelationsReportPdfExporter(from, to);
        byte[] pdf = pdfExporter.export(driverVehicleRelationReport.getReports(), DriverVehicleRelationReportData.class, "Izveštaj o relacijama vozaca");

        ByteArrayResource resource = new ByteArrayResource(pdf);


        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    //Endpoint koji obradjuje zahtev za izvestaj o relacijama vozaca po vozilu u xls formatu za zadati period i za data vozila
    @GetMapping("/vehicle/driver-relations/xls")
    public ResponseEntity<ByteArrayResource> exportDriverVehicleRelationsReportInXls(

            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            @RequestParam("to") LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis,
            @RequestParam(name="minDistance")
            Double minDistance
    ) {
        //Pozovi servisnu metodu
        DriverVehicleRelationReport driverRelationsReport = reportsService.getDriverRelationReportByVehicle(from, to, imeis,minDistance);
        //Proveri da li postoji ime vozila
        Optional<String> optionalRegistration = driverRelationsReport.getReports().stream().filter(x -> x.getRegistration() != null && !x.getRegistration().isEmpty()).findFirst().map(x -> x.getRegistration());
        //Ako postoji ime vozila uzmi ga, ako ne uzmi prazan string
        String registration = optionalRegistration.isPresent() ? optionalRegistration.get() : "";
        //Kreiraj hedere za xls fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Izvetaj o vozilima vozaca " + registration + " od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".xls");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        //Kreiraj xls exporter i obradi podatke
        XlsExporter xlsExporter = new DriverVehicleRelationsReportXlsExporter(from, to);
        byte[] pdf = xlsExporter.export(driverRelationsReport.getReports(), DriverVehicleRelationReportData.class, "Izveštaj o relacijama vozaca");

        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    //Endpoint koji vraca tacke kratanja vozila(imei) za zadati period
    @GetMapping("/vehicles/{imei}/path")
    public ResponseEntity<List<DriverRelationVehicleRoutingAllInfo>> getVehiclePath(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
            @RequestParam("to") LocalDateTime to,

            @PathVariable(name = "imei")
            String imeis
    ) {

        //Pozovi servisnu metodu
        List<DriverRelationVehicleRoutingAllInfo> vehiclePathForPeriod = reportsService.getVehiclePathForPeriod(from, to, imeis);
        return ResponseEntity.ok(vehiclePathForPeriod);
    }


    //Endpoint koji vraca izvestaj o relacijama  vozila-gorivo u periodu za zadata vozila,podaci se filtriraju po marginama za obracun goriva i marigini za utakanje/istakanje
    @GetMapping("/driver-relations-fuel")
    public ResponseEntity<List<DriverRelationsFuelReport>> driverRelationsFuelReport(

            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            @RequestParam("to") LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis,
            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin,
            @RequestParam(name = "emptyingMargin",defaultValue = "30")
            Integer emptyingMargin,
            @RequestParam(name="minDistance")
            Double minDistance
    ) {
        //Pozovi servisnu metodu
        List<DriverRelationsFuelReport> driverRelationsFuelReport = reportsService.getDriverRelationsFuelReport(from, to, imeis, fuelMargin,emptyingMargin,minDistance);

        return ResponseEntity.ok(driverRelationsFuelReport);
    }


    //Endpoint koji vraca PDF o relacijama  vozila-gorivo u periodu za zadata vozila,podaci se filtriraju po marginama za obracun goriva i marigini za utakanje/istakanje
//
    @GetMapping("/driver-relations-fuel/pdf")
    public ResponseEntity<ByteArrayResource> exportDriverRelationsFuelReportInPdf(

            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            @RequestParam("to") LocalDateTime to,
            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin,
            @RequestParam(name = "emptyingMargin",defaultValue = "30")
            Integer emptyingMargin,
            @RequestParam(name = "imeis")
            List<String> imeis,
            @RequestParam(name="minDistance")
            Double minDistance
    ) {
        //Pozovi servisnu metodu
        List<DriverRelationsFuelReport> driverRelationsFuelReport = reportsService.getDriverRelationsFuelReport(from, to, imeis, fuelMargin, emptyingMargin,minDistance);

        //Kreiraj hedere za pdf fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Izvestaj o relacijama vozila - gorivo od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);


//        //        //for testing
//        List<DriverRelationsFuelReport> reportsCopy = driverRelationsFuelReport.stream()
//                .map(report -> {
//                    DriverRelationsFuelReport driverRelationsFuelReport1 = new DriverRelationsFuelReport(
//                            "fewfw",
//                            report.getRegistration(),
//                            "wgwer",
//                            LocalDateTime.now(),
//                            "gfwe",
//                            LocalDateTime.now(),
//                            "ewr",
//                            20.0,10L,10L,10L,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0
//                    );
//                    driverRelationsFuelReport1.setRegistration((report.getRegistration())+ "-cao");
//                    return driverRelationsFuelReport1;
//                }).collect(Collectors.toList());
//
////
//        driverRelationsFuelReport.addAll(reportsCopy);
////        //---------------------------------


        //Kreiraj pdf exporter i obradi podatke
        PdfExporter pdfExporter = new DriverRelationsFuelReportPdfExporter(to, from);
        byte[] pdf = pdfExporter.export(driverRelationsFuelReport, DriverRelationsFuelReport.class, "Izvestaj o relacijama vozila - gorivo");
        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    //    //Endpoint koji vraca XLS o relacijama  vozila-gorivo u periodu za zadata vozila,podaci se filtriraju po marginama za obracun goriva i marigini za utakanje/istakanje
    @GetMapping("/driver-relations-fuel/xls")
    public ResponseEntity<ByteArrayResource> exportDriverRelationsFuelReportInXLS(

            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            @RequestParam("to") LocalDateTime to,
            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin,
            @RequestParam(name = "emptyingMargin",defaultValue = "30")
            Integer emptyingMargin,
            @RequestParam(name = "imeis")
            List<String> imeis,
            @RequestParam(name="minDistance")
            Double minDistance
    ) {
        //Pozovi servisnu metodu
        List<DriverRelationsFuelReport> driverRelationsFuelReport = reportsService.getDriverRelationsFuelReport(from, to, imeis, fuelMargin, emptyingMargin,minDistance);

        //Kreiraj hedere za xls fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Izvestaj o relacijama vozila - gorivo od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".xls");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);


//        //        //for testing
//        List<DriverRelationsFuelReport> reportsCopy = driverRelationsFuelReport.stream()
//                .map(report -> {
//                    DriverRelationsFuelReport driverRelationsFuelReport1 = new DriverRelationsFuelReport(
//                            "fewfw",
//                            report.getRegistration(),
//                            "wgwer",
//                            LocalDateTime.now(),
//                            "gfwe",
//                            LocalDateTime.now(),
//                            "ewr",
//                            20.0,10L,10L,10L,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0
//                    );
//                    driverRelationsFuelReport1.setRegistration((report.getRegistration())+ "-cao");
//                    return driverRelationsFuelReport1;
//                }).collect(Collectors.toList());
//
////
//        driverRelationsFuelReport.addAll(reportsCopy);
////        //---------------------------------
       //Kreiraj xls exporter i obradi podatke
        XlsExporter xlsExporter = new DriverRelationsFuelRpoertXLSExporter( from,to);
        byte[] pdf = xlsExporter.export(driverRelationsFuelReport, DriverRelationsFuelReport.class, "Izvestaj o relacijama vozila - gorivo");
        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    //Endpoint koji vraca izvestaj o aktivaciji senzora u nekom periodu za zadatata vozila(imei)
    @GetMapping("/sensor-activation")
    public ResponseEntity<List<SensorActivationReport>> getSensorActivationRport(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis
    ) {
        //Pozovi servisnu metodu
        List<SensorActivationReport> sensorActivationReport = reportsService.getSensorActivationReport(from, to, imeis);
        return ResponseEntity.ok(sensorActivationReport);

    }


    //Endpoint koji vraca PDF o aktivaciji senzora u nekom periodu za zadatata vozila(imei)

    @GetMapping("/sensor-activation/pdf")
    public ResponseEntity<ByteArrayResource> getSensorActivationRportExportPdf(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis
    ) {
        //Pozovi servisnu metodu
        List<SensorActivationReport> sensorActivationReport = reportsService.getSensorActivationReport(from, to, imeis);

        //Kreiraj hedere za pdf fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Izveštaj o aktivaciji senzora od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        log.info("####################################");
        log.info(LocalDateTime.now() + " - Generisanje PDF izvestaja o aktivaciji senzora za period od: " + from + " do: " + to + " za vozila: " + imeis);

        //Kreiraj pdf exporter i obradi podatke
        PdfExporter pdfExporter = new SensorsReportPdfExporter(to, from);
        byte[] pdf = pdfExporter.export(sensorActivationReport, SensorActivationReport.class, "Izveštaj o aktivaciji senzora");
        ByteArrayResource resource = new ByteArrayResource(pdf);

        log.info("####################################");
        log.info(LocalDateTime.now() + " - PDF izvestaj o aktivaciji senzora generisan za period od: " + from + " do: " + to + " za vozila: " + imeis);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    //Endpoint koji vraca XLS o aktivaciji senzora u nekom periodu za zadatata vozila(imei)

    @GetMapping("/sensor-activation/xls")
    public ResponseEntity<ByteArrayResource> getSensorActivationRportExportXls(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis
    ) {
        //Pozovi servisnu metodu
        List<SensorActivationReport> sensorActivationReport = reportsService.getSensorActivationReport(from, to, imeis);

        //Kreiraj hedere za xls fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Izveštaj o aktivaciji senzora od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".xls");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        log.info("####################################");
        log.info(LocalDateTime.now() + " - Generisanje XLS izvestaja o aktivaciji senzora za period od: " + from + " do: " + to + " za vozila: " + imeis);

        //Kreiraj xls exporter i obradi podatke
        XlsExporter xlsExporter = new SensorsReportXlsExporter(from, to);
        byte[] xls = xlsExporter.export(sensorActivationReport, SensorActivationReport.class, "Izveštaj o aktivaciji senzora");

        log.info("####################################");
        log.info(LocalDateTime.now() + " - XLS izvestaj o aktivaciji senzora generisan za period od: " + from + " do: " + to + " za vozila: " + imeis);

        ByteArrayResource resource = new ByteArrayResource(xls);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(xls.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    //Endpoint koji vraca mesecni PDF izvestaj u nekom periodu za zadatata vozila(imei) za zadatu marginu obracuna goriva i marginu za utakanje/istakanje

    @GetMapping("/monthly-fuel-reports/pdf")
    public ResponseEntity<ByteArrayResource> exportMonthlyFuelReportsInPdf(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,
            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin,
            @RequestParam(name = "emptyingMargin",defaultValue = "20")
            Integer emptyingMargin,

            @RequestParam(name = "imeis")
            List<String> imeis
    ) {
        //Pozovi servisnu metodu
        List<MonthlyFuelConsumptionReport> monthFuelReport = reportsService.getMonthFuelReport(from, to, imeis, fuelMargin, emptyingMargin);

        //Kreiraj hedere za pdf fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Mesecni izvestaj o potrosnji goriva od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);


        //Kreiraj pdf exporter i obradi podatke
        PdfExporter pdfExporter = new MonthlyReportPdfExporter(from, to);
        byte[] pdf = pdfExporter.export(monthFuelReport, MonthlyFuelConsumptionReport.class, "Mesecni izvestaj potrosnje goriva");

        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    //Endpoint koji vraca mesecni XLS izvestaj u nekom periodu za zadatata vozila(imei) za zadatu marginu obracuna goriva i marginu za utakanje/istakanje

    @GetMapping("/monthly-fuel-reports/xls")
    public ResponseEntity<byte[]> exportMonthlyFuelReportsInXls(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,

            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin,
            @RequestParam(name = "emptyingMargin",defaultValue = "20")
            Integer emptyingMargin,
            @RequestParam(name = "imeis")
            List<String> imeis
    ) {
        //Pozovi servisnu metodu
        List<MonthlyFuelConsumptionReport> monthFuelReport = reportsService.getMonthFuelReport(from, to, imeis, fuelMargin, emptyingMargin);

        //Kreiraj hedere za xls fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Mesecni izvestaj o potrosnji goriva od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".xls");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);


        //Kreiraj xls exporter i obradi podatke
        XlsExporter xlsExporter = new MontlyXlsExporter(from, to);
        byte[] xls = xlsExporter.export(monthFuelReport, MonthlyFuelConsumptionReport.class, "Mesecni izvestaj  potrosnje goriva");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(xls.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(xls);
    }
    //Endpoint koji vraca mesecni  izvestaj u nekom periodu za zadatata vozila(imei) za zadatu marginu obracuna goriva i marginu za utakanje/istakanje

    @GetMapping("/monthly-fuel-reports")
    public ResponseEntity<List<MonthlyFuelConsumptionReport>> getMonthlyFuelReports(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis,
            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin   ,
            @RequestParam(name = "emptyingMargin",defaultValue = "20")
            Integer emptyingMargin
    ) {
        //Pozovi servisnu metodu
        List<MonthlyFuelConsumptionReport> monthFuelReport = reportsService.getMonthFuelReport(from, to, imeis, fuelMargin,emptyingMargin);
        return ResponseEntity.ok(monthFuelReport);
    }

//Endpoint koji vraca izvestaj efektivnih radnih sati masine za odredjeni period po masinama za zadati RPM i marginu obracuna goriva i marginu za utakanje/istakanje
    @GetMapping("/effective_working_hours")
    public ResponseEntity<EffectiveWorkingHoursReport> getEffectiveWorkingHoursReport(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,
            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin,
            @RequestParam(name = "emptyingMargin",defaultValue = "30")
            Integer emptyingMargin,
            @RequestParam(name = "imeis")
            List<String> imeis,
            @RequestParam(name = "rpm")
            Integer rpm
    ) {
        //Pozovi servisnu metodu
        EffectiveWorkingHoursReport monthFuelReport = reportsService.getEffectiveWorkingHoursReport(from, to, imeis, rpm, fuelMargin,emptyingMargin);
        return ResponseEntity.ok(monthFuelReport);
    }


    //Endpoint koji vraca izvestaj PDF efektivnih radnih sati masine za odredjeni period po masinama za zadati RPM i marginu obracuna goriva i marginu za utakanje/istakanje

    @GetMapping("/effective_working_hours/pdf")
    public ResponseEntity<ByteArrayResource> exportEffectiveWorkingHoursInPdf(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis,
            @RequestParam(name = "emptyingMargin",defaultValue = "30")
            Integer emptyingMargin,
            @RequestParam(name = "rpm")
            Integer rpm,
            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin
    ) {
        //Pozovi servisnu metodu
        EffectiveWorkingHoursReport effectiveWorkingHoursReportData = reportsService.getEffectiveWorkingHoursReport(from, to, imeis, rpm, fuelMargin, emptyingMargin);

        //Kreiraj hedere za pdf fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Izvestaj efektivnih radnih sati od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

////        //for testing
//        List<EffectiveWorkingHoursReportData> reportsCopy = effectiveWorkingHoursReportData.getReports().stream()
//                .map(report -> new EffectiveWorkingHoursReportData(report.getRegistration() + "-cao", null,LocalDateTime.now(),LocalDateTime.now(),100L,100L,100L,100L,100L,10.0,10.0))
//                .collect(Collectors.toList());
////
//        effectiveWorkingHoursReportData.getReports().addAll(reportsCopy);
////        //---------------------------------

        //Kreiraj pdf exporter i obradi podatke
        PdfExporter pdfExporter = new EffectiveWokringHoursPdfExporter(to, from);

        byte[] pdf = pdfExporter.export(effectiveWorkingHoursReportData.getReports(), EffectiveWorkingHoursReportData.class, "Izvestaj efektivnih radnih sati");

        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    //Endpoint koji vraca izvestaj XLS efektivnih radnih sati masine za odredjeni period po masinama za zadati RPM i marginu obracuna goriva i marginu za utakanje/istakanje

    @GetMapping("/effective_working_hours/xls")
    public ResponseEntity<byte[]> exportEffectiveWorkingHoursInXls(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis,
            @RequestParam(name = "emptyingMargin",defaultValue = "30")
            Integer emptyingMargin,
            @RequestParam(name = "rpm")
            Integer rpm,
            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin
    ) {
        //Pozovi servisnu metodu
        EffectiveWorkingHoursReport monthFuelReport = reportsService.getEffectiveWorkingHoursReport(from, to, imeis, rpm, fuelMargin, emptyingMargin);


////        //for testing
//        List<EffectiveWorkingHoursReportData> reportsCopy = monthFuelReport.getReports().stream()
//                .map(report -> new EffectiveWorkingHoursReportData(report.getRegistration() + "-cao", null,LocalDateTime.now(),LocalDateTime.now(),100L,100L,100L,100L,100L,10.0,10.0))
//                .collect(Collectors.toList());
////
//        monthFuelReport.getReports().addAll(reportsCopy);
////        //---------------------------------


        //Kreiraj hedere za xls fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Izvestaj efektivnih radnih sati od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".xls");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        //Kreiraj xls exporter i obradi podatke
        XlsExporter xlsExporter = new EffectiveWorkingHoursXlsExporter(from, to);
        byte[] xls = xlsExporter.export(monthFuelReport.getReports(), EffectiveWorkingHoursReportData.class, "Izvestaj efektivnih radnih sati");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(xls.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(xls);
    }

    @GetMapping("/firm/{firmId}/daily-movement-consumption")
    public ResponseEntity< List<DailyMovementConsumptionReport>> getDailyMovementConsumptionReport(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis,

            @RequestParam(name = "emptyingMargin",defaultValue = "30")
            Integer emptyingMargin,

            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin
    ){

//        ZoneId zone = ZoneId.of("Europe/Belgrade");
//        from = from.atZone(zone).with(LocalTime.MIN).toLocalDateTime();
//        to   = to.atZone(zone).with(LocalTime.of(22, 59)).toLocalDateTime();
        from = from.with(LocalTime.MIN);
        to   = to.with(LocalTime.of(22, 59));

        List<DailyMovementConsumptionReport> dailyMovementConsumptionReport = reportsService.getDailyMovementConsumptionReport(from, to, imeis, emptyingMargin, fuelMargin);
        return ResponseEntity.ok(dailyMovementConsumptionReport);
    }
    @GetMapping("/firm/{firmId}/daily-movement-consumption/xls")
    public  ResponseEntity<ByteArrayResource> exportDailyMovementConsumptionReportXLS(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis,

            @RequestParam(name = "emptyingMargin",defaultValue = "30")
            Integer emptyingMargin,

            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin
    ){
        from=from.with(LocalTime.MIN);
        to = to.with(LocalTime.of(22, 59));
        //Pozovi servisnu metodu
        List<DailyMovementConsumptionReport> dailyMovementConsumptionReport = reportsService.getDailyMovementConsumptionReport(from, to, imeis, emptyingMargin, fuelMargin);

        //promeni datum - u .
        for (DailyMovementConsumptionReport movementConsumptionReport : dailyMovementConsumptionReport) {
            movementConsumptionReport.setDatum(movementConsumptionReport.getDatum().replace("-","."));
        }

//        //Kreiraj hedere za xls fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Dnevni izvešaj o kretanju i potrošnji goriva od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".xls");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
//
//        //Kreiraj xls exporter i obradi podatke
        XlsExporter xlsExporter = new DailyMovementConsumptionXlsExporter(from, to,emptyingMargin,fuelMargin);
        byte[] xls = xlsExporter.export(dailyMovementConsumptionReport, DailyMovementConsumptionReport.class, "Dnevni izvešaj o kretanju i potrošnji goriva");
        ByteArrayResource resource = new ByteArrayResource(xls);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(xls.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

    @GetMapping("/firm/{firmId}/daily-movement-consumption/pdf")
    public  ResponseEntity<ByteArrayResource> exportDailyMovementConsumptionReportPDF(
            @RequestParam("from")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime from,

            @RequestParam("to")
            @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
            LocalDateTime to,

            @RequestParam(name = "imeis")
            List<String> imeis,

            @RequestParam(name = "emptyingMargin",defaultValue = "30")
            Integer emptyingMargin,

            @RequestParam(name = "fuelMargin",defaultValue = "2")
            Integer fuelMargin
    ){
        from=from.with(LocalTime.MIN);
        to = to.with(LocalTime.of(22, 59));
        //Pozovi servisnu metodu
        List<DailyMovementConsumptionReport> dailyMovementConsumptionReport = reportsService.getDailyMovementConsumptionReport(from, to, imeis, emptyingMargin, fuelMargin);

        //promeni datum - u .
        for (DailyMovementConsumptionReport movementConsumptionReport : dailyMovementConsumptionReport) {
            movementConsumptionReport.setDatum(movementConsumptionReport.getDatum().replace("-","."));
        }


//        //Kreiraj hedere za xls fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Dnevni izvešaj o kretanju i potrošnji goriva od: " + from.toLocalDate() + " do: " + to.toLocalDate() + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
//
//        //Kreiraj xls exporter i obradi podatke
        PdfExporter pdfExporter = new DailyMovementConsumptionPdfExporter(from, to,emptyingMargin,fuelMargin);
        byte[] pdf = pdfExporter.export(dailyMovementConsumptionReport, DailyMovementConsumptionReport.class, "Dnevni izvešaj o kretanju i potrošnji goriva");
        ByteArrayResource resource = new ByteArrayResource(pdf);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }



}
