package rs.oris.back.domain.reports.monthly_fuel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.export.Exporable;
import rs.oris.back.export.annotations.PdfTableElement;
import rs.oris.back.export.annotations.Round;
import rs.oris.back.export.annotations.TimestampFormat;
import rs.oris.back.export.annotations.XlsTableElement;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MonthlyFuelConsumptionReport implements Exporable {

    //Broj registracije
    @PdfTableElement(title = "Registracija")
    @XlsTableElement(title = "Registracija")
    private String registration;

    //Proizvodjac/Model
    @PdfTableElement(title = "Proizvodjac/Model")
    @XlsTableElement(title = "Proizvodjac/Model")
    private String model;

    //Pocetno vreme kada ima ignition false tad se sece vremenski interval

    private LocalDateTime startTime;

    //Krajnje vreme

    private LocalDateTime endTime;
    @PdfTableElement(title = "Vreme pocetka")
    @XlsTableElement(title = "Vreme pocetka")
    private LocalDateTime displayStartTime;

    @PdfTableElement(title = "Vreme kraja")
    @XlsTableElement(title = "Vreme kraja")
    private LocalDateTime displayEndTime;

    //ukupno vreme
    @PdfTableElement(title = "Ukupno vreme")
    @XlsTableElement(title = "Ukupno vreme")
    @TimestampFormat
    private Long totalTime;

    //Predjen put(Euclid distance od pocetne tacke A(lat, lng) do krajnje tacke B(lat,lng)
    @PdfTableElement(title = "Predjeni put")
    @XlsTableElement(title = "Predjeni put")
    @Round(roundTo=2)
    private Double distanceTraveled;

    //Ukupno vreme voznje racunam sve dok je movement !=0
    @PdfTableElement(title = "Vreme putavanja")
    @XlsTableElement(title = "Vreme putavanja")
    @TimestampFormat
    private Long  drivingTime;

    //Vreme mirovanja(Vozilo na kontaktu ,ali stoji il ise krece brzinom manjom od 3hm/h)
    //gledam movement da bude 0 ili speed da bude <=3
    @PdfTableElement(title = "Vreme mirovanja")
    @XlsTableElement(title = "Vreme mirovanja")
    @TimestampFormat
    private Long idleTime;



    //Ukupno potroseno(l)
    //LVC Fuel Consumed(L) ovo je sa koliko L je pocelo da vozi
    //mi kada izracunamo kada je kraj destiancije (io je null) onda vidimo prethodno koliko je imalo goriva
    @PdfTableElement(title = "Potroseno gorivo")
    @XlsTableElement(title = "Potroseno gorivo")
    @Round(roundTo=2)
    private Double fuelSpent;

    //Ukupna prosecna potrosnja(l/100km)
    @PdfTableElement(title = "Prosecna potrosnja goriva na 100km")
    @XlsTableElement(title = "Prosecna potrosnja goriva na 100km")
    @Round(roundTo=2)
    private Double averageFuelSpentPer100Km;

    //Ukupna prosecna potrosnja(l/1h)
    @PdfTableElement(title = "Prosecna potrosnja goriva na sat")
    @XlsTableElement(title = "Prosecna potrosnja goriva na sat")
    @Round(roundTo=2)
    private Double getAverageFuelSpentPer1h;



}
