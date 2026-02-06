package rs.oris.back.domain.reports.driver_relation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.export.annotations.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverVehicleRelationReportData {

    private String imei;

    //Broj Registracije
    @PdfTableElement(title = "Registracija")
    @XlsTableElement(title = "Registracija")
    private String registration;

    //Proizvođač/Model,
    @PdfTableElement(title = "Proizvodjac/Model")
    @XlsTableElement(title = "Proizvodjac/Model")
    private String model;

    //Ime,
    private List<String> rfids;

    @PdfTableElement(title = "Vozac")
    @XlsTableElement(title = "Vozac")
    @ListNames
    private List<String> drivernames;


    //Vreme početka,
    @PdfTableElement(title = "Vreme početka")
    @XlsTableElement(title = "Vreme početka")
    private LocalDateTime startTime;

    //Početna lokacija,
    @PdfTableElement(title = "Početna lokacija")
    @XlsTableElement(title = "Početna lokacija")
    private String startLocation;

    //Vreme kraja,
    @PdfTableElement(title = "Vreme kraja")
    @XlsTableElement(title = "Vreme kraja")
    private LocalDateTime endTime;

    //Krajnja lokacija,
    @PdfTableElement(title = "Krajnja lokacija")
    @XlsTableElement(title = "Krajnja lokacija")
    private String endLocation;

    //Pređeni put,
    @PdfTableElement(title = "Pređeni put")
    @XlsTableElement(title = "Pređeni put")
    @Round(roundTo=2)
    private Double traveledDistance;

    //Ukupno vreme,
    @PdfTableElement(title = "Ukupno vreme")
    @XlsTableElement(title = "Ukupno vreme")
    @TimestampFormat
    private Long totalTime;

    //Vreme vožnje,
    @PdfTableElement(title = "Vreme vožnje")
    @XlsTableElement(title = "Vreme vožnje")
    @TimestampFormat
    private Long movementTime;

    //Vreme mirovanja (vozilo na kontaktu ali stoji ili se kreće brzinom manjom od 3 km/h),
    @PdfTableElement(title = "Vreme mirovanja")
    @XlsTableElement(title = "Vreme mirovanja")
    @TimestampFormat
    private Long idleTime;

    //Prosečna brzina,
    @PdfTableElement(title = "Prosečna brzina")
    @XlsTableElement(title = "Prosečna brzina")
    @Round(roundTo=2)
    private Double avgSpeed;

    //Maksimalna brzina
    @PdfTableElement(title = "Maksimalna brzina")
    @XlsTableElement(title = "Maksimalna brzina")
    @Round(roundTo=2)
    private Double maxSpeed;

}
