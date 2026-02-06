package rs.oris.back.domain.reports.route;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Gps {
    private long timestamp;
    private double priority;
    private double lat;
    private double lng;
    private double ang;
    private double satellites;
    private double speed;
    private boolean ignition;
    private boolean sleep_mode;


    @Override
    public String toString() {
        return "Gps{" +
                "timestamp=" + timestamp +
                ", priority=" + priority +
                ", lat=" + lat +
                ", lng=" + lng +
                ", ang=" + ang +
                ", satellites=" + satellites +
                ", speed=" + speed +
                '}';
    }
}
