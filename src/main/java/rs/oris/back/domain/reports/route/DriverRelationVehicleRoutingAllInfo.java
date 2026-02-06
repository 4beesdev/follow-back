package rs.oris.back.domain.reports.route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverRelationVehicleRoutingAllInfo {
    private String imei;
    private String registration;
    private Gps gps;
    private HashMap<String, Double> io;
    private String rfid;

}
