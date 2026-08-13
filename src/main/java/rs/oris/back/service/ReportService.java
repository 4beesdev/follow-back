package rs.oris.back.service;

import static rs.oris.back.util.DateUtil.toSerbianTimeZone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.domain.dto.DTORotue;
import rs.oris.back.domain.dto.DTOSpeed;
import rs.oris.back.domain.dto.Idle;
import rs.oris.back.domain.dto.daily_movement_consumption.DailyMovementConsumptionReportAddutionalDataDTO;
import rs.oris.back.domain.dto.report.driver_relation.DriverRelationProjectionData;
import rs.oris.back.domain.dto.report.driver_relation.DriverVehicleRelationProjectionData;
import rs.oris.back.domain.dto.report.driver_relation_fuel.DriverRelationFuelProjectionData;
import rs.oris.back.domain.dto.report.monthly.MonthFuelReportEngineDTO;
import rs.oris.back.domain.dto.reports.sensor.SensorActivtionVehicleDTO;
import rs.oris.back.domain.dto.route.DriverRelationVehicleRoutingInfo;
import rs.oris.back.domain.projection.ReportEngineProjection;
import rs.oris.back.domain.reports.daily_movement_consumption.DailyMovementConsumptionReport;
import rs.oris.back.domain.reports.driver_relation.DriverRelationReport;
import rs.oris.back.domain.reports.driver_relation.DriverRelationReportData;
import rs.oris.back.domain.reports.driver_relation.DriverVehicleRelationReport;
import rs.oris.back.domain.reports.driver_relation.DriverVehicleRelationReportData;
import rs.oris.back.domain.reports.driver_relation_fuel.DriverRelationsFuelReport;
import rs.oris.back.domain.reports.effective_hours.EffectiveWorkingHoursReport;
import rs.oris.back.domain.reports.fms_fuel_company_consumption.FmsFuelCompanyFuelConsumption;
import rs.oris.back.domain.reports.monthly_fuel.MonthlyFuelConsumptionReport;
import rs.oris.back.domain.reports.route.DriverRelationVehicleRoutingAllInfo;
import rs.oris.back.domain.reports.sensor_activation.SensorActivationReport;
import rs.oris.back.exceptions.VehicleNotFoundException;
import rs.oris.back.repository.DriverRepository;
import rs.oris.back.repository.FuelConsumptionRepository;
import rs.oris.back.repository.InterventionRepository;
import rs.oris.back.repository.KilometersAdministrationRepository;
import rs.oris.back.repository.PNRepository;
import rs.oris.back.repository.RegistrationRepository;
import rs.oris.back.repository.TicketRepository;
import rs.oris.back.repository.VehicleRepository;
import rs.oris.back.config.security.WebConfig;
import rs.oris.back.util.DateUtil;
import rs.oris.back.web.GalebRestComunication;


@Service
public class ReportService {
    public static final String FONT = "assets/fonts/reg.ttf";

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private InterventionRepository interventionRepository;
    @Autowired
    private FuelConsumptionRepository fuelConsumptionRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private PNRepository pnRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private KilometersAdministrationRepository kilometersAdministrationRepository;

    @Autowired
    private GalebRestComunication galebRestComunication;

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    //Metoda koja varca DriverRelationsFuelReport objekat koji sadrzi informacije o reportu

    public List<DriverRelationsFuelReport> getDriverRelationsFuelReport(LocalDateTime from, LocalDateTime to, List<String> imeis, Integer fuelMargin,
            Integer emptyingMargin, Double minDistance) {

        //Pronadji vozila iz postgres baze po imei polju
        List<ReportEngineProjection> byImeiIn = vehicleRepository.findByImeiIn(imeis);

        //Mapiraj ih u DriverRelationProjectionData objekat
        List<DriverRelationFuelProjectionData> mappedList = byImeiIn.stream().map(element -> new DriverRelationFuelProjectionData(
                        element.getImei(),
                        element.getFuelMargine() != null ? element.getFuelMargine() : 0,
                        element.getRegistration(),
                        (element.getModel() == null || element.getModel().isEmpty()) ? element.getManufacturer() : element.getModel()))
                .collect(Collectors.toList());

        //Pozovi metodu iz galebRestComunication koja poziva Mongo mikroservis
        return galebRestComunication.getDriverRelationsReportFuel(from, to, mappedList, fuelMargin, emptyingMargin, minDistance).getBody();

    }

    //Metoda koja varca DriverRelationReport objekat koji sadrzi informacije o reportu
    public DriverRelationReport getDriverRelationReportByDriver(LocalDateTime from, LocalDateTime to, List<Integer> driverIds, Double minDistance) {

        //Pronadji vozila iz postgres baze po imei polju
        List<Driver> drivers = driverRepository.findAllByDriverIdIn(driverIds);

        //Mapiraj ih u DriverRelationProjectionData objekat
        List<DriverRelationProjectionData> collect = drivers.stream()
                .map(driver -> new DriverRelationProjectionData(driver.getDriverId(), driver.getIdentificationNumber(), driver.getName()))
                .collect(Collectors.toList());

        //Pozovi metodu iz galebRestComunication koja poziva Mongo mikroservis

        DriverRelationReport result = galebRestComunication.getDriverRelationReport(from, to, collect, minDistance).getBody();

        //Dobijeni rezultat preradi tako da mapiras driver name
        for (DriverRelationReportData report : result.getReports()) {
            Optional<Vehicle> byImei = vehicleRepository.findByImei(report.getImei());
            if (byImei.isPresent()) {
                report.setRegistration(byImei.get().getRegistration());
                report.setModel(byImei.get().getModel());
            }
            if (report.getRfids() != null) {
                List<Driver> allByIdNumberIn = driverRepository.findAllByIdentificationNumberIn(report.getRfids());
                List<String> collect1 = allByIdNumberIn.stream().filter(Objects::nonNull).map(Driver::getName).collect(Collectors.toList());
                report.setDrivernames(collect1);
            }

        }
        return result;
    }

    //Metoda koja varca DriverVehicleRelationReport objekat koji sadrzi informacije o reportu
    public DriverVehicleRelationReport getDriverRelationReportByVehicle(LocalDateTime from, LocalDateTime to, List<String> imeis, Double minDistance) {
        //Pronadji vozila iz postgres baze po imei polju
        List<ReportEngineProjection> byImeiIn = vehicleRepository.findByImeiIn(imeis);

        //Mapiraj ih u DriverVehicleRelationProjectionData objekat
        List<DriverVehicleRelationProjectionData> mappedList = byImeiIn.stream().map(element -> {

                            return new DriverVehicleRelationProjectionData(
                                    element.getImei(),
                                    element.getRegistration(),
                                    (element.getModel() == null || element.getModel().isEmpty()) ? element.getManufacturer() : element.getModel()
                            );
                        }
                )
                .collect(Collectors.toList());

        //Pozovi metodu iz galebRestComunication koja poziva Mongo mikroservis
        DriverVehicleRelationReport body = galebRestComunication.getDriverVehicleRelationReport(from, to, mappedList, minDistance).getBody();

        //Dobijeni rezultat preradi tako da mapiras driver name
        for (DriverVehicleRelationReportData report : body.getReports()) {
            if (report.getRfids() != null && !report.getRfids().isEmpty()) {
                List<Driver> allByIdNumberIn = driverRepository.findAllByIdentificationNumberIn(report.getRfids());
                List<String> collect = allByIdNumberIn.stream().filter(Objects::nonNull).map(Driver::getName).collect(Collectors.toList());

                List<String> notFound = report.getRfids().stream().filter(x -> allByIdNumberIn.stream().noneMatch(y -> y.getIdentificationNumber().equals(x)))
                        .collect(Collectors.toList());

                collect.addAll(notFound);

                report.setDrivernames(collect);
            }

        }

        return body;
    }

    //Metoda koja varca DriverRelationVehicleRoutingAllInfo objekat koji sadrzi informacije o kretanju vozila
    public List<DriverRelationVehicleRoutingAllInfo> getVehiclePathForPeriod(LocalDateTime from, LocalDateTime to, String imei) {
        //Pronadji vozila iz postgres baze po imei polju
        List<ReportEngineProjection> byImeiIn = vehicleRepository.findByImeiIn(new LinkedList() {{
            push(imei);
        }});

        ReportEngineProjection vehicle = byImeiIn.get(0);
        if (vehicle == null) {
            throw new VehicleNotFoundException("Failed to find vehicle");
        }

        //Mapiranje podataka u  DriverRelationVehicleRoutingInfo
        DriverRelationVehicleRoutingInfo driverRelationVehicleRoutingInfo = new DriverRelationVehicleRoutingInfo(
                vehicle.getFuelMargine() != null ? vehicle.getFuelMargine() : 0, vehicle.getRegistration());
        //Poziva se metoda na drugom mikroserivsu koja obradjuje dalje
        return galebRestComunication.getVehicleRouting(driverRelationVehicleRoutingInfo, imei, from, to).getBody();
    }

    //Metoda koja varca MonthlyFuelConsumptionReport objekat koji sadrzi informacije o kretanju vozila
    public List<MonthlyFuelConsumptionReport> getMonthFuelReport(LocalDateTime from, LocalDateTime to, List<String> imeis, Integer fuelMargin,
            Integer emptyingMargin) {

        //Pronadji vozila iz postgres baze po imei polju
        List<ReportEngineProjection> byImeiIn = vehicleRepository.findByImeiIn(imeis);

        //Mapiranje podataka u  DriverRelationVehicleRoutingInfo
        List<MonthFuelReportEngineDTO> mappedList = byImeiIn.stream().map(element -> new MonthFuelReportEngineDTO(
                        element.getImei(),
                        element.getFuelMargine() != null ? element.getFuelMargine().doubleValue() : null,
                        element.getRegistration(),
                        (element.getModel() == null || element.getModel().isEmpty()) ? element.getManufacturer() : element.getModel()))
                .collect(Collectors.toList());

        long nonPositiveFuelMargineCount = mappedList.stream()
                .filter(element -> element.getFuelMargine() == null || element.getFuelMargine() <= 0)
                .count();
        log.info("Monthly fuel payload summary: from={}, to={}, imeiCount={}, fuelMargin={}, emptyingMargin={}, nonPositiveFuelMargineCount={}",
                from, to, mappedList.size(), fuelMargin, emptyingMargin, nonPositiveFuelMargineCount);
        if (nonPositiveFuelMargineCount > 0) {
            String badFuelMargineImeis = mappedList.stream()
                    .filter(element -> element.getFuelMargine() == null || element.getFuelMargine() <= 0)
                    .map(MonthFuelReportEngineDTO::getImei)
                    .limit(20)
                    .collect(Collectors.joining(","));
            log.warn("Monthly fuel payload contains non-positive fuelMargine for imeis={}", badFuelMargineImeis);
        }

        //Poziva se metoda na drugom mikroserivsu koja obradjuje dalje
        List<MonthlyFuelConsumptionReport> reportRows = galebRestComunication.getMonthlyFuelConsumptionReports(from, to, mappedList, fuelMargin, emptyingMargin).getBody();
        if (reportRows == null) {
            return Collections.emptyList();
        }

        for (MonthlyFuelConsumptionReport row : reportRows) {
            Long drivingTime = row.getDrivingTime();
            Long idleTime = row.getIdleTime();
            Double fuelSpent = row.getFuelSpent();

            long totalSeconds = (drivingTime != null ? drivingTime : 0L) + (idleTime != null ? idleTime : 0L);
            if (totalSeconds <= 0L || fuelSpent == null || fuelSpent <= 0.0) {
                row.setGetAverageFuelSpentPer1h(0.0);
                continue;
            }

            double totalHours = totalSeconds / 3600.0;
            row.setGetAverageFuelSpentPer1h(fuelSpent / totalHours);
        }

        return reportRows;

    }

    //Metoda koja varca SensorActivationReport objekat koji sadrzi informacije o kretanju vozila
    public List<SensorActivationReport> getSensorActivationReport(LocalDateTime from, LocalDateTime to, List<String> imeis) {
        //Pronadji vozila iz postgres baze po imei polju
        List<ReportEngineProjection> byImeiIn = vehicleRepository.findByImeiIn(imeis);

        //Mapiranje podataka u  DriverRelationVehicleRoutingInfo
        List<SensorActivtionVehicleDTO> mappedList = byImeiIn.stream().map(element -> new SensorActivtionVehicleDTO(
                        element.getImei(),
                        element.getRegistration(),
                        (element.getModel() == null || element.getModel().isEmpty()) ? element.getManufacturer() : element.getModel()))
                .collect(Collectors.toList());

        //Poziva se metoda na drugom mikroserivsu koja obradjuje dalje
        log.info("####################################");
        log.info(LocalDateTime.now() + " - Sending request for sensor activation report for imeis: " + imeis);
        ZoneId serbiaZone = ZoneId.of("Europe/Belgrade");
        Long fromEpochMs = from.atZone(serbiaZone).toInstant().toEpochMilli();
        Long toEpochMs = to.atZone(serbiaZone).toInstant().toEpochMilli();

        return galebRestComunication.getSensorActivationReports(fromEpochMs, toEpochMs, mappedList).getBody();
    }

    //Metoda koja varca EffectiveWorkingHoursReport objekat koji sadrzi informacije o kretanju vozila
    public EffectiveWorkingHoursReport getEffectiveWorkingHoursReport(LocalDateTime from, LocalDateTime to, List<String> imeis, Integer rpm, Integer fuelMargin,
            Integer emptyingMargin) {
        //Pronadji vozila iz postgres baze po imei polju
        List<ReportEngineProjection> byImeiIn = vehicleRepository.findByImeiIn(imeis);

        log.info("Effective working hours metadata lookup: requestedImeis={}, resolvedVehicleCount={}, from={}, to={}, rpm={}, fuelMargin={}, emptyingMargin={}",
                imeis, byImeiIn.size(), from, to, rpm, fuelMargin, emptyingMargin);

        //Mapiranje podataka u  DriverRelationVehicleRoutingInfo
        List<MonthFuelReportEngineDTO> mappedList = byImeiIn.stream().map(element -> new MonthFuelReportEngineDTO(
                        element.getImei(),
                        element.getFuelMargine() != null ? element.getFuelMargine().doubleValue() : null,
                        element.getRegistration(),
                        (element.getModel() == null || element.getModel().isEmpty()) ? element.getManufacturer() : element.getModel()))
                .collect(Collectors.toList());

        if (mappedList.isEmpty()) {
            log.warn("Effective working hours payload is empty because no vehicle metadata was found for requested imeis={}", imeis);
            return new EffectiveWorkingHoursReport(0L, 0L, Collections.emptyList());
        }

        long nonPositiveFuelMargineCount = mappedList.stream()
                .filter(element -> element.getFuelMargine() == null || element.getFuelMargine() <= 0)
                .count();
        log.info("Effective working hours payload summary: from={}, to={}, imeiCount={}, rpm={}, fuelMargin={}, emptyingMargin={}, nonPositiveFuelMargineCount={}",
                from, to, mappedList.size(), rpm, fuelMargin, emptyingMargin, nonPositiveFuelMargineCount);
        if (nonPositiveFuelMargineCount > 0) {
            String badFuelMargineImeis = mappedList.stream()
                    .filter(element -> element.getFuelMargine() == null || element.getFuelMargine() <= 0)
                    .map(MonthFuelReportEngineDTO::getImei)
                    .limit(20)
                    .collect(Collectors.joining(","));
            log.warn("Effective working hours payload contains non-positive fuelMargine for imeis={}", badFuelMargineImeis);
        }

        //Poziva se metoda na drugom mikroserivsu koja obradjuje dalje
        ResponseEntity<EffectiveWorkingHoursReport> response = galebRestComunication.getEffectiveWorkingHoursReport(from, to, mappedList, rpm, fuelMargin, emptyingMargin);
        EffectiveWorkingHoursReport report = response.getBody();
        if (report == null || report.getReports() == null || report.getReports().isEmpty()) {
            log.warn("Effective working hours service returned an empty response for imeis={}, from={}, to={}, rpm={}, fuelMargin={}, emptyingMargin={}. This will produce an empty report/export.",
                    mappedList.stream().map(MonthFuelReportEngineDTO::getImei).collect(Collectors.joining(",")),
                    from, to, rpm, fuelMargin, emptyingMargin);
            return new EffectiveWorkingHoursReport(0L, 0L, Collections.emptyList());
        }
        log.info("Effective working hours service returned {} report rows for imeis={}, from={}, to={}, rpm={}, fuelMargin={}, emptyingMargin={}",
                report.getReports().size(),
                mappedList.stream().map(MonthFuelReportEngineDTO::getImei).collect(Collectors.joining(",")),
                from, to, rpm, fuelMargin, emptyingMargin);
        return report;
    }

    /**
     * izrada izvestaja o predjenom putu
     */
    public String ipp(String res, String imei, Vehicle v, String from, String to) throws Exception {
        if (res.length() < 10) {
            return "";
        }

        return res;
    }

    public String ippm(String res, String imei, Vehicle v) throws Exception {
        if (res.length() < 10) {
            return "";
        }
        return res;
    }

    public String ippmh(String res, String imei, Vehicle v) throws Exception {
        if (res.length() < 10) {
            return "";
        }
        return res;
    }

    public Response<List<DTOSpeed>> speed(String res, String imei, Vehicle v) throws Exception {
        //        if (res.length() < 20) {
        //            return null;
        //        }

        Optional<Vehicle> optionalVehicle = vehicleRepository.findByImei(imei);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Failed to find vehicle");
        }

        ObjectMapper mapper = new ObjectMapper();
        List<Gs100> list2 = mapper.readValue(res.substring(8, res.length() - 1), new TypeReference<List<Gs100>>() {
        });

        List<DTOSpeed> responseList = new ArrayList<>();

        responseList.sort(Comparator.comparing(DTOSpeed::getStartTimestamp));
        boolean spree = true;
        List<Gs100> gpsList = new ArrayList<>();

        //filter ones with same TS
        long currentTimestamp = 0;

        List<Gs100> list = new ArrayList<>();

        for (Gs100 gs100 : list2) {
            if (currentTimestamp == gs100.getGps().getTimestamp().getTime()) {
                continue;
            }
            currentTimestamp = gs100.getGps().getTimestamp().getTime();
            list.add(gs100);
        }

        int listLength = list.size();

        DTOSpeed dtoSpeed = null;
        //        private Timestamp startTimestamp;
        //        private Timestamp endTimestamp;
        //        private double roadTraveled;
        //        private double timeOfTravel;
        //        private double maxSpeed;
        //        private double avgSpeed;
        //        private boolean peak;
        //        private List<Gps> gpsList;

        System.out.println("_-----------------");
        System.out.println("LISTA " + list.size());
        System.out.println("_-----------------");

        int streakNum = 1;

