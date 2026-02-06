package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="notification_vehicle")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationVehicle {
    @Id
    @GeneratedValue
    @Column(name = "notification_vehicle_id")
    private int notificationVehicleId;
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notification notification;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private boolean mail;
    private boolean sms;
    private Boolean push=false;
    @ManyToOne
    @JoinColumn(name = "geozone_id")
    private Geozone geozone;
    int speed;
    int fuelMargin;
    private Double engine;

    private Integer firmId;


    private Integer serviceIntervalLength;
    private Integer serviceIntervalNotif;
    //Koristimo ovo da kad se rastartuje mongo da on nastavi da koristi vehicleMileage koji se izbrisao prilikom restarta
    private Double vehicleMileage=0.0;
    private Double gpsMileage=0.0;
    private Double vehicleMileageReminder=0.0;
    private Double startPointLat;
    private Double startPointLng;
    private Boolean isMileage=false;
    private Long startTimestamp;
    private LocalDate vehicleRegistration;
    private Integer notifyDaysBeforeVehicleRegistration;
    private Integer emptyingFuelMargine;
    private Integer serviceIntervalLengthBoard;
    private Integer serviceIntervalNotifBoard;

    private Integer serviceIntervalLengthGps;
    private Integer serviceIntervalNotifGps;
    private Double kilometers;

    private Boolean fuelDiffFound;






}
