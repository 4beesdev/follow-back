package rs.oris.back.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import rs.oris.back.config.MongoServerConfig;
import rs.oris.back.controller.wrapper.ForbiddenException;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Event;
import rs.oris.back.domain.*;
import rs.oris.back.domain.dto.*;
import rs.oris.back.domain.reports.fms_fuel_company_consumption.FmsFuelCompanyFuelConsumption;
import rs.oris.back.exceptions.VehicleNotFoundException;
import rs.oris.back.export.pdf.PdfExporter;
import rs.oris.back.export.pdf.impl.FuelConsumptionFuelCompaniesPdfExport;
import rs.oris.back.export.xml.XlsExporter;
import rs.oris.back.export.xml.impl.FuelConsumptionFuelCompaniesXlsExport;
import rs.oris.back.repository.DriverRepository;
import rs.oris.back.repository.GeozoneRepository;
import rs.oris.back.repository.RouteRepository;
import rs.oris.back.repository.VehicleRepository;
import rs.oris.back.service.*;

import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private GeozoneRepository geozoneRepository;
    @Autowired
    private UserReportService userReportService;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    ;

    @Autowired
    private KilometersAdministrationService kilometersAdministrationService;
    @Autowired
    private MongoServerConfig mongoServerConfig;
    @Autowired
    private RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    @PostMapping("/api/load/days")
    private String loadDays(@RequestBody DaysDTO daysDTO) throws Exception {
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/load/days";


        String result = restTemplate.postForObject(uri, daysDTO, String.class);

        return result;
    }

    @GetMapping("/api/find/zips")
    public List<String> findAllZippedDays() {
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/find/zips";


        List<String> result = restTemplate.getForObject(uri, List.class);

        return result;
    }

    @GetMapping("/api/loaded/days")
    public List<LoadedDays> findLoadedDays() {
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/loaded/days";



        List<LoadedDays> result = restTemplate.getForObject(uri, List.class);

        return result;
    }

    //    @GetMapping("/api/golub")
    //    private boolean snedMejl() throws Exception {
    //        String as = "galebele:5HdBRAT4";
    //        String encodedString = Base64.getEncoder().encodeToString(as.getBytes());
    //        System.out.println(encodedString);
    //        String uri = "https://bulk.mobile-gw.com:9090/api/message";
    //
    //
    //
    //        MediaType mediaType = MediaType.parseMediaType("application/json");
    //
    //        HttpHeaders headers = new HttpHeaders();
    //        headers.add("Authorization","Basic "+encodedString);
    //        headers.add("Content-Type", "application/json");
    //        headers.add("Cache-Control", "no-cache");
    //
    //
    //        Map<String, Object> map = new HashMap<>();
    //        map.put("sender", "Oris34");
    //        map.put("phoneNumber", "+381643345522");
    //        System.out.println("381643345522");
    //        map.put("text", "Radi pls");
    //        map.put("priority", "NORMAL");
    //        map.put("validity", 60);
    //        map.put("statusReport", 7);
    //        map.put("statusUrl", "https://host/dlr");
    //        map.put("trackingData", "MSG12345");
    //
    //        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
    //
    //        ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);
    //
    //        if (response.getStatusCode() == HttpStatus.OK) {
    //            System.out.println("Request Successful");
    //            System.out.println(response.getBody());
    //        } else {
    //            System.out.println("Request Failed");
    //            System.out.println(response.getStatusCode());
    //        }
    //        return true;
    //    }

    //NOTIFICATION

    /**
     * Salje jednu notifikaciju, verovatno korisceno radi probe
     *
     * @return - wow
     */
    // ovo je mesto gde on dobije ovu SingleNotification i onda je cuva u bazu kao objekat NotificationApp,i nad tim objektom se radi polling
    @PostMapping("/api/single-notification")
    private String NotificationIsHere(@RequestBody SingleNotification singleNotification) {

        try {
            log.info("Slanje notifikacije: " + singleNotification);
            userReportService.snedNotification(singleNotification);
        } catch (Exception e) {

        }
        return "wow";
    }

    /////////////////////////////////////////////////////////PREDJENI PUT///////////////////////////////////////////////////////////////////

    /**
     * Funkcija vraca izvestaj o predjenom putu (prvi od mogucih izvestaja sa front-a
     *
     * @throws Exception ako nije postavljen deviceType vozila
     */
    @GetMapping("api/firm/{firm_id}/report/ipp/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}")
    private String izvestajOPredjenomPutu(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new ForbiddenException("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getTeltonika(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto);
        } else {
            res = getGs100(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto);
        }
        return reportService.ipp(res, imei, v, dateFromS, dateToS);
    }

    @PostMapping("api/firm/{firm_id}/report/ipp/imeis/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}")
    private List<Ipp> izvestajOPredjenomPutu2(@RequestBody List<String> imeis, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS, @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto) throws Exception {

        List<Vehicle> vehicles = vehicleService.findAllByImeiIn(imeis);

        for (Vehicle v : vehicles) {
            if (v.getDeviceType() == null) {
                throw new ForbiddenException("Device type is not set for the vehicle." + v.getRegistration()
                        + "Please go to vehicle administration and set device type.");
            }
        }

        ArrayList<Ipp> ippList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        int batchSize = 100;
        for (int i = 0; i < vehicles.size(); i += batchSize) {
            int end = Math.min(i + batchSize, vehicles.size());
            List<Vehicle> subList = vehicles.subList(i, end);

            List<String> teltonikaImeis = subList.stream()
                    .filter(v -> v.getDeviceType() == 0)
                    .map(Vehicle::getImei)
                    .collect(Collectors.toList());

            List<String> gs100Imeis = subList.stream()
                    .filter(v -> v.getDeviceType() != 0)
                    .map(Vehicle::getImei)
                    .collect(Collectors.toList());

            if (!teltonikaImeis.isEmpty()) {
                Map<String, JsonNode> teltonikaData = getTeltonikaBatch(teltonikaImeis, dateFromS, dateToS, hfrom, mfrom, hto, mto);

                for (Vehicle v : vehicles) {
                    JsonNode node = teltonikaData.get(v.getImei());
                    if (node != null && node.toString().length() > 10) {
                        Ipp ipp = mapper.treeToValue(node, Ipp.class);
                        ipp.setRegistraiton(v.getRegistration());
                        ipp.setModel(v.getModel());
                        ipp.setManufacturer(v.getManufacturer());
                        ipp.setType(v.getVehicleType());
                        ippList.add(ipp);
                    }
                }
            }

            if (!gs100Imeis.isEmpty()) {
                Map<String, JsonNode> gs100Data = getGs100Batch(gs100Imeis, dateFromS, dateToS, hfrom, mfrom, hto, mto);

                for (Vehicle v : vehicles) {
                    JsonNode node = gs100Data.get(v.getImei());
                    if (node != null && node.toString().length() > 10) {
                        Ipp ipp = mapper.treeToValue(node, Ipp.class);
                        ipp.setRegistraiton(v.getRegistration());
                        ipp.setModel(v.getModel());
                        ipp.setManufacturer(v.getManufacturer());
                        ipp.setType(v.getVehicleType());
                        ippList.add(ipp);
                    }
                }
            }
        }

        return ippList;
    }



    @PostMapping("api/firm/{firm_id}/report/ipp/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/export/{export_id}")
    /**
     * Vraca izvestaj o predjenom putu vozila u datom intervalu kao byte array
     *
     * @param export =2 vraca pdf, u suprotnom workbook
     */
    public byte[] izvestajOPredjenomPutuExport(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
            @PathVariable("export_id") int export, @RequestBody String[] imeis) throws Exception {
        log.info("####################################");
        log.info(LocalDateTime.now() + " - Starting ipp export for imeis: " + Arrays.toString(imeis));
        ArrayList<Ipp> ippList = new ArrayList<>();
        for (int i = 0; i < imeis.length; i++) {
            Vehicle v = vehicleService.findByImei(imeis[i]);
            if (v.getDeviceType() == null) {
                log.info("####################################");
                log.info(LocalDateTime.now() + " - Device type not set for imei: " + imeis[i]);
                throw new ForbiddenException("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
            }
            String res = "";
            if (v.getDeviceType() == 0) {
                log.info("####################################");
                log.info(LocalDateTime.now() + " - Fetching Teltonika data for imei: " + imeis[i]);
                res = getTeltonika(imeis[i], dateFromS, dateToS, hfrom, mfrom, hto, mto);
            } else {
                log.info("####################################");
                log.info(LocalDateTime.now() + " - Fetching GS100 data for imei: " + imeis[i]);
                res = getGs100(imeis[i], dateFromS, dateToS, hfrom, mfrom, hto, mto);
            }

            if (res.length() > 10) {
                log.info("####################################");
                log.info(LocalDateTime.now() + " - Processing data for imei: " + imeis[i]);
                Gson g = new Gson();
                Ipp ipp = g.fromJson(res, Ipp.class);
                ipp.setRegistraiton(v.getRegistration());
                ipp.setModel(v.getModel());
                ipp.setManufacturer(v.getManufacturer());
                ipp.setType(v.getVehicleType());
                ippList.add(ipp);
            }
        }

        Date dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(dateToS);
        Date dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(dateFromS);
        dateTo.setHours(0);
        dateFrom.setHours(0);
        long fromMs = mfrom * 60 * 1000 + hfrom * 60 * 60 * 1000;
        long toMs = mto * 60 * 1000 + hto * 60 * 60 * 1000;
        long from = dateFrom.getTime();
        long to = dateTo.getTime();

        from += fromMs;
        to += toMs;

        Timestamp tsFrom = new Timestamp(from);
        Timestamp tsTo = new Timestamp(to);

        log.info("####################################");
        log.info(LocalDateTime.now() + " - Generating report for imeis: " + Arrays.toString(imeis));

        return reportService.ippExport(ippList, export, tsFrom, tsTo);
    }

    @PostMapping("api/firm/{firm_id}/report/ipp/imeis/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/export/{export_id}")
    /**
     * Vraca izvestaj o predjenom putu vozila u datom intervalu kao byte array
     *
     * @param export =2 vraca pdf, u suprotnom workbook
     */
    public byte[] izvestajOPredjenomPutuExport2(@PathVariable("from") String dateFromS, @PathVariable("to") String dateToS, @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto, @PathVariable("export_id") int export, @RequestBody List<String> imeis) throws Exception {

        ArrayList<Ipp> ippList= (ArrayList<Ipp>) izvestajOPredjenomPutu2(imeis, dateFromS, dateToS, hfrom, mfrom, hto, mto);

        Date dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(dateToS);
        Date dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(dateFromS);
        dateTo.setHours(0);
        dateFrom.setHours(0);
        long fromMs = mfrom * 60 * 1000 + hfrom * 60 * 60 * 1000;
        long toMs = mto * 60 * 1000 + hto * 60 * 60 * 1000;
        long from = dateFrom.getTime();
        long to = dateTo.getTime();

        from += fromMs;
        to += toMs;

        Timestamp tsFrom = new Timestamp(from);
        Timestamp tsTo = new Timestamp(to);

        return reportService.ippExport(ippList, export, tsFrom, tsTo);
    }




    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////PREDJENI PUT MESECNO////////////////////////////////////////////////////

    /**
     * Vraca mesecni izvestaj o predjenom putu vozila kao String
     *
     * @param imei vozilo
     */
    @GetMapping("api/firm/{firm_id}/report/ippm/imei/{IMEI}/from/{from}/to/{to}")
    private String izvestajOPredjenomPutuMesecni(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS)
            throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getTeltonikaMonthly(imei, dateFromS, dateToS);

        } else {
            res = getGs100Monthly(imei, dateFromS, dateToS);
        }
        return reportService.ippm(res, imei, v);
    }


    @PostMapping("api/firm/{firm_id}/report/ippm/imeis/from/{from}/to/{to}")
    private List<Ippm> izvestajOPredjenomPutuMesecni3(@RequestBody List<String> imeis, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS) throws Exception {
        List<Vehicle> vehicles = vehicleService.findAllByImeiIn(imeis);

        for (Vehicle v : vehicles) {
            if (v.getDeviceType() == null) {
                throw new ForbiddenException("Device type is not set for the vehicle." + v.getRegistration()
                        + "Please go to vehicle administration and set device type.");
            }
        }


        ObjectMapper mapper = new ObjectMapper();
        List<Ippm> results = new ArrayList<>();

        int batchSize = 30;
        for (int i = 0; i < vehicles.size(); i += batchSize) {
            int end = Math.min(i + batchSize, vehicles.size());
            List<Vehicle> subList = vehicles.subList(i, end);

            List<String> teltonikaImeis = subList.stream()
                    .filter(v -> v.getDeviceType() == 0)
                    .map(Vehicle::getImei)
                    .collect(Collectors.toList());

            List<String> gs100Imeis = subList.stream()
                    .filter(v -> v.getDeviceType() != 0)
                    .map(Vehicle::getImei)
                    .collect(Collectors.toList());

            if (!teltonikaImeis.isEmpty()) {
                Map<String, JsonNode> teltonikaData = getTeltonikaMonthlyBatch(teltonikaImeis, dateFromS, dateToS);

                for (Vehicle v : subList) {
                    JsonNode node = teltonikaData.get(v.getImei());
                    if (node != null && node.toString().length() > 10) {
                        Map<Integer, Double> retMap = mapper.convertValue(node, new TypeReference<Map<Integer, Double>>() {});
                        Ippm ippm = new Ippm();
                        ippm.setRegistration(v.getRegistration());
                        ippm.setModel(v.getModel());
                        ippm.setManufacturer(v.getManufacturer());
                        ippm.setMap(retMap);
                        if (ippm.getMap() != null) {
                            double sum = ippm.getMap().values().stream().mapToDouble(Double::doubleValue).sum();
                            ippm.setUkupno(sum);
                        }
                        results.add(ippm);
                    }
                }
            }

            if (!gs100Imeis.isEmpty()) {
                Map<String, JsonNode> gs100Data = getGs100MonthlyBatch(gs100Imeis, dateFromS, dateToS);

                for (Vehicle v : subList) {
                    JsonNode node = gs100Data.get(v.getImei());
                    if (node != null && node.toString().length() > 10) {
                        Map<Integer, Double> retMap = mapper.convertValue(node, new TypeReference<Map<Integer, Double>>() {});
                        Ippm ippm = new Ippm();
                        ippm.setRegistration(v.getRegistration());
                        ippm.setModel(v.getModel());
                        ippm.setManufacturer(v.getManufacturer());
                        ippm.setMap(retMap);
                        if (ippm.getMap() != null) {
                            double sum = ippm.getMap().values().stream().mapToDouble(Double::doubleValue).sum();
                            ippm.setUkupno(sum);
                        }
                        results.add(ippm);
                    }
                }
            }
        }

        return results;
    }




    /**
     * vraca mesecni izvestaj o predjenom putu vozila kao byte array
     *
     * @param imei   vozilo
     * @param export =2 vraca pdf, u suprotnom workbook
     * @return
     * @throws Exception
     */
    @PostMapping("api/firm/{firm_id}/report/ippm/imei/{IMEI}/from/{from}/to/{to}/export/{export_id}")//done
    public byte[] izvestajOPredjenomPutuMesecniExport(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS, @PathVariable("export_id") int export, @RequestBody String[] imeis) throws Exception {
        ArrayList<Ippm> ippmArrayList = new ArrayList<>();
        for (int i = 0; i < imeis.length; i++) {
            Vehicle v = vehicleService.findByImei(imeis[i]);
            if (v.getDeviceType() == null) {
                throw new ForbiddenException("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
            }
            if (v.getDeviceType() == null) {
                throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
            }
            String res = "";
            if (v.getDeviceType() == 0) {
                res = getTeltonikaMonthly(imeis[i], dateFromS, dateToS);
            } else {
                res = getGs100Monthly(imeis[i], dateFromS, dateToS);
            }
            if (res.length() > 5) {
                Map<Integer, Double> retMap = new Gson().fromJson(
                        res, new TypeToken<HashMap<Integer, Double>>() {
                        }.getType()
                );
                Ippm ippm = new Ippm();
                ippm.setRegistration(v.getRegistration());
                ippm.setModel(v.getModel());
                ippm.setManufacturer(v.getManufacturer());
                ippm.setMap(retMap);
                ippmArrayList.add(ippm);
            }
        }

        Date dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(dateToS);
        Date dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(dateFromS);
        dateTo.setHours(0);
        dateFrom.setHours(0);
        long from = dateFrom.getTime();
        long to = dateTo.getTime();

        Timestamp tsFrom = new Timestamp(from);
        Timestamp tsTo = new Timestamp(to);

        return reportService.ippmExport(ippmArrayList, tsFrom, tsTo, export, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }


    @PostMapping("api/firm/{firm_id}/report/ippm/imeis/from/{from}/to/{to}/export/{export_id}")
    public byte[] izvestajOPredjenomPutuMesecniExport2(@RequestBody List<String> imeis, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS, @PathVariable("export_id") int export) throws Exception {

        ArrayList<Ippm> ippmArrayList= (ArrayList<Ippm>) izvestajOPredjenomPutuMesecni3(imeis,dateFromS,dateToS);


        Date dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(dateToS);
        Date dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(dateFromS);
        dateTo.setHours(0);
        dateFrom.setHours(0);
        long from = dateFrom.getTime();
        long to = dateTo.getTime();

        Timestamp tsFrom = new Timestamp(from);
        Timestamp tsTo = new Timestamp(to);

        return reportService.ippmExport(ippmArrayList, tsFrom, tsTo, export, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * vraca mesecni izvestaj o predjenom putu konkretnog vozila
     */
    @GetMapping("api/firm/{firm_id}/report/ippmh/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}")
    private String izvestajOPredjenomPutuTelMesecniRV(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto) throws Exception {
        System.out.println("ovde");
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getTeltonikaMonthlyHours(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto);
        } else {
            res = getGs100MonthlyHours(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto);
        }
        return reportService.ippmh(res, imei, v);
    }

    ////////////////////////////////////////////////IZVESTAJ O PREDJENOM PUTU MESECNO RADNO VREME/////////////////////////////////////////////////////////////////////

    /**
     * vraca mesecni izvestaj o predjenom putu konkretnog vozila
     *
     * @param working =1 WORKING, =0 NOT WORKING
     * @return
     * @throws Exception
     */
    @GetMapping("api/firm/{firm_id}/report/ippmh/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/{hfromsa}/{mfromsa}/{htosa}/{mtosa}/{hfromsu}/{mfromsu}/{htosu}/{mtosu}/working/{working}")
    //working hours 1, not working 0
    private String izvestajOPredjenomPutuTelMesecniVRV(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
            @PathVariable("hfromsa") int hfromsa, @PathVariable("mfromsa") int mfromsa, @PathVariable("htosa") int htosa, @PathVariable("mtosa") int mtosa,
            @PathVariable("hfromsu") int hfromsu, @PathVariable("mfromsu") int mfromsu, @PathVariable("htosu") int htosu, @PathVariable("mtosu") int mtosu,
            @PathVariable("working") int working) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getTeltonikaMonthlyHoursVrv(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, hfromsa, mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu, mtosu,
                    working);
        } else {
            res = getGs100MonthlyHoursVrv(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, hfromsa, mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu, mtosu,
                    working);
        }
        return reportService.ippmh(res, imei, v);
    }

    @PostMapping("api/firm/{firm_id}/report/ippmh/imeis/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/{hfromsa}/{mfromsa}/{htosa}/{mtosa}/{hfromsu}/{mfromsu}/{htosu}/{mtosu}/working/{working}")
    //working hours 1, not working 0
    private String izvestajOPredjenomPutuMesecniVRV2(@RequestBody List<String> imeis, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS, @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
                                                     @PathVariable("hfromsa") int hfromsa, @PathVariable("mfromsa") int mfromsa, @PathVariable("htosa") int htosa, @PathVariable("mtosa") int mtosa, @PathVariable("hfromsu") int hfromsu, @PathVariable("mfromsu") int mfromsu, @PathVariable("htosu") int htosu,
                                                     @PathVariable("mtosu") int mtosu, @PathVariable("working") int working) throws Exception {


        List<Vehicle> vehicles = vehicleService.findAllByImeiIn(imeis);

        List<String> teltonikaImeis = new ArrayList<>();
        List<String> gs100Imeis = new ArrayList<>();

        for (Vehicle v : vehicles) {
            if (v.getDeviceType() == null) {
                throw new ForbiddenException("Device type is not set for vehicle " + v.getRegistration());
            }
            if (v.getDeviceType() == 0) {
                teltonikaImeis.add(v.getImei());
            } else {
                gs100Imeis.add(v.getImei());
            }
        }


        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> results = new ArrayList<>();

        if (!teltonikaImeis.isEmpty()) {
            Map<String, JsonNode> teltonikaData =getTeltonikaMonthlyHoursVrvBatch(teltonikaImeis, dateFromS, dateToS, hfrom, mfrom, hto, mto, hfromsa,
                    mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu, mtosu, working);

            teltonikaData.values().stream()
                    .filter(node -> node != null && node.size() > 0)
                    .forEach(results::add);
        }

        if (!gs100Imeis.isEmpty()) {
            Map<String, JsonNode> gs100Data =getGs100MonthlyHoursVrvBatch(imeis, dateFromS, dateToS, hfrom, mfrom, hto, mto, hfromsa, mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu, mtosu,
                    working);

            gs100Data.values().stream()
                    .filter(node -> node != null && node.size() > 0)
                    .forEach(results::add);
        }

        if (results.size() == 1) {
            return mapper.writeValueAsString(results.get(0));
        }
        return mapper.writeValueAsString(results);
    }



    @PostMapping("api/firm/{firm_id}/report/ippmh/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/{hfromsa}/{mfromsa}/{htosa}/{mtosa}/{hfromsu}/{mfromsu}/{htosu}/{mtosu}/working/{working}/export/{export_id}")
    //working hours 1, not working 0
    public byte[] izvestajOPredjenomPutuTelMesecniVRVExport(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
            @PathVariable("hfromsa") int hfromsa, @PathVariable("mfromsa") int mfromsa, @PathVariable("htosa") int htosa, @PathVariable("mtosa") int mtosa,
            @PathVariable("hfromsu") int hfromsu, @PathVariable("mfromsu") int mfromsu, @PathVariable("htosu") int htosu, @PathVariable("mtosu") int mtosu,
            @PathVariable("working") int working, @PathVariable("export_id") int export, @RequestBody String[] imeis) throws Exception {
        ArrayList<Ippm> ippmArrayList = new ArrayList<>();
        for (int i = 0; i < imeis.length; i++) {
            Vehicle v = vehicleService.findByImei(imeis[i]);
            if (v.getDeviceType() == null) {
                throw new ForbiddenException("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
            }
            if (v.getDeviceType() == null) {
                throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
            }
            String res = "";
            if (v.getDeviceType() == 0) {
                res = getTeltonikaMonthlyHoursVrv(imeis[i], dateFromS, dateToS, hfrom, mfrom, hto, mto, hfromsa, mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu,
                        mtosu, working);
            } else {
                res = getGs100MonthlyHoursVrv(imeis[i], dateFromS, dateToS, hfrom, mfrom, hto, mto, hfromsa, mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu,
                        mtosu, working);
            }
            if (res.length() > 5) {
                Map<Integer, Double> retMap = new Gson().fromJson(
                        res, new TypeToken<HashMap<Integer, Double>>() {
                        }.getType()
                );
                Ippm ippm = new Ippm();
                ippm.setRegistration(v.getRegistration());
                ippm.setModel(v.getModel());
                ippm.setManufacturer(v.getManufacturer());
                ippm.setMap(retMap);
                ippmArrayList.add(ippm);
            }
        }

        Date dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(dateToS);
        Date dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(dateFromS);
        dateTo.setHours(0);
        dateFrom.setHours(0);
        long fromMs = mfrom * 60 * 1000 + hfrom * 60 * 60 * 1000;
        long toMs = mto * 60 * 1000 + hto * 60 * 60 * 1000;
        long from = dateFrom.getTime();
        long to = dateTo.getTime();

        from += fromMs;
        to += toMs;

        Timestamp tsFrom = new Timestamp(from);
        Timestamp tsTo = new Timestamp(to);

        //{hfromsa}/{mfromsa}/{htosa}/{mtosa}/{hfromsu}/{mfromsu}/{htosu}/{mtosu}
        return reportService.ippmExport(ippmArrayList, tsFrom, tsTo, export, working, hfrom, mfrom, hto, mto, hfromsa, mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu, mtosu);
    }


    @PostMapping("api/firm/{firm_id}/report/ippmh/imeis/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/{hfromsa}/{mfromsa}/{htosa}/{mtosa}/{hfromsu}/{mfromsu}/{htosu}/{mtosu}/working/{working}/export/{export_id}")
    //working hours 1, not working 0
    public byte[] izvestajOPredjenomPutuTelMesecniVRVExport2(@RequestBody List<String> imeis, @PathVariable("from") String dateFromS,
                                                            @PathVariable("to") String dateToS,
                                                            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
                                                            @PathVariable("hfromsa") int hfromsa, @PathVariable("mfromsa") int mfromsa, @PathVariable("htosa") int htosa, @PathVariable("mtosa") int mtosa,
                                                            @PathVariable("hfromsu") int hfromsu, @PathVariable("mfromsu") int mfromsu, @PathVariable("htosu") int htosu, @PathVariable("mtosu") int mtosu,
                                                            @PathVariable("working") int working, @PathVariable("export_id") int export) throws Exception {

        List<Vehicle> vehicles = vehicleService.findAllByImeiIn(imeis);

        for (Vehicle v : vehicles) {
            if (v.getDeviceType() == null) {
                throw new ForbiddenException("Device type is not set for the vehicle " + v.getRegistration()
                        + ". Please go to vehicle administration and set device type.");
            }
        }

        List<String> teltonikaImeis = vehicles.stream()
                .filter(v -> v.getDeviceType() == 0)
                .map(Vehicle::getImei)
                .collect(Collectors.toList());

        List<String> gs100Imeis = vehicles.stream()
                .filter(v -> v.getDeviceType() != 0)
                .map(Vehicle::getImei)
                .collect(Collectors.toList());

        ArrayList<Ippm> ippmArrayList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        if (!teltonikaImeis.isEmpty()) {
            Map<String, JsonNode> teltonikaData = getTeltonikaMonthlyHoursVrvBatch(teltonikaImeis, dateFromS, dateToS, hfrom, mfrom, hto, mto, hfromsa,
                    mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu, mtosu, working);

            for (Vehicle v : vehicles) {
                JsonNode node = teltonikaData.get(v.getImei());
                if (node != null && node.toString().length() > 10) {
                    Map<Integer, Double> retMap = mapper.convertValue(node, new TypeReference<Map<Integer, Double>>() {});
                    Ippm ippm = new Ippm();
                    ippm.setRegistration(v.getRegistration());
                    ippm.setModel(v.getModel());
                    ippm.setManufacturer(v.getManufacturer());
                    ippm.setMap(retMap);
                    ippmArrayList.add(ippm);
                }
            }
        }

        if (!gs100Imeis.isEmpty()) {
            Map<String, JsonNode> gs100Data = getGs100MonthlyHoursVrvBatch(gs100Imeis, dateFromS, dateToS, hfrom, mfrom, hto, mto, hfromsa,
                    mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu, mtosu, working);

            for (Vehicle v : vehicles) {
                JsonNode node = gs100Data.get(v.getImei());
                if (node != null && node.toString().length() > 10) {
                    Map<Integer, Double> retMap = mapper.convertValue(node, new TypeReference<Map<Integer, Double>>() {});
                    Ippm ippm = new Ippm();
                    ippm.setRegistration(v.getRegistration());
                    ippm.setModel(v.getModel());
                    ippm.setManufacturer(v.getManufacturer());
                    ippm.setMap(retMap);
                    ippmArrayList.add(ippm);
                }
            }
        }

        Date dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(dateToS);
        Date dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(dateFromS);
        dateTo.setHours(0);
        dateFrom.setHours(0);
        long fromMs = mfrom * 60 * 1000 + hfrom * 60 * 60 * 1000;
        long toMs = mto * 60 * 1000 + hto * 60 * 60 * 1000;
        long from = dateFrom.getTime();
        long to = dateTo.getTime();

        from += fromMs;
        to += toMs;

        Timestamp tsFrom = new Timestamp(from);
        Timestamp tsTo = new Timestamp(to);

        //{hfromsa}/{mfromsa}/{htosa}/{mtosa}/{hfromsu}/{mfromsu}/{htosu}/{mtosu}
        return reportService.ippmExport(ippmArrayList, tsFrom, tsTo, export, working, hfrom, mfrom, hto, mto, hfromsa, mfromsa, htosa, mtosa, hfromsu, mfromsu, htosu, mtosu);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////PREKORACENJE BRZINE//////////////////////////////////////////////////////////////

    /**
     * Vraca izvestaj o prekoracenju brzine
     */
    @GetMapping("api/firm/{firm_id}/report/speed/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/max/{max}")
    private Response<List<DTOSpeed>> izvestajOPrekoracenjuBrzine(@PathVariable("IMEI") String imei,
            @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom,
            @PathVariable("mfrom") int mfrom,
            @PathVariable("hto") int hto,
            @PathVariable("mto") int mto,
            @PathVariable("max") int max) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getTeltonikaSpeed(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, max);
        } else {
            res = getGs100Speed(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, max);
        }

        return reportService.speed(res, imei, v);
    }

    @PostMapping("api/firm/{firm_id}/report/speed/imeis/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/max/{max}")
    private Response<List<DTOSpeed>> izvestajOPrekoracenjuBrzine2(@RequestBody List<String> imeis,
                                                                 @PathVariable("from") String dateFromS,
                                                                 @PathVariable("to") String dateToS,
                                                                 @PathVariable("hfrom") int hfrom,
                                                                 @PathVariable("mfrom") int mfrom,
                                                                 @PathVariable("hto") int hto,
                                                                 @PathVariable("mto") int mto,
                                                                 @PathVariable("max") int max) throws Exception {

        List<Vehicle> vehicles = vehicleService.findAllByImeiIn(imeis);

        for (Vehicle v : vehicles) {
            if (v.getDeviceType() == null) {
                throw new ForbiddenException("Device type is not set for the vehicle." + v.getRegistration()
                        + "Please go to vehicle administration and set device type.");
            }
        }

        List<String> teltonikaImeis = vehicles.stream()
                .filter(v -> v.getDeviceType() == 0)
                .map(Vehicle::getImei)
                .collect(Collectors.toList());

        List<String> gs100Imeis = vehicles.stream()
                .filter(v -> v.getDeviceType() != 0)
                .map(Vehicle::getImei)
                .collect(Collectors.toList());

        List<DTOSpeed> results = new ArrayList<>();
        String res= "";
        if (!teltonikaImeis.isEmpty()) {
            res=getTeltonikaSpeedBatch(teltonikaImeis, dateFromS, dateToS, hfrom, mfrom, hto, mto, max);
            List<DTOSpeed> list = reportService.speed2(res, teltonikaImeis);

            list.forEach(sp -> {
                Vehicle v = vehicles.stream()
                        .filter(vc -> vc.getImei().equals(sp.getImei()))
                        .findFirst()
                        .orElse(null);

                if (v != null) {
                    sp.setRegistration(v.getRegistration());
                    sp.setManufacturer(v.getManufacturer());
                    sp.setModel(v.getModel());
                }
            });

            results.addAll(list);
        }

        if (!gs100Imeis.isEmpty()) {
            res = getGs100SpeedBatch(gs100Imeis, dateFromS, dateToS, hfrom, mfrom, hto, mto, max);

            List<DTOSpeed> list = reportService.speed2(res, gs100Imeis);

            list.forEach(sp -> {
                Vehicle v = vehicles.stream()
                        .filter(vc -> vc.getImei().equals(sp.getImei()))
                        .findFirst()
                        .orElse(null);

                if (v != null) {
                    sp.setRegistration(v.getRegistration());
                    sp.setManufacturer(v.getManufacturer());
                    sp.setModel(v.getModel());
                }
            });

            results.addAll(list);
        }

        Response<List<DTOSpeed>> response = new Response<>();
        response.setData(results);
        return response;

    }

    @GetMapping("api/firm/{firm_id}/report/speed/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/max/{max}/export/{export_id}")

    /**
     *
     * vraca izvestaj o prekoracenjima brzine kao byte array
     * @param export =2 vraca pdf, u suprotnom workbook
     */
    public byte[] izvestajOPrekoracenjuBrzineExport(@PathVariable("IMEI") String imei,
            @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom,
            @PathVariable("mfrom") int mfrom,
            @PathVariable("hto") int hto,
            @PathVariable("mto") int mto,
            @PathVariable("max") int max,
            @PathVariable("export_id") int export,
            @RequestParam() boolean peakSelected) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }

        Response<List<DTOSpeed>> listResponse = izvestajOPrekoracenjuBrzine(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, max);
        for (int i = 0; i < listResponse.getData().size(); i++) {
            DTOSpeed data = listResponse.getData().get(i); // Assuming DataType is the type of your list elements
            System.out.println(i + " " + data.getEndTimestamp());
        }
        if (listResponse != null) {
            List<DTOSpeed> dtoSpeedList = listResponse.getData();
            return reportService.speedExport(dtoSpeedList, imei, v, export, dateFromS, dateToS, peakSelected, max);
        }
        return new byte[0];
    }

    @PostMapping("api/firm/{firm_id}/report/speed/imeis/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/max/{max}/export/{export_id}")
    /**
     *
     * vraca izvestaj o prekoracenjima brzine kao byte array
     * @param export =2 vraca pdf, u suprotnom workbook
     */
    public byte[] izvestajOPrekoracenjuBrzineExport2(@RequestBody String[] imeis,
                                                    @PathVariable("from") String dateFromS,
                                                    @PathVariable("to") String dateToS,
                                                    @PathVariable("hfrom") int hfrom,
                                                    @PathVariable("mfrom") int mfrom,
                                                    @PathVariable("hto") int hto,
                                                    @PathVariable("mto") int mto,
                                                    @PathVariable("max") int max,
                                                    @PathVariable("export_id") int export,
                                                    @RequestParam() boolean peakSelected) throws Exception {
        List<Vehicle> vehicles = vehicleService.findAllByImeiIn(Arrays.asList(imeis));

        for (Vehicle v : vehicles) {
            if (v.getDeviceType() == null) {
                throw new ForbiddenException("Device type is not set for the vehicle " + v.getRegistration()
                        + ". Please go to vehicle administration and set device type.");
            }
        }

        List<DTOSpeed> allData = new ArrayList<>();

        Response<List<DTOSpeed>> listResponse = izvestajOPrekoracenjuBrzine2(Arrays.asList(imeis), dateFromS, dateToS, hfrom, mfrom, hto, mto, max);


        if (listResponse != null && listResponse.getData() != null) {
                allData.addAll(listResponse.getData());
        }


        return reportService.speedExport2(allData, Arrays.asList(imeis), vehicles, export, dateFromS, dateToS, peakSelected, max);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////Relacije vozila/////////////////////////////////////////////////////////////////////
    @GetMapping("api/firm/{firm_id}/report/route/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}")
    private String izvestajORelacijamaVozila(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
            @RequestParam("minDistance") Double minDistance) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getTeltonikaRoute(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, minDistance);
        } else {
            res = getGs100Route(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, minDistance);
        }
        return reportService.route(res, imei, v);
    }

    @GetMapping("api/firm/{firm_id}/report/route/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/export/{export_id}")
    //done
    public byte[] izvestajORelacijamaVozilaExport(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
            @RequestParam("minDistance") Double minDistance, @PathVariable("export_id") int export) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getTeltonikaRoute(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, minDistance);
        } else {
            res = getGs100Route(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, minDistance);
        }
        return reportService.routeExport(res, imei, v, export, dateFromS, dateToS);
    }

    //@PostMapping("api/firm/{firm_id}/report/route/imeis/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/export/{export_id}")
    //done
    public byte[] izvestajORelacijamaVozilaExportAutomatski(@RequestBody String[] imeis, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
                                                  @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
                                                  @RequestParam("minDistance") Double minDistance, @PathVariable("export_id") int export) throws Exception {
        List<Vehicle> vehicles = vehicleService.findAllByImeiIn(Arrays.asList(imeis));

        ObjectMapper mapper = new ObjectMapper();

        for (Vehicle v : vehicles) {
            if (v.getDeviceType() == null) {
                throw new ForbiddenException("Device type is not set for the vehicle." + v.getRegistration()
                        + "Please go to vehicle administration and set device type.");
            }
        }

        List<String> teltonikaImeis = vehicles.stream()
                .filter(v -> v.getDeviceType() == 0)
                .map(Vehicle::getImei)
                .collect(Collectors.toList());

        List<String> gs100Imeis = vehicles.stream()
                .filter(v -> v.getDeviceType() != 0)
                .map(Vehicle::getImei)
                .collect(Collectors.toList());

        List<RouteReport> results = new ArrayList<>();

        if (!teltonikaImeis.isEmpty()) {
            Map<String, JsonNode> teltonikaData = getTeltonikaRouteBatch(teltonikaImeis, dateFromS, dateToS, hfrom, mfrom, hto, mto, minDistance);
            for (Vehicle v : vehicles) {
                JsonNode node = teltonikaData.get(v.getImei());
                if (node != null && node.toString().length() > 10) {
                    List<RouteReport> list = mapper.convertValue(node, new TypeReference<List<RouteReport>>() {});
                    for (RouteReport rr : list) {
                        rr.setImei(v.getImei());
                    }
                    results.addAll(list);
                }
            }


        }

        if (!gs100Imeis.isEmpty()) {
            Map<String, JsonNode> gs100Data = getGs100RouteBatch(gs100Imeis, dateFromS, dateToS, hfrom, mfrom, hto, mto, minDistance);
            for (Vehicle v : vehicles) {
                JsonNode node = gs100Data.get(v.getImei());
                if (node != null && node.toString().length() > 10) {
                    List<RouteReport> list = mapper.convertValue(node, new TypeReference<List<RouteReport>>() {});
                    for (RouteReport rr : list) {
                        rr.setImei(v.getImei());
                    }
                    results.addAll(list);
                }
            }
        }

        return reportService.routeExport2(results, Arrays.asList(imeis), vehicles, export, dateFromS, dateToS);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////STAJANJE VOZILA///////////////////////////////////////////////////////////////////

    /**
     * vraca izvestaj o rutama vozila u datom intervalu kao byte array
     */
    @GetMapping("api/firm/{firm_id}/report/standing/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/min/{min}/minIdle/{minIdle}")
    private String izvestajOStajanju(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
            @PathVariable("min") int min,
            @PathVariable("minIdle") int minIdle,
            @RequestParam("isIdle") boolean isIdle) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            //Uzmi teltonika podatke
            res = getTeltonikaIdle(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, min, isIdle, minIdle);
        } else {
            //Uzmi gs100 podatke
            res = getGs100OIdle(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, min, isIdle, minIdle);
        }
        return reportService.route(res, imei, v);
    }


    @PostMapping("api/firm/{firm_id}/report/standing/imeis/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/min/{min}/minIdle/{minIdle}")
    private List<Idle> izvestajOStajanju2(@RequestBody List<String> imeis, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
                                          @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
                                          @PathVariable("min") int min,
                                          @PathVariable("minIdle") int minIdle,
                                          @RequestParam("isIdle") boolean isIdle) throws Exception {


        try {
            List<Vehicle> vehicles = vehicleService.findAllByImeiIn(imeis);

            for (Vehicle v : vehicles) {
                if (v.getDeviceType() == null) {
                    throw new ForbiddenException("Device type is not set for the vehicle." + v.getRegistration()
                            + "Please go to vehicle administration and set device type.");
                }
            }

            List<String> teltonikaImeis = vehicles.stream()
                    .filter(v -> v.getDeviceType() == 0)
                    .map(Vehicle::getImei)
                    .collect(Collectors.toList());

            List<String> gs100Imeis = vehicles.stream()
                    .filter(v -> v.getDeviceType() != 0)
                    .map(Vehicle::getImei)
                    .collect(Collectors.toList());

            ObjectMapper mapper = new ObjectMapper();
            List<Idle> results = new ArrayList<>();

            Map<String, JsonNode> teltonikaData = !teltonikaImeis.isEmpty() ?
                    getTeltonikaIdleBatch(teltonikaImeis, dateFromS, dateToS, hfrom, mfrom, hto, mto, min, isIdle, minIdle)
                    : new HashMap<>();

            Map<String, JsonNode> gs100Data = !gs100Imeis.isEmpty() ?
                    getGs100OIdleBatch(gs100Imeis, dateFromS, dateToS, hfrom, mfrom, hto, mto, min, isIdle, minIdle)
                    : new HashMap<>();

            LocalDate dateFrom = LocalDate.parse(dateFromS);
            LocalDate dateTo = LocalDate.parse(dateToS);
            ZoneId zone = ZoneId.of("Europe/Belgrade");
            long dayStart = dateFrom.atStartOfDay(zone).toInstant().toEpochMilli();
            long dayEnd = dateTo.atTime(23, 59, 59).atZone(zone).toInstant().toEpochMilli();

            for (Vehicle v : vehicles) {
                JsonNode node = v.getDeviceType() == 0 ? teltonikaData.get(v.getImei()) : gs100Data.get(v.getImei());
                List<Idle> vehicleIdleList = new ArrayList<>();

                if (node != null && node.toString().length() > 10) {
                    // Ima javljanja
                    List<Idle> list = mapper.convertValue(node, new TypeReference<List<Idle>>() {
                    });
                    for (Idle idle : list) {
                        idle.setVehicle(v);
                        vehicleIdleList.add(idle);
                    }

                    // Popravi poslednji idle da traje do kraja dana
                    if (!vehicleIdleList.isEmpty()) {
                        Idle last = vehicleIdleList.get(vehicleIdleList.size() - 1);
                        last.setEnd(dayEnd);
                        last.setTime(dayEnd - last.getStart());
                        vehicleIdleList.set(vehicleIdleList.size() - 1, last);
                    }

                } else {
                    // Nema javljanja — ceo dan idle
                    Idle fullDayIdle = new Idle();
                    fullDayIdle.setVehicle(v);
                    fullDayIdle.setStart(dayStart);
                    fullDayIdle.setEnd(dayEnd);
                    fullDayIdle.setTime(dayEnd - dayStart);
                    vehicleIdleList.add(fullDayIdle);
                }

                results.addAll(vehicleIdleList);
            }
            return results;
        }catch (Exception e){
            log.error("Greška prilikom izveštaja o stajanju: ", e);
            throw e;
        }
    }

    /**
     * vraca izvestaj o stajanjima vozila u obliku byte array-a
     */
    @PostMapping("api/firm/{firm_id}/report/standing/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/min/{min}/minIdle/{minIdle}/export/{eid}")
    public byte[] izvestajOStajanjuExport(
            @PathVariable("eid") int eid,
            @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom,
            @PathVariable("mfrom") int mfrom,
            @PathVariable("hto") int hto,
            @PathVariable("mto") int mto,
            @PathVariable("min") int min,
            @PathVariable("minIdle") int minIdle,
            @RequestBody String[] imeis,
            @RequestParam("isIdle") boolean isIdle
    ) throws Exception {
        try {
            List<Idle> idleList = izvestajOStajanju2(Arrays.asList(imeis), dateFromS, dateToS, hfrom, mfrom, hto, mto, min, minIdle, isIdle);

            return reportService.standingExport(idleList, eid, dateFromS, dateToS, min, minIdle);
        }catch (Exception e) {
            throw e;
        }
    }

    ////////////////////////////////////////////////////ZELENA VOZNJA////////////////////////////////////////////////////////////////////

    /**
     * vraca izvestaj o zelenoj voznji odredjenog vozila u vremenskom intervalu kao stirng
     */
    @GetMapping("api/firm/{firm_id}/report/green/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}")
    private String izvestajOZelenojVoznji(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getTeltonikaGreen(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto);
        } else {
            res = getGs100OGreen(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto);
        }
        return reportService.route(res, imei, v);
    }

    /**
     * vraca izvestaj o zelenoj voznji vozila kao bytearray za export
     *
     * @param eid =2 vraca pdf, u suprotnom workbook
     */
    @GetMapping("api/firm/{firm_id}/report/green/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/export/{eid}")
    public byte[] izvestajOZelenojVoznjiExport(@PathVariable("eid") int eid, @PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getTeltonikaGreen(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto);
        } else {
            res = getGs100OGreen(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto);
        }
        return reportService.greenExport(res, imei, v, eid, dateFromS, dateToS);
    }

    ////////////////////////////////////////////////////////TEMPERATURE//////////////////////////////////////////////////////////////////////

    /**
     * vraca izvestaj o temperaturama, String
     */
    @GetMapping("api/firm/{firm_id}/report/temp/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}")
    private String izvestajOTemperaturama(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";

        if (v.getDeviceType() == 0) {
            res = getTeltonikaTemp(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto);
        } else {
            res = getGs100Temp(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto);
        }
        return reportService.route(res, imei, v);
    }

    @GetMapping("api/firm/{firm_id}/report/temp/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}/export/{export_id}")

    /**
     *
     * @param export
     * @param imei
     * @param dateFromS
     * @param dateToS
     * @param hfrom
     * @param mfrom
     * @param hto
     * @param mto
     * @param imeis
     * @return
     * @throws Exception
     */
    private byte[] izvestajOTemperaturamaExport(@PathVariable("export_id") int export, @PathVariable("IMEI") String imei,
            @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
            @RequestBody String[] imeis) throws Exception {
        ArrayList<Temperature> temperatureArrayList = new ArrayList<>();
        for (int i = 0; i < imeis.length; i++) {
            Vehicle v = vehicleService.findByImei(imeis[i]);
            String res = "";
            if (v.getDeviceType() == null) {
                throw new Exception(
                        "Device type is not set for the vehicle. Please go to vehicle administration and set device type. Reg: " + v.getRegistration());
            }
            if (v.getDeviceType() == 0) {
                res = getTeltonikaTemp(imeis[i], dateFromS, dateToS, hfrom, mfrom, hto, mto);
            } else {
                res = getGs100Temp(imeis[i], dateFromS, dateToS, hfrom, mfrom, hto, mto);
            }
            if (res.length() > 5) {
                Gson gson = new Gson(); // Or use new GsonBuilder().create();
                Temperature temperature = gson.fromJson(res, Temperature.class);
                temperature.setRegistraiton(v.getRegistration());
                temperatureArrayList.add(temperature);
            }

        }
        return reportService.tempExport(temperatureArrayList, dateFromS, dateToS, export);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////IZVESTAJ O DOGADJAJIMA VOZILA////////////////////////////////////////////////////////////////////////////

    /**
     * vraca izvestaj o dogadjajima vozila u vremenskom intervalu - String
     */
    @PostMapping("api/firm/{firm_id}/report/event/imei/{IMEI}/from/{from}/to/{to}/{hfrom}/{mfrom}/{hto}/{mto}")
    private String izvestajODogadjajima(@PathVariable("IMEI") String imei, @PathVariable("from") String dateFromS, @PathVariable("to") String dateToS,
            @PathVariable("hfrom") int hfrom, @PathVariable("mfrom") int mfrom, @PathVariable("hto") int hto, @PathVariable("mto") int mto,
            @RequestBody Event event) throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        event.setMaxSpeed(v.getMaxSpeed());
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getTeltonikaEvent(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, event);
        } else {
            res = getGs100Event(imei, dateFromS, dateToS, hfrom, mfrom, hto, mto, event);
        }
        return reportService.route(res, imei, v);
    }

    ///////////////////////////////////////////////////////////LAST POSITION//////////////////////////////////////////////////////////////////////

    /**
     * Vraca poslednju poznatu lokaciju vozila
     *
     * @param imei prirodni kljuc vozila
     */
    @GetMapping("/api/imei/{imei}/last")
    private Object returnLastLoc1(@PathVariable("imei") String imei) throws Exception {
        return returnLastLoc(imei);
    }

    //razlicinto za teltoniku i gs100
    private Gs100 returnLastLoc(String imei) {
        String uri = "";
        Vehicle byImei = vehicleRepository.findByImei(imei).orElseThrow(() -> new VehicleNotFoundException("Vehicle with imei: " + imei + " not found"));
        //        boolean isGs100 = vehicleRepository.existsByImeiAndDeviceType(imei, 1);
        //        uri = "http://localhost:8080/api/imei/" + imei + "/last" + "?isGs100="+isGs100;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/imei/" + imei + "/last" + "?isGs100=" + (byImei.getDeviceType() == 1);
//        uri = "http://localhost:8080/api/imei/" + imei + "/last" + "?isGs100=" + (byImei.getDeviceType() == 1);

        Gs100 result = restTemplate.getForObject(uri, Gs100.class);
        System.out.println("REZULTAT GS100: "+result.toString());
        if (result != null && result.getRfid() != null && !result.getRfid().isEmpty()) {
            List<Driver> allByIdentificationNumberIn = driverRepository.findAllByIdentificationNumberIn(new LinkedList<String>() {{
                add(result.getRfid());
            }});

            result.setDriverName(allByIdentificationNumberIn.size() > 0 ? allByIdentificationNumberIn.get(0).getName() : result.getRfid());
        }
        

        return result;
    }

    ///////////////////////////////////////////////////////////HISTORY//////////////////////////////////////////////////////////////////////

    /**
     * Vraca poslednju poznatu lokaciju vozila
     *
     * @param imei prirodni kljuc vozila
     */
    @GetMapping("api/history/0/imei/{IMEI}/from/{from}/to/{to}")
    private Object historyTelt(@PathVariable("IMEI") String imei, @PathVariable("from") long from, @PathVariable("to") long to) throws Exception {
        return getHistoryTeltonika(imei, from, to);
    }

    /**
     * Vraca istoriju vozila sa teltonika uredjajem
     */
    @GetMapping("api/history/1/imei/{IMEI}/from/{from}/to/{to}")
    private Object historyGs100(@PathVariable("IMEI") String imei, @PathVariable("from") long from, @PathVariable("to") long to) throws Exception {
        return getGs100History(imei, from, to);
    }

    ///////////////////////////////////////////////////////////IZVESTAJ O POTROSNJI GORIVA///////////////////////////////////////////////////////////////

    /**
     * vraca istoriju vozila s gs100 uredjajem
     */
    @GetMapping("api/firm/{firm_id}/report/fuel/imei/{IMEI}/from/{from}/to/{to}")
    private String izvestajOPotrosnjiGoriva(@PathVariable("IMEI") String imei, @PathVariable("from") long dateFromS, @PathVariable("to") long dateToS)
            throws Exception {
        Vehicle v = vehicleService.findByImei(imei);
        if (v.getDeviceType() == null) {
            throw new Exception("Device type is not set for the vehicle. Please go to vehicle administration and set device type.");
        }
        String res = "";
        if (v.getDeviceType() == 0) {
            res = getHistoryTeltonika(imei, dateFromS, dateToS, v.getFuelMargine());
        } else {
            res = getGs100History(imei, dateFromS, dateToS, v.getFuelMargine());

        }
        return reportService.route(res, imei, v);
    }

    /**
     * vraca izvestaj o potrosnji vozila
     *
     * @param imei vozilo
     */
    private String getHistoryTeltonika(String imei, long dateFromS, long dateToS, Integer fuelMargine) {
        if (fuelMargine == null) {
            fuelMargine = 0;
        }
        String uri = "http://localhost:8080/api/history/0/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/history/0/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + fuelMargine;

        try {
            String result = restTemplate.getForObject(uri, String.class);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Vraca istoriju vozila u datom intervalu sa spoljnog api-a sa gs100 uredjaja
     *
     * @param imei vozilo
     */
    private String getGs100History(String imei, long dateFromS, long dateToS, Integer fuelMargine) {
        if (fuelMargine == null) {
            fuelMargine = 0;
        }
        String uri = "http://localhost:8080/api/history/1/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/history/1/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + fuelMargine;

        try {
            String result = restTemplate.getForObject(uri, String.class);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Vraca istoriju vozila u datom intervalu sa spoljnog api-a sa Teltonika uredjaja za dat fuelMargine
     *
     * @param imei vozilo
     */
    private List<TeltonikaHistoryDTO> getHistoryTeltonika(String imei, long dateFromS, long dateToS) {
        List<TeltonikaHistoryDTO> gsHistoryDTOS = new LinkedList<>();
        Map<String, String> cacheDriverNames = new HashMap<>();

        String uri = "http://localhost:8080/api/history/0/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/history/0/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;

        try {
            ResponseEntity<Map<String, List<Teltonika>>> responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, List<Teltonika>>>() {
                    }
            );
            Map<String, List<Teltonika>> resultMap = responseEntity.getBody();
            List<Teltonika> data = resultMap.get("data");
            for (Teltonika teltonika : data) {
                String driverName = cacheDriverNames.getOrDefault(teltonika.getRfid(), "");
                if (driverName.isEmpty()) {
                    Optional<Driver> allByIdNumberIn = (!teltonika.getRfid().isEmpty()) ?
                            driverRepository.findFirstByIdentificationNumber(teltonika.getRfid()) :
                            Optional.empty();
                    cacheDriverNames.put(teltonika.getRfid(), allByIdNumberIn.isPresent() ? allByIdNumberIn.get().getName() : "");
                    gsHistoryDTOS.add(new TeltonikaHistoryDTO(teltonika, allByIdNumberIn.isPresent() ? allByIdNumberIn.get().getName() : ""));
                } else
                    gsHistoryDTOS.add(new TeltonikaHistoryDTO(teltonika, driverName));

            }

            return gsHistoryDTOS;
        } catch (Exception e) {
            return new LinkedList<>();
        }
    }

    private List<GsHistoryDTO> getGs100History(String imei, long dateFromS, long dateToS) {
        List<GsHistoryDTO> gsHistoryDTOS = new LinkedList<>();
        String uri = "http://localhost:8080/api/history/1/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/history/1/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
        Map<String, String> cacheDriverNames = new HashMap<>();

        try {
            ResponseEntity<Map<String, List<Gs100>>> responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, List<Gs100>>>() {
                    }
            );
            Map<String, List<Gs100>> resultMap = responseEntity.getBody();
            List<Gs100> data = resultMap.get("data");
            for (Gs100 gs100 : data) {
                String driverName = cacheDriverNames.getOrDefault(gs100.getRfid(), "");
                if (driverName.isEmpty()) {
                    Optional<Driver> allByIdNumberIn = (!gs100.getRfid().isEmpty()) ?
                            driverRepository.findFirstByIdentificationNumber(gs100.getRfid()) :
                            Optional.empty();
                    cacheDriverNames.put(gs100.getRfid(), allByIdNumberIn.isPresent() ? allByIdNumberIn.get().getName() : gs100.getRfid());
                    gsHistoryDTOS.add(new GsHistoryDTO(gs100, allByIdNumberIn.isPresent() ? allByIdNumberIn.get().getName() : gs100.getRfid()));
                } else
                    gsHistoryDTOS.add(new GsHistoryDTO(gs100, driverName));

            }

            return gsHistoryDTOS;
        } catch (Exception e) {
            return new LinkedList<>();
        }
    }

    /////FMS REPORTS/////
    /////////////////////
    /////FMS REPORTS/////

    //Driver fuel consumption

    /**
     * FMS izvestaj o potrosnji jednog vozaca u datom intervalu
     */

    @GetMapping("api/firm/{firm_id}/report/kilometers")
    public ResponseEntity<KilometersAdministrationPageList> getKilometersAdministration(
            @PathVariable("firm_id") int firmId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size,
            @DateTimeFormat(pattern = "dd-MM-yyyy")
            @RequestParam(name = "from_date") java.time.LocalDate fromDate,
            @DateTimeFormat(pattern = "dd-MM-yyyy")
            @RequestParam(name = "to_date") java.time.LocalDate toDate,
            @RequestParam(name = "registration", required = false) String registration

    ) {
        page--;

        Page<KilometersAdministration> result = kilometersAdministrationService.getKilometersAdministration(page, size, fromDate, toDate, registration, firmId);

        List<KilometersAdministrationDTO> mappedResult = result.stream()
                .map(element -> new KilometersAdministrationDTO(element.getId(), element.getVehicle().getVehicleId(), element.getVehicle().getRegistration(),
                        element.getDate(), element.getMileage())).collect(Collectors.toList());

        return ResponseEntity.ok(new KilometersAdministrationPageList(result.getTotalElements(), mappedResult));
    }

    @DeleteMapping("api/firm/{firm_id}/report/kilometers/{id}")
    public ResponseEntity<Void> deleteKilometersAdministration(
            @PathVariable(name = "id") long id

    ) {

        kilometersAdministrationService.deleteKilometerAdministration(id);
        return ResponseEntity.ok(null);
    }

    @PostMapping("api/firm/{firm_id}/report/kilometers")
    public ResponseEntity<KilometersAdministration> addNewKilometers(
            @RequestBody NewKilometersRecordDTO newKilometersRecordDTO
    ) {
        KilometersAdministration kilometersAdministration = kilometersAdministrationService.addNewKilometerRecord(newKilometersRecordDTO);
        return ResponseEntity.ok(kilometersAdministration);
    }

    @PutMapping("api/firm/{firm_id}/report/kilometers/{id}")
    public ResponseEntity<KilometersAdministration> updateKilometeres(
            @PathVariable(name = "id") Long id,
            @RequestBody KilometersUpdateDTO kilometersUpdateDTO) {
        KilometersAdministration kilometersAdministration = kilometersAdministrationService.updateKilometeres(kilometersUpdateDTO, id);
        return ResponseEntity.ok(kilometersAdministration);
    }

    @PutMapping("api/firm/{firm_id}/report/fuel-consumption/fuel-companies")
    public ResponseEntity<List<FmsFuelCompanyFuelConsumption>> getFuelCompanies(

            @RequestBody List<Integer> ids,
            @DateTimeFormat(pattern = "dd-MM-yyyy")
            @RequestParam(name = "fromDate")
            LocalDate fromDate,
            @DateTimeFormat(pattern = "dd-MM-yyyy")
            @RequestParam(name = "toDate")
            LocalDate toDate,
            Double maxSpeed,
            Double maxWheelSpin

    ) {
        //Da bi gledao i taj ceo dan,jer nemamo time.Kad se posalje 15 on nece da gleda 15 nego do 15 a mi hocemo i 15 pa smo dodali dan da bude do 16 znaci i 15 se gleda
        toDate = toDate.plusDays(1);
        List<FmsFuelCompanyFuelConsumption> fmsFuelCompanyConsumptionReport = reportService.getFmsFuelCompanyConsumptionReport(ids, fromDate, toDate, maxSpeed,
                maxWheelSpin);
        return ResponseEntity.ok(fmsFuelCompanyConsumptionReport);
    }

    @GetMapping("api/firm/{firm_id}/report/fuel-consumption/driver/{driver_id}/from/{from}/to/{to}")
    private Response<List<FuelConsumption>> getFromDateToDateDriverFuel(@PathVariable("driver_id") int driverId, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS) throws Exception {
        Date dateFrom = null;
        try {
            if (!dateFromS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateFrom = sdf.parse(dateFromS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Date dateTo = null;
        try {
            if (!dateToS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateTo = sdf.parse(dateToS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        return reportService.findDriverFuel(dateFrom, dateTo, driverId);
    }

    @PutMapping("api/firm/{firm_id}/report/fuel-consumption/fuel-companies/pdf")
    public ResponseEntity<ByteArrayResource> getFuelConsumptionFuelCompaniesPdfExport(
            @RequestBody List<Integer> ids,
            @DateTimeFormat(pattern = "dd-MM-yyyy")
            @RequestParam(name = "fromDate")
            LocalDate fromDate,
            @DateTimeFormat(pattern = "dd-MM-yyyy")
            @RequestParam(name = "toDate")
            LocalDate toDate,
            Double maxSpeed,
            Double maxWheelSpin
    ) {
        //Pozovi servisnu metodu
        //toDate = toDate.plusDays(1);
        List<FmsFuelCompanyFuelConsumption> fmsFuelCompanyConsumptionReport = reportService.getFmsFuelCompanyConsumptionReport(ids, fromDate, toDate, maxSpeed,
                maxWheelSpin);

        //Kreiraj hedere za pdf fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + "FMS Izveštaj o potrošnji goriva sa gorvnih kompanija od: " + fromDate + " do: " + toDate + ".pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        //Kreiraj pdf exporter i obradi podatke
        PdfExporter pdfExporter = new FuelConsumptionFuelCompaniesPdfExport(toDate.atStartOfDay(), fromDate.atStartOfDay(), maxSpeed, maxWheelSpin);
        byte[] pdf = pdfExporter.export(fmsFuelCompanyConsumptionReport, FmsFuelCompanyFuelConsumption.class,
                "FMS Izveštaj o potrošnji goriva sa gorvnih kompanija");
        ByteArrayResource resource = new ByteArrayResource(pdf);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PutMapping("api/firm/{firm_id}/report/fuel-consumption/fuel-companies/xls")
    public ResponseEntity<ByteArrayResource> getFuelConsumptionFuelCompaniesXlsExport(
            @RequestBody List<Integer> ids,
            @DateTimeFormat(pattern = "dd-MM-yyyy")
            @RequestParam(name = "fromDate")
            LocalDate fromDate,
            @DateTimeFormat(pattern = "dd-MM-yyyy")
            @RequestParam(name = "toDate")
            LocalDate toDate,
            Double maxSpeed,
            Double maxWheelSpin
    ) {
        //Pozovi servisnu metodu
        //toDate = toDate.plusDays(1);
        List<FmsFuelCompanyFuelConsumption> fmsFuelCompanyConsumptionReport = reportService.getFmsFuelCompanyConsumptionReport(ids, fromDate, toDate, maxSpeed,
                maxWheelSpin);

        //Kreiraj hedere za pdf fajl
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + "FMS Izveštaj o potrošnji goriva sa gorvnih kompanija od: " + fromDate + " do: " + toDate + ".xls");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        //Kreiraj pdf exporter i obradi podatke
        XlsExporter pdfExporter = new FuelConsumptionFuelCompaniesXlsExport(fromDate.atStartOfDay(), toDate.atStartOfDay(), maxSpeed, maxWheelSpin);
        byte[] pdf = pdfExporter.export(fmsFuelCompanyConsumptionReport, FmsFuelCompanyFuelConsumption.class,
                "FMS Izveštaj o potrošnji goriva sa gorvnih kompanija");
        ByteArrayResource resource = new ByteArrayResource(pdf);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Autowired
    private InterventionService interventionService;
    @Autowired
    private FuelConsumptionService fuelConsumptionService;

    @Autowired
    private MillageService millageService;

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private TicketService ticketService;

    @Autowired
    private TechnicalInspectionService technicalInspectionService;

    @GetMapping("api/firm/{firm_id}/vehicles/{vehicle_id}/tracking")
    @Transactional
    private FMSTrackingDTO getFmsTrackingResponse(
            @PathVariable("firm_id") Integer firmId,
            @PathVariable("vehicle_id") Integer vehicleId,
            @RequestParam("start-doneDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date startDoneDate,
            @RequestParam("end-doneDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            Date endDoneDate) {
        List<InterventionDTO> interventions = interventionService.getAllForVehicleFilteredByStartAndEndDate(vehicleId, startDoneDate, endDoneDate);
        List<FuelConsumption> fuelConsumptions = fuelConsumptionService.getAllByVehicleByDate(vehicleId, startDoneDate, endDoneDate);
        List<Millage> mileages = millageService.getAllForVehicleByStartAndEndDate(vehicleId, startDoneDate, endDoneDate);
        List<Registration> registrations = registrationService.getAllByFirmAndStartDateAndEndDate(firmId, startDoneDate, endDoneDate);
        List<Ticket> tickets = ticketService.getAllByVehicleAndStartDateAndEndDate(vehicleId, startDoneDate, endDoneDate);
        List<TechnicalInspection> technicalInspections = technicalInspectionService.getAllByStartDateAndEndDate(vehicleId, startDoneDate, endDoneDate);

        interventions.sort(Comparator.comparing(InterventionDTO::getDoneDate));

        //Calculate intervention sum price
        AtomicReference<Double> interventionSum = new AtomicReference<>(0.0);
        interventions.forEach(interventionDTO -> interventionSum.updateAndGet(v -> v + interventionDTO.getPrice()));

        //Calculate distance traveled
        AtomicReference<Double> mileageSum = new AtomicReference<>(0.0);
        mileages.forEach(millage -> mileageSum.updateAndGet(v -> v + millage.getAmount()));

        //Fuel consumption total
        AtomicReference<Double> fuelConsumptionSum = new AtomicReference<>(0.0);
        fuelConsumptions.forEach(fuelConsumption -> fuelConsumptionSum.updateAndGet(v -> v + fuelConsumption.getAmount()));

        //Date of last interventions
        Date dateOfLastRegistrion = (registrations.size() > 0) ? registrations.get(registrations.size() - 1).getRegistrationDate() : null;

        //Date of last interventions
        Date dateOfLastInterevention = (interventions.size() > 0) ? interventions.get(interventions.size() - 1).getDoneDate() : null;

        //Date of last ticket
        Date dateOfLastTicekt = (tickets.size() > 0) ? tickets.get(tickets.size() - 1).getDate() : null;

        ;
        //
        registrations.sort(Comparator.comparing(Registration::getRegistrationDate));

        //
        tickets.sort(Comparator.comparing(Ticket::getDate));

        return new FMSTrackingDTO(
                interventions.size(),
                dateOfLastInterevention,
                interventionSum.get(),
                mileageSum.get(),
                dateOfLastRegistrion,
                tickets.size(),
                dateOfLastTicekt,
                fuelConsumptionSum.get(),
                interventions,
                technicalInspections
        );
    }

    //Driver ticket

    /**
     * izvestaj o kaznama jednog vozaca u datom intervalu
     */
    @GetMapping("api/firm/{firm_id}/report/ticket/driver/{driver_id}/from/{from}/to/{to}")
    private Response<List<Ticket>> getFromDateToDateDriverTicket(@PathVariable("driver_id") int driverId, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS) throws Exception {
        Date dateFrom = null;
        try {
            if (!dateFromS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateFrom = sdf.parse(dateFromS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Date dateTo = null;
        try {
            if (!dateToS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateTo = sdf.parse(dateToS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        return reportService.findDriverTicket(dateFrom, dateTo, driverId);
    }

    //Driver pn
    @GetMapping("api/firm/{firm_id}/report/pn/driver/{driver_id}/from/{from}/to/{to}")
    private Response<List<PN>> getFromDateToDateDriverPN(@PathVariable("driver_id") int driverId, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS) throws Exception {
        Date dateFrom = null;
        try {
            if (!dateFromS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateFrom = sdf.parse(dateFromS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Date dateTo = null;
        try {
            if (!dateToS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateTo = sdf.parse(dateToS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        return reportService.findDriverPN(dateFrom, dateTo, driverId);
    }

    //Registraiton

    /**
     * Vraca sve putne naloge jednog vozacau datom intervalu
     */
    @GetMapping("api/firm/{firm_id}/report/registration/vehicle/{vehicle_id}/from/{from}/to/{to}")
    private Response<List<Registration>> getFromDateToDateReg(@PathVariable("vehicle_id") int vehicleId, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS) throws Exception {
        Date dateFrom = null;
        try {
            if (!dateFromS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateFrom = sdf.parse(dateFromS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Date dateTo = null;
        try {
            if (!dateToS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateTo = sdf.parse(dateToS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        return reportService.findRegistration(dateFrom, dateTo, vehicleId);
    }

    @PostMapping("api/firm/{firm_id}/report/registration/vehicles/from/{from}/to/{to}/export/{export}")
    private byte[] getFromDateToDateRegExport(@RequestBody List<Integer> vehicleIds, @PathVariable("from") String dateFromS,
                                                              @PathVariable("to") String dateToS, @PathVariable("export") int export) throws Exception {
        Date dateFrom = null;
        try {
            if (!dateFromS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateFrom = sdf.parse(dateFromS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Date dateTo = null;
        try {
            if (!dateToS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateTo = sdf.parse(dateToS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        List<Registration> registrations = reportService.findRegistrationsForIDs(dateFrom, dateTo, vehicleIds);
        return reportService.registrationsExport(registrations, dateFrom, dateTo, export);
    }

    //Fuel

    /**
     * vraca istoriju potrosnje (troskova na potrosnju) konkretnog vozila u datom intervalu
     */
    @GetMapping("api/firm/{firm_id}/report/fuel/vehicle/{vehicle_id}/from/{from}/to/{to}")
    private Response<List<FuelConsumption>> getFromDateToDateFuel(@PathVariable("vehicle_id") int vehicleId, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS) throws Exception {
        Date dateFrom = null;
        try {
            if (!dateFromS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateFrom = sdf.parse(dateFromS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Date dateTo = null;
        try {
            if (!dateToS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateTo = sdf.parse(dateToS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        return reportService.findFuel(dateFrom, dateTo, vehicleId);
    }

    @PostMapping("api/firm/{firm_id}/report/fuel/vehicles/from/{from}/to/{to}")
    private Response<Map<Integer, List<FuelConsumption>>> getFromDateToDateFuelForVehicles(@RequestBody List<Integer> vehicleIds, @PathVariable("from") String dateFromS,
                                                                  @PathVariable("to") String dateToS) throws Exception {
        Date dateFrom = null;
        try {
            if (!dateFromS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateFrom = sdf.parse(dateFromS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Date dateTo = null;
        try {
            if (!dateToS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateTo = sdf.parse(dateToS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        return new Response<>(reportService.findFuelForVehicles(dateFrom, dateTo, vehicleIds));
    }

    @PostMapping("api/firm/{firm_id}/report/fuel/vehicles/from/{from}/to/{to}/export/{export}")
    private byte[] getFromDateToDateFuelForVehiclesExport(@RequestBody List<Integer> vehicleIds, @PathVariable("from") String dateFromS,
                                                                                           @PathVariable("to") String dateToS, @PathVariable int export) throws Exception {
        Date dateFrom = null;
        try {
            if (!dateFromS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateFrom = sdf.parse(dateFromS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Date dateTo = null;
        try {
            if (!dateToS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateTo = sdf.parse(dateToS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Map<Integer, List<FuelConsumption>> list = reportService.findFuelForVehicles(dateFrom, dateTo, vehicleIds);
        return reportService.fuelForVehiclesExport(dateFrom, dateTo, list, export);
    }

    //Intervention

    /**
     * Vraca listu intervencija nekog vozila u datom vremenskom intervalu
     *
     * @param vehicleId vozilo
     * @param dateFromS datum od
     * @param dateToS   datum do
     * @return
     * @throws Exception ako je los unos datuma ili vozilo ne postoji
     */
    @GetMapping("api/firm/{firm_id}/report/intervention/vehicle/{vehicle_id}/from/{from}/to/{to}")
    private Response<List<Intervention>> getFromDateToDateIntervention(@PathVariable("vehicle_id") int vehicleId, @PathVariable("from") String dateFromS,
            @PathVariable("to") String dateToS) throws Exception {
        Date dateFrom = null;
        try {
            if (!dateFromS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateFrom = sdf.parse(dateFromS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Date dateTo = null;
        try {
            if (!dateToS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateTo = sdf.parse(dateToS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }
        return reportService.findIntervention(dateFrom, dateTo, vehicleId);
    }

    @PostMapping("api/firm/{firm_id}/report/intervention/vehicles/from/{from}/to/{to}/export/{export}")
    private byte[] getFromDateToDateInterventionExport(@RequestBody List<Integer> vehicleIds, @PathVariable("from") String dateFromS,
                                                                       @PathVariable("to") String dateToS, @PathVariable("export") int export) throws Exception {
        Date dateFrom = null;
        try {
            if (!dateFromS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateFrom = sdf.parse(dateFromS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }

        Date dateTo = null;
        try {
            if (!dateToS.equalsIgnoreCase("-")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateTo = sdf.parse(dateToS);
            }
        } catch (Exception e) {
            throw new Exception("Bad request");
        }
        List<Intervention> interventions = reportService.findInterventionsForIds(dateFrom, dateTo, vehicleIds);
        return reportService.interventionExport(interventions, export, dateFrom, dateTo);
    }

    //GEOZONE

    /**
     * Vraca izvestaj o geozoni konkretnog vozila pozivajuci spoljni api - Lista objekata
     *
     * @param geozoneId geozona
     * @param imei      vozilo
     * @param dateFromS datum od
     * @param dateToS   datum do
     * @return lista VGR objekata
     * @throws Exception ako geozona ne postoji
     */
    @GetMapping("api/firm/{firm_id}/report/imei/{imei}/geozone/{geozone_id}/from/{from}/to/{to}")
    private Response<List<VGR>> getGeozoneReport(@PathVariable("geozone_id") int geozoneId, @PathVariable("imei") String imei,
            @PathVariable("from") long dateFromS, @PathVariable("to") long dateToS) throws Exception {

        List<VGR> vgrList = new ArrayList<>();
        Optional<Geozone> optionalGeozone = geozoneRepository.findById(geozoneId);
        if (!optionalGeozone.isPresent()) {
            throw new Exception("Geozone not present");
        }

        Geozone geozone = optionalGeozone.get();

        Vehicle vehicle = vehicleService.findByImei(imei);
        if (vehicle == null) {
            throw new Exception("IMEI does not exist");
        }

        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/history/1/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
        if (vehicle.getDeviceType() == 0) {
            uri = mongoServerConfig.getMongoBaseUrl() + "/api/history/0/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
        }
//        String uri = "http://localhost:8080/api/history/1/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
//        if (vehicle.getDeviceType() == 0) {
//            uri = "http://localhost:8080/api/history/0/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
//        }


        String result = restTemplate.getForObject(uri, String.class);
        List<Gs100> gs100List = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        if (result.length() > 22) {
            result = result.substring(8, result.length() - 1);
            gs100List = mapper.readValue(result, new TypeReference<List<Gs100>>() {
            });
        }
        Map<String, Driver> drivers = new HashMap<>();
        Collections.reverse(gs100List);

        Polygon polygon = new Polygon();
        if (!geozone.getType().equals("circ")) {// ako nije krug
            String geozoneString = geozone.getJson();
            int x = geozoneString.indexOf("\"bounds\"");
            int y = geozoneString.length() - 1;
            String s2 = geozoneString.substring(x + 9, y);

            ObjectMapper mapper2 = new ObjectMapper();
            try {
                ArrayList<LatLng> latLngList = mapper2.readValue(s2, new TypeReference<List<LatLng>>() {
                });
                for (LatLng latLng : latLngList) {
                    polygon.addPoint((int) (latLng.getLat() * 100000), (int) (latLng.getLng() * 100000));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new Response<>(new ArrayList<>());
            }
        }

        boolean inside = false;

        VGR vgr = new VGR();
        vgr.setGeozone(geozone);
        vgr.setVehicle(vehicle);
        long stajanje = 0;
        double millage = 0;
        long mirovanje = 0;

        boolean crico = geozone.getType().equalsIgnoreCase("circ");

        if (gs100List.size() == 0) {
            return new Response<>(new ArrayList<>());
        }

        double daljinaPrvog = 0.0;
        if (geozone.getLat() != null && geozone.getLng() != null)
            daljinaPrvog = distance1((double) gs100List.get(0).getGps().getLat(), (double) gs100List.get(0).getGps().getLng(),
                    (double) geozone.getLat(), (double) geozone.getLng());

        if (crico) {
            if ((daljinaPrvog * 1000) < geozone.getRadius()) {
                inside = true;
                vgr.setEntryTime(dateFromS);
                Double fuel = gs100List.get(0).getIo().getOrDefault("LVCAN Fuel Level (liters)", 0.0);
                Double fuelPercentage = gs100List.get(0).getIo().getOrDefault("LVCAN Fuel Level (percentage)", 0.0);
                if (fuel == 0.0 && fuelPercentage != 0.0) {
                    fuel = (fuelPercentage * vehicle.getFuelMargine()) / 100;
                } else if (fuel == 0.0 && fuelPercentage == 0.0)
                    fuel = gs100List.get(0).getIo().getOrDefault("Techton liters", 0.0);
                System.out.println("Namestam gorivo start na 1-" + fuel);

                vgr.setFuelStart(fuel);
            }
        } else {
            //ovde za polygon najruzniji kod ikad
            if (polygon.contains((int) (gs100List.get(0).getGps().getLat() * 100000), (int) (gs100List.get(0).getGps().getLng() * 100000))) {
                inside = true;
                vgr.setEntryTime(dateFromS);
                Double fuel = gs100List.get(0).getIo().getOrDefault("LVCAN Fuel Level (liters)", 0.0);
                Double fuelPercentage = gs100List.get(0).getIo().getOrDefault("LVCAN Fuel Level (percentage)", 0.0);
                if (fuel == 0.0 && fuelPercentage != 0.0) {
                    fuel = (fuelPercentage * vehicle.getFuelMargine()) / 100;
                } else if (fuel == 0.0 && fuelPercentage == 0.0)
                    fuel = gs100List.get(0).getIo().getOrDefault("Techton liters", 0.0);
                System.out.println("Namestam gorivo start na 2-" + fuel);

                vgr.setFuelStart(fuel);
            }
        }

        for (int i = 1; i < gs100List.size(); i++) {
            String rfid = gs100List.get(i).getRfid();

            Driver driverChache = drivers.getOrDefault(rfid, null);
            List<Driver> byVehicle = driverRepository.findByVehicle(vehicle);

            if (driverChache != null) {
                vgr.setDriverName(drivers.get(rfid).getName());
            } else {
                Optional<Driver> allByIdNumberIn = (!rfid.isEmpty()) ? driverRepository.findFirstByIdentificationNumber(rfid) : Optional.empty();

                drivers.put(rfid, allByIdNumberIn.orElseGet(() -> !byVehicle.isEmpty() ? byVehicle.get(0) : null));
                vgr.setDriverName(allByIdNumberIn.isPresent() ? allByIdNumberIn.get().getName() : "");
            }

            if (crico) {

                double length = distance1((double) gs100List.get(i).getGps().getLat(), (double) gs100List.get(i).getGps().getLng(),
                        (double) geozone.getLat(), (double) geozone.getLng());

                if (inside) {
                    if (vgr.getEntryTime() == 0) {
                        vgr.setEntryTime(gs100List.get(i).getGps().getTimestamp().getTime());
                        Double fuel = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (liters)", 0.0);
                        Double fuelPercentage = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (percentage)", 0.0);
                        if (fuel == 0.0 && fuelPercentage != 0.0) {
                            fuel = (fuelPercentage * vehicle.getFuelMargine()) / 100;
                        } else if (fuel == 0.0 && fuelPercentage == 0.0)
                            fuel = gs100List.get(i).getIo().getOrDefault("Techton liters", 0.0);
                        System.out.println("Namestam gorivo start na 3-" + fuel);

                        vgr.setFuelStart(fuel);

                    }

                    double duljina = distance1(gs100List.get(i).getGps().getLat(), gs100List.get(i).getGps().getLng(), gs100List.get(i - 1).getGps().getLat(),
                            gs100List.get(i - 1).getGps().getLng());
                    if (!Double.isNaN(duljina)) {
                        millage += duljina;
                    }

                    if (gs100List.get(i).getGps().isIgnition()) {
                        if (gs100List.get(i).getGps().getSpeed() < 3)
                            mirovanje += Math.abs(gs100List.get(i).getGps().getTimestamp().getTime() - gs100List.get(i - 1).getGps().getTimestamp().getTime());
                    } else {
                        stajanje += Math.abs(gs100List.get(i).getGps().getTimestamp().getTime() - gs100List.get(i - 1).getGps().getTimestamp().getTime());
                    }

                    //     if (i==(gs100List.size()-1))

                    if (!Double.isNaN(length)) {
                        if ((length * 1000) > geozone.getRadius()) {// izasao
                            vgr.setStajanje(stajanje);
                            vgr.setMillage(millage);
                            vgr.setMirovanje(mirovanje);
                            vgr.setExitTime(gs100List.get(i).getGps().getTimestamp().getTime());
                            Double fuel = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (liters)", 0.0);
                            Double fuelPercentage = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (percentage)", 0.0);

                            if (fuel == 0.0 && fuelPercentage != 0.0) {
                                fuel = fuelPercentage * vehicle.getFuelMargine() / 100;
                            } else if (fuel == 0.0 && fuelPercentage == 0.0)
                                fuel = gs100List.get(i).getIo().getOrDefault("Techton liters", 0.0);
                            vgr.setFuelEnd(fuel);
                            if (vgr.getFuelStart() != 0.0 && vgr.getFuelEnd() != 0.0)
                                vgr.setFuelSpend(vgr.getFuelStart() - vgr.getFuelEnd());
                            if (vgrList.size() == 2)
                                System.out.println("cao");
                            vgrList.add(vgr);

                            //reset all
                            inside = false;
                            stajanje = 0;
                            millage = 0;
                            mirovanje = 0;
                            vgr = new VGR();
                            vgr.setGeozone(geozone);
                            vgr.setVehicle(vehicle);
                        } else {
                            if (i == (gs100List.size() - 1)) {
                                vgr.setStajanje(stajanje);
                                vgr.setMillage(millage);
                                vgr.setMirovanje(mirovanje);
                                vgr.setExitTime(dateToS);
                                Double fuel = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (liters)", 0.0);
                                Double fuelPercentage = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (percentage)", 0.0);

                                if (fuel == 0.0 && fuelPercentage != 0.0) {
                                    fuel = fuelPercentage * vehicle.getFuelMargine() / 100;
                                } else if (fuel == 0.0 && fuelPercentage == 0.0)
                                    fuel = gs100List.get(i).getIo().getOrDefault("Techton liters", 0.0);
                                vgr.setFuelEnd(fuel);
                                if (vgr.getFuelStart() != 0.0 && vgr.getFuelEnd() != 0.0)
                                    vgr.setFuelSpend(vgr.getFuelStart() - vgr.getFuelEnd());
                                if (vgrList.size() == 2)
                                    System.out.println("cao");
                                vgrList.add(vgr);

                            }
                        }
                    }
                } else {// ako nije unutra
                    if (!Double.isNaN(length)) {
                        if ((length * 1000) < geozone.getRadius()) {
                            vgr.setEntryTime(gs100List.get(i).getGps().getTimestamp().getTime());
                            Double fuel = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (liters)", 0.0);
                            Double fuelPercentage = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (percentage)", 0.0);

                            if (fuel == 0.0 && fuelPercentage != 0.0) {
                                fuel = fuelPercentage * vehicle.getFuelMargine() / 100;
                            } else if (fuel == 0.0 && fuelPercentage == 0.0)
                                fuel = gs100List.get(i).getIo().getOrDefault("Techton liters", 0.0);
                            //                            System.out.println("Namestam gorivo start na  4 -"+ fuel);
                            System.out.println("Namestam gorivo start na 4-" + fuel);

                            vgr.setFuelStart(fuel);
                            inside = true;
                        }
                    }
                }
            } else {//polygon
                if (inside) {
                    if (i == 1938)
                        System.out.println("cao");
                    millage += distance1(gs100List.get(i).getGps().getLat(), gs100List.get(i).getGps().getLng(), gs100List.get(i - 1).getGps().getLat(),
                            gs100List.get(i - 1).getGps().getLng());

                    if (gs100List.get(i).getGps().isIgnition()) {
                        if (gs100List.get(i).getGps().getSpeed() < 3)
                            mirovanje += Math.abs(gs100List.get(i).getGps().getTimestamp().getTime() - gs100List.get(i - 1).getGps().getTimestamp().getTime());
                    } else {
                        stajanje += Math.abs(gs100List.get(i).getGps().getTimestamp().getTime() - gs100List.get(i - 1).getGps().getTimestamp().getTime());
                    }
                    if (!polygon.contains((int) (gs100List.get(i).getGps().getLat() * 100000), (int) (gs100List.get(i).getGps().getLng() * 100000))) {// izasao

                        vgr.setStajanje(stajanje);
                        vgr.setMillage(millage);
                        vgr.setMirovanje(mirovanje);
                        vgr.setExitTime(gs100List.get(i).getGps().getTimestamp().getTime());
                        Double fuel = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (liters)", 0.0);
                        Double fuelPercentage = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (percentage)", 0.0);

                        if (fuel == 0.0 && fuelPercentage != 0.0) {
                            fuel = fuelPercentage * vehicle.getFuelMargine() / 100;
                        } else if (fuel == 0.0 && fuelPercentage == 0.0)
                            fuel = gs100List.get(i).getIo().getOrDefault("Techton liters", 0.0);
                        vgr.setFuelEnd(fuel);
                        if (vgr.getFuelStart() != 0.0 && vgr.getFuelEnd() != 0.0)
                            vgr.setFuelSpend(vgr.getFuelStart() - vgr.getFuelEnd());

                        vgrList.add(vgr);

                        //reset all

                        inside = false;
                        stajanje = 0;
                        millage = 0;
                        mirovanje = 0;
                        vgr = new VGR();
                        vgr.setGeozone(geozone);
                        vgr.setVehicle(vehicle);
                    } else {
                        if (i == (gs100List.size() - 1)) {
                            vgr.setStajanje(stajanje);
                            vgr.setMillage(millage);
                            vgr.setMirovanje(mirovanje);
                            vgr.setExitTime(dateToS);
                            Double fuel = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (liters)", 0.0);
                            Double fuelPercentage = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (percentage)", 0.0);

                            if (fuel == 0.0 && fuelPercentage != 0.0) {
                                fuel = fuelPercentage * vehicle.getFuelMargine() / 100;
                            } else if (fuel == 0.0 && fuelPercentage == 0.0)
                                fuel = gs100List.get(i).getIo().getOrDefault("Techton liters", 0.0);

                            vgr.setFuelEnd(fuel);
                            if (vgr.getFuelStart() != 0.0 && vgr.getFuelEnd() != 0.0)
                                vgr.setFuelSpend(vgr.getFuelStart() - vgr.getFuelEnd());
                            if (vgrList.size() == 2)
                                System.out.println("cao");
                            vgrList.add(vgr);

                        }
                    }
                } else {// ako nije unutra
                    if (polygon.contains((int) (gs100List.get(i).getGps().getLat() * 100000), (int) (gs100List.get(i).getGps().getLng() * 100000))) {// usao
                        vgr.setEntryTime(gs100List.get(i).getGps().getTimestamp().getTime());

                        Double fuel = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (liters)", 0.0);
                        Double fuelPercentage = gs100List.get(i).getIo().getOrDefault("LVCAN Fuel Level (percentage)", 0.0);
                        if (fuel == 0.0 && fuelPercentage != 0.0) {
                            fuel = (fuelPercentage * vehicle.getFuelMargine()) / 100;
                        } else if (fuel == 0.0 && fuelPercentage == 0.0)
                            fuel = gs100List.get(i).getIo().getOrDefault("Techton liters", 0.0);
                        //                        System.out.println("Namestam gorivo start na 5- "+ fuel +" lsita size "+ vgrList.size()+ gs100List.get(i).getGps() + gs100List.get(i).getIo());

                        System.out.println("Namestam gorivo start na 5-" + fuel);

                        vgr.setFuelStart(fuel);
                        inside = true;
                    }
                }
            }
        }

        Collections.sort(vgrList, new Comparator<VGR>() {
            @Override
            public int compare(VGR o1, VGR o2) {
                if (o1.getEntryTime() < o2.getEntryTime())
                    return -1;
                return 1;
            }
        });

        return new Response<>(vgrList);
    }

    /**
     * Vraca izvestaj o geozoni konkretnog vozila pozivajuci spoljni api - workbook
     *
     * @param imei      vozilo
     * @param dateFromS datum od
     * @param dateToS   datum do
     * @return bytearray workbook
     * @throws Exception ako geozona ne postoji
     */
    @PostMapping("api/firm/{firm_id}/report/geozone/imei/{imei}/from/{from}/to/{to}/export/{eid}")//done
    public byte[] getGeozoneReportExprot(@PathVariable("eid") int eid, @RequestBody List<Integer> ids, @PathVariable("imei") String imei,
            @PathVariable("from") long dateFromS, @PathVariable("to") long dateToS) throws Exception {
        List<VGR> vgrList = new ArrayList<>();
        for (Integer integer : ids) {
            vgrList.addAll(getGeozoneReport(integer, imei, dateFromS, dateToS).getData());
        }
        System.out.println(vgrList.size());
        return reportService.geozoneExcport(vgrList, eid, dateFromS, dateToS);
    }

    //RUTICE

    /**
     * Vraca izvestaj o rutama vozila u datom intervalu kao listu objekata
     */
    @GetMapping(value = "api/firm/{firm_id}/report/imei/{imei}/route/{route_id}/from/{from}/to/{to}/groute")
    private Response<List<DTORotue>> getRuticeReportic(@PathVariable("route_id") int routeId, @PathVariable("imei") String imei,
            @PathVariable("from") long dateFromS, @PathVariable("to") long dateToS) throws Exception {
        List<VGR> vgrList = new ArrayList<>();
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (!optionalRoute.isPresent()) {
            throw new Exception("Route not present");
        }
        Route route = optionalRoute.get();

        Vehicle vehicle = vehicleService.findByImei(imei);
        if (vehicle == null) {
            throw new Exception("IMEI does not exist");
        }

        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/history/1/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
        if (vehicle.getDeviceType() == 0) {
            uri = mongoServerConfig.getMongoBaseUrl() + "/api/history/0/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS;
        }


        String result = restTemplate.getForObject(uri, String.class);
        List<Gs100> gs100List = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        if (result.length() > 22) {
            result = result.substring(8, result.length() - 1);

            gs100List = mapper.readValue(result, new TypeReference<List<Gs100>>() {
            });
        }

        if (route.getRouteString() == null || route.getRouteString().length() < 10) {
            return null;
        }

        String routeString = route.getRouteString();
        int x = routeString.indexOf("\"overview_path\"");
        int y = routeString.indexOf("\"status\"");
        String s2 = routeString.substring(x + 16, y - 1);

        ArrayList<LatLng> latLngList = new ArrayList<>();
        ObjectMapper mapper2 = new ObjectMapper();
        try {
            latLngList = mapper2.readValue(s2, new TypeReference<List<LatLng>>() {
            });
        } catch (Exception e) {
            return null;
        }

        List<DTORotue> responseList = new ArrayList<>();
        DTORotue dtoRotue = null;

        for (int i = 1; i < gs100List.size(); i++) {
            double najblizaTacka = najblizaTacka(gs100List.get(i).getGps().getLat(), gs100List.get(i).getGps().getLng(), latLngList);
            if (najblizaTacka < 0.68) {//INSIDE
                if (dtoRotue != null) {
                    dtoRotue.setEndTime(gs100List.get(i).getGps().getTimestamp());
                    responseList.add(new DTORotue(dtoRotue));
                    dtoRotue = null;
                } else {
                    continue;
                }
            } else { // OUTSIDE
                if (dtoRotue != null) { // vec se spolja znaci meri se, idemo dalje, samo izracunaj stvari
                    double length = distance1((double) gs100List.get(i).getGps().getLat(), (double) gs100List.get(i).getGps().getLng(),
                            (double) gs100List.get(i - 1).getGps().getLat(), (double) gs100List.get(i - 1).getGps().getLng());

                    if (!Double.isNaN(length)) {
                        dtoRotue.setRoadTraveled(dtoRotue.getRoadTraveled() + length);
                    }

                    if (gs100List.get(i).getGps().isIgnition()) {
                        if (gs100List.get(i).getGps().getSpeed() < 3)
                            dtoRotue.setIdleTime(dtoRotue.getIdleTime() + Math.abs(
                                    gs100List.get(i).getGps().getTimestamp().getTime() - gs100List.get(i - 1).getGps().getTimestamp().getTime()));
                        else
                            dtoRotue.setTimeOfTravel(dtoRotue.getTimeOfTravel() + Math.abs(
                                    gs100List.get(i).getGps().getTimestamp().getTime() - gs100List.get(i - 1).getGps().getTimestamp().getTime()));
                    } else {
                        dtoRotue.setStoppedTime(dtoRotue.getStoppedTime() + Math.abs(
                                gs100List.get(i).getGps().getTimestamp().getTime() - gs100List.get(i - 1).getGps().getTimestamp().getTime()));
                    }
                    dtoRotue.getLatLngList().add(gs100List.get(i));
                } else { // pocinje
                    dtoRotue = new DTORotue();
                    dtoRotue.setVehicle(vehicle);
                    dtoRotue.setRouteName(route.getName());
                    dtoRotue.setStartTime(gs100List.get(i).getGps().getTimestamp());
                    dtoRotue.getLatLngList().add(gs100List.get(i));
                }
            }
        }

        if (dtoRotue != null) {
            dtoRotue.setEndTime(gs100List.get(gs100List.size() - 1).getGps().getTimestamp());
            responseList.add(new DTORotue(dtoRotue));
        }
        return new Response<>(responseList);
    }

    //RUTICE

    /**
     * Vraca izvestaj o rutama vozila u datom intervalu kao byte array workbook
     */
    @PostMapping(value = "api/firm/{firm_id}/report/imei/{imei}/from/{from}/to/{to}/groute/export/{eid}")
    private byte[] getRuticeReporticExport(
            @PathVariable("firm_id") int firmId,
            @PathVariable("eid") int eid,
            @PathVariable("imei") String imei,
            @PathVariable("from") long dateFromS,
            @PathVariable("to") long dateToS,
            @RequestBody List<Integer> routeIds
    ) throws Exception {
        List<DTORotue> wholeList = new ArrayList<>();
        for (Integer integer : routeIds) {
            wholeList.addAll(getRuticeReportic(integer, imei, dateFromS, dateToS).getData());
        }
        return reportService.exportRouteIskiakanje(wholeList, eid, dateFromS, dateToS);
    }

    private double najblizaTacka(double lat, double lng, ArrayList<LatLng> latLngList) {
        double min = 999999999;
        for (LatLng latLng : latLngList) {
            double dist = distance1(lat, lng, latLng.getLat(), latLng.getLng());
            if (dist < min) {
                min = dist;
            }
        }
        return min;
    }

    //Driver

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////

    /**
     * poziva spoljni api za izvestaj o vozilu za teltonika uredjaje
     */
    private String getTeltonikaMonthlyHours(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto) {
        String uri =
                "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/monthly/hours";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/monthly/hours";


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    /**
     * poziva spoljni api za izvestaj o vozilu za gs100 uredjaje
     */
    private String getGs100MonthlyHours(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto) {
        String uri =
                "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/monthly/hours";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/monthly/hours";


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    //////////////////////////////////////VRV////////////////////////////////////////////////////////////

    /**
     * vraca mesecni vrv izvestaj za gs100 uredjaje
     */
    private String getGs100MonthlyHoursVrv(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int hfromsa, int mfromsa,
            int htosa, int mtosa, int hfromsu, int mfromsu, int htosu, int mtosu, int working) {
        String uri =
                "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/" + hfromsa + "/" + mfromsa + "/" + htosa + "/" + mtosa + "/" + hfromsu + "/" + mfromsu + "/" + htosu + "/" + mtosu + "/working/"
                        + working;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                + "/" + hfromsa + "/" + mfromsa + "/" + htosa + "/" + mtosa + "/" + hfromsu + "/" + mfromsu + "/" + htosu + "/" + mtosu + "/working/" + working;


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    private Map<String, JsonNode> getGs100MonthlyHoursVrvBatch(List<String> gs100Imeis, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int hfromsa, int mfromsa, int htosa, int mtosa, int hfromsu, int mfromsu, int htosu, int mtosu, int working) {
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imeis/" + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/" + hfromsa + "/" + mfromsa + "/" + htosa + "/" + mtosa + "/" + hfromsu + "/" + mfromsu + "/" + htosu + "/" + mtosu + "/working/"
                + working+ "?imeis=" + String.join("&imeis=", gs100Imeis);;


        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, JsonNode>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Gs100 batch response", e);
        }
    }

    /**
     * mesecni gs100 izvestaj
     */

    private String getTeltonikaMonthlyHoursVrv(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int hfromsa, int mfromsa,
            int htosa, int mtosa, int hfromsu, int mfromsu, int htosu, int mtosu, int working) {
        String uri =
                "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/" + hfromsa + "/" + mfromsa + "/" + htosa + "/" + mtosa + "/" + hfromsu + "/" + mfromsu + "/" + htosu + "/" + mtosu + "/working/"
                        + working;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/" + hfromsa + "/" + mfromsa + "/" + htosa + "/" + mtosa + "/" + hfromsu + "/" + mfromsu + "/" + htosu + "/" + mtosu + "/working/"
                + working;


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }


    private Map<String, JsonNode> getTeltonikaMonthlyHoursVrvBatch(List<String> teltonikaImeis, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int hfromsa, int mfromsa,
                                                                   int htosa, int mtosa, int hfromsu, int mfromsu, int htosu, int mtosu, int working) {

       String uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imeis/" + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/" + hfromsa + "/" + mfromsa + "/" + htosa + "/" + mtosa + "/" + hfromsu + "/" + mfromsu + "/" + htosu + "/" + mtosu + "/working/"
                + working+ "?imeis=" + String.join("&imeis=", teltonikaImeis);;


        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, JsonNode>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Teltonika batch response", e);
        }
    }

    //////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////

    /**
     * mesecni teltonika izvestaj
     */
    private String getGs100Monthly(String imei, String dateFromS, String dateToS) {
        String uri = "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/monthly";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/monthly";


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    /**
     * Vraca izvestaj o predjenom putu vozila sa drugog api-ja, specifican za teltonika uredjaje
     */

    private String getTeltonikaMonthly(String imei, String from, String to) {
        String uri = "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + from + "/to/" + to + "/monthly";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + from + "/to/" + to + "/monthly";


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    private Map<String, JsonNode> getGs100MonthlyBatch(List<String> gs100Imeis, String dateFromS, String dateToS) {
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imeis/from/"+ dateFromS + "/to/" + dateToS + "/monthly" + "?imeis=" + String.join("&imeis=", gs100Imeis);


        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, JsonNode>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing GS100 batch response", e);
        }
    }

    private Map<String, JsonNode> getTeltonikaMonthlyBatch(List<String> teltonikaImeis, String dateFromS, String dateToS) {
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imeis/from/" + dateFromS + "/to/" + dateToS + "/monthly" + "?imeis=" + String.join("&imeis=", teltonikaImeis);


        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, JsonNode>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Teltonika batch response", e);
        }
    }
    //////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////

    private String getGs100(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto) {
        String uri =
                "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto;


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    private String getTeltonika(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto) {
        String uri = "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto;


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }


    private Map<String, JsonNode> getGs100Batch(List<String> gs100Imeis, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto) {
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imeis/from/"+ dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                + "?imeis=" + String.join("&imeis=", gs100Imeis);


        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, JsonNode>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing GS100 batch response", e);
        }
    }

    private Map<String, JsonNode> getTeltonikaBatch(List<String> teltonikaImeis, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto) {
        String uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imeis/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                + "?imeis=" + String.join("&imeis=", teltonikaImeis);


        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, JsonNode>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Teltonika batch response", e);
        }

    }

    //////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * Vraca izvestaj o max brzini sa spoljnog api-a specifican za Teltonika uredjaje
     */
    private String getTeltonikaSpeed(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int max) {
        String uri =
                "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/speed/max/" + max;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/speed/max/" + max;


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    private String getTeltonikaSpeedBatch(List<String> teltonikaImeis, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int max) {
        String  uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imeis/" + "from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/speed/max/" + max + "?imeis=" + String.join("&imeis=", teltonikaImeis);


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    /**
     * Vraca izvestaj o max brzini sa spoljnog api-a specifican za Gs100 uredjaje
     */
    private String getGs100Speed(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int max) {
        String uri =
                "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/speed/max/" + max;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                + "/speed/max/" + max;


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    private String getGs100SpeedBatch(List<String> gs100imeis, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int max) {
        String  uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imeis/" + "from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/speed/max/" + max + "?imeis=" + String.join("&imeis=", gs100imeis);


        String result = restTemplate.getForObject(uri, String.class);
        return result;

    }

    ////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Vraca izvestaj o rutama sa spoljnog api-a specifican za teltonika uredjaje
     */
    private String getTeltonikaRoute(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, Double minDistance) {
        String uri =
                "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/routes?minDistance=" + minDistance;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/routes?minDistance=" + minDistance;


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    private Map<String, JsonNode> getTeltonikaRouteBatch(List<String> teltonikaImeis, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, Double minDistance) {
        String  uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imeis/" + "from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/routes?minDistance=" + minDistance + "&imeis=" + String.join("&imeis=", teltonikaImeis);


        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, JsonNode>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Teltonika batch response", e);
        }
    }

    /**
     * Vraca izvestaj o rutama sa spoljnog api-a specifican za gs100 uredjaje
     */
    private String getGs100Route(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, Double minDistance) {
        String uri =
                "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/routes?minDistance=" + minDistance;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                + "/routes?minDistance=" + minDistance;


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    private Map<String, JsonNode> getGs100RouteBatch(List<String> teltonikaImeis, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, Double minDistance) {
        String  uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imeis/" + "from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/routes?minDistance=" + minDistance + "&imeis=" + String.join("&imeis=", teltonikaImeis);


        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, JsonNode>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Teltonika batch response", e);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////

    //Poziv ka mongo mikroservisu da pokupi podatke za teltoniku

    /**
     * vraca informaciju o stajanjima vozila sa teltonika uredjajem u vremenskom intervalu
     */
    private String getTeltonikaIdle(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int min, boolean isIdle,
            int minIdle) {
        String uri =
                "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                       + "/min/" + min + "/minIdle" + "/" + minIdle + "/idle?isIdle=" + isIdle;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/min/" + min + "/minIdle" + "/" + minIdle + "/idle?isIdle=" + isIdle;


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    private Map<String, JsonNode> getTeltonikaIdleBatch(List<String> teltonikaImeis, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int min, boolean isIdle, int minIdle) {
        String uri =mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imeis/" + "from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
               + mto + "/min/" + min + "/minIdle" + "/" + minIdle + "/idle?isIdle=" + isIdle + "&imeis=" + String.join("&imeis=", teltonikaImeis);


        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, JsonNode>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Teltonika batch response", e);
        }

    }

    //Poziv ka mongo mikroservisu da pokupi podatke za gs100

    /**
     * vraca informaciju o stajanjima vozila sa gs100 uredjajem u vremenskom intervalu
     */
    private String getGs100OIdle(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int min, boolean isIdle, int minIdle) {
        String uri =
                "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/min/" + min + "/minIdle" + "/" + minIdle + "/idle?isIdle=" + isIdle;
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                + "/min/" + min + "/minIdle" + "/" + minIdle + "/idle?isIdle=" + isIdle;


        String result = restTemplate.getForObject(uri, String.class);
        return result;
    }

    private Map<String, JsonNode> getGs100OIdleBatch(List<String> gs100Imeis, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, int min, boolean isIdle, int minIdle) {
        String uri =mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imeis/" + "from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/min/" + min + "/minIdle" + "/" + minIdle + "/idle?isIdle=" + isIdle + "&imeis=" + String.join("&imeis=", gs100Imeis);


        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, JsonNode>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing GS100 batch response", e);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////

    /**
     * vraca izvestaj o zelenoj voznji specifican za teltonika uredjaje
     */

    private String getTeltonikaGreen(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto) {
        String uri =
                "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/green";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/green";


        try {
            String result = restTemplate.getForObject(uri, String.class);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * vraca izvestaj o zelenoj voznji specifican za gs100 uredjaje
     */
    private String getGs100OGreen(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto) {
        String uri =
                "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/green";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                + "/green";

        try {
            String result = restTemplate.getForObject(uri, String.class);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * vraca izvestaj o temperaturanma sa spoljnog api-a specifican za teltonika uredjaje
     */
    private String getTeltonikaTemp(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto) {
        String uri =
                "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/temp";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/temp";

        try {
            String result = restTemplate.getForObject(uri, String.class);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * vraca izvestaj o temperaturama sa spoljno api-a specifican za gs100 uredjaje
     */
    private String getGs100Temp(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto) {
        String uri =
                "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/temp";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                + "/temp";

        try {
            String result = restTemplate.getForObject(uri, String.class);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * vraca informacije o dogadjajima vozila u vremenskom intervalu teltonika uredjaja
     */
    private String getTeltonikaEvent(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, Event event) {
        String uri =
                "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/event";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/"
                + mto + "/event";
        //        System.out.println("Sdasdasd");
        //
        //        System.out.println(event.getMaxSpeed()+" max brzina");
        //        System.out.println(event.isSpeed()+"brzina");
        //        System.out.println(event.isAuthorized()+" autoriz");
        //        System.out.println(event.isContact()+"kontakt ");
        //        System.out.println(event.isLocation()+" lokesn");


        try {
            String result = restTemplate.postForObject(uri, event, String.class);
            return result;
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * vraca informacije o dogadjajima vozila u vremenskom intervalu za gs100 uredjaje
     */
    private String getGs100Event(String imei, String dateFromS, String dateToS, int hfrom, int mfrom, int hto, int mto, Event event) {
        String uri =
                "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                        + "/event";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/" + hfrom + "/" + mfrom + "/" + hto + "/" + mto
                + "/event";


        try {
            String result = restTemplate.postForObject(uri, event, String.class);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * vraca izvestaj o gorivu teltonika u nekom intervalu
     * NE KORISTI SE
     */
    private String getTeltonikaFuelSond(String imei, String dateFromS, String dateToS) {
        String uri = "http://localhost:8080/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/fuel";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/teltonika/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/fuel";


        try {
            String result = restTemplate.getForObject(uri, String.class);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * vraca izvestaj o gorivu gs100 u nekom intervalu
     * NE KORISTI SE
     */
    private String getGs100FuelSond(String imei, String dateFromS, String dateToS) {
        String uri = "http://localhost:8080/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/fuel";
        uri = mongoServerConfig.getMongoBaseUrl() + "/api/gs100/imei/" + imei + "/from/" + dateFromS + "/to/" + dateToS + "/fuel";

        try {
            String result = restTemplate.getForObject(uri, String.class);
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * metoda racuna tacnu distancu izmedju dve tacke
     */
    private double distance1(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        if (theta == 0)
            return 0.0;
        double dist = (double) Math.sin(deg2rad(lat1)) * (double) Math.sin(deg2rad(lat2)) + (double) Math.cos(deg2rad(lat1)) * (double) Math.cos(deg2rad(lat2))
                * (double) Math.cos(deg2rad(theta));
        dist = (double) Math.acos(dist);
        dist = (double) rad2deg(dist);
        dist = (double) dist * 60.0 * 1.1515 * 1.609344;

        return (double) (dist);
    }

    /**
     * prevod iz stepena u radijane
     */
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /**
     * prevod iz radijana u stepene
     */
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * Proverava da j je konkretna tacka sa gs100 uredjaja pripada geozoni koja je praovugaonog oblika
     *
     * @param gs100   uredjaj
     * @param geozone geozona
     * @return true ili false
     */
    private boolean checkIfItsInRect(Gs100 gs100, Geozone geozone) {
        if (geozone.getLatTL() >= gs100.getGps().getLat() && gs100.getGps().getLat() >= geozone.getLatBR()) {
                /* If your bounding box doesn't wrap
                   the date line the value
                   must be between the bounds.
                   If your bounding box does wrap the
                   date line it only needs to be
                   higher than the left bound or
                   lower than the right bound. */
            if (geozone.getLngTL() <= geozone.getLngBR() && geozone.getLngTL() <= gs100.getGps().getLng() && gs100.getGps().getLng() <= geozone.getLngBR()) {
                return true;
            } else if (geozone.getLngTL() > geozone.getLngBR() && (geozone.getLngTL() <= gs100.getGps().getLng()
                    || gs100.getGps().getLng() <= geozone.getLngBR())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Proverava da li je konkretna tacka s teltonika uredjaja pripada geozoni koja je pravougaonog oblika
     *
     * @param teltonika uredjaj
     * @param geozone   geozona
     * @return true ili false
     */
    private boolean checkIfItsInRectTelt(Teltonika teltonika, Geozone geozone) {
        if (geozone.getLatTL() >= teltonika.getGps().getLat() && teltonika.getGps().getLat() >= geozone.getLatBR()) {
                /* If your bounding box doesn't wrap
                   the date line the value
                   must be between the bounds.
                   If your bounding box does wrap the
                   date line it only needs to be
                   higher than the left bound or
                   lower than the right bound. */
            if (geozone.getLngTL() <= geozone.getLngBR() && geozone.getLngTL() <= teltonika.getGps().getLng()
                    && teltonika.getGps().getLng() <= geozone.getLngBR()) {
                return true;
            } else if (geozone.getLngTL() > geozone.getLngBR() && (geozone.getLngTL() <= teltonika.getGps().getLng()
                    || teltonika.getGps().getLng() <= geozone.getLngBR())) {
                return true;
            }
        }
        return false;
    }

}
