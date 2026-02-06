package rs.oris.back.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "driver_vehicle")
public class DriverVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private LocalDateTime fromTime;
    private LocalDateTime toTime;
}
