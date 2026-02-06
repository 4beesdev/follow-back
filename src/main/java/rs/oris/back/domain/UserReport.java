package rs.oris.back.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="user_report")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserReport {

    @Id
    @GeneratedValue
    @Column(name = "user_report_id")
    private int userReportId;
    private String[] imei; //
    private int[] geozoneIds; //
    private int[] routeIds;
    private String routeImei;//
    private int report;
    private int xlsxpdf=1;
    private Date dateFrom; //
    private int reportHour;
    private int period; //
    private int periodType;
    private int maxSpeed; //
    private int working; //
    private int hfrom; //
    private int hto; //
    private int mfrom; //
    private int mto;//
    private int hfromsa; //
    private int htosa; //
    private int mfromsa; //
    private int mtosa; //
    private int hfromsu; //
    private int htosu; //
    private int mfromsu; //
    private int mtosu;//

    private Integer rpm=0;
 
    private Integer fuelMargin=0;
    private Integer emptyingMargin=0;

    private String email;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    private int sendingHour;

    //negde se ovo koristi u izvestajima na vise mesta
    private Double minDistance; //
    private Integer min;
    //Izvestaj o stajanju vozila
    private Boolean isIdle;//
    private Integer minIdle;//
    private Boolean isSentToday=false;
    private Integer driverId;


}
