package rs.oris.back.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import rs.oris.back.domain.Geozone;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotifBody {
    private Geozone geozone;
    private int speed;
    private int fuelMargin;
    private boolean email;
    private boolean sms;
    private boolean push;
    private int serviceIntervalLength;
    private int serviceIntervalNotif;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate vehicleRegistration;
    private int notifyDaysBeforeVehicleRegistration;
    private int emptyingFuelMargine;
    private int serviceIntervalLengthBoard;
    private int serviceIntervalNotifBoard;

    private int serviceIntervalLengthGps;
    private int serviceIntervalNotifGps;



}
