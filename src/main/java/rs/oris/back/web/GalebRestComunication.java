package rs.oris.back.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.dto.daily_movement_consumption.DailyMovementConsumptionReportAddutionalDataDTO;
import rs.oris.back.domain.dto.FmsFuelCompanyConsumptionAdditionalDataDTO;
import rs.oris.back.domain.dto.daily_movement_consumption.DailyMovementConsumptionReportProjectionData;
import rs.oris.back.domain.dto.report.driver_relation.DriverDriverRelationAdditionalDataDTO;
import rs.oris.back.domain.dto.report.driver_relation.DriverRelationProjectionData;
import rs.oris.back.domain.dto.report.driver_relation.DriverVehicleRelationAdditionDataDTO;
import rs.oris.back.domain.dto.report.driver_relation.DriverVehicleRelationProjectionData;
import rs.oris.back.domain.dto.report.driver_relation_fuel.DriverRelationFuelAdditionalDataDTO;
import rs.oris.back.domain.dto.report.driver_relation_fuel.DriverRelationFuelProjectionData;
import rs.oris.back.domain.dto.report.monthly.MonthFuelReportEngineDTO;
import rs.oris.back.domain.dto.report.monthly.MonthlyFuelConsumptionAditionalDataDTO;
import rs.oris.back.domain.dto.reports.sensor.SensorActivtionVehicleDTO;
import rs.oris.back.domain.dto.reports.sensor.mongo.SensorActivtionAditionalDataDTO;
import rs.oris.back.domain.dto.route.DriverRelationVehicleRoutingInfo;
import rs.oris.back.domain.reports.daily_movement_consumption.DailyMovementConsumptionReport;
import rs.oris.back.domain.reports.driver_relation.DriverRelationReport;
import rs.oris.back.domain.reports.driver_relation.DriverVehicleRelationReport;
import rs.oris.back.domain.reports.driver_relation_fuel.DriverRelationsFuelReport;
import rs.oris.back.domain.reports.effective_hours.EffectiveWorkingHoursReport;
import rs.oris.back.domain.reports.fms_fuel_company_consumption.FmsFuelCompanyFuelConsumption;
import rs.oris.back.domain.reports.monthly_fuel.MonthlyFuelConsumptionReport;
import rs.oris.back.domain.reports.route.DriverRelationVehicleRoutingAllInfo;
import rs.oris.back.domain.reports.sensor_activation.SensorActivationReport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GalebRestComunication {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private rs.oris.back.config.MongoServerConfig mongoServerConfig;

    private String SENSOR_ACTIVATION_REPORT_ENDPOINT = "/sensors-reports";
    private String getBaseUrl() { return mongoServerConfig.getMongoBaseUrl() + "/api/reports"; }

    private String MONTH_FUEL_REPORT_ENDPOINT = "/monthly-fuel-reports";
    private String EFFECTIVE_WORKING_HOURS_REPORT_ENDPOINT = "/effective-working-hours";
    private String DRIVER_RELATION_FUEL_ENDPOINT = "/driver-relation-fuel";
    private String DRIVER_RELATION_ROUTING_INFO_ENDPOINT = "/vehicles/{{imei}}/path";
    private String DRIVER_VEHICLE_RELATION_REPORT_ENDPOINT = "/vehicle/driver-relation";
    private String DRIVER_RELATION_REPORT_ENDPOINT = "/driver/driver-relation";
    private   String FMS_COMPANY_FUEL_CONSUMPTION_ENDPOINT = "/fms/fuel-company/fuel-consumption";
    private   String DAILY_MOVEMENT_CONSUMPTION_REPORT_ENDPOINT = "/daily-movement-consumption";

    public ResponseEntity<List<MonthlyFuelConsumptionReport>> getMonthlyFuelConsumptionReports(LocalDateTime from, LocalDateTime to, List<MonthFuelReportEngineDTO> mappedList, Integer fuelMargin, Integer emptyingMargin) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(getBaseUrl() + MONTH_FUEL_REPORT_ENDPOINT)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("fuelMargin", fuelMargin)
                .queryParam("emptyingMargin", emptyingMargin);

        String url = builder.toUriString();

        //Build map from mappedList
        Map<String, MonthlyFuelConsumptionAditionalDataDTO> vehicles = new HashMap<>();
        for (MonthFuelReportEngineDTO monthFuelReportEngineDTO : mappedList) {
            vehicles.put(monthFuelReportEngineDTO.getImei(), new MonthlyFuelConsumptionAditionalDataDTO(monthFuelReportEngineDTO.getEngineSize(), monthFuelReportEngineDTO.getRegistration(), monthFuelReportEngineDTO.getModel()));
        }

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(vehicles),
                new ParameterizedTypeReference<List<MonthlyFuelConsumptionReport>>() {
                }
        );
    }

    public ResponseEntity<List<SensorActivationReport>> getSensorActivationReports(LocalDateTime from, LocalDateTime to, List<SensorActivtionVehicleDTO> mappedList) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(getBaseUrl() + SENSOR_ACTIVATION_REPORT_ENDPOINT)
                .queryParam("from", from)
                .queryParam("to", to);

        String url = builder.toUriString();

        //Build map from mappedList
        Map<String, SensorActivtionAditionalDataDTO> vehicles = new HashMap<>();
        for (SensorActivtionVehicleDTO sensorActivtionVehicleDTO : mappedList) {
            vehicles.put(sensorActivtionVehicleDTO.getImei(), new SensorActivtionAditionalDataDTO(sensorActivtionVehicleDTO.getRegistration(), sensorActivtionVehicleDTO.getModel()));
        }

        log.info("####################################");
        log.info(LocalDateTime.now() + " - Sending request to Galeb for Sensor Activation Report. URL: " + url + " with vehicles: " + vehicles.keySet());

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(vehicles),
                new ParameterizedTypeReference<List<SensorActivationReport>>() {
                }
        );
    }

    public ResponseEntity<EffectiveWorkingHoursReport> getEffectiveWorkingHoursReport(LocalDateTime from, LocalDateTime to, List<MonthFuelReportEngineDTO> mappedList, Integer rpm, Integer fuelMargin, Integer emptyingMargin) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(getBaseUrl() + EFFECTIVE_WORKING_HOURS_REPORT_ENDPOINT)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("rpm", rpm)
                .queryParam("emptyingMargin", emptyingMargin)
                .queryParam("fuelMargin", fuelMargin);

        String url = builder.toUriString();


        //Build map from mappedList
        Map<String, MonthlyFuelConsumptionAditionalDataDTO> vehicles = new HashMap<>();
        for (MonthFuelReportEngineDTO monthFuelReportEngineDTO : mappedList) {
            vehicles.put(monthFuelReportEngineDTO.getImei(), new MonthlyFuelConsumptionAditionalDataDTO(monthFuelReportEngineDTO.getEngineSize(), monthFuelReportEngineDTO.getRegistration(), monthFuelReportEngineDTO.getModel()));
        }

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(vehicles),
                new ParameterizedTypeReference<EffectiveWorkingHoursReport>() {
                }
        );
    }

    public ResponseEntity<List<DriverRelationsFuelReport>> getDriverRelationsReportFuel(LocalDateTime from, LocalDateTime to, List<DriverRelationFuelProjectionData> mappedList, Integer fuelMargin, Integer emptyingMargin,Double minDisatnce) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(getBaseUrl() + DRIVER_RELATION_FUEL_ENDPOINT)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("emptyingMargin", emptyingMargin)
                .queryParam("fuelMargin", fuelMargin)
                .queryParam("minDistance",minDisatnce)
                ;

        String url = builder.toUriString();


        //Build map from mappedList
        Map<String, DriverRelationFuelAdditionalDataDTO> vehicles = new HashMap<>();
        for (DriverRelationFuelProjectionData driverRelationFuelProjectionData : mappedList) {
            vehicles.put(driverRelationFuelProjectionData.getImei(), new DriverRelationFuelAdditionalDataDTO(driverRelationFuelProjectionData.getEngineSize(), driverRelationFuelProjectionData.getRegistration(), driverRelationFuelProjectionData.getModel()));
        }

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(vehicles),
                new ParameterizedTypeReference<List<DriverRelationsFuelReport>>() {
                }
        );

    }

    public ResponseEntity<List<DriverRelationVehicleRoutingAllInfo>> getVehicleRouting(DriverRelationVehicleRoutingInfo driverRelationVehicleRoutingInfo, String imei, LocalDateTime from, LocalDateTime to) {
        String newURL = DRIVER_RELATION_ROUTING_INFO_ENDPOINT.replace("{{imei}}", imei);
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(getBaseUrl() + newURL)
                .queryParam("from", from)
                .queryParam("to", to);

        String url = builder.toUriString();


        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(driverRelationVehicleRoutingInfo),
                new ParameterizedTypeReference<List<DriverRelationVehicleRoutingAllInfo>>() {
                }
        );
    }

    public ResponseEntity<DriverVehicleRelationReport> getDriverVehicleRelationReport(LocalDateTime from, LocalDateTime to, List<DriverVehicleRelationProjectionData> mappedList,Double minDistance) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(getBaseUrl() + DRIVER_VEHICLE_RELATION_REPORT_ENDPOINT)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("minDistance",minDistance);

        String url = builder.toUriString();


        //Build map from mappedList
        Map<String, DriverVehicleRelationAdditionDataDTO> vehicles = new HashMap<>();
        for (DriverVehicleRelationProjectionData monthFuelReportEngineDTO : mappedList) {
            vehicles.put(monthFuelReportEngineDTO.getImei(), new DriverVehicleRelationAdditionDataDTO(monthFuelReportEngineDTO.getRegistration(), monthFuelReportEngineDTO.getModel()));
        }


        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(vehicles),
                new ParameterizedTypeReference<DriverVehicleRelationReport>() {
                }
        );


    }

    public ResponseEntity<DriverRelationReport> getDriverRelationReport(LocalDateTime from, LocalDateTime to, List<DriverRelationProjectionData> drivers,Double minDistance) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(getBaseUrl() + DRIVER_RELATION_REPORT_ENDPOINT)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("minDistance",minDistance);

        String url = builder.toUriString();
        //rfid->id
        Map<String, DriverDriverRelationAdditionalDataDTO> vehicles = new HashMap<>();
        for (DriverRelationProjectionData driverRelationProjectionData : drivers) {
            vehicles.put(driverRelationProjectionData.getDriverNumber(), new DriverDriverRelationAdditionalDataDTO(driverRelationProjectionData.getDriverId(), driverRelationProjectionData.getDriverName()));
        }
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(vehicles),
                new ParameterizedTypeReference<DriverRelationReport>() {
                }
        );
    }

    public List<FmsFuelCompanyFuelConsumption> getFmsFuelCompanyConsumptionReport(List<Vehicle> vehiclesByImeiIn, LocalDate fromDate, LocalDate toDate, Double maxSpeed, Double maxWheelSpin) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(getBaseUrl() + FMS_COMPANY_FUEL_CONSUMPTION_ENDPOINT)
                .queryParam("from", fromDate)
                .queryParam("to", toDate)
                .queryParam("speed-limit",maxSpeed)
                .queryParam("max-rpm",maxWheelSpin)
                ;

        String url = builder.toUriString();


        Map<String, FmsFuelCompanyConsumptionAdditionalDataDTO> vehicles = new HashMap<>();
        for (Vehicle vehicle : vehiclesByImeiIn) {
            if(vehicle.getImei() != null)
                vehicles.put(vehicle.getImei(), new FmsFuelCompanyConsumptionAdditionalDataDTO(vehicle.getRegistration(),vehicle.getManufacturer(), vehicle.getModel()));
        }
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(vehicles),
                new ParameterizedTypeReference<List<FmsFuelCompanyFuelConsumption>>() {
                }
        ).getBody();
    }

    public ResponseEntity<List<DailyMovementConsumptionReport>> getDailyMovementConsumptionReport(List<DailyMovementConsumptionReportAddutionalDataDTO> mappedList, LocalDateTime from, LocalDateTime to, Integer emptyingMargin, Integer fuelMargin) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(getBaseUrl() + DAILY_MOVEMENT_CONSUMPTION_REPORT_ENDPOINT)
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("emptyingMargin",emptyingMargin)
                .queryParam("fuelMargin",fuelMargin);

        String url = builder.toUriString();


        Map<String, DailyMovementConsumptionReportProjectionData> vehicles = new HashMap<>();
        for (DailyMovementConsumptionReportAddutionalDataDTO vehicle : mappedList) {
            if(vehicle.getImei() != null)
                vehicles.put(vehicle.getImei(), new DailyMovementConsumptionReportProjectionData(vehicle.getRegistration(),vehicle.getManufacturer(), vehicle.getModel(),  vehicle.getEngineSize()));
        }
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(vehicles),
                new ParameterizedTypeReference<List<DailyMovementConsumptionReport>>() {
                }
        );

    }
}