        for (int i = 0; i < listLength; i++) {

            if (i == 0) {
                dtoSpeed = new DTOSpeed();
                gpsList = new ArrayList<>();
                dtoSpeed.setStartTimestamp(list.get(i).getGps().getTimestamp());
                dtoSpeed.setMaxSpeed(list.get(i).getGps().getSpeed());
                gpsList.add(list.get(i));
                continue;
            }

            int j = i - 1;

            if (Math.abs(list.get(i).getGps().getTimestamp().getTime() - list.get(j).getGps().getTimestamp().getTime()) > 25000) {
                //KRAJ

                if (streakNum <= 1) {//PEAK
                    dtoSpeed.setPeak(true);
                }

                dtoSpeed.setGpsList(new ArrayList<>(gpsList));
                responseList.add(new DTOSpeed(dtoSpeed));

                //RESET
                dtoSpeed = new DTOSpeed();
                gpsList = new ArrayList<>();
                dtoSpeed.setStartTimestamp(list.get(i).getGps().getTimestamp());
                dtoSpeed.setMaxSpeed(list.get(i).getGps().getSpeed());
                gpsList.add(list.get(i));

                streakNum = 0;

            } else {

                double cc = Math.abs(distance1(list.get(i).getGps().getLat(), list.get(i).getGps().getLng(),
                        list.get(j).getGps().getLat(), list.get(j).getGps().getLng()));
                if (!Double.isNaN(cc))
                    dtoSpeed.setRoadTraveled(dtoSpeed.getRoadTraveled() + cc);
                gpsList.add(list.get(i));

                dtoSpeed.setEndTimestamp(list.get(i).getGps().getTimestamp());

                dtoSpeed.setTimeOfTravel(
                        dtoSpeed.getTimeOfTravel() + Math.abs(list.get(i).getGps().getTimestamp().getTime() - list.get(j).getGps().getTimestamp().getTime()));
                //dtoSpeed.setAvgSpeed((dtoSpeed.getAvgSpeed() + list.get(i).getGps().getSpeed()) / gpsList.size());

                if (list.get(i).getGps().getSpeed() > dtoSpeed.getMaxSpeed()) {
                    dtoSpeed.setMaxSpeed(list.get(i).getGps().getSpeed());
                }

                streakNum++;
            }
        }

        for (DTOSpeed dtoSpeed1 : responseList) {
            double sum = 0;
            for (Gs100 gps : dtoSpeed1.getGpsList()) {
                sum += gps.getGps().getSpeed();
            }
            dtoSpeed1.setAvgSpeed(sum / dtoSpeed1.getGpsList().size());
        }

        return new Response<>(responseList);
    }

    public List<DTOSpeed> speed2(String res, List<String> imeis) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        //System.out.println(res);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        mapper.setDateFormat(df);
        mapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        Map<String, List<Gs100>> allPointsMap = mapper.readValue(res, new TypeReference<Map<String, List<Gs100>>>() {});


        List<DTOSpeed> result = new ArrayList<>();

        for (String imei : imeis) {
//            List<Gs100> points;
//
//            if (allPointsMap.containsKey("data")) {
//                points = allPointsMap.get("data");
//            } else {
//                points = allPointsMap.getOrDefault(imei, Collections.emptyList());
//            }
//
//            if (points == null || points.isEmpty()) continue;
            List<Gs100> points = allPointsMap.getOrDefault(imei, Collections.emptyList())
                    .stream()
                    .sorted(Comparator.comparing(g -> g.getGps().getTimestamp()))
                    .collect(Collectors.toList());

            if (points.isEmpty()) continue;

            List<Gs100> list = new ArrayList<>();
            long currentTs = 0L;
            for (Gs100 gs100 : points) {
                long ts = gs100.getGps().getTimestamp().getTime();
                if (currentTs == ts) continue;
                list.add(gs100);
                currentTs = ts;

            }

            int n = list.size();
            if (n == 0) continue;

            DTOSpeed seg = null;
            List<Gs100> segGps = new ArrayList<>();
            int streakNum = 1;
            list.sort(Comparator.comparing(g -> g.getGps().getTimestamp()));
            for (int i = 0; i < n; i++) {
                if (i == 0) {
                    seg = new DTOSpeed();
                    seg.setImei(imei);
                    segGps = new ArrayList<>();
                    //seg.setStartTimestamp(list.get(i).getGps().getTimestamp());
                    seg.setStartTimestamp(Timestamp.from(list.get(i).getGps().getTimestamp().toInstant()));
                    seg.setMaxSpeed(list.get(i).getGps().getSpeed());
                    segGps.add(list.get(i));
                    continue;
                }

                int j = i - 1;
                long tsi = list.get(i).getGps().getTimestamp().getTime();
                long tsj = list.get(j).getGps().getTimestamp().getTime();
                long delta = Math.abs(tsi - tsj);

                if (delta > 25_000) {
                    // kraj segmenta - identično staroj metodi
                    if (streakNum <= 1) seg.setPeak(true);
                    seg.setGpsList(new ArrayList<>(segGps));
                    //result.add(new DTOSpeed(seg));
                    result.add(seg);
//                    if (seg.getTimeOfTravel() / 1000 >= 60) {
//                        result.add(new DTOSpeed(seg));
//                    }

                    // reset
                    seg = new DTOSpeed();
                    seg.setImei(imei);
                    segGps = new ArrayList<>();
                    seg.setStartTimestamp(list.get(i).getGps().getTimestamp());
                    seg.setMaxSpeed(list.get(i).getGps().getSpeed());
                    segGps.add(list.get(i));
                    streakNum = 0;
                } else {
                    double d = Math.abs(distance1(
                            list.get(i).getGps().getLat(), list.get(i).getGps().getLng(),
                            list.get(j).getGps().getLat(), list.get(j).getGps().getLng()
                    ));
                    if (!Double.isNaN(d)) seg.setRoadTraveled(seg.getRoadTraveled() + d);

                    segGps.add(list.get(i));
                    //seg.setEndTimestamp(list.get(i).getGps().getTimestamp());
                    seg.setEndTimestamp(Timestamp.from(list.get(i).getGps().getTimestamp().toInstant()));
                    seg.setTimeOfTravel(seg.getTimeOfTravel() + delta);

                    if (list.get(i).getGps().getSpeed() > seg.getMaxSpeed()) {
                        seg.setMaxSpeed(list.get(i).getGps().getSpeed());
                    }
                    streakNum++;
                }
            }

            // Napomena: kao u staroj metodi, POSLEDNJI segment se ne zatvara automatski
            // Ako želiš da ga dodaš, otkomentariši:

        if (seg != null && !segGps.isEmpty()) {
            if (streakNum <= 1) seg.setPeak(true);
            seg.setGpsList(new ArrayList<>(segGps));
            //result.add(new DTOSpeed(seg));
            result.add(seg);
//            if (seg.getTimeOfTravel() / 1000 >= 60) { // filter kratkih segmenata
//                result.add(new DTOSpeed(seg));
//            }
        }

        }

        // izračunaj avgSpeed za zatvorene segmente (kao u staroj metodi)
        for (DTOSpeed s : result) {
            List<Gs100> gpsList = s.getGpsList();
            if (gpsList != null && !gpsList.isEmpty()) {
                double sum = 0.0;
                for (Gs100 g : gpsList) sum += g.getGps().getSpeed();
                s.setAvgSpeed(sum / gpsList.size());
            }
        }

        // opciono, možeš sortirati segmente tek sada (stara metoda je imala sort pre nego što je lista uopšte bila napunjena,
        // što je efektivno NO-OP); ostavi bez sortiranja za “byte-to-byte” izlaz.
        // result.sort(Comparator.comparing(DTOSpeed::getStartTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));


        return result;

    }


    /**
     * proverava duzinu ulaznog stringa i vraca predefinisani string ako nije odgovarajuca
     */
    public String route(String res, String imei, Vehicle v) {
        if (res.length() < 10) {
            return "{\"data\":[]}";
        }
        return res;
    }

    /**
     * Exportuje izvestaj o rutama u Excel Workbook, pretvara ga u
     * byte array i vraca
     *
     * @param export =2 znaci vrati pdf, u suprotnom workbook
     */
    public byte[] routeExport(String res, String imei, Vehicle v, int export, String fromS, String toS, String firmName) throws Exception {
        if (res.length() < 20) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        List<RouteReport> list = mapper.readValue(res.substring(8, res.length() - 1), new TypeReference<List<RouteReport>>() {
        });

        list.forEach(x -> {
            if (x.getBoardKmDiffStartEnd() == null)
                x.setBoardKmDiffStartEnd(0.0);
            if (x.getEndBoardInKm() == null)
                x.setEndBoardInKm(0.0);
            if (x.getStartBoardInKm() == null)
                x.setStartBoardInKm(0.0);
            if (x.getEndEngineWorkTimeInHours() == null)
                x.setEndEngineWorkTimeInHours(0.0);
            if (x.getStartEngineWorkTimeInHours() == null)
                x.setStartEngineWorkTimeInHours(0.0);
            if (x.getDiffEngineWorkTimeInHours() == null)
                x.setDiffEngineWorkTimeInHours(0.0);
        });

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Relacije");

        DateTimeFormatter periodFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String period = "Od: " + LocalDate.parse(fromS).format(periodFmt) + "  Do: " + LocalDate.parse(toS).format(periodFmt);
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o relacijama vozila", firmName, period);
        int tableStartRow = rowCount;

        XSSFCellStyle dateCellStyle2 = workbook.createCellStyle();
        dateCellStyle2.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 223, 227)));

        XSSFCellStyle locationStyle = workbook.createCellStyle();
        locationStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        locationStyle.setAlignment(HorizontalAlignment.CENTER);
        locationStyle.setWrapText(true);

        XSSFCellStyle rest = workbook.createCellStyle();
        rest.setVerticalAlignment(VerticalAlignment.CENTER);
        rest.setAlignment(HorizontalAlignment.LEFT);

        XSSFCellStyle upperStyle = workbook.createCellStyle();
        upperStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        upperStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font2 = workbook.createFont();
        font2.setFontHeightInPoints((short) 11);
        font2.setBold(true);
        upperStyle.setFont(font2);
        upperStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        upperStyle.setAlignment(HorizontalAlignment.CENTER);
        upperStyle.setWrapText(true);

        XSSFCellStyle titleUpperStyle = workbook.createCellStyle();
        titleUpperStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        titleUpperStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleUpperStyle.setFont(font2);
        titleUpperStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleUpperStyle.setAlignment(HorizontalAlignment.CENTER);
        titleUpperStyle.setWrapText(true);

        int cellCount = 0;
        XSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(222, 222, 222)));
        textStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));

        Row row = sheet.createRow(rowCount++);
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.setHeightInPoints(70);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 10 * 256);
        row.getCell(cellCount++).setCellValue("Broj reg.");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 12 * 256);
        row.getCell(cellCount++).setCellValue("Vreme početka");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 20 * 256);
        row.getCell(cellCount++).setCellValue("Početna lokacija");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 12 * 256);
        row.getCell(cellCount++).setCellValue("Vreme kraja");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 20 * 256);
        row.getCell(cellCount++).setCellValue("Krajnja lokacija");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Pređeni put");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 10 * 256);
        row.getCell(cellCount++).setCellValue("Ukupno vreme");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 10 * 256);
        row.getCell(cellCount++).setCellValue("Vreme mirovanja");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Prosečna brzina");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Maks. brzina");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Km početak");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Km kraj");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Km ken");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 8 * 256);
        row.getCell(cellCount++).setCellValue("Radni sati početak");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 8 * 256);
        row.getCell(cellCount++).setCellValue("Radni sati kraj");

        row.createCell(cellCount);
        row.setHeightInPoints(50);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 8 * 256);
        row.getCell(cellCount++).setCellValue("Radni sati");

        rowCount++;
        cellCount = 0;

        //Dodaj podatke u celijama

        double sumPredjeniPut = 0.0;
        double sumUkupnoVreme = 0.0;
        double sumVremeMirovanja = 0.0;
        double sumKilometrazaKen = 0.0;
        double sumRadniSati = 0.0;

        for (RouteReport r : list) {
            row = sheet.createRow(rowCount);

            Date date = new Date(r.getTimestampStart());
            Date dateend = new Date(r.getTimestampEnd());
            row.createCell(cellCount);
            row.setHeightInPoints(45);
            row.getCell(cellCount).setCellStyle(locationStyle);
            row.getCell(cellCount++).setCellValue(v.getRegistration());

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(locationStyle);
            row.getCell(cellCount++).setCellValue(df.format(date));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(locationStyle);
            row.getCell(cellCount++).setCellValue(r.getStartAddress());

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(locationStyle);
            row.getCell(cellCount++).setCellValue(df.format(dateend));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(locationStyle);
            row.getCell(cellCount++).setCellValue(r.getEndAddress());

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getRoadTraveled()));
            sumPredjeniPut += r.getRoadTraveled();

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(formatSeconds((int) r.getTime() / 1000));
            sumUkupnoVreme += r.getTime() / 1000;

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(formatSeconds((int) r.getIdleTime() / 1000));
            sumVremeMirovanja += r.getIdleTime() / 1000;

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getAverageSpeed()));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.2f", r.getMaxSpeed()));
            //
            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getStartBoardInKm()));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getEndBoardInKm()));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getBoardKmDiffStartEnd()));
            sumKilometrazaKen += r.getBoardKmDiffStartEnd();

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getStartEngineWorkTimeInHours()));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getEndEngineWorkTimeInHours()));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getDiffEngineWorkTimeInHours()));

            sumRadniSati += r.getDiffEngineWorkTimeInHours();

            cellCount = 0;
            rowCount++;
        }

        //        sumKilometrazaKen/=list.size();
        //        sumRadniSati/=list.size();
        //        sumPredjeniPut/=list.size();
        //        sumUkupnoVreme/=list.size();
        //        sumVremeMirovanja/=list.size();

        XSSFCellStyle ukupnoStyle = workbook.createCellStyle();
        ukupnoStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240))); // Light gray background
        ukupnoStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        ukupnoStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        ukupnoStyle.setAlignment(HorizontalAlignment.CENTER);
        ukupnoStyle.setWrapText(true);

        XSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldFont.setFontHeightInPoints((short) 11);
        ukupnoStyle.setFont(boldFont);

        row = sheet.createRow(++rowCount);

        cellCount = 0;

        row.createCell(cellCount);
        row.setHeightInPoints(75);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 8 * 256);
        row.getCell(cellCount++).setCellValue("Ukupno");

        //Empty
        //Vreme pocetka
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        if (!list.isEmpty())
            row.getCell(cellCount++).setCellValue(df.format(new Date(list.get(0).getTimestampStart())));
        else
            row.getCell(cellCount++).setCellValue("");

        //Empty
        //Pocetna lokacija
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        if (!list.isEmpty())
            row.getCell(cellCount++).setCellValue(list.get(0).getStartAddress());
        else
            row.getCell(cellCount++).setCellValue("");

        //Empty
        //Vreme kraja
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        if (!list.isEmpty())
            row.getCell(cellCount++).setCellValue(df.format(new Date(list.get(list.size() - 1).getTimestampEnd())));
        else
            row.getCell(cellCount++).setCellValue("");

        //Empty
        //Krajnja lokacija
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        if (!list.isEmpty())
            row.getCell(cellCount++).setCellValue(list.get(list.size() - 1).getEndAddress());
        else
            row.getCell(cellCount++).setCellValue("");

        //Predjeni put
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue(String.format("%.1f", sumPredjeniPut));

        //Ukupno vreme
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue(formatSeconds(Integer.valueOf((int) sumUkupnoVreme)));

        //Vreme mirovanja
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue(formatSeconds(Integer.valueOf((int) sumVremeMirovanja)));

        //Prosecna brzina
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Maksimalna brzina
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Empty
        //Km pocetak
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Km kraj
        //Empty
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Km ken
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue(String.format("%.1f", sumKilometrazaKen));

        //Empty
        //Radni sati pocetak
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Empty
        //Radni sati kraj
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Radni sati
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue(String.format("%.1f", sumRadniSati));

        for (int i = 0; i < 2; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception e) {
            }
        }

        //
        //        for (int i = 0; i < 10; i++) {
        //            try {
        //                sheet.autoSizeColumn(i);
        //            } catch (Exception e) {
        //            }
        //        }
        //
        //        String s = "";
        //        Date d = new Date();
        //        int x = d.getSeconds();
        //        int x2 = d.getMinutes();
        //        s = "excel" + x + "" + x2 + ".xlsx";
        //
        //
        //        try (FileOutputStream outputStream = new FileOutputStream(s)) {
        //            workbook.write(outputStream);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        centerDataCells(sheet, tableStartRow);

        //Ako je export 2 onda se radi pdf export
        if (export == 2) {
            return getPdf(workbook, true);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();
        byte[] encoded = Base64.getEncoder().encode(bytes);

        return encoded;
    }


    public byte[] routeExport2(List<RouteReport> list, List<String> imeis, List<Vehicle> vehicles, int export, String fromS, String toS, String firmName) throws Exception {
        return routeExport2(list, imeis, vehicles, export, fromS, toS, firmName, null);
    }

    public byte[] routeExport2(List<RouteReport> list, List<String> imeis, List<Vehicle> vehicles, int export, String fromS, String toS, String firmName, String warningMessage) throws Exception {
        if ((list == null || list.isEmpty()) && (warningMessage == null || warningMessage.trim().isEmpty())) {
            return null;
        }

        if (list == null) {
            list = new ArrayList<>();
        }


        //Postavi default vrednosti
        list.forEach(x -> {
            if (x.getBoardKmDiffStartEnd() == null)
                x.setBoardKmDiffStartEnd(0.0);
            if (x.getEndBoardInKm() == null)
                x.setEndBoardInKm(0.0);
            if (x.getStartBoardInKm() == null)
                x.setStartBoardInKm(0.0);
            if (x.getEndEngineWorkTimeInHours() == null)
                x.setEndEngineWorkTimeInHours(0.0);
            if (x.getStartEngineWorkTimeInHours() == null)
                x.setStartEngineWorkTimeInHours(0.0);
            if (x.getDiffEngineWorkTimeInHours() == null)
                x.setDiffEngineWorkTimeInHours(0.0);
        });

        //Napravi objekat koji radi sa xls-om
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Relacije");

        //Definisi stilove
        XSSFCellStyle dateCellStyle2 = workbook.createCellStyle();
        dateCellStyle2.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 223, 227)));

        XSSFCellStyle locationStyle = workbook.createCellStyle();
        locationStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        locationStyle.setAlignment(HorizontalAlignment.CENTER);
        locationStyle.setWrapText(true);

        XSSFCellStyle rest = workbook.createCellStyle();
        rest.setVerticalAlignment(VerticalAlignment.CENTER);
        rest.setAlignment(HorizontalAlignment.LEFT);

        XSSFCellStyle upperStyle = workbook.createCellStyle();
        upperStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        upperStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font2 = workbook.createFont();
        font2.setFontHeightInPoints((short) 11);
        font2.setBold(true);
        upperStyle.setFont(font2);
        //Postavi da text bude vertikalan
        //        upperStyle.setRotation((short) 90);
        upperStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        upperStyle.setAlignment(HorizontalAlignment.CENTER);
        upperStyle.setWrapText(true);

        XSSFCellStyle titleUpperStyle = workbook.createCellStyle();
        titleUpperStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        titleUpperStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleUpperStyle.setFont(font2);
        titleUpperStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleUpperStyle.setAlignment(HorizontalAlignment.CENTER);
        titleUpperStyle.setWrapText(true);

        DateTimeFormatter periodFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String period = "Od: " + LocalDate.parse(fromS).format(periodFmt) + "  Do: " + LocalDate.parse(toS).format(periodFmt);
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o relacijama vozila", firmName, period, warningMessage);
        int tableStartRow = rowCount;
        int cellCount = 0;

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));

        //Dodaj naslove
        Row row = sheet.createRow(rowCount++);

        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.setHeightInPoints(70);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 10 * 256);
        row.getCell(cellCount++).setCellValue("Broj reg.");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 12 * 256);
        row.getCell(cellCount++).setCellValue("Vreme početka");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 20 * 256);
        row.getCell(cellCount++).setCellValue("Početna lokacija");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 12 * 256);
        row.getCell(cellCount++).setCellValue("Vreme kraja");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 20 * 256);
        row.getCell(cellCount++).setCellValue("Krajnja lokacija");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Pređeni put");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 10 * 256);
        row.getCell(cellCount++).setCellValue("Ukupno vreme");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 10 * 256);
        row.getCell(cellCount++).setCellValue("Vreme mirovanja");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Prosečna brzina");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Maks. brzina");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Km početak");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Km kraj");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 6 * 256);
        row.getCell(cellCount++).setCellValue("Km ken");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 8 * 256);
        row.getCell(cellCount++).setCellValue("Radni sati početak");

        row.createCell(cellCount);
        row.setHeightInPoints(70);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 8 * 256);
        row.getCell(cellCount++).setCellValue("Radni sati kraj");

        row.createCell(cellCount);
        row.setHeightInPoints(50);
        row.getCell(cellCount).setCellStyle(upperStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 8 * 256);
        row.getCell(cellCount++).setCellValue("Radni sati");

        rowCount++;
        cellCount = 0;

        //Dodaj podatke u celijama

        double sumPredjeniPut = 0.0;
        double sumUkupnoVreme = 0.0;
        double sumVremeMirovanja = 0.0;
        double sumKilometrazaKen = 0.0;
        double sumRadniSati = 0.0;

        for (RouteReport r : list) {
            row = sheet.createRow(rowCount);

            Vehicle v = vehicles.stream().filter(x -> x.getImei().equals(r.getImei())).findFirst().orElse(null);

            Date date = new Date(r.getTimestampStart());
            Date dateend = new Date(r.getTimestampEnd());
            row.createCell(cellCount);
            row.setHeightInPoints(45);
            row.getCell(cellCount).setCellStyle(locationStyle);
            row.getCell(cellCount++).setCellValue(v.getRegistration());

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(locationStyle);
            row.getCell(cellCount++).setCellValue(df.format(date));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(locationStyle);
            row.getCell(cellCount++).setCellValue(r.getStartAddress());

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(locationStyle);
            row.getCell(cellCount++).setCellValue(df.format(dateend));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(locationStyle);
            row.getCell(cellCount++).setCellValue(r.getEndAddress());

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getRoadTraveled()));
            sumPredjeniPut += r.getRoadTraveled();

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(formatSeconds((int) r.getTime() / 1000));
            sumUkupnoVreme += r.getTime() / 1000;

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(formatSeconds((int) r.getIdleTime() / 1000));
            sumVremeMirovanja += r.getIdleTime() / 1000;

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getAverageSpeed()));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.2f", r.getMaxSpeed()));
            //
            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getStartBoardInKm()));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getEndBoardInKm()));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getBoardKmDiffStartEnd()));
            sumKilometrazaKen += r.getBoardKmDiffStartEnd();

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getStartEngineWorkTimeInHours()));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getEndEngineWorkTimeInHours()));

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(rest);
            row.getCell(cellCount++).setCellValue(String.format("%.1f", r.getDiffEngineWorkTimeInHours()));

            sumRadniSati += r.getDiffEngineWorkTimeInHours();

            cellCount = 0;
            rowCount++;
        }

        //        sumKilometrazaKen/=list.size();
        //        sumRadniSati/=list.size();
        //        sumPredjeniPut/=list.size();
        //        sumUkupnoVreme/=list.size();
        //        sumVremeMirovanja/=list.size();

        XSSFCellStyle ukupnoStyle = workbook.createCellStyle();
        ukupnoStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240))); // Light gray background
        ukupnoStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        ukupnoStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        ukupnoStyle.setAlignment(HorizontalAlignment.CENTER);
        ukupnoStyle.setWrapText(true);

        XSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldFont.setFontHeightInPoints((short) 11);
        ukupnoStyle.setFont(boldFont);

        row = sheet.createRow(++rowCount);

        cellCount = 0;

        row.createCell(cellCount);
        row.setHeightInPoints(75);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 8 * 256);
        row.getCell(cellCount++).setCellValue("Ukupno");

        //Empty
        //Vreme pocetka
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        if (!list.isEmpty())
            row.getCell(cellCount++).setCellValue(df.format(new Date(list.get(0).getTimestampStart())));
        else
            row.getCell(cellCount++).setCellValue("");

        //Empty
        //Pocetna lokacija
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        if (!list.isEmpty())
            row.getCell(cellCount++).setCellValue(list.get(0).getStartAddress());
        else
            row.getCell(cellCount++).setCellValue("");

        //Empty
        //Vreme kraja
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        if (!list.isEmpty())
            row.getCell(cellCount++).setCellValue(df.format(new Date(list.get(list.size() - 1).getTimestampEnd())));
        else
            row.getCell(cellCount++).setCellValue("");

        //Empty
        //Krajnja lokacija
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        if (!list.isEmpty())
            row.getCell(cellCount++).setCellValue(list.get(list.size() - 1).getEndAddress());
        else
            row.getCell(cellCount++).setCellValue("");

        //Predjeni put
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue(String.format("%.1f", sumPredjeniPut));

        //Ukupno vreme
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue(formatSeconds(Integer.valueOf((int) sumUkupnoVreme)));

        //Vreme mirovanja
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue(formatSeconds(Integer.valueOf((int) sumVremeMirovanja)));

        //Prosecna brzina
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Maksimalna brzina
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Empty
        //Km pocetak
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Km kraj
        //Empty
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Km ken
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue(String.format("%.1f", sumKilometrazaKen));

        //Empty
        //Radni sati pocetak
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Empty
        //Radni sati kraj
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue("");

        //Radni sati
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(ukupnoStyle);
        row.getCell(cellCount++).setCellValue(String.format("%.1f", sumRadniSati));

        for (int i = 0; i < 2; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception e) {
            }
        }

        //
        //        for (int i = 0; i < 10; i++) {
        //            try {
        //                sheet.autoSizeColumn(i);
        //            } catch (Exception e) {
        //            }
        //        }
        //
        //        String s = "";
        //        Date d = new Date();
        //        int x = d.getSeconds();
        //        int x2 = d.getMinutes();
        //        s = "excel" + x + "" + x2 + ".xlsx";
        //
        //
        //        try (FileOutputStream outputStream = new FileOutputStream(s)) {
        //            workbook.write(outputStream);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        centerDataCells(sheet, tableStartRow);

        //Ako je export 2 onda se radi pdf export
        if (export == 2) {
            return getPdf(workbook, true);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();
        byte[] encoded = Base64.getEncoder().encode(bytes);

        return encoded;
    }

    /**
     * Exportuje izvestaj o brzinama u Excel Workbook, pretvara ga u
     * byte array i vraca
     *
     * @param export =2 znaci vrati pdf, u suprotnom workbook
     * @param max
     */
