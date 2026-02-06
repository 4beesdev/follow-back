package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOUserReport {
    private int userReportId;
    private String[] imei;
    private String routeImei;
    private int report;
    private int xlsxpdf = 1;
    private Date dateFrom;
    private int reportHour;
    private int period;
    private int periodType;
    private int maxSpeed;
    private int working;
    private int hfrom;
    private int hto;
    private int mfrom;
    private int mto;
    private int hfromsa;
    private int htosa;
    private int mfromsa;
    private int mtosa;
    private int hfromsu;
    private int htosu;
    private int mfromsu;
    private int mtosu;
    private String email;
    private User user;
    private int sendingHour;
    private List<Vehicle> vehicleList;
    private List<Geozone> geozoneIds;
    private List<Route> routeIds;
    //Izvestaj o relacijama vozila
    private Double minDistance;
    private Integer min;
    //Izvestaj o stajanju vozila
    private Boolean isIdle;
    private Integer minIdle;
    private Integer fuelMargin ;
    private Integer emptyingMargin;
    private Integer rpm;

    public DTOUserReport(UserReport ur) {
        vehicleList = new ArrayList<>();
        geozoneIds = new ArrayList<>();
        routeIds = new ArrayList<>();
        this.userReportId = ur.getUserReportId();
        this.imei = ur.getImei();
        this.routeImei = ur.getRouteImei();
        this.report = ur.getReport();
        this.xlsxpdf = ur.getXlsxpdf();
        this.dateFrom = ur.getDateFrom();
        this.reportHour = ur.getReportHour();
        this.period = ur.getPeriod();
        this.periodType = ur.getPeriodType();
        this.maxSpeed = ur.getMaxSpeed();
        this.working = ur.getWorking();
        this.hfrom = ur.getHfrom();
        this.hto = ur.getHto();
        this.mfrom = ur.getMfrom();
        this.mto = ur.getMto();
        this.hfromsa = ur.getHfromsa();
        this.htosa = ur.getHtosa();
        this.mfromsa = ur.getMfromsa();
        this.mtosa = ur.getMtosa();
        this.hfromsu = ur.getHfromsu();
        this.htosu = ur.getHtosu();
        this.mfromsu = ur.getMfromsu();
        this.mtosu = ur.getMtosu();
        this.email = ur.getEmail();
        this.user = ur.getUser();
        this.sendingHour = ur.getSendingHour();
        this.minIdle=ur.getMinIdle();
        this.isIdle=ur.getIsIdle();
        this.minDistance=ur.getMinDistance();
        this.min=ur.getMin();
        this.fuelMargin=ur.getFuelMargin();
        this.emptyingMargin=ur.getEmptyingMargin();
        this.rpm=ur.getRpm();
    }


}
