package rs.oris.back.domain.dto.report.monthly;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MonthlyFuelConsumptionReportDTO {

    private String registration;
    private String model;
    //Broj registracije
    private String imei;

    //Pocetno vreme kada ima ignition false tad se sece vremenski interval
    private LocalDateTime startTime;

    //Krajnje vreme
    private LocalDateTime endTime;

    //ukupno vreme
    private Long totalTime;

    //Predjen put(Euclid distance od pocetne tacke A(lat, lng) do krajnje tacke B(lat,lng)
    private Double distanceTraveled;

    //Ukupno vreme voznje racunam sve dok je movement !=0
    private Long  drivingTime;

    //Vreme mirovanja(Vozilo na kontaktu ,ali stoji il ise krece brzinom manjom od 3hm/h)
    //gledam movement da bude 0 ili speed da bude <=3
    private Long idleTime;


    //Ukupno potroseno(l)
    //LVC Fuel Consumed(L) ovo je sa koliko L je pocelo da vozi
    //mi kada izracunamo kada je kraj destiancije (io je null) onda vidimo prethodno koliko je imalo goriva
    private Double fuelSpent;

    //Ukupna prosecna potrosnja(l/100km)
    private Double averageFuelSpentPer100Km;

    //Ukupna prosecna potrosnja(l/1h)
    private Double getAverageFuelSpentPer1h;





}