//    public byte[] speedExport(List<DTOSpeed> list, String imei, Vehicle v, int export, String dateF, String dateT, boolean peakSelected, int max)
//            throws Exception {
//
//        Optional<Vehicle> optionalVehicle = vehicleRepository.findByImei(imei);
//
//        if (!optionalVehicle.isPresent()) {
//            throw new Exception("Failed to find vehicle");
//        }
//
//        Vehicle veh = optionalVehicle.get();
//
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet("Prekoračenje brzine");
//
//        XSSFCellStyle dateCellStyle2 = workbook.createCellStyle();
//        dateCellStyle2.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 223, 227)));
//        //style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
//        //        CreationHelper createHelper = workbook.getCreationHelper();
//        //        dateCellStyle2.setDataFormat(
//        //                createHelper.createDataFormat().getFormat("dd/mm/yyyy HH:mm:ss"));
//        XSSFCellStyle upperStyle = workbook.createCellStyle();
//        upperStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
//        //style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
//        upperStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        upperStyle.setBorderBottom(BorderStyle.MEDIUM);
//        XSSFFont font2 = workbook.createFont();
//        font2.setFontHeightInPoints((short) 9);
//        font2.setBold(true);
//        upperStyle.setFont(font2);
//        upperStyle.setAlignment(HorizontalAlignment.CENTER);
//
//        XSSFCellStyle textStyle = workbook.createCellStyle();
//        //  textStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(222, 222, 222)));
//        textStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        XSSFFont font3 = workbook.createFont();
//        font3.setFontHeightInPoints((short) 10);
//        textStyle.setFont(font3);
//        XSSFCellStyle smolStyle = workbook.createCellStyle();
//        smolStyle.setFont(font3);
//        smolStyle.setAlignment(HorizontalAlignment.CENTER);
//
//        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
//
//        int rowCount = 0;
//        int cellCount = 0;
//        Row row = sheet.createRow(rowCount++);
//        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//        row.getCell(cellCount++).setCellValue("Izveštaj o prekoračenju brzine");
//        row = sheet.createRow(++rowCount);
//        cellCount = 0;
//
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//        row.getCell(cellCount++).setCellValue("OD:");
//        cellCount = 0;
//        row = sheet.createRow(++rowCount);
//        row.createCell(cellCount);
//        row.getCell(cellCount++).setCellValue(dateF);
//
//        cellCount = 0;
//        row = sheet.createRow(++rowCount);
//
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//        row.getCell(cellCount++).setCellValue("DO:");
//        cellCount = 0;
//        row = sheet.createRow(++rowCount);
//        row.createCell(cellCount);
//        row.getCell(cellCount++).setCellValue(dateT);
//
//        cellCount = 0;
//
//        row = sheet.createRow(++rowCount);
//        row = sheet.createRow(++rowCount);
//
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//        row.getCell(cellCount++).setCellValue("Registracija");
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//        row.getCell(cellCount++).setCellValue("Proizvođač/Model");
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//        row.getCell(cellCount++).setCellValue("Ograničenje");
//
//        rowCount++;
//        row = sheet.createRow(rowCount);
//        cellCount = 0;
//        row.createCell(cellCount);
//        row.getCell(cellCount++).setCellValue(veh.getRegistration());
//        row.createCell(cellCount);
//        row.getCell(cellCount++).setCellValue(veh.getManufacturer() + " " + veh.getModel());
//        row.createCell(cellCount);
//        row.getCell(cellCount++).setCellValue(max + " km/h");
//
//        rowCount++;
//        rowCount++;
//        cellCount = 0;
//
//        row = sheet.createRow(rowCount);
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//        row.getCell(cellCount++).setCellValue("Vreme početka");
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//
//        row.getCell(cellCount++).setCellValue("Vreme kraja");
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//
//        row.getCell(cellCount++).setCellValue("Pređeni put");
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//
//        row.getCell(cellCount++).setCellValue("Vreme vožnje");
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//
//        row.getCell(cellCount++).setCellValue("Max brzina");
//        row.createCell(cellCount);
//        row.getCell(cellCount).setCellStyle(upperStyle);
//
//        row.getCell(cellCount++).setCellValue("Prosečna brzina");
//
//        rowCount++;
//        cellCount = 0;
//
//        for (DTOSpeed speedRep : list) {
//            if (speedRep.isPeak() && !peakSelected)
//                continue;
//            row = sheet.createRow(rowCount);
//
//            int sec = (int) speedRep.getTimeOfTravel() / 1000; //
//
//            row.createCell(cellCount);
//            row.getCell(cellCount).setCellStyle(smolStyle);
//            row.getCell(cellCount++).setCellValue(toSerbianTimeZone(speedRep.getStartTimestamp().toLocalDateTime()));
//            row.createCell(cellCount);
//            row.getCell(cellCount).setCellStyle(smolStyle);
//            if (speedRep.getEndTimestamp() == null)
//                row.getCell(cellCount++).setCellValue("/");
//
//            else
//                row.getCell(cellCount++).setCellValue(toSerbianTimeZone(speedRep.getEndTimestamp().toLocalDateTime()));
//            row.createCell(cellCount);
//            row.getCell(cellCount).setCellStyle(smolStyle);
//
//            row.getCell(cellCount++).setCellValue(String.format("%.1f", speedRep.getRoadTraveled()));
//            row.createCell(cellCount);
//            row.getCell(cellCount).setCellStyle(smolStyle);
//
//            if (!speedRep.isPeak()) {
//                row.getCell(cellCount++).setCellValue(formatSeconds(sec));
//            } else {
//                row.getCell(cellCount++).setCellValue("Pik");
//            }
//            row.createCell(cellCount);
//            row.getCell(cellCount).setCellStyle(smolStyle);
//
//            row.getCell(cellCount++).setCellValue(String.format("%.1f", speedRep.getMaxSpeed()));
//            row.createCell(cellCount);
//            row.getCell(cellCount).setCellStyle(smolStyle);
//
//            row.getCell(cellCount++).setCellValue(String.format("%.1f", speedRep.getAvgSpeed()));
//
//            cellCount = 0;
//            rowCount++;
//        }
//        for (int i = 0; i < 10; i++) {
//            try {
//                sheet.autoSizeColumn(i);
//            } catch (Exception e) {
//            }
//        }
//        //        String s = "";
//        //        Date d = new Date();
//        //        int x = d.getSeconds();
//        //        int x2 = d.getMinutes();
//        //        s = "excel" + x + "" + x2 + ".xlsx";
//        //
//        //
//        //        try (FileOutputStream outputStream = new FileOutputStream(s)) {
//        //            workbook.write(outputStream);
//        //        } catch (Exception e) {
//        //            e.printStackTrace();
//        //        }
//
//        if (export == 2) {
//            return getPdf(workbook, true);
//        }
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            workbook.write(bos);
//        } finally {
//            bos.close();
//        }
//        byte[] bytes = bos.toByteArray();
//        byte[] encoded = Base64.getEncoder().encode(bytes);
//
//        return encoded;
//    }


    public byte[] speedExport(
            List<DTOSpeed> list,
            String imei,
            Vehicle v,                   // ostavljen zbog potpisa; u daljem kodu se koristi veh iz repozitorijuma (kao ranije)
            int export,
            String dateF,
            String dateT,
            boolean peakSelected,
            int max,
            String firmName
    ) throws Exception {

        Optional<Vehicle> optionalVehicle = vehicleRepository.findByImei(imei);
        if (!optionalVehicle.isPresent()) throw new Exception("Failed to find vehicle");
        Vehicle veh = optionalVehicle.get();

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sh = wb.createSheet("Prekoračenje brzine");

        // ===== FONTOVI =====
        XSSFFont titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short)14);

        XSSFFont headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short)10);
        headerFont.setColor(rgb(255,255,255)); // belo

        XSSFFont bodyFont = wb.createFont();
        bodyFont.setFontHeightInPoints((short)10);

        // ===== STILOVI =====
        // naslov
        XSSFCellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.LEFT);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderBottom(BorderStyle.MEDIUM);

        // meta etiketa (siva levo)
        XSSFCellStyle metaLabel = wb.createCellStyle();
        metaLabel.setFont(bodyFont);
        metaLabel.setFillForegroundColor(rgb(220,223,227));
        metaLabel.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        metaLabel.setAlignment(HorizontalAlignment.LEFT);
        metaLabel.setVerticalAlignment(VerticalAlignment.CENTER);

        // meta vrednost (desno)
        XSSFCellStyle metaValue = wb.createCellStyle();
        metaValue.setFont(bodyFont);
        metaValue.setAlignment(HorizontalAlignment.LEFT);
        metaValue.setVerticalAlignment(VerticalAlignment.CENTER);

        // header (plava pozadina, belo bold, wrap, centar, tanki borderi)
        XSSFCellStyle header = wb.createCellStyle();
        header.setFillForegroundColor(rgb(44,115,178)); // Office plava
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header.setFont(headerFont);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setVerticalAlignment(VerticalAlignment.CENTER);
        header.setWrapText(true);
        header.setBorderBottom(BorderStyle.THIN);
        header.setBorderTop(BorderStyle.THIN);
        header.setBorderLeft(BorderStyle.THIN);
        header.setBorderRight(BorderStyle.THIN);

        // telo – levo/centar sa borderima
        XSSFCellStyle bodyLeft = wb.createCellStyle();
        bodyLeft.setFont(bodyFont);
        bodyLeft.setAlignment(HorizontalAlignment.LEFT);
        bodyLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyLeft.setBorderBottom(BorderStyle.THIN);
        bodyLeft.setBorderTop(BorderStyle.THIN);
        bodyLeft.setBorderLeft(BorderStyle.THIN);
        bodyLeft.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle bodyCenter = wb.createCellStyle();
        bodyCenter.cloneStyleFrom(bodyLeft);
        bodyCenter.setAlignment(HorizontalAlignment.CENTER);

        // formati brojeva
        CreationHelper ch = wb.getCreationHelper();
        short fmtDist = ch.createDataFormat().getFormat("0.0");   // Pređeni put
        short fmtSpd  = ch.createDataFormat().getFormat("0.0");   // Max/Prosečna brzina

        XSSFCellStyle distStyle = wb.createCellStyle();
        distStyle.cloneStyleFrom(bodyCenter);
        distStyle.setDataFormat(fmtDist);

        XSSFCellStyle spdStyle = wb.createCellStyle();
        spdStyle.cloneStyleFrom(bodyCenter);
        spdStyle.setDataFormat(fmtSpd);

        // ===== CRTANJE: NASLOV, META, TABELA =====
        String period = "Od: " + dateF + "  Do: " + dateT;
        int rIdx = addExcelReportHeader(wb, sh, "Izveštaj o prekoračenju brzine", firmName, period);
        int tableStartRow = rIdx;

        String mm = (Objects.toString(veh.getManufacturer(), "") +
                (veh.getModel()!=null && !veh.getModel().isEmpty() ? (" " + veh.getModel()) : "")).trim();

        // Header tabele
        String[] cols = new String[]{
                "Broj registracije",
                "Proizvođač/\nModel",
                "Vreme Početka",
                "Vreme Kraja",
                "Pređeni put",
                "Vreme vožnje",
                "Max brzina",
                "Prosečna brzina"
        };
        Row h = sh.createRow(rIdx++);
        h.setHeightInPoints(28);
        for (int c = 0; c < cols.length; c++) {
            Cell hc = h.createCell(c);
            hc.setCellValue(cols[c]);
            hc.setCellStyle(header);
        }

        // PODACI: zadržavamo istu logiku filtriranja kao ranije (peakSelected, itd.)
        int rowStart = rIdx;
        for (DTOSpeed speedRep : list) {
            if (speedRep.isPeak() && !peakSelected) continue;

            Row row = sh.createRow(rIdx++);
            int c = 0;

            // 1) Broj registracije
            Cell cell = row.createCell(c++); cell.setCellValue(veh.getRegistration()); cell.setCellStyle(bodyLeft);

            // 2) Proizvođač/Model
            cell = row.createCell(c++); cell.setCellValue(mm); cell.setCellStyle(bodyLeft);

            // 3) Vreme Početka
            cell = row.createCell(c++);
            if (speedRep.getStartTimestamp() == null) { cell.setCellValue("/"); }
            else { cell.setCellValue(DateUtil.toSerbianTimeZone(speedRep.getStartTimestamp().toLocalDateTime())); }
            cell.setCellStyle(bodyCenter);

            // 4) Vreme Kraja
            cell = row.createCell(c++);
            if (speedRep.getEndTimestamp() == null) { cell.setCellValue("/"); }
            else { cell.setCellValue(DateUtil.toSerbianTimeZone(speedRep.getEndTimestamp().toLocalDateTime())); }
            cell.setCellStyle(bodyCenter);

//            // 3) Vreme Početka
//            cell = row.createCell(c++);
//            if (speedRep.getStartTimestamp() == null) {
//                cell.setCellValue("/");
//            } else {
//                cell.setCellValue(speedRep.getStartTimestamp().toLocalDateTime()
//                        .format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss")));
//            }
//            cell.setCellStyle(bodyCenter);
//
//            // 4) Vreme Kraja
//            cell = row.createCell(c++);
//            if (speedRep.getEndTimestamp() == null) {
//                cell.setCellValue("/");
//            } else {
//                cell.setCellValue(speedRep.getEndTimestamp().toLocalDateTime()
//                        .format(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss")));
//            }

            // 5) Pređeni put
            cell = row.createCell(c++); cell.setCellValue(safeDouble(speedRep.getRoadTraveled())); cell.setCellStyle(distStyle);

            // 6) Vreme vožnje
            cell = row.createCell(c++);
            if (!speedRep.isPeak()) {
                int sec = (int)(speedRep.getTimeOfTravel() / 1000);
                cell.setCellValue(formatSeconds(sec));
            } else {
                cell.setCellValue("Pik");
            }
            cell.setCellStyle(bodyCenter);

            // 7) Max brzina
            cell = row.createCell(c++); cell.setCellValue(safeDouble(speedRep.getMaxSpeed())); cell.setCellStyle(spdStyle);

            // 8) Prosečna brzina
            cell = row.createCell(c++); cell.setCellValue(safeDouble(speedRep.getAvgSpeed())); cell.setCellStyle(spdStyle);
        }

        // Auto-size + minimalne širine (dodatno prošireno)
        for (int i = 0; i < cols.length; i++) {
            try { sh.autoSizeColumn(i); } catch (Exception ignored) {}
        }
        // min širine (u karakterima * 256)
        setMinWidth(sh, 0, 18); // Broj registracije
        setMinWidth(sh, 1, 22); // Proizvođač/Model
        setMinWidth(sh, 2, 22); // Vreme Početka (prošireno)
        setMinWidth(sh, 3, 22); // Vreme Kraja (prošireno)
        setMinWidth(sh, 4, 15); // Pređeni put
        setMinWidth(sh, 5, 16); // Vreme vožnje
        setMinWidth(sh, 6, 14); // Max brzina
        setMinWidth(sh, 7, 16); // Prosečna brzina

        centerDataCells(sh, tableStartRow);

        // Izvoz
        if (export == 2) {
            return getPdf(wb, true);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try { wb.write(bos); } finally { bos.close(); }
        return Base64.getEncoder().encode(bos.toByteArray());
    }

    /* ===== Helperi ===== */

    private static void setMinWidth(Sheet sh, int col, int chars) {
        int min = chars * 256;
        if (sh.getColumnWidth(col) < min) sh.setColumnWidth(col, min);
    }

    private static double safeDouble(Double d) { return d == null ? 0d : d; }





    public byte[] speedExport2(List<DTOSpeed> list, List<String> imeis, List<Vehicle> vehicles, int export, String dateF, String dateT, boolean peakSelected, int max, String firmName) throws Exception {
        return speedExport2(list, imeis, vehicles, export, dateF, dateT, peakSelected, max, firmName, null);
    }

    public byte[] speedExport2(List<DTOSpeed> list, List<String> imeis, List<Vehicle> vehicles, int export, String dateF, String dateT, boolean peakSelected, int max, String firmName, String warningMessage) throws Exception {
        Map<String, Vehicle> byImei = vehicles.stream()
                .collect(Collectors.toMap(Vehicle::getImei, v -> v, (a, b) -> a));

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sh = wb.createSheet("Prekoračenje brzine");

        // ===== FONTS =====
        XSSFFont titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short)14);

        XSSFFont headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short)9);
        headerFont.setColor(IndexedColors.WHITE.getIndex()); // belo

        XSSFFont bodyFont = wb.createFont();
        bodyFont.setFontHeightInPoints((short)10);

        // ===== STYLES =====
        // naslov
        XSSFCellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.LEFT);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderBottom(BorderStyle.MEDIUM);

        // meta: leva ćelija siva (etiketa)
        XSSFCellStyle metaLabel = wb.createCellStyle();
        metaLabel.setFont(bodyFont);
        metaLabel.setAlignment(HorizontalAlignment.LEFT);
        metaLabel.setVerticalAlignment(VerticalAlignment.CENTER);
        metaLabel.setFillForegroundColor(rgb(220,223,227));
        metaLabel.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // meta: desna ćelija (vrednost)
        XSSFCellStyle metaValue = wb.createCellStyle();
        metaValue.setFont(bodyFont);
        metaValue.setAlignment(HorizontalAlignment.LEFT);
        metaValue.setVerticalAlignment(VerticalAlignment.CENTER);

        // header tabele – plavi, beli bold font, wrap, tanki borderi
        XSSFCellStyle header = wb.createCellStyle();
        header.setFont(headerFont);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setVerticalAlignment(VerticalAlignment.CENTER);
        header.setWrapText(true);
        header.setFillForegroundColor(rgb(44,115,178));
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header.setBorderBottom(BorderStyle.THIN);
        header.setBorderTop(BorderStyle.THIN);
        header.setBorderLeft(BorderStyle.THIN);
        header.setBorderRight(BorderStyle.THIN);

        // telo – levo/centar + borderi (beli redovi)
        XSSFCellStyle bodyLeft = wb.createCellStyle();
        bodyLeft.setFont(bodyFont);
        bodyLeft.setAlignment(HorizontalAlignment.LEFT);
        bodyLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyLeft.setBorderBottom(BorderStyle.THIN);
        bodyLeft.setBorderTop(BorderStyle.THIN);
        bodyLeft.setBorderLeft(BorderStyle.THIN);
        bodyLeft.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle bodyCenter = wb.createCellStyle();
        bodyCenter.cloneStyleFrom(bodyLeft);
        bodyCenter.setAlignment(HorizontalAlignment.CENTER);

        // telo – alternating (svetlo sivi redovi)
        XSSFCellStyle bodyLeftAlt = wb.createCellStyle();
        bodyLeftAlt.cloneStyleFrom(bodyLeft);
        bodyLeftAlt.setFillForegroundColor(rgb(241,245,249));
        bodyLeftAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle bodyCenterAlt = wb.createCellStyle();
        bodyCenterAlt.cloneStyleFrom(bodyCenter);
        bodyCenterAlt.setFillForegroundColor(rgb(241,245,249));
        bodyCenterAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // brojčani formati
        CreationHelper ch = wb.getCreationHelper();
        XSSFCellStyle distStyle = wb.createCellStyle();
        distStyle.cloneStyleFrom(bodyCenter);
        distStyle.setDataFormat(ch.createDataFormat().getFormat("0.##"));
        XSSFCellStyle distStyleAlt = wb.createCellStyle();
        distStyleAlt.cloneStyleFrom(bodyCenterAlt);
        distStyleAlt.setDataFormat(ch.createDataFormat().getFormat("0.##"));

        XSSFCellStyle maxStyle = wb.createCellStyle();
        maxStyle.cloneStyleFrom(bodyCenter);
        maxStyle.setDataFormat(ch.createDataFormat().getFormat("0"));
        XSSFCellStyle maxStyleAlt = wb.createCellStyle();
        maxStyleAlt.cloneStyleFrom(bodyCenterAlt);
        maxStyleAlt.setDataFormat(ch.createDataFormat().getFormat("0"));

        XSSFCellStyle avgStyle = wb.createCellStyle();
        avgStyle.cloneStyleFrom(bodyCenter);
        avgStyle.setDataFormat(ch.createDataFormat().getFormat("0.00"));
        XSSFCellStyle avgStyleAlt = wb.createCellStyle();
        avgStyleAlt.cloneStyleFrom(bodyCenterAlt);
        avgStyleAlt.setDataFormat(ch.createDataFormat().getFormat("0.00"));

        // ===== LOGO + HEADER =====
        int rIdx = 0;

        // Logo (rows 0-2, columns 0-1)
        try {
            InputStream logoStream = getClass().getResourceAsStream("/images/oris-logo.png");
            if (logoStream != null) {
                byte[] logoBytes = IOUtils.toByteArray(logoStream);
                logoStream.close();
                int pictureIdx = wb.addPicture(logoBytes, Workbook.PICTURE_TYPE_PNG);
                XSSFDrawing drawing = sh.createDrawingPatriarch();
                XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 0, 2, 3);
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                drawing.createPicture(anchor, pictureIdx);
            }
        } catch (Exception ignored) {}

        // Reserve rows for logo
        for (int i = 0; i < 3; i++) {
            Row lr = sh.createRow(rIdx++);
            lr.setHeightInPoints(20);
        }

        // Prazan red
        rIdx++;

        // Naslov
        Row r = sh.createRow(rIdx++);
        r.setHeightInPoints(28);
        r.createCell(0).setCellValue("Izveštaj o prekoračenju brzine");
        r.getCell(0).setCellStyle(titleStyle);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 0, 7));

        // Prazan red
        rIdx++;

        // Meta: Kompanija
        XSSFFont metaBoldFont = wb.createFont();
        metaBoldFont.setBold(true);
        metaBoldFont.setFontHeightInPoints((short) 10);
        XSSFCellStyle metaLabelBold = wb.createCellStyle();
        metaLabelBold.cloneStyleFrom(metaLabel);
        metaLabelBold.setFont(metaBoldFont);

        r = sh.createRow(rIdx++);
        r.setHeightInPoints(18);
        r.createCell(0).setCellValue("Kompanija:");
        r.getCell(0).setCellStyle(metaLabelBold);
        r.createCell(1).setCellValue(firmName != null ? firmName : "");
        r.getCell(1).setCellStyle(metaValue);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 1, 3));

        // Meta: Generisano
        r = sh.createRow(rIdx++);
        r.setHeightInPoints(18);
        r.createCell(0).setCellValue("Generisano:");
        r.getCell(0).setCellStyle(metaLabelBold);
        String generatedAt = java.time.LocalDateTime.now(java.time.ZoneId.of("Europe/Belgrade"))
                .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        r.createCell(1).setCellValue(generatedAt);
        r.getCell(1).setCellStyle(metaValue);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 1, 3));

        // Meta: Prekoračenje brzine
        r = sh.createRow(rIdx++);
        r.setHeightInPoints(18);
        r.createCell(0).setCellValue("Prekoračenje brzine:");
        r.getCell(0).setCellStyle(metaLabelBold);
        r.createCell(1).setCellValue(max + " (Km/h)");
        r.getCell(1).setCellStyle(metaValue);

        // Meta: Datum/Vreme
        r = sh.createRow(rIdx++);
        r.setHeightInPoints(18);
        r.createCell(0).setCellValue("Datum/Vreme:");
        r.getCell(0).setCellStyle(metaLabelBold);
        r.createCell(1).setCellValue("Od: " + dateF + "  Do: " + dateT);
        r.getCell(1).setCellStyle(metaValue);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 1, 4));

        if (warningMessage != null && !warningMessage.trim().isEmpty()) {
            XSSFFont warningFont = wb.createFont();
            warningFont.setBold(true);
            warningFont.setFontHeightInPoints((short) 10);
            warningFont.setColor(IndexedColors.RED.getIndex());

            XSSFCellStyle warningStyle = wb.createCellStyle();
            warningStyle.setFont(warningFont);
            warningStyle.setWrapText(true);
            warningStyle.setAlignment(HorizontalAlignment.LEFT);
            warningStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            r = sh.createRow(rIdx++);
            r.setHeightInPoints(36);
            r.createCell(0).setCellValue(warningMessage);
            r.getCell(0).setCellStyle(warningStyle);
            sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 0, 7));
        }

        // Prazan red
        rIdx++;

        // ===== HEADER TABELE (plavi red) =====
        String[] cols = new String[]{
                "Broj\nRegistracije",
                "Proizvođač/\nModel",
                "Vreme Početka",
                "Vreme Kraja",
                "Pređeni\nput",
                "Vreme\nvožnje",
                "Max\nbrzina",
                "Prosečna\nbrzina"};

        // Ako želiš tačno 9 kolona (A..I) – poslednja je prazna “distancer”.
        int columnCount = 8;
        int tableStartRow = rIdx;
        Row h = sh.createRow(rIdx++);
        h.setHeightInPoints(34);
        for (int c = 0; c < columnCount; c++) {
            Cell hc = h.createCell(c);
            if (c < 8) hc.setCellValue(cols[c]);  // stvarne kolone do H
            hc.setCellStyle(header);
        }

        // ===== PODACI – jedna tabela, bez blokova po IMEI =====
        List<DTOSpeed> data = list.stream()
                .filter(s -> s.getGpsList() != null && !s.getGpsList().isEmpty())
                .collect(Collectors.toList());

        int rb = 0;
        for (DTOSpeed sp : data) {
            if (sp.isPeak() && !peakSelected) continue;

            boolean alt = (rb % 2 == 1);
            XSSFCellStyle bL = alt ? bodyLeftAlt   : bodyLeft;
            XSSFCellStyle bC = alt ? bodyCenterAlt : bodyCenter;
            XSSFCellStyle dS = alt ? distStyleAlt  : distStyle;
            XSSFCellStyle mS = alt ? maxStyleAlt   : maxStyle;
            XSSFCellStyle aS = alt ? avgStyleAlt   : avgStyle;

            Gs100 first = sp.getGpsList().get(0);
            Vehicle v = (first != null) ? byImei.get(first.getImei()) : null;

            String reg = (v != null && v.getRegistration() != null) ? v.getRegistration() : "";
            String mm  = (v == null ? "" :
                    (Objects.toString(v.getManufacturer(), "") +
                            (v.getModel() != null && !v.getModel().isEmpty() ? (" " + v.getModel()) : ""))).trim();

            Row row = sh.createRow(rIdx++);
            int c = 0;

            // 1) Broj registracije (zapravo “Registracije” u tabeli) – u slici ova kolona je sama;
            // po dogovoru: prva kolona je "Registracije" (nema posebnog rednog broja).
            Cell cc = row.createCell(c++); cc.setCellValue(reg); cc.setCellStyle(bL);

            // 2) Proizvođač/Model
            cc = row.createCell(c++); cc.setCellValue(mm); cc.setCellStyle(bL);

            // 3) Vreme Početka
            cc = row.createCell(c++);
            cc.setCellValue(formatTs(sp.getStartTimestamp()));
            cc.setCellStyle(bC);

            // 4) Vreme Kraja
            cc = row.createCell(c++);
            cc.setCellValue(formatTs(sp.getEndTimestamp()));
            cc.setCellStyle(bC);

            // 5) Pređeni put
            cc = row.createCell(c++); cc.setCellValue(d(sp.getRoadTraveled())); cc.setCellStyle(dS);

            // 6) Vreme vožnje (ili "Pik")
            cc = row.createCell(c++);
            if (!sp.isPeak()) {
                int sec = (int) (sp.getTimeOfTravel() / 1000);
                cc.setCellValue(formatSeconds2(sec));
            } else {
                cc.setCellValue("Pik");
            }
            cc.setCellStyle(bC);

            cc = row.createCell(c++); cc.setCellValue(d(sp.getMaxSpeed())); cc.setCellStyle(mS);
            cc = row.createCell(c++); cc.setCellValue(d(sp.getAvgSpeed())); cc.setCellStyle(aS);

            rb++;
        }

        // ===== COLUMN WIDTHS =====
        sh.setColumnWidth(0, 12 * 256);  // Registracije
        sh.setColumnWidth(1, 18 * 256);  // Proizvođač/Model
        sh.setColumnWidth(2, 21 * 256);  // Vreme Početka
        sh.setColumnWidth(3, 21 * 256);  // Vreme Kraja
        sh.setColumnWidth(4, 10 * 256);  // Pređeni put
        sh.setColumnWidth(5, 11 * 256);  // Vreme vožnje
        sh.setColumnWidth(6, 10 * 256);  // Max brzina
        sh.setColumnWidth(7, 11 * 256);  // Prosečna brzina

        centerDataCells(sh, tableStartRow);

        // ===== IZVOZ =====
        if (export == 2) {
            return getPdf(wb, true); // tvoja postojeća PDF rutina
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try { wb.write(bos); } finally { bos.close(); }
        return Base64.getEncoder().encode(bos.toByteArray());
    }

    /* =================== POMOĆNE =================== */

    // siguran XSSFColor preko RGB (kompatibilno sa većinom POI verzija)
    private static XSSFColor rgb(int r, int g, int b) {
        return new XSSFColor(new java.awt.Color(r, g, b));
    }

    // null-safe double
    private static double d(Double v) { return v == null ? 0d : v; }

    // Timestamp -> "dd.MM.yy HH:mm:ss" u zoni Europe/Belgrade
    private static String formatTs(java.sql.Timestamp ts) {
        if (ts == null) return "/";
        return ts.toInstant()
                .atZone(java.time.ZoneId.of("Europe/Belgrade"))
                .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss"));
    }

    // HH:mm:ss iz sekundi
    private  String formatSeconds2(int totalSec) {
        int h = totalSec / 3600;
        int m = (totalSec % 3600) / 60;
        int s = totalSec % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }




    /**
     * Exportuje izvestaj o predjenom putu u Excel Workbook, pretvra ga u
     * byte array i vraca
     *
     * @param export =2 znaci vrati pdf, u suprotnom workbook
     */
    public byte[] ippExport(ArrayList<Ipp> ippList, int export, Timestamp dateFromS, Timestamp dateToS, String firmName) throws Exception {
        return ippExport(ippList, export, dateFromS, dateToS, firmName, null);
    }

    public byte[] ippExport(ArrayList<Ipp> ippList, int export, Timestamp dateFromS, Timestamp dateToS, String firmName, String warningMessage) throws Exception {

        log.info("####################################");
        log.info(LocalDateTime.now() + " - Generisanje izvestaja o predjenom putu za " + ippList.size() + " vozila.");

        if (ippList.size() == 0 && (warningMessage == null || warningMessage.trim().isEmpty())) {
            log.info("####################################");
            log.info(LocalDateTime.now() + " - Nema podataka za izvestaj o predjenom putu.");
            return null;
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Pređeni put");

        XSSFCellStyle upperStyle = workbook.createCellStyle();
        upperStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        //style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        upperStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        upperStyle.setBorderBottom(BorderStyle.MEDIUM);

        XSSFFont font2 = workbook.createFont();
        font2.setFontHeightInPoints((short) 11);
        font2.setBold(true);
        upperStyle.setFont(font2);
        upperStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(222, 222, 222)));
        textStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //
        //        XSSFCellStyle dateCellStyle2 = workbook.createCellStyle();
        //        dateCellStyle2.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 223, 227)));
        //        //style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        //        CreationHelper createHelper = workbook.getCreationHelper();
        //        dateCellStyle2.setDataFormat(
        //                createHelper.createDataFormat().getFormat("dd/mm/yyyy HH:mm:ss"));

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String period = "Od: " + df.format(dateFromS) + "  Do: " + df.format(dateToS);
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o pređenom putu", firmName, period, warningMessage);

        // proširi border bottom title reda preko cele tabele (kolone 0-8, do "Tačnost").
        // addExcelReportHeader pravi merge 0-7 sa borderBottom samo na ćeliji 0, pa
        // vizuelno linija ne ide do kraja tabele kao u PDF-u. Uklanjamo stari merge,
        // postavljamo border na svaku ćeliju 0..8, pa dodajemo novi merge preko cele širine.
        int titleRowIdx = 4;
        int totalCols = 9;
        Row titleRow = sheet.getRow(titleRowIdx);
        if (titleRow != null && titleRow.getCell(0) != null) {
            for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
                CellRangeAddress region = sheet.getMergedRegion(i);
                if (region.getFirstRow() == titleRowIdx && region.getFirstColumn() == 0) {
                    sheet.removeMergedRegion(i);
                    break;
                }
            }
            XSSFCellStyle titleCellStyle = (XSSFCellStyle) titleRow.getCell(0).getCellStyle();
            for (int col = 0; col < totalCols; col++) {
                if (titleRow.getCell(col) == null) {
                    titleRow.createCell(col);
                }
                titleRow.getCell(col).setCellStyle(titleCellStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(titleRowIdx, titleRowIdx, 0, totalCols - 1));
        }

        int tableStartRow = rowCount;
        int cellCount = 0;
        Row row = sheet.createRow(rowCount++);

        cellCount = 0;
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Reg.");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Proi./Model");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Tip vozila");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Pređeni put");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Vreme vožnje");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Vreme mir.");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Vreme staj.");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Prosečna brzina");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Tačnost");

        cellCount = 0;

        DataFormat dataFormat = workbook.createDataFormat();

        XSSFCellStyle decimalStyle = workbook.createCellStyle();
        decimalStyle.setDataFormat(dataFormat.getFormat("0.00")); // npr. 12.34
        decimalStyle.setAlignment(HorizontalAlignment.RIGHT);

        XSSFCellStyle percentStyle = workbook.createCellStyle();
        percentStyle.setAlignment(HorizontalAlignment.CENTER);

        for (Ipp ipp : ippList) {
            row = sheet.createRow(rowCount);

            double sec = ipp.getTimeOfTravel();
            long secl = (long) sec;
            Duration duration = Duration.ofSeconds(secl);

            double sec2 = ipp.getTimeIdle();
            long sec2l = (long) sec2;
            Duration duration2 = Duration.ofSeconds(sec2l);

            double sec3 = ipp.getTimeSleeping();
            long sec3l = (long) sec3;
            Duration duration3 = Duration.ofSeconds(sec3l);

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(textStyle);
            row.getCell(cellCount++).setCellValue(ipp.getRegistraiton());
            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(textStyle);
            row.getCell(cellCount++).setCellValue(ipp.getManufacturer() + " " + ipp.getModel());
            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(ipp.getType());
            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(decimalStyle);
            row.getCell(cellCount++).setCellValue(ipp.getRoadTraveled());
            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(formatDuration(duration));
            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(formatDuration(duration2));
            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(formatDuration(duration3));
            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(decimalStyle);
            row.getCell(cellCount++).setCellValue(ipp.getAvgSpeed());
            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(((int) ipp.getAccuracy()) + " %");

            cellCount = 0;
            rowCount++;
        }

        for (int i = 0; i < 12; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception e) {
            }
        }

        centerDataCells(sheet, tableStartRow);

        //        String s = "";
        //        Date d = new Date();
        //        int x = d.getSeconds();
        //        int x2 = d.getMinutes();
        //        s = "excel" + x + "" + x2 + ".xlsx";
        //
        //        try (FileOutputStream outputStream = new FileOutputStream(s)) {
        //            workbook.write(outputStream);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();
        byte[] encoded = Base64.getEncoder().encode(bytes);

        if (export == 2) {
            return getPdf(workbook, true);
        }

        log.info("####################################");
        log.info(LocalDateTime.now() + " - Izvestaj o predjenom putu generisan za " + ippList.size() + " vozila.");

        return encoded;
    }

    /**
     * Adds a standardized header (logo, title, company, date, period) to any Excel report sheet.
     * Returns the next available row index.
     */
    private int addExcelReportHeader(XSSFWorkbook wb, XSSFSheet sh, String title, String firmName, String period) {
        return addExcelReportHeader(wb, sh, title, firmName, period, null);
    }

    private int addExcelReportHeader(XSSFWorkbook wb, XSSFSheet sh, String title, String firmName, String period, String warningMessage) {
        int rIdx = 0;
        try {
            InputStream logoStream = getClass().getResourceAsStream("/images/oris-logo.png");
            if (logoStream != null) {
                byte[] logoBytes = IOUtils.toByteArray(logoStream);
                logoStream.close();
                int pictureIdx = wb.addPicture(logoBytes, Workbook.PICTURE_TYPE_PNG);
                XSSFDrawing drawing = sh.createDrawingPatriarch();
                XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 0, 2, 3);
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                drawing.createPicture(anchor, pictureIdx);
            }
        } catch (Exception ignored) {}

        for (int i = 0; i < 3; i++) {
            Row lr = sh.createRow(rIdx++);
            lr.setHeightInPoints(20);
        }
        rIdx++;

        XSSFFont tFont = wb.createFont();
        tFont.setBold(true);
        tFont.setFontHeightInPoints((short) 14);
        XSSFFont mBold = wb.createFont();
        mBold.setBold(true);
        mBold.setFontHeightInPoints((short) 10);
        XSSFFont mFont = wb.createFont();
        mFont.setFontHeightInPoints((short) 10);

        XSSFCellStyle tStyle = wb.createCellStyle();
        tStyle.setFont(tFont);
        tStyle.setAlignment(HorizontalAlignment.LEFT);
        tStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tStyle.setBorderBottom(BorderStyle.MEDIUM);

        XSSFCellStyle mlStyle = wb.createCellStyle();
        mlStyle.setFont(mBold);
        mlStyle.setAlignment(HorizontalAlignment.LEFT);
        mlStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        mlStyle.setFillForegroundColor(rgb(220, 223, 227));
        mlStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle mvStyle = wb.createCellStyle();
        mvStyle.setFont(mFont);
        mvStyle.setAlignment(HorizontalAlignment.LEFT);
        mvStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Row r = sh.createRow(rIdx++);
        r.setHeightInPoints(28);
        r.createCell(0).setCellValue(title);
        r.getCell(0).setCellStyle(tStyle);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 0, 7));
        rIdx++;

        r = sh.createRow(rIdx++);
        r.setHeightInPoints(18);
        r.createCell(0).setCellValue("Kompanija:");
        r.getCell(0).setCellStyle(mlStyle);
        r.createCell(1).setCellValue(firmName != null ? firmName : "");
        r.getCell(1).setCellStyle(mvStyle);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 1, 3));

        r = sh.createRow(rIdx++);
        r.setHeightInPoints(18);
        r.createCell(0).setCellValue("Generisano:");
        r.getCell(0).setCellStyle(mlStyle);
        String genAt = java.time.LocalDateTime.now(java.time.ZoneId.of("Europe/Belgrade"))
                .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        r.createCell(1).setCellValue(genAt);
        r.getCell(1).setCellStyle(mvStyle);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 1, 3));

        if (period != null && !period.isEmpty()) {
            r = sh.createRow(rIdx++);
            r.setHeightInPoints(18);
            r.createCell(0).setCellValue("Period:");
            r.getCell(0).setCellStyle(mlStyle);
            r.createCell(1).setCellValue(period);
            r.getCell(1).setCellStyle(mvStyle);
            sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 1, 4));
        }

        if (warningMessage != null && !warningMessage.trim().isEmpty()) {
            XSSFFont warningFont = wb.createFont();
            warningFont.setBold(true);
            warningFont.setFontHeightInPoints((short) 10);
            warningFont.setColor(IndexedColors.RED.getIndex());

            XSSFCellStyle warningStyle = wb.createCellStyle();
            warningStyle.setFont(warningFont);
            warningStyle.setWrapText(true);
            warningStyle.setAlignment(HorizontalAlignment.LEFT);
            warningStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            r = sh.createRow(rIdx++);
            r.setHeightInPoints(36);
            r.createCell(0).setCellValue(warningMessage);
            r.getCell(0).setCellStyle(warningStyle);
            sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 0, 7));
        }

        rIdx++;
        return rIdx;
    }

    /**
     * Centrira horizontalno sve celije tabele izvestaja, od reda sa nazivima
     * kolona (fromRow) do poslednjeg reda. POI stilovi su deljeni izmedju
     * celija pa se ne smeju mutirati — za svaki zateceni stil pravi se
     * centrirani klon i kesira po indeksu stila, da se ne bi promenio header
     * blok iznad tabele i da se ne bi napravilo previse stilova.
     */
    private void centerDataCells(XSSFSheet sheet, int fromRow) {
        XSSFWorkbook wb = sheet.getWorkbook();
        Map<Short, XSSFCellStyle> centeredStyles = new HashMap<>();
        for (int r = fromRow; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            for (Cell cell : row) {
                XSSFCellStyle source = (XSSFCellStyle) cell.getCellStyle();
                XSSFCellStyle centered = centeredStyles.get(source.getIndex());
                if (centered == null) {
                    centered = wb.createCellStyle();
                    centered.cloneStyleFrom(source);
                    centered.setAlignment(HorizontalAlignment.CENTER);
                    centeredStyles.put(source.getIndex(), centered);
                }
                cell.setCellStyle(centered);
            }
        }
    }

    /**
     * vraca pdf verziju izvestaja koju dobija iz workbook fajla
     */
    private byte[] getPdf(XSSFWorkbook workbook, boolean orientation) throws Exception {
        //Generisu se bajtovi
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();

        //Salje se na 3rd party API (Gotenberg) koji preradi to i vrati nam PDF
        String gotenbergUrl = System.getenv("GOTENBERG_URL");
        String uri = (gotenbergUrl != null && !gotenbergUrl.isEmpty())
                ? gotenbergUrl + "/convert/office"
                : "http://gotenberg:3000/convert/office";
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(WebConfig.HTTP_CONNECT_TIMEOUT_MS)
                .setSocketTimeout(WebConfig.HTTP_READ_TIMEOUT_MS)
                .build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setConfig(requestConfig);
        // FileBody uploadFilePart = new FileBody(new File(s));
        ContentBody cd = new InputStreamBody(new ByteArrayInputStream(bytes), "my-file.xlsx");
        org.apache.http.entity.mime.content.StringBody s2 = new StringBody("true");
        org.apache.http.entity.mime.content.StringBody s3 = new StringBody("0");
        org.apache.http.entity.mime.content.StringBody s4 = new StringBody("2");
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("files", cd);
        reqEntity.addPart("marginTop", s3);
        reqEntity.addPart("marginBottom", s3);
        reqEntity.addPart("marginLeft", s3);
        reqEntity.addPart("marginRight", s3);
        reqEntity.addPart("landscape", s2);
        reqEntity.addPart("scale", s4);

        httpPost.setEntity(reqEntity);
        byte[] file;
        try (CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build()) {
            HttpResponse response = httpclient.execute(httpPost);
            file = IOUtils.toByteArray(response.getEntity().getContent());
        }

        //        File outputFile = new File("outputFile2.pdf");
        //        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
        //            outputStream.write(file);
        //        }
        byte[] encoded = Base64.getEncoder().encode(file);

        return encoded;
    }

    /**
     * Exportuje izvestaj o mesecnom predjenom putu u Excel Workbook, pretvara ga u
     * byte array i vraca
     *
     * @param export =2 znaci vrati pdf, u suprotnom workbook
     */
    public byte[] ippmExport(ArrayList<Ippm> ippmArrayList, Timestamp tsFrom, Timestamp tsTo, int export, int working, int hFrom, int mFrom,
            int hTo, int mTo, int hfromsa,int mfromsa,int htosa,int mtosa,int hfromsu,int mfromsu,int htosu,int mtosu, String firmName) throws Exception {
        return ippmExport(ippmArrayList, tsFrom, tsTo, export, working, hFrom, mFrom, hTo, mTo, hfromsa, mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu, mtosu, firmName, null);
    }

    public byte[] ippmExport(ArrayList<Ippm> ippmArrayList, Timestamp tsFrom, Timestamp tsTo, int export, int working, int hFrom, int mFrom,
            int hTo, int mTo, int hfromsa,int mfromsa,int htosa,int mtosa,int hfromsu,int mfromsu,int htosu,int mtosu, String firmName, String warningMessage) throws Exception {
        if (ippmArrayList.size() == 0 && (warningMessage == null || warningMessage.trim().isEmpty())) {
            return null;
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Pređeni put (mesečno)");

        XSSFCellStyle upperStyle = workbook.createCellStyle();
        upperStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        //style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        upperStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        upperStyle.setBorderBottom(BorderStyle.MEDIUM);
        XSSFFont font2 = workbook.createFont();
        font2.setFontHeightInPoints((short) 7);
        font2.setBold(true);
        upperStyle.setFont(font2);
        upperStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(222, 222, 222)));
        textStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font3 = workbook.createFont();
        font3.setFontHeightInPoints((short) 6);
        textStyle.setFont(font3);
        XSSFCellStyle smolStyle = workbook.createCellStyle();
        smolStyle.setFont(font3);
        smolStyle.setAlignment(HorizontalAlignment.CENTER);


        XSSFFont font4 = workbook.createFont();
        font4.setFontHeightInPoints((short) 6);

        XSSFCellStyle rightStyle = workbook.createCellStyle();
        rightStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightStyle.setFont(font4);

        Calendar c = Calendar.getInstance();
        c.setTime(tsFrom);

        int days = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat df2 = new SimpleDateFormat("dd.MM.yyyy");
        String period = "Od: " + df2.format(tsFrom) + "  Do: " + df2.format(tsTo);
        int rowCount = addExcelReportHeader(workbook, sheet, "Mesečni izveštaj o pređenom putu", firmName, period, warningMessage);

        // proširi border bottom title reda preko cele tabele (Reg + days + Ukupno).
        // addExcelReportHeader pravi merge 0-7 sa borderBottom samo na ćeliji 0, pa
        // vizuelno linija ide samo do kolone H. Uklanjamo stari merge, postavljamo border
        // na svaku ćeliju 0..(totalCols-1), pa dodajemo novi merge preko cele širine.
        int titleRowIdx = 4;
        int totalCols = days + 2;
        Row titleRow = sheet.getRow(titleRowIdx);
        if (titleRow != null && titleRow.getCell(0) != null) {
            for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
                CellRangeAddress region = sheet.getMergedRegion(i);
                if (region.getFirstRow() == titleRowIdx && region.getFirstColumn() == 0) {
                    sheet.removeMergedRegion(i);
                    break;
                }
            }
            XSSFCellStyle titleCellStyle = (XSSFCellStyle) titleRow.getCell(0).getCellStyle();
            for (int col = 0; col < totalCols; col++) {
                if (titleRow.getCell(col) == null) {
                    titleRow.createCell(col);
                }
                titleRow.getCell(col).setCellStyle(titleCellStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(titleRowIdx, titleRowIdx, 0, totalCols - 1));
        }

        int cellCount = 0;
        Row row;

        // RADNO VREME redovi nastavljaju odmah ispod Period reda — bez praznog reda 10
        // (addExcelReportHeader ostavlja rowCount = 10 sa ugrađenim rIdx++ separator-om;
        //  pomeramo nazad za 2 da bi ++rowCount sledeći put dao zero-indexed 9 = Excel red 10)
        rowCount -= 2;

        // proširi merged regions Kompanija/Generisano/Period iz addExcelReportHeader
        // sa 1-3 (1-4 za Period) na 1-7 — pojedinačne kolone ostaju autoSize (uniformno
        // u tabeli dani), ali vrednost se prikazuje preko više kolona pa staje
        // "Galeb Electronics", datum, period itd.
        List<Integer> mergedToRemove = new ArrayList<>();
        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            int firstRow = region.getFirstRow();
            // Kompanija (6), Generisano (7), Period (8) — sve sa kolonom 1
            if ((firstRow == 6 || firstRow == 7 || firstRow == 8) && region.getFirstColumn() == 1) {
                mergedToRemove.add(i);
            }
        }
        for (Integer idx : mergedToRemove) {
            sheet.removeMergedRegion(idx);
        }
        // svi shared header redovi sa vrednostima (Kompanija, Generisano, Period) idu
        // u merge 1-15 da niko ne odsečen — kolone su autoSize uske (~3-4 char) jer
        // sadrže brojeve dana 1, 2, 3 itd., pa treba dovoljno kolona u merge-u
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 1, 15));
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 1, 15));
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 1, 15));

        // stilovi identični onima u addExcelReportHeader za Kompanija/Generisano/Period
        XSSFFont mBoldLocal = workbook.createFont();
        mBoldLocal.setBold(true);
        mBoldLocal.setFontHeightInPoints((short) 10);
        XSSFFont mFontLocal = workbook.createFont();
        mFontLocal.setFontHeightInPoints((short) 10);

        XSSFCellStyle mlStyleLocal = workbook.createCellStyle();
        mlStyleLocal.setFont(mBoldLocal);
        mlStyleLocal.setAlignment(HorizontalAlignment.LEFT);
        mlStyleLocal.setVerticalAlignment(VerticalAlignment.CENTER);
        mlStyleLocal.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 223, 227)));
        mlStyleLocal.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle mvStyleLocal = workbook.createCellStyle();
        mvStyleLocal.setFont(mFontLocal);
        mvStyleLocal.setAlignment(HorizontalAlignment.LEFT);
        mvStyleLocal.setVerticalAlignment(VerticalAlignment.CENTER);

        if (working == 1 || working == 0) {
            // "Tip izveštaja:" red — vrednost zavisi od working parametra
            row = sheet.createRow(++rowCount);
            row.setHeightInPoints(18);
            row.createCell(0).setCellValue("Tip izveštaja:");
            row.getCell(0).setCellStyle(mlStyleLocal);
            row.createCell(1);
            row.getCell(1).setCellStyle(mvStyleLocal);
            row.getCell(1).setCellValue(working == 1 ? "Radno vreme" : "Van radnog vremena");
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 1, 15));

            row = sheet.createRow(++rowCount);
            row.setHeightInPoints(18);
            row.createCell(0).setCellValue("RADNO VREME:");
            row.getCell(0).setCellStyle(mlStyleLocal);
            row.createCell(1);
            row.getCell(1).setCellStyle(mvStyleLocal);
            String workInterval = String.format("%02d:%02d - %02d:%02d", hFrom, mFrom, hTo, mTo);
            row.getCell(1).setCellValue(workInterval);
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 1, 15));
        }

        if (hfromsa != 0 || mfromsa != 0 || htosa != 0 || mtosa != 0) {
            row = sheet.createRow(++rowCount);
            row.setHeightInPoints(18);
            row.createCell(0).setCellValue("RADNO VREME (Subota):");
            row.getCell(0).setCellStyle(mlStyleLocal);
            row.createCell(1);
            row.getCell(1).setCellStyle(mvStyleLocal);
            String saturdayInterval = String.format("%02d:%02d - %02d:%02d", hfromsa, mfromsa, htosa, mtosa);
            row.getCell(1).setCellValue(saturdayInterval);
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 1, 15));
        }

        if (hfromsu != 0 || mfromsu != 0 || htosu != 0 || mtosu != 0) {
            row = sheet.createRow(++rowCount);
            row.setHeightInPoints(18);
            row.createCell(0).setCellValue("RADNO VREME (Nedelja):");
            row.getCell(0).setCellStyle(mlStyleLocal);
            row.createCell(1);
            row.getCell(1).setCellStyle(mvStyleLocal);
            String sundayInterval = String.format("%02d:%02d - %02d:%02d", hfromsu, mfromsu, htosu, mtosu);
            row.getCell(1).setCellValue(sundayInterval);
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 1, 15));
        }

        // razmak između RADNO VREME blokova i tabele Reg./dani
        rowCount++;








        cellCount = 0;
        int tableStartRow = rowCount + 1;
        row = sheet.createRow(++rowCount);
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Reg.");
        //        row.createCell(cellCount);
        //        row.getCell(cellCount).setCellStyle(upperStyle);
        //        row.getCell(cellCount++).setCellValue("Proizv./Model");

        for (int i = 1; i <= days; i++) {
            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(upperStyle);
            row.getCell(cellCount++).setCellValue(i);
        }
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Ukupno");

        rowCount++;
        cellCount = 0;

        // Stil za brojeve sa 1 decimalom
        DataFormat dataFormat = workbook.createDataFormat();
        XSSFCellStyle decimalStyle = workbook.createCellStyle();
        decimalStyle.cloneStyleFrom(smolStyle);
        decimalStyle.setDataFormat(dataFormat.getFormat("0.0"));

        // Stil za sume sa 2 decimale
        XSSFCellStyle sumStyle = workbook.createCellStyle();
        sumStyle.cloneStyleFrom(rightStyle);
        sumStyle.setDataFormat(dataFormat.getFormat("0.00"));

        for (Ippm ippm : ippmArrayList) {
            row = sheet.createRow(rowCount);

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(textStyle);
            row.getCell(cellCount++).setCellValue(ippm.getRegistration());

            //            row.createCell(cellCount);
            //            row.getCell(cellCount).setCellStyle(textStyle);
            //            row.getCell(cellCount++).setCellValue(ippm.getManufacturer() + " " + ippm.getModel());
            double sum = 0;
            for (int i = 1; i <= days; i++) {
                row.createCell(cellCount);
                row.getCell(cellCount).setCellStyle(decimalStyle);
                if (ippm.getMap().containsKey(i)) {
                   // row.getCell(cellCount).setCellStyle(smolStyle);
                   // row.getCell(cellCount++).setCellValue(String.format("%.1f", ippm.getMap().get(i)));
                    row.getCell(cellCount++).setCellValue(ippm.getMap().get(i));
                    sum += ippm.getMap().get(i);
                } else {
                    row.getCell(cellCount).setCellStyle(smolStyle);
                    row.getCell(cellCount++).setCellValue(String.format("%.1f", 0.0));
                }
            }
            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(sumStyle);
            //row.getCell(cellCount++).setCellValue(String.format("%.2f", sum));
            row.getCell(cellCount++).setCellValue(sum);

            cellCount = 0;
            rowCount++;
        }

        for (int i = 0; i < 34; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception e) {
            }
        }

        // kolona 0 = širi labels ("RADNO VREME (Nedelja):" je ~22 char)
        if (sheet.getColumnWidth(0) < 22 * 256) sheet.setColumnWidth(0, 22 * 256);

        rowCount++;
        cellCount = 0;

        int max = 0;
        //        for (int i = 2; i < 34; i++) {
        //            try {
        //                if (sheet.getColumnWidth(i) > max) {
        //                    max = sheet.getColumnWidth(i);
        //                }
        //            } catch (Exception e) {
        //            }
        //        }

        //        for (int i = 2; i < 34; i++) {
        //            try {
        //                sheet.setColumnWidth(i, max);
        //            } catch (Exception e) {
        //            }
        //        }

        //
        //                String s = "";
        //        Date d = new Date();
        //        int x = d.getSeconds();
        //        int x2 = d.getMinutes();
        //        s = "excel" + x + "" + x2 + ".xlsx";
        //
        //
        //        try (FileOutputStream outputStream = new FileOutputStream(s)) {
        //            workbook.write(outputStream);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        centerDataCells(sheet, tableStartRow);

        if (export == 2) {
            return getPdf(workbook, true);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }

        byte[] bytes = bos.toByteArray();
        byte[] encoded = Base64.getEncoder().encode(bytes);

        return encoded;
    }

    /**
     * neko testiranje je palo ovde za export temperatura
     */
    public byte[] tempExport(ArrayList<Temperature> temperatureArrayList, String dateFromS, String dateToS, int export) {
        return null; //grafik samo
    }

    /**
     * @param lat1 geogr duzina 1
     * @param lon1 geogr sirina 1
     * @param lat2 geogr duzina 2
     * @param lon2 geogr sirina 2
     * @return distanca izmedju dve tacke
     */
    private double distance1(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = (double) Math.sin(deg2rad(lat1)) * (double) Math.sin(deg2rad(lat2)) + (double) Math.cos(deg2rad(lat1)) * (double) Math.cos(deg2rad(lat2))
                * (double) Math.cos(deg2rad(theta));
        dist = (double) Math.acos(dist);
        dist = (double) rad2deg(dist);
        dist = (double) dist * 60.0 * 1.1515 * 1.609344;
        return (double) (dist);
    }

    /**
     * KONVERTUJE
     *
     * @param deg STEPEN
     * @return RADIJAN
     */
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * KONVERTUJE
     *
     * @param rad RADIJAN
     * @return STEPEN
     */
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * FORMATIRA U STRING NEKU DUZINU VREMENA
     *
     * @param duration duzina vremena
     * @return formatirani string
     */

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    /**
     * Vraca sve intervencije vozila u nekom vremenskom intervalu
     *
     * @param dateFrom  datum od
     * @param dateTo    datum do
     * @param vehicleId vozilo
     * @return lista intervencija
     * @throws Exception ako vozilo ne postoji
     */
    @Transactional(readOnly = true)
    public Response<List<InterventionDTO>> findIntervention(Date dateFrom, Date dateTo, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }

        List<InterventionDTO> list = interventionRepository.findByVehicleVehicleIdAndDoneDateBetween(vehicleId, dateFrom, dateTo)
                .stream().map(InterventionDTO::from).collect(Collectors.toList());
        return new Response<>(list);

    }

    @Transactional(readOnly = true)
    public List<InterventionDTO> findInterventionsForIds(Date dateFrom, Date dateTo, List<Integer> vehicleIds) throws Exception {
        return interventionRepository.findByVehicleVehicleIdInAndDoneDateBetween(vehicleIds, dateFrom, dateTo)
                .stream().map(InterventionDTO::from).collect(Collectors.toList());
    }

    public byte[] interventionExport(List<InterventionDTO> interventions, int export, Date dateFrom, Date dateTo) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Interventions");

        // Stilovi
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        XSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setWrapText(true);
        textStyle.setAlignment(HorizontalAlignment.LEFT);
        textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        textStyle.setVerticalAlignment(VerticalAlignment.TOP);

        XSSFCellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setWrapText(true);

        // Stil za opis
        XSSFCellStyle descriptionStyle = workbook.createCellStyle();
        descriptionStyle.setWrapText(true);
        descriptionStyle.setAlignment(HorizontalAlignment.LEFT);
        descriptionStyle.setVerticalAlignment(VerticalAlignment.TOP);

        // Stil za napomenu
        XSSFCellStyle noteStyle = workbook.createCellStyle();
        noteStyle.setWrapText(true);
        noteStyle.setAlignment(HorizontalAlignment.LEFT);
        noteStyle.setVerticalAlignment(VerticalAlignment.TOP);

        int rowCount = 0;

        // Naslov
        Row row = sheet.createRow(rowCount++);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        row.createCell(0).setCellValue("Izveštaj o intervencijama");
        row.getCell(0).setCellStyle(headerStyle);

        XSSFCellStyle grayStyle = workbook.createCellStyle();
        grayStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 220, 220)));
        grayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        grayStyle.setAlignment(HorizontalAlignment.CENTER);
        grayStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        grayStyle.setBorderBottom(BorderStyle.THIN);
        XSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);
        grayStyle.setFont(boldFont);

        // Period
        row = sheet.createRow(rowCount++);
        row.createCell(0).setCellValue("OD: " + new SimpleDateFormat("dd.MM.yyyy").format(dateFrom));
        row.getCell(0).setCellStyle(grayStyle);
        row = sheet.createRow(rowCount++);
        row.createCell(0).setCellValue("DO: " + new SimpleDateFormat("dd.MM.yyyy").format(dateTo));
        row.getCell(0).setCellStyle(grayStyle);

        // Header kolone
        int tableStartRow = rowCount + 1;
        row = sheet.createRow(++rowCount);
        String[] headers = {"Registarski broj", "Proizvodjac/Model", "Servisna lokacija", "Datum", "Opis", "Cena", "Napomena"};
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
            row.getCell(i).setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 20 * 256);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        sheet.setColumnWidth(0, 12 * 256); // Registarski broj - uža
        sheet.setColumnWidth(1, 20 * 256); // Proizvođač/Model
        sheet.setColumnWidth(2, 20 * 256); // Servisna lokacija
        sheet.setColumnWidth(3, 12 * 256); // Datum - uža
        sheet.setColumnWidth(4, 40 * 256); // Opis - šira (wrap)
        sheet.setColumnWidth(5, 10 * 256); // Cena - uža, dovoljno da se vidi broj
        sheet.setColumnWidth(6, 15 * 256); // Napomena - uža, wrap-ovana

        // Podaci
        for (InterventionDTO intervention : interventions) {
            row = sheet.createRow(++rowCount);
            int cellCount = 0;

            row.createCell(cellCount).setCellValue(intervention.getVehicle().getRegistration());
            row.getCell(cellCount++).setCellStyle(centerStyle);

            row.createCell(cellCount).setCellValue(intervention.getVehicle().getModel());
            row.getCell(cellCount++).setCellStyle(textStyle);

            row.createCell(cellCount).setCellValue(intervention.getServiceLocation() != null ? intervention.getServiceLocation().getName() : "");
            row.getCell(cellCount++).setCellStyle(textStyle);

            row.createCell(cellCount).setCellValue(intervention.getDoneDate() != null ? dateFormat.format(intervention.getDoneDate()) : "");
            row.getCell(cellCount++).setCellStyle(centerStyle);

            row.createCell(cellCount).setCellValue(intervention.getDescription());
            row.getCell(cellCount++).setCellStyle(descriptionStyle);

            row.createCell(cellCount).setCellValue(intervention.getPrice());
            row.getCell(cellCount++).setCellStyle(centerStyle);

            row.createCell(cellCount).setCellValue(intervention.getNote());
            row.getCell(cellCount++).setCellStyle(noteStyle);

            //row.setHeight((short) -1);
            autoResizeRow(row, sheet, workbook);
        }

