package rs.oris.back.domain.reports.driver_relation_fuel;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.export.annotations.PdfTableElement;
import rs.oris.back.export.annotations.Round;
import rs.oris.back.export.annotations.TimestampFormat;
import rs.oris.back.export.annotations.XlsTableElement;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DriverRelationsFuelReport {

    //Imei
    private String imei;

    //Broj Registracije
    @PdfTableElement(title = "Registracija")
    @XlsTableElement(title = "Registracija")
    private String registration;

    //Proizvođač/Model
    @PdfTableElement(title = "Proizvodjac/Model")
    @XlsTableElement(title = "Proizvodjac/Model")
    private String model;

    //Vreme početka
    @PdfTableElement(title = "Vreme početka")
    @XlsTableElement(title = "Vreme početka")
    private LocalDateTime startTime;

    //Početna lokacija
    @PdfTableElement(title = "Početna lokacija")
    @XlsTableElement(title = "Početna lokacija")
    private String startLocation;

    //Vreme kraja
    @PdfTableElement(title = "Vreme kraja")
    @XlsTableElement(title = "Vreme kraja")
    private LocalDateTime endTime;

    // Krajnja lokacija
    @PdfTableElement(title = "Krajnja lokacija")
    @XlsTableElement(title = "Krajnja lokacija")
    private String endLocation;

    //Pređeni put
    @PdfTableElement(title = "Pređeni put")
    @XlsTableElement(title = "Pređeni put")
    @Round(roundTo = 2)
    private Double traveledDistance;

    //Ukupno vreme
    @PdfTableElement(title = "Ukupno vreme")
    @XlsTableElement(title = "Ukupno vreme")
    @TimestampFormat
    private Long totalTime;

    //Vreme vožnje
    @PdfTableElement(title = "Vreme vožnje")
    @XlsTableElement(title = "Vreme vožnje")
    @TimestampFormat
    private Long movementTime;

    //Vreme mirovanja (vozilo na kontaktu ali stoji ili se kreće brzinom manjom od 3 km/h),
    @PdfTableElement(title = "Vreme mirovanja")
    @XlsTableElement(title = "Vreme mirovanja")
    @TimestampFormat
    private Long idleTime;

    //Prosečna brzina
    @PdfTableElement(title = "Prosečna brzina")
    @XlsTableElement(title = "Prosečna brzina")
    @Round(roundTo=2)
    private Double avgSpeed;

    //Maksimalna brzina
    @PdfTableElement(title = "Maksimalna brzina")
    @XlsTableElement(title = "Maksimalna brzina")
    @Round(roundTo=2)
    private Double maxSpeed;

    //Početak (l)Gorivo na pocetku
    @PdfTableElement(title = "Gorivo na pocetku")
    @XlsTableElement(title = "Gorivo na pocetku")
    @Round(roundTo=2)
    private Double fuelStart;

    //Kraj (l)
    @PdfTableElement(title = "Gorivo na kraju")
    @XlsTableElement(title = "Gorivo na kraju")
    @Round(roundTo=2)
    private Double fuelEnd;

    // Razlika (l)
    @PdfTableElement(title = "Razlika u gorivu")
    @XlsTableElement(title = "Razlika u gorivu")
    @Round(roundTo=2)
    private Double fuelDifference;

    //Potrošeno (l)
    @PdfTableElement(title = "Potrošeno goriva")
    @XlsTableElement(title = "Potrošeno goriva")
    @Round(roundTo=2)
    private Double fuelConsumed;

    //Potrošnja (l/100km)
    @PdfTableElement(title = "Potrošnja goriva na 100km")
    @XlsTableElement(title = "Potrošnja goriva na 100km")
    @Round(roundTo=2)
    private Double fuelConsumedInKm;

    //Potrošnja (l/1h)
    @PdfTableElement(title = "Potrošnja goriva na sat")
    @XlsTableElement(title = "Potrošnja goriva na sat")
    @Round(roundTo=2)
    private Double fuelConsumedInH;
}
