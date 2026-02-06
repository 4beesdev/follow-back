package rs.oris.back.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SingleNotification {
    private int vehicleId;
    private String imei;
    private String registration;
    private String manufacturer;
    private String model;
    private Timestamp timestamp;
    private int notificationId;
    private String notificaitonName;
    private String geozoneName;
    private int userId;
    private boolean sms;
    private boolean email;
    private boolean push;
    private String greenType;
    private int speed;
    private String fuelType;
    private Integer firmId;
    private double vehicleMileage;
    private Long startTimestamp;
    private int serviceIntervalLength;
    private int serviceIntervalNotif;
    private int nmid;
    private boolean isMileage;
    private Double gpsMileage=0.0;
    private Double vehicleMileageReminder=0.0;
    private Double startPointLat;
    private Double startPointLng;
    private LocalDate vehicleRegistration;
    private Integer notifyDaysBeforeVehicleRegistration;
    private Integer serviceIntervalLengthBoard;
    private Integer serviceIntervalNotifBoard;
    private int serviceIntervalLengthGps;
    private int serviceIntervalNotifGps;
    private Double kilometers;
}