//        for (int i = 0; i <= rowCount; i++) {
//            Row currentRow = sheet.getRow(i);
//            if (currentRow != null) {
//                currentRow.setHeight((short) -1);
//            }
//        }

        centerDataCells(sheet, tableStartRow);

        if (export == 2) {
            return getPdf(workbook, true);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }

        byte[] bytes = bos.toByteArray();
        return Base64.getEncoder().encode(bytes);
    }

    private void autoResizeRow(Row row, XSSFSheet sheet, XSSFWorkbook workbook) {
        int maxLines = 1; // minimalna visina

        for (Cell cell : row) {
            CellStyle style = cell.getCellStyle();
            if (style.getWrapText() && cell.getCellType() == Cell.CELL_TYPE_STRING) {
                String text = cell.getStringCellValue();
                int columnWidth = sheet.getColumnWidth(cell.getColumnIndex()) / 256; // širina u karakterima
                String[] lines = text.split("\n");
                int linesCount = 0;
                for (String line : lines) {
                    // koliko linija zauzima ovaj deo teksta u toj širini kolone
                    linesCount += Math.ceil((double) line.length() / columnWidth);
                }
                if (linesCount > maxLines) {
                    maxLines = linesCount;
                }
            }
        }

        // Postavi visinu reda prema najvećem broju linija u redu
        if (maxLines > 1) {
            float fontHeight = workbook.getFontAt(row.getCell(0).getCellStyle().getFontIndex()).getFontHeightInPoints();
            row.setHeightInPoints(maxLines * fontHeight * 1.2f); // faktor 1.2 daje padding
        }
    }


    /**
     * Vraca potrosnju vozila u intervalu
     *
     * @param dateFrom  datum od
     * @param dateTo    datum do
     * @param vehicleId vozilo
     * @return lista potrosnji
     * @throws Exception ako vozilo ne postoji
     */
    public Response<List<FuelConsumption>> findFuel(Date dateFrom, Date dateTo, int vehicleId) throws Exception {
//        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
//        if (!optionalVehicle.isPresent()) {
//            throw new Exception("Invalid vehicle id");
//        }

        List<FuelConsumption> list = fuelConsumptionRepository.findByVehicleVehicleIdAndDateBetween(vehicleId, dateFrom, dateTo);
        return new Response<>(list);
    }

    public Map<Integer, List<FuelConsumption>> findFuelForVehicles(Date dateFrom, Date dateTo, List<Integer> vehicleIds) throws Exception {
        if(vehicleIds ==null || vehicleIds.isEmpty()){
            throw new Exception("Vehicle ids list is empty");
        }
        List<FuelConsumption> list = fuelConsumptionRepository.findByVehicleVehicleIdInAndDateBetween(vehicleIds, dateFrom, dateTo);
        Map<Integer, List<FuelConsumption>> grouped = list.stream()
                .collect(Collectors.groupingBy(fc -> fc.getVehicle().getVehicleId()));
        return grouped;
    }

    public byte[] fuelForVehiclesExport(Date dateFrom, Date dateTo, Map<Integer, List<FuelConsumption>> list, int export) throws Exception {
        if (list == null || list.isEmpty()) {
            return null;
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Potrošnja goriva");

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        XSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        textStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        DataFormat format = workbook.createDataFormat();
        XSSFCellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat(format.getFormat("0.00"));
        numberStyle.setAlignment(HorizontalAlignment.RIGHT);

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String timeFrom = dateFrom != null ? df.format(dateFrom) : "-";
        String timeTo = dateTo != null ? df.format(dateTo) : "-";

        int rowCount = 0;

        Row titleRow = sheet.createRow(rowCount++);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(headerStyle);
        titleCell.setCellValue("Izveštaj o potrošnji goriva");

        Row periodRow = sheet.createRow(rowCount++);
        periodRow.createCell(0).setCellValue("Od: " + timeFrom + "  Do: " + timeTo);

        rowCount++;

        int tableStartRow = rowCount;
        Row headerRow = sheet.createRow(rowCount++);
        String[] headers = {"Vozilo", "Gorivo", "Količina (l)", "Gorivna kompanija", "Iznos", "Gorivna stanica", "Datum", "Vozač", "Broj računa", "Kilometraža"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(headers[i]);
        }

        for (Map.Entry<Integer, List<FuelConsumption>> entry : list.entrySet()) {
            for (FuelConsumption fc : entry.getValue()) {
                Row row = sheet.createRow(rowCount++);
                int c = 0;

                row.createCell(c).setCellValue(fc.getVehicle() != null ? fc.getVehicle().getRegistration() : "-"); c++;
                row.createCell(c).setCellValue(fc.getFuel() != null ? fc.getFuel() : "-"); c++;
                Cell valueCell = row.createCell(c);
                valueCell.setCellStyle(numberStyle);
                valueCell.setCellValue(fc.getAmount()); c++;
                row.createCell(c).setCellValue(fc.getFuelCompany() != null ? fc.getFuelCompany().getName() : "-"); c++;
                Cell amountCell = row.createCell(c);
                amountCell.setCellStyle(numberStyle);
                amountCell.setCellValue(fc.getValue()); c++;
                row.createCell(c).setCellValue(fc.getFuelStation() != null ? fc.getFuelStation().getName() : "-"); c++;
                row.createCell(c).setCellValue(fc.getDate() != null ? df.format(fc.getDate()) : "-"); c++;
                row.createCell(c).setCellValue(fc.getDriver() != null ? fc.getDriver().getName() : "-"); c++;
                row.createCell(c).setCellValue(fc.getAccount() != null ? fc.getAccount() : "-"); c++;
                Cell kmCell = row.createCell(c);
                kmCell.setCellStyle(numberStyle);
                kmCell.setCellValue(fc.getKm());
            }
        }

        for (int i = 0; i < headers.length; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception ignored) {}
        }

        centerDataCells(sheet, tableStartRow);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {

            if (export == 2) {
                return getPdf(workbook, false);
            }

            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
            byte[] encoded = Base64.getEncoder().encode(bytes);

            return encoded;

        } catch (IOException e) {
            throw new Exception("Greška prilikom generisanja fajla", e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                // ignore
            }
            try {
                workbook.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Vraca sve registracije nekog vozila u daotm intervalu
     *
     * @param dateFrom  interval:
     * @param dateTo
     * @param vehicleId vozilo
     * @return
     * @throws Exception
     */
    public Response<List<Registration>> findRegistration(Date dateFrom, Date dateTo, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }

        List<Registration> list = registrationRepository.findByVehicleVehicleIdAndRegistrationDateBetween(vehicleId, dateFrom, dateTo);
        return new Response<>(list);

    }

    public List<Registration> findRegistrationsForIDs(Date dateFrom, Date dateTo, List<Integer> vehicleIds) throws Exception {
        return registrationRepository.findByVehicleVehicleIdInAndRegistrationDateBetween(vehicleIds, dateFrom, dateTo);
    }

    public byte[] registrationsExport(List<Registration> registrations, Date dateFrom, Date dateTo, int export) throws Exception {
        if (registrations == null || registrations.isEmpty()) {
            return null;
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Registrations");

        // Stilovi
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        XSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setWrapText(true);
        textStyle.setAlignment(HorizontalAlignment.LEFT);
        textStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFCellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setWrapText(true);

        // Stil za napomenu
        XSSFCellStyle noteStyle = workbook.createCellStyle();
        noteStyle.setWrapText(true);
        noteStyle.setAlignment(HorizontalAlignment.LEFT);
        noteStyle.setVerticalAlignment(VerticalAlignment.TOP);

        int rowCount = 0;

        // Naslov
        Row row = sheet.createRow(rowCount++);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        row.createCell(0).setCellValue("Izveštaj o registracijama vozila");
        row.getCell(0).setCellStyle(headerStyle);

        // Sivi stil za OD/DO
        XSSFCellStyle grayStyle = workbook.createCellStyle();
        grayStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 220, 220)));
        grayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        grayStyle.setAlignment(HorizontalAlignment.CENTER);
        grayStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        grayStyle.setBorderBottom(BorderStyle.THIN);
        XSSFFont boldFont = workbook.createFont();
        boldFont.setBold(true);
        grayStyle.setFont(boldFont);

        // Period
        row = sheet.createRow(rowCount++);
        row.createCell(0).setCellValue("OD: " + new SimpleDateFormat("dd.MM.yyyy").format(dateFrom));
        row.getCell(0).setCellStyle(grayStyle);
        row = sheet.createRow(rowCount++);
        row.createCell(0).setCellValue("DO: " + new SimpleDateFormat("dd.MM.yyyy").format(dateTo));
        row.getCell(0).setCellStyle(grayStyle);

        // Header kolone
        int tableStartRow = rowCount + 1;
        row = sheet.createRow(++rowCount);
        String[] headers = {"Vozilo", "Datum registracije", "Datum isteka", "Odgovorna osoba", "Iznos registracije", "Napomena"};
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
            row.getCell(i).setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 20 * 256);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        // Podaci
        for (Registration registration : registrations) {
            row = sheet.createRow(++rowCount);
            int cellCount = 0;

            row.createCell(cellCount).setCellValue(
                    registration.getVehicle() != null && registration.getVehicle().getRegistration() != null
                            ? registration.getVehicle().getRegistration()
                            : ""
            );
            row.getCell(cellCount++).setCellStyle(centerStyle);

            row.createCell(cellCount).setCellValue(
                    registration.getRegistrationDate() != null ? dateFormat.format(registration.getRegistrationDate()) : ""
            );
            row.getCell(cellCount++).setCellStyle(centerStyle);

            row.createCell(cellCount).setCellValue(
                    registration.getRegistrationExpiryDate() != null ? dateFormat.format(registration.getRegistrationExpiryDate()) : ""
            );
            row.getCell(cellCount++).setCellStyle(centerStyle);

            row.createCell(cellCount).setCellValue(
                    registration.getResponsible() != null ? registration.getResponsible() : ""
            );
            row.getCell(cellCount++).setCellStyle(textStyle);

            row.createCell(cellCount).setCellValue(registration.getAmount());
            row.getCell(cellCount++).setCellStyle(centerStyle);

            row.createCell(cellCount).setCellValue(
                    registration.getNote() != null ? registration.getNote() : ""
            );
            row.getCell(cellCount++).setCellStyle(noteStyle);
            autoResizeRow(row, sheet, workbook);
        }

        centerDataCells(sheet, tableStartRow);

        // Ako je PDF eksport
        if (export == 2) {
            return getPdf(workbook, true);
        }

        // Inače Excel eksport
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }

        byte[] bytes = bos.toByteArray();
        return Base64.getEncoder().encode(bytes);
    }

    /**
     * Vraca svu potrosnju jednog vozaca u nekom intervalu
     */

    public Response<List<FuelConsumption>> findDriverFuel(Date dateFrom, Date dateTo, int driverId) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Invalid driver id");
        }

        List<FuelConsumption> list = fuelConsumptionRepository.findByDriverDriverIdAndDateBetween(driverId, dateFrom, dateTo);
        return new Response<>(list);
    }

    /**
     * Vraca sve kazne vozaca u nekom intervalu
     */
    public Response<List<Ticket>> findDriverTicket(Date dateFrom, Date dateTo, int driverId) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Invalid driver id");
        }
        List<Ticket> list = ticketRepository.findByDriverDriverIdAndDateBetween(driverId, dateFrom, dateTo);
        return new Response<>(list);

    }

    /**
     * Vraca sve putne naloge jednog vozacau datom intervalu
     */
    public Response<List<PN>> findDriverPN(Date dateFrom, Date dateTo, int driverId) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Invalid driver id");
        }

        List<PN> list = pnRepository.findByDriverDriverIdAndDateBetween(driverId, dateFrom, dateTo);
        return new Response<>(list);

    }

    /**
     * Izvestaj o povredi ruta — multi-vehicle.
     * Za svako vozilo ide blok: naslov + header + data redovi + "Ukupno" red sa sumama.
     *
     * @param perVehicle insertion-ordered mapa imei → lista povreda za to vozilo
     * @return izveštaj u byte array-u (Base64 XLS ili PDF ako eid==2)
     */
    public byte[] exportRouteIskiakanje(LinkedHashMap<String, List<DTORotue>> perVehicle, int eid, long dateFromS, long dateToS, String firmName) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Povreda rute");

        // sortiraj povrede po vremenu početka unutar svakog vozila
        for (List<DTORotue> list : perVehicle.values()) {
            if (list == null) continue;
            Collections.sort(list, new Comparator<DTORotue>() {
                @Override
                public int compare(DTORotue o1, DTORotue o2) {
                    if (o1.getStartTime().before(o2.getStartTime()))
                        return -1;
                    return 1;
                }
            });
        }

        // stilovi
        XSSFFont boldFont11 = workbook.createFont();
        boldFont11.setFontHeightInPoints((short) 11);
        boldFont11.setBold(true);

        XSSFCellStyle vehicleTitleStyle = workbook.createCellStyle();
        vehicleTitleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        vehicleTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        vehicleTitleStyle.setBorderBottom(BorderStyle.MEDIUM);
        XSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 12);
        titleFont.setBold(true);
        vehicleTitleStyle.setFont(titleFont);
        vehicleTitleStyle.setAlignment(HorizontalAlignment.LEFT);
        vehicleTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFCellStyle upperStyle = workbook.createCellStyle();
        upperStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        upperStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        upperStyle.setBorderBottom(BorderStyle.MEDIUM);
        upperStyle.setFont(boldFont11);
        upperStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle ukupnoStyle = workbook.createCellStyle();
        ukupnoStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240, 240, 240)));
        ukupnoStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        ukupnoStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // levo poravnanje kao data redovi iznad (oni ne postavljaju alignment, pa uzimaju default LEFT)
        ukupnoStyle.setAlignment(HorizontalAlignment.LEFT);
        ukupnoStyle.setFont(boldFont11);

        SimpleDateFormat dfHeader = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));

        String period = "Od: " + dfHeader.format(new Timestamp(dateFromS)) + "  Do: " + dfHeader.format(new Timestamp(dateToS));
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o povredama ruta", firmName, period);
        int tableStartRow = rowCount;

        final String[] HEADERS = {
                "Registracija", "Proizvođač / Model", "Naziv rute",
                "Vreme početka", "Vreme kraja", "Pređeni put",
                "Vreme vožnje", "Vreme mirovanja", "Vreme stajanja"
        };
        final int COLS = HEADERS.length;

        // proširi border bottom title reda preko svih COLS kolona (route-fallout ima 9).
        // addExcelReportHeader pravi merge 0-7 sa borderBottom samo na ćeliji 0, pa
        // vizuelno linija ide samo do kolone H. Uklanjamo stari merge, postavljamo border
        // na svaku ćeliju 0-(COLS-1), pa dodajemo novi merge preko punih 9 kolona.
        int headerTitleRowIdx = 4; // pozicija "Izveštaj o povredama ruta" reda u shared header-u
        Row headerTitleRow = sheet.getRow(headerTitleRowIdx);
        if (headerTitleRow != null && headerTitleRow.getCell(0) != null) {
            for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
                CellRangeAddress region = sheet.getMergedRegion(i);
                if (region.getFirstRow() == headerTitleRowIdx && region.getFirstColumn() == 0) {
                    sheet.removeMergedRegion(i);
                    break;
                }
            }
            XSSFCellStyle headerTitleStyle = (XSSFCellStyle) headerTitleRow.getCell(0).getCellStyle();
            for (int col = 0; col < COLS; col++) {
                if (headerTitleRow.getCell(col) == null) {
                    headerTitleRow.createCell(col);
                }
                headerTitleRow.getCell(col).setCellStyle(headerTitleStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(headerTitleRowIdx, headerTitleRowIdx, 0, COLS - 1));
        }
        // eksplicitne širine kolona — autoSize razvuče header "Vreme stajanja" preko A4 landscape granice
        final int[] COL_WIDTHS = { 12, 16, 18, 16, 16, 12, 14, 14, 14 };
        for (int c = 0; c < COLS; c++) {
            sheet.setColumnWidth(c, COL_WIDTHS[c] * 256);
        }
        // garantuje fit na jednu stranicu po širini pri Gotenberg/LibreOffice konverziji
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setFitWidth((short) 1);
        sheet.getPrintSetup().setFitHeight((short) 0);
        sheet.getPrintSetup().setLandscape(true);

        boolean firstVehicle = true;
        for (Map.Entry<String, List<DTORotue>> entry : perVehicle.entrySet()) {
            List<DTORotue> list = entry.getValue();
            if (list == null || list.isEmpty()) continue;

            Vehicle v = list.get(0).getVehicle();
            String regAndModel = v.getRegistration() + " - " + v.getManufacturer() + " / " + v.getModel();

            // prazan red između vozila
            if (!firstVehicle) {
                rowCount++;
            }
            firstVehicle = false;

            // naslov vozila (merge preko svih kolona)
            Row titleRow = sheet.createRow(rowCount);
            for (int c = 0; c < COLS; c++) {
                titleRow.createCell(c);
                titleRow.getCell(c).setCellStyle(vehicleTitleStyle);
            }
            titleRow.getCell(0).setCellValue(regAndModel);
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, COLS - 1));
            titleRow.setHeightInPoints(20);
            rowCount++;

            // header kolona
            Row hdr = sheet.createRow(rowCount++);
            for (int c = 0; c < COLS; c++) {
                hdr.createCell(c);
                hdr.getCell(c).setCellStyle(upperStyle);
                hdr.getCell(c).setCellValue(HEADERS[c]);
            }

            // data redovi i sume
            double sumPredjeniPut = 0.0;
            long sumTimeOfTravel = 0L;
            long sumIdleTime = 0L;
            long sumStoppedTime = 0L;

            for (DTORotue d : list) {
                Row row = sheet.createRow(rowCount++);
                int cc = 0;
                row.createCell(cc++).setCellValue(d.getVehicle().getRegistration());
                row.createCell(cc++).setCellValue(d.getVehicle().getManufacturer() + "(" + d.getVehicle().getModel() + ")");
                row.createCell(cc++).setCellValue(d.getRouteName());
                row.createCell(cc++).setCellValue(sdf.format(d.getStartTime()));
                row.createCell(cc++).setCellValue(sdf.format(d.getEndTime()));
                row.createCell(cc++).setCellValue(String.format("%.2f", d.getRoadTraveled()));
                row.createCell(cc++).setCellValue(formatSeconds((int) d.getTimeOfTravel() / 1000));
                row.createCell(cc++).setCellValue(formatSeconds((int) d.getIdleTime() / 1000));
                row.createCell(cc++).setCellValue(formatSeconds((int) d.getStoppedTime() / 1000));

                sumPredjeniPut += d.getRoadTraveled();
                sumTimeOfTravel += d.getTimeOfTravel();
                sumIdleTime += d.getIdleTime();
                sumStoppedTime += d.getStoppedTime();
            }

            // "Ukupno" red — Reg, Model, Naziv rute, Vreme početka, Vreme kraja prazni; sume po koloni
            Row uku = sheet.createRow(rowCount++);
            for (int cc = 0; cc < COLS; cc++) {
                uku.createCell(cc);
                uku.getCell(cc).setCellStyle(ukupnoStyle);
            }
            uku.getCell(0).setCellValue("Ukupno");
            uku.getCell(5).setCellValue(String.format("%.2f", sumPredjeniPut));
            uku.getCell(6).setCellValue(formatSeconds((int) (sumTimeOfTravel / 1000)));
            uku.getCell(7).setCellValue(formatSeconds((int) (sumIdleTime / 1000)));
            uku.getCell(8).setCellValue(formatSeconds((int) (sumStoppedTime / 1000)));
        }

        centerDataCells(sheet, tableStartRow);

        if (eid == 2) {
            return getPdf(workbook, true);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }

        byte[] bytes = bos.toByteArray();
        byte[] encoded = Base64.getEncoder().encode(bytes);

        return encoded;

    }

    /**
     * @param timeInSeconds vreme, sekunde
     *                      formatira vreme u string hh:mm:ss formatu
     */
    public static String formatSeconds(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + ":";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds;

        return formattedTime;
    }

    /**
     * Vraca izvestaj geozona
     *
     * @param vgrList
     * @param eid
     * @return
     * @throws Exception
     */
    public byte[] geozoneExcport(List<VGR> vgrList, int eid, long from, long timeTo, String firmName) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sh = wb.createSheet("Geozone");

        Collections.sort(vgrList, new Comparator<VGR>() {
            @Override
            public int compare(VGR o1, VGR o2) {
                if (o1.getEntryTime() < o2.getEntryTime()) return -1;
                return 1;
            }
        });

        // ===== FONTS =====
        XSSFFont titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);

        XSSFFont headerFont = wb.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 8);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        XSSFFont bodyFont = wb.createFont();
        bodyFont.setFontHeightInPoints((short) 8);

        XSSFFont metaBoldFont = wb.createFont();
        metaBoldFont.setBold(true);
        metaBoldFont.setFontHeightInPoints((short) 9);

        // ===== STYLES =====
        XSSFCellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.LEFT);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderBottom(BorderStyle.MEDIUM);

        XSSFCellStyle metaLabel = wb.createCellStyle();
        metaLabel.setFont(metaBoldFont);
        metaLabel.setAlignment(HorizontalAlignment.LEFT);
        metaLabel.setVerticalAlignment(VerticalAlignment.CENTER);
        metaLabel.setFillForegroundColor(rgb(220, 223, 227));
        metaLabel.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle metaValue = wb.createCellStyle();
        metaValue.setFont(bodyFont);
        metaValue.setAlignment(HorizontalAlignment.LEFT);
        metaValue.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFCellStyle header = wb.createCellStyle();
        header.setFont(headerFont);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setVerticalAlignment(VerticalAlignment.CENTER);
        header.setWrapText(true);
        header.setFillForegroundColor(rgb(44, 115, 178));
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        header.setBorderBottom(BorderStyle.THIN);
        header.setBorderTop(BorderStyle.THIN);
        header.setBorderLeft(BorderStyle.THIN);
        header.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle bodyLeft = wb.createCellStyle();
        bodyLeft.setFont(bodyFont);
        bodyLeft.setAlignment(HorizontalAlignment.LEFT);
        bodyLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyLeft.setBorderBottom(BorderStyle.THIN);
        bodyLeft.setBorderTop(BorderStyle.THIN);
        bodyLeft.setBorderLeft(BorderStyle.THIN);
        bodyLeft.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle bodyCenter = wb.createCellStyle();
        bodyCenter.cloneStyleFrom(bodyLeft);
        bodyCenter.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle bodyLeftAlt = wb.createCellStyle();
        bodyLeftAlt.cloneStyleFrom(bodyLeft);
        bodyLeftAlt.setFillForegroundColor(rgb(241, 245, 249));
        bodyLeftAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle bodyCenterAlt = wb.createCellStyle();
        bodyCenterAlt.cloneStyleFrom(bodyCenter);
        bodyCenterAlt.setFillForegroundColor(rgb(241, 245, 249));
        bodyCenterAlt.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));

        // ===== LOGO =====
        int rIdx = 0;
        try {
            InputStream logoStream = getClass().getResourceAsStream("/images/oris-logo.png");
            if (logoStream != null) {
                byte[] logoBytes = IOUtils.toByteArray(logoStream);
                logoStream.close();
                int pictureIdx = wb.addPicture(logoBytes, Workbook.PICTURE_TYPE_PNG);
                XSSFDrawing drawing = sh.createDrawingPatriarch();
                XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 0, 2, 3);
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                drawing.createPicture(anchor, pictureIdx);
            }
        } catch (Exception ignored) {}

        for (int i = 0; i < 3; i++) {
            Row lr = sh.createRow(rIdx++);
            lr.setHeightInPoints(20);
        }
        rIdx++;

        // ===== NASLOV =====
        Row r = sh.createRow(rIdx++);
        r.setHeightInPoints(28);
        r.createCell(0).setCellValue("Izveštaj o geozonama");
        r.getCell(0).setCellStyle(titleStyle);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 0, 7));
        rIdx++;

        // ===== META =====
        r = sh.createRow(rIdx++);
        r.setHeightInPoints(18);
        r.createCell(0).setCellValue("Kompanija:");
        r.getCell(0).setCellStyle(metaLabel);
        r.createCell(1).setCellValue(firmName != null ? firmName : "");
        r.getCell(1).setCellStyle(metaValue);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 1, 3));

        r = sh.createRow(rIdx++);
        r.setHeightInPoints(18);
        r.createCell(0).setCellValue("Generisano:");
        r.getCell(0).setCellStyle(metaLabel);
        String generatedAt = java.time.LocalDateTime.now(java.time.ZoneId.of("Europe/Belgrade"))
                .format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        r.createCell(1).setCellValue(generatedAt);
        r.getCell(1).setCellStyle(metaValue);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 1, 3));

        r = sh.createRow(rIdx++);
        r.setHeightInPoints(18);
        r.createCell(0).setCellValue("Period:");
        r.getCell(0).setCellStyle(metaLabel);
        r.createCell(1).setCellValue("Od: " + df.format(new Date(from)) + "  Do: " + df.format(new Date(timeTo)));
        r.getCell(1).setCellStyle(metaValue);
        sh.addMergedRegion(new CellRangeAddress(rIdx - 1, rIdx - 1, 1, 4));

        rIdx++;

        // ===== HEADER TABELE =====
        String[] cols = {
                "Registracija", "Proizvođač/\nModel", "Naziv\ngeozone",
                "Vreme\nmirovanja", "Vreme\nstajanja", "Pređeni\nput",
                "Vreme ulaska", "Vreme izlaska",
                "Poč. gorivo", "Kraj. gorivo", "Razlika\ngoriva", "Vozač"
        };

        int tableStartRow = rIdx;
        Row hr = sh.createRow(rIdx++);
        hr.setHeightInPoints(30);
        for (int c = 0; c < cols.length; c++) {
            hr.createCell(c).setCellValue(cols[c]);
            hr.getCell(c).setCellStyle(header);
        }

        // ===== DATA =====
        int dataRowIdx = 0;
        for (VGR item : vgrList) {
            boolean alt = dataRowIdx % 2 == 1;
            XSSFCellStyle left = alt ? bodyLeftAlt : bodyLeft;
            XSSFCellStyle center = alt ? bodyCenterAlt : bodyCenter;

            Row row = sh.createRow(rIdx++);
            row.setHeightInPoints(18);
            int c = 0;

            row.createCell(c).setCellValue(item.getVehicle().getRegistration());
            row.getCell(c++).setCellStyle(left);

            row.createCell(c).setCellValue(item.getVehicle().getManufacturer() + " (" + item.getVehicle().getModel() + ")");
            row.getCell(c++).setCellStyle(left);

            row.createCell(c).setCellValue(item.getGeozone().getName());
            row.getCell(c++).setCellStyle(left);

            row.createCell(c).setCellValue(formatSeconds((int) item.getMirovanje() / 1000));
            row.getCell(c++).setCellStyle(center);

            row.createCell(c).setCellValue(formatSeconds((int) item.getStajanje() / 1000));
            row.getCell(c++).setCellStyle(center);

            row.createCell(c).setCellValue(String.format("%.2f", item.getMillage()));
            row.getCell(c++).setCellStyle(center);

            row.createCell(c).setCellValue(sdf.format(item.getEntryTime()));
            row.getCell(c++).setCellStyle(center);

            row.createCell(c).setCellValue(sdf.format(item.getExitTime()));
            row.getCell(c++).setCellStyle(center);

            row.createCell(c).setCellValue(item.getFuelStart());
            row.getCell(c++).setCellStyle(center);

            row.createCell(c).setCellValue(item.getFuelEnd());
            row.getCell(c++).setCellStyle(center);

            row.createCell(c).setCellValue(item.getFuelSpend());
            row.getCell(c++).setCellStyle(center);

            row.createCell(c).setCellValue(item.getDriverName());
            row.getCell(c++).setCellStyle(left);

            dataRowIdx++;
        }

        // ===== COLUMN WIDTHS (characters * 256) =====
        sh.setColumnWidth(0, 12 * 256);  // Registracija
        sh.setColumnWidth(1, 14 * 256);  // Proizvođač/Model
        sh.setColumnWidth(2, 14 * 256);  // Naziv geozone
        sh.setColumnWidth(3, 10 * 256);  // Vreme mirovanja
        sh.setColumnWidth(4, 10 * 256);  // Vreme stajanja
        sh.setColumnWidth(5, 9 * 256);   // Pređeni put
        sh.setColumnWidth(6, 14 * 256);  // Vreme ulaska
        sh.setColumnWidth(7, 14 * 256);  // Vreme izlaska
        sh.setColumnWidth(8, 9 * 256);   // Poč. gorivo
        sh.setColumnWidth(9, 9 * 256);   // Kraj. gorivo
        sh.setColumnWidth(10, 9 * 256);  // Razlika goriva
        sh.setColumnWidth(11, 14 * 256); // Vozač

        centerDataCells(sh, tableStartRow);

        if (eid == 2) {
            return getPdf(wb, true);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            wb.write(bos);
        } finally {
            bos.close();
        }

        byte[] bytes = bos.toByteArray();
        byte[] encoded = Base64.getEncoder().encode(bytes);
        return encoded;
    }

    /**
     * Vraca izvestaj o stajanjima
     *
     * @param list         lista stajanja
     * @param eid          =2 - vraca pdf, u suprotnom workbook
     * @param minIdle      minimalan period mirovanja u minutima
     * @param showMinIdle  ako true, prikazuje blok "Minimalan period mirovanja" i vrednost; ako false, taj blok se ne iscrtava
     * @throws Exception
     */
    public byte[] standingExport(List<Idle> list, int eid, String fromS, String toS, int min, int minIdle, boolean showMinIdle, String firmName) throws Exception {
        return standingExport(list, eid, fromS, toS, min, minIdle, showMinIdle, firmName, null);
    }

    public byte[] standingExport(List<Idle> list, int eid, String fromS, String toS, int min, int minIdle, boolean showMinIdle, String firmName, String warningMessage) throws Exception {

        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Stajanje");
            int stoppedTime = 0;

            //Stil upperStyle
            XSSFCellStyle upperStyle = workbook.createCellStyle();
            upperStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
            //style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
            upperStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            upperStyle.setBorderBottom(BorderStyle.MEDIUM);
            upperStyle.setAlignment(HorizontalAlignment.CENTER);
            upperStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            upperStyle.setAlignment(HorizontalAlignment.CENTER);
            upperStyle.setWrapText(true);


            //Stil locationStyle
            XSSFCellStyle locationStyle = workbook.createCellStyle();
            locationStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            locationStyle.setAlignment(HorizontalAlignment.CENTER);
            locationStyle.setWrapText(true);

            //Stil locationStyle
            XSSFCellStyle rest = workbook.createCellStyle();
            rest.setVerticalAlignment(VerticalAlignment.CENTER);
            rest.setAlignment(HorizontalAlignment.LEFT);

            //Stil locationStyle
            XSSFFont font2 = workbook.createFont();
            font2.setFontHeightInPoints((short) 11);
            font2.setBold(true);
            upperStyle.setFont(font2);

            //Stil locationStyle
            XSSFCellStyle textStyle = workbook.createCellStyle();
            textStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(222, 222, 222)));
            textStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            textStyle.setAlignment(HorizontalAlignment.LEFT);

            XSSFFont metaLabelFont = workbook.createFont();
            metaLabelFont.setBold(true);
            metaLabelFont.setFontHeightInPoints((short) 10);

            XSSFFont metaValueFont = workbook.createFont();
            metaValueFont.setFontHeightInPoints((short) 10);

            XSSFCellStyle metaLabelStyle = workbook.createCellStyle();
            metaLabelStyle.setFont(metaLabelFont);
            metaLabelStyle.setAlignment(HorizontalAlignment.LEFT);
            metaLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            metaLabelStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 223, 227)));
            metaLabelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            metaLabelStyle.setWrapText(true);

            XSSFCellStyle metaValueStyle = workbook.createCellStyle();
            metaValueStyle.setFont(metaValueFont);
            metaValueStyle.setAlignment(HorizontalAlignment.LEFT);
            metaValueStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            //Stil locationStyle
            XSSFCellStyle dateCellStyle2 = workbook.createCellStyle();
            dateCellStyle2.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 223, 227)));
            //style1.setFillPattern(CellStyle.SOLID_FOREGROUND);

            String period = "Od: " + fromS + "  Do: " + toS;
            int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o stajanjima", firmName, period, warningMessage);
            Row metaRow = sheet.createRow(rowCount++);
            metaRow.setHeightInPoints(18);
            XSSFRichTextString minStandingText = new XSSFRichTextString("Minimalno stajanje: " + min);
            minStandingText.applyFont(0, "Minimalno stajanje:".length(), metaLabelFont);
            minStandingText.applyFont("Minimalno stajanje:".length(), minStandingText.length(), metaValueFont);
            metaRow.createCell(0).setCellValue(minStandingText);
            metaRow.getCell(0).setCellStyle(metaLabelStyle);
            metaRow.createCell(1).setCellStyle(metaLabelStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowCount - 1, rowCount - 1, 0, 1));

            if (showMinIdle) {
                metaRow = sheet.createRow(rowCount++);
                metaRow.setHeightInPoints(18);
                XSSFRichTextString minIdleText = new XSSFRichTextString("Minimalno mirovanje: " + minIdle);
                minIdleText.applyFont(0, "Minimalno mirovanje:".length(), metaLabelFont);
                minIdleText.applyFont("Minimalno mirovanje:".length(), minIdleText.length(), metaValueFont);
                metaRow.createCell(0).setCellValue(minIdleText);
                metaRow.getCell(0).setCellStyle(metaLabelStyle);
                metaRow.createCell(1).setCellStyle(metaLabelStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowCount - 1, rowCount - 1, 0, 1));
            }

            rowCount++;
            int cellCount = 0;
            sheet.setColumnWidth(0, 5000);
            int tableStartRow = rowCount;
            Row row = sheet.createRow(rowCount++);


            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(upperStyle);
            row.getCell(cellCount).setCellValue("Reg.");
            sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 12 * 256);
            cellCount++;

            //        row.createCell(cellCount);
            //        row.getCell(cellCount).setCellStyle(upperStyle);
            //        row.getCell(cellCount++).setCellValue("Proiz./Mod.");
            //        row.createCell(cellCount);
            //        row.getCell(cellCount).setCellStyle(upperStyle);
            //        row.getCell(cellCount++).setCellValue("Tip vozila");
            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(upperStyle);
            sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 15 * 256);
            row.getCell(cellCount++).setCellValue("Vreme početka");

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(upperStyle);
            sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 15 * 256);
            row.getCell(cellCount++).setCellValue("Vreme kraja");

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(upperStyle);
            sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 10 * 256);
            row.getCell(cellCount++).setCellValue("Stajanje");

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(upperStyle);
            sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 30 * 256);
            row.getCell(cellCount++).setCellValue("Lokacija");


            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(upperStyle);
            sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 13 * 256);
            row.getCell(cellCount++).setCellValue("Kon. brava");

            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(upperStyle);
            sheet.setColumnWidth(row.getCell(cellCount).getColumnIndex(), 15 * 256);
            row.getCell(cellCount++).setCellValue("Kilometraža");

            for (Idle idle : list) {
                row = sheet.createRow(++rowCount);
                row.setHeightInPoints(30);

                cellCount = 0;

                row.createCell(cellCount);
                row.getCell(cellCount).setCellStyle(rest);
                row.getCell(cellCount++).setCellValue(idle.getVehicle().getRegistration());
                //            row.createCell(cellCount);
                //            row.getCell(cellCount++).setCellValue( idle.getVehicle().getModel());
                //            row.createCell(cellCount);
                //            row.getCell(cellCount++).setCellValue(idle.getVehicle().getVehicleType());

                Timestamp tsStart = new Timestamp(idle.getStart());
                Timestamp tsEnd = new Timestamp(idle.getEnd());

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                sdf.setTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));

                row.createCell(cellCount);
                row.getCell(cellCount).setCellStyle(locationStyle);
                row.getCell(cellCount++).setCellValue(sdf.format(tsStart));

                row.createCell(cellCount);
                row.getCell(cellCount).setCellStyle(locationStyle);
                row.getCell(cellCount++).setCellValue(sdf.format(tsEnd));

