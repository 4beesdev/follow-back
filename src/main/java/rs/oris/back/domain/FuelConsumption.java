package rs.oris.back.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "fuel_consumption")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuelConsumption {

    @Id
    @GeneratedValue
    @Column(name = "fuel_consumption_id")
    private int fuelConsumptionId;
    private double value;
    private double amount;
    private String fuel;

    private double km;
    private String account;
    private Date date;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fuel_station_id")
    private FuelStation fuelStation;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    private FuelCompany fuelCompany;

}