//                row.createCell(cellCount);
//                row.getCell(cellCount).setCellStyle(rest);
//                row.getCell(cellCount++).setCellValue(formatSeconds((int) idle.getTime() / 1000));
//                stoppedTime += idle.getTime() / 1000;
                long duration = idle.getEnd() - idle.getStart();

                row.createCell(cellCount);
                row.getCell(cellCount).setCellStyle(rest);
                row.getCell(cellCount++).setCellValue(formatSeconds((int) (duration / 1000)));
                stoppedTime += duration / 1000;

                row.createCell(cellCount);
                row.getCell(cellCount).setCellStyle(locationStyle);
                row.getCell(cellCount++).setCellValue(idle.getLocation());

                row.createCell(cellCount);
                row.getCell(cellCount).setCellStyle(rest);
                row.getCell(cellCount++).setCellValue(idle.isContactLock() ? "Aktivna" : "Deaktivirana");

                row.createCell(cellCount);
                row.getCell(cellCount).setCellStyle(rest);
                row.getCell(cellCount++).setCellValue(idle.getKilometers()!=null ? idle.getKilometers(): 0);

            }

            // Create a new row for the total idle time
            row = sheet.createRow(++rowCount);
            cellCount = 0; // Start from column 0

            // Merge columns 0, 1, and 2 for the label
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 2));

            // Style for total row
            XSSFCellStyle totalStyle = workbook.createCellStyle();
            totalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalStyle.setAlignment(HorizontalAlignment.CENTER);
            totalStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(200, 200, 200)));
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            totalStyle.setFont(boldFont);

            // Label "Ukupno vreme stajanja"
            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(totalStyle);
            row.getCell(cellCount).setCellValue("Ukupno vreme stajanja:");

            // Ensure all merged cells (columns 0, 1, 2) get the style
            for (int i = 1; i <= 2; i++) {
                row.createCell(i);
                row.getCell(i).setCellStyle(totalStyle);
            }

            // Add the total formatted time in column 3
            cellCount = 3;
            row.createCell(cellCount);
            row.getCell(cellCount).setCellStyle(totalStyle);
            row.getCell(cellCount).setCellValue(formatSeconds(stoppedTime));

            //        for (int i = 0; i < 7; i++) {
            //            try {
            //                sheet.autoSizeColumn(i);
            //            } catch (Exception e) {
            //            }
            //        }
            //        String s = "";
            //        Date d = new Date();
            //        int x = d.getSeconds();
            //        int x2 = d.getMinutes();
            //        s = "excel" + x + "" + x2 + ".xlsx";
            //
            //
            //        try (FileOutputStream outputStream = new FileOutputStream(s)) {
            //            workbook.write(outputStream);
            //        } catch (Exception e) {
            //            e.printStackTrace();
            //        }
            centerDataCells(sheet, tableStartRow);

            if (eid == 2) {
                //Ako je eid 2 pretvori u pdf
                return getPdf(workbook, true);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                workbook.write(bos);
            } finally {
                bos.close();
            }

            byte[] bytes = bos.toByteArray();
            byte[] encoded = Base64.getEncoder().encode(bytes);

            return encoded;
        }catch (Exception e){
            throw e;
        }

    }

    /**
     * @param eid =2 vraca pdf, a workbook u suprotnom
     * @return izvestaj o zelenoj voznji
     * @throws Exception
     */
    public byte[] greenExport(String res, String imei, Vehicle v, int eid, String fromS, String toS, String firmName) throws Exception {

        res = res.substring(8, res.length() - 1);

        ObjectMapper mapper = new ObjectMapper();
        List<Green> list = mapper.readValue(res, new TypeReference<List<Green>>() {
        });

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Stajanje");

        XSSFCellStyle upperStyle = workbook.createCellStyle();
        upperStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(165, 211, 242)));
        //style1.setFillPattern(CellStyle.SOLID_FOREGROUND);
        upperStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        upperStyle.setBorderBottom(BorderStyle.MEDIUM);

        XSSFFont font2 = workbook.createFont();
        font2.setFontHeightInPoints((short) 11);
        font2.setBold(true);
        upperStyle.setFont(font2);
        upperStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(222, 222, 222)));
        textStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle dateCellStyle2 = workbook.createCellStyle();
        dateCellStyle2.setFillForegroundColor(new XSSFColor(new java.awt.Color(220, 223, 227)));
        //style1.setFillPattern(CellStyle.SOLID_FOREGROUND);

        String period = "Od: " + fromS + "  Do: " + toS;
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o sigurnoj vožnji", firmName, period);
        int tableStartRow = rowCount;
        int cellCount = 0;
        Row row = sheet.createRow(rowCount++);

        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Reg.");
        //        row.createCell(cellCount);
        //        row.getCell(cellCount).setCellStyle(upperStyle);
        //        row.getCell(cellCount++).setCellValue("Proiz./Mod.");
        //        row.createCell(cellCount);
        //        row.getCell(cellCount).setCellStyle(upperStyle);
        //        row.getCell(cellCount++).setCellValue("Tip vozila");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Tip");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Vreme");
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Brzina");

        for (Green green : list) {
            row = sheet.createRow(++rowCount);
            cellCount = 0;

            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(v.getRegistration());
            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(v.getModel());
            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(v.getVehicleType());

            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(green.getType());

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));

            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(sdf.format(green.getTimestmap()));

            row.createCell(cellCount);
            row.getCell(cellCount++).setCellValue(green.getSspeed());

        }

        for (int i = 0; i < 7; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception e) {
            }
        }

        centerDataCells(sheet, tableStartRow);

        String s = "";
        Date d = new Date();
        int x = d.getSeconds();
        int x2 = d.getMinutes();
        s = "excel" + x + "" + x2 + ".xlsx";

        try (FileOutputStream outputStream = new FileOutputStream(s)) {
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (eid == 2) {
            return getPdf(workbook, true);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }

        byte[] bytes = bos.toByteArray();
        byte[] encoded = Base64.getEncoder().encode(bytes);

        return encoded;

    }

    public List<FmsFuelCompanyFuelConsumption> getFmsFuelCompanyConsumptionReport(List<Integer> ids, LocalDate fromDate, LocalDate toDate, Double maxSpeed,
            Double maxWheelSpin) {
        List<Vehicle> vehiclesByImeiIn = vehicleRepository.findVehiclesByVehicleIdIn(ids);

        List<FmsFuelCompanyFuelConsumption> fmsFuelCompanyConsumptionReport = galebRestComunication.getFmsFuelCompanyConsumptionReport(vehiclesByImeiIn,
                fromDate, toDate, maxSpeed, maxWheelSpin);

        for (FmsFuelCompanyFuelConsumption fmsFuelCompanyFuelConsumption : fmsFuelCompanyConsumptionReport) {
            List<FuelConsumption> byVehicleRegistrationAndDateBetween = fuelConsumptionRepository.findByVehicleRegistrationAndDateBetween(
                    fmsFuelCompanyFuelConsumption.getVehicleRegistration(), Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            double sum = 0.0;
            for (FuelConsumption fuelConsumption : byVehicleRegistrationAndDateBetween) {
                sum += fuelConsumption.getAmount();
            }
            fmsFuelCompanyFuelConsumption.setFuelFilled(sum);

            if (fmsFuelCompanyFuelConsumption.getFuelFilled() != null && fmsFuelCompanyFuelConsumption.getMileage() != null
                    && fmsFuelCompanyFuelConsumption.getFuelFilled() > 0.0) {
                fmsFuelCompanyFuelConsumption.setAverageFuelConsumption(
                        (fmsFuelCompanyFuelConsumption.getFuelFilled() / fmsFuelCompanyFuelConsumption.getMileage()) * 100);
            }
        }

        vehiclesByImeiIn.removeIf(x -> fmsFuelCompanyConsumptionReport.stream().anyMatch(y -> y.getVehicleRegistration().equals(x.getRegistration())));

        for (Vehicle vehicle : vehiclesByImeiIn) {
            FmsFuelCompanyFuelConsumption fmsFuelCompanyFuelConsumption = new FmsFuelCompanyFuelConsumption();

            List<KilometersAdministration> kilometersRecords = kilometersAdministrationRepository
                    .findAllByDateBetweenAndVehicle_Registration(fromDate.minusDays(2), toDate.plusDays(2), vehicle.getRegistration())
                    .stream()
                    .filter(km -> km.getMileage() != null) // Ensure mileage is not null
                    .sorted(Comparator.comparing(KilometersAdministration::getDate)) // Sort by date
                    .collect(Collectors.toList());

            if (!kilometersRecords.isEmpty()) {
                Optional<KilometersAdministration> minRecord = kilometersRecords.stream()
                        .filter(km -> !km.getDate().isAfter(fromDate.plusDays(2)))
                        .findFirst(); // Earliest within the range

                Optional<KilometersAdministration> maxRecord = kilometersRecords.stream()
                        .filter(km -> !km.getDate().isBefore(toDate.minusDays(2)))
                        .reduce((first, second) -> second); // Latest within the range

                if (minRecord.isPresent() && maxRecord.isPresent()) {
                    fmsFuelCompanyFuelConsumption.setMileage(maxRecord.get().getMileage() - minRecord.get().getMileage());
                } else {
                    fmsFuelCompanyFuelConsumption.setMileage(0.0);
                }
            } else {
                fmsFuelCompanyFuelConsumption.setMileage(0.0);
            }

            List<FuelConsumption> byVehicleRegistrationAndDateBetween = fuelConsumptionRepository.findByVehicleVehicleIdAndDateBetween(vehicle.getVehicleId(),
                    Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            double sum = 0.0;
            for (FuelConsumption fuelConsumption : byVehicleRegistrationAndDateBetween) {
                sum += fuelConsumption.getAmount();
            }
            fmsFuelCompanyFuelConsumption.setFuelFilled(sum);

            if (fmsFuelCompanyFuelConsumption.getFuelFilled() != null
                    && fmsFuelCompanyFuelConsumption.getMileage() != null && fmsFuelCompanyFuelConsumption.getMileage() != 0
                    && fmsFuelCompanyFuelConsumption.getFuelFilled() > 0.0) {
                fmsFuelCompanyFuelConsumption.setAverageFuelConsumption(
                        (fmsFuelCompanyFuelConsumption.getFuelFilled() / fmsFuelCompanyFuelConsumption.getMileage()) * 100
                );
            }

            fmsFuelCompanyFuelConsumption.setManufacturer(vehicle.getManufacturer());
            fmsFuelCompanyFuelConsumption.setModel(vehicle.getModel());
            fmsFuelCompanyFuelConsumption.setVehicleRegistration(vehicle.getRegistration());
            fmsFuelCompanyConsumptionReport.add(fmsFuelCompanyFuelConsumption);
        }
        return fmsFuelCompanyConsumptionReport;
    }

    public List<DailyMovementConsumptionReport> getDailyMovementConsumptionReport(LocalDateTime from, LocalDateTime to, List<String> imeis,
            Integer emptyingMargin, Integer fuelMargin) {
        List<Vehicle> vehiclesByImeiIn = vehicleRepository.findVehiclesByImeiIn(imeis);

        List<DailyMovementConsumptionReportAddutionalDataDTO> mappedList = new LinkedList<>();
        for (Vehicle vehicle : vehiclesByImeiIn) {
            mappedList.add(new DailyMovementConsumptionReportAddutionalDataDTO(vehicle.getRegistration(), vehicle.getManufacturer(), vehicle.getModel(),
                    vehicle.getFuelMargine() != null ? vehicle.getFuelMargine() : 0, vehicle.getImei()));
        }

        List<DailyMovementConsumptionReport> dailyMovementConsumptionReport = galebRestComunication.getDailyMovementConsumptionReport(mappedList, from, to,
                emptyingMargin, fuelMargin).getBody();
        return dailyMovementConsumptionReport;
    }



}

