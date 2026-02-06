package rs.oris.back.domain.reports.sensor_activation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.LatLng;
import rs.oris.back.export.annotations.BooleanReplacer;
import rs.oris.back.export.annotations.PdfTableElement;
import rs.oris.back.export.annotations.TimestampFormat;
import rs.oris.back.export.annotations.XlsTableElement;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SensorActivationReport {

    //Broj registracije
    @PdfTableElement(title = "Registracija")
    @XlsTableElement(title = "Registracija")
    private String registration;

    private String imei;

    //Proizvodjac/Model
    @PdfTableElement(title = "Proizvođač/Model")
    @XlsTableElement(title = "Proizvođač/Model")
    private String model;

    //Pocetno vreme kada ima ignition false tad se sece vremenski interval
    @PdfTableElement(title = "Vreme početka")
    @XlsTableElement(title = "Vreme početka")
    private LocalDateTime startTime;

    //Krajnje vreme
    @PdfTableElement(title = "Vreme kraja")
    @XlsTableElement(title = "Vreme kraja")
    private LocalDateTime endTime;

    //ukupno vreme
    @PdfTableElement(title = "Ukupno vreme")
    @XlsTableElement(title = "Ukupno vreme")
    @TimestampFormat
    private Long totalTime;

    //Gleda se ignition
    @PdfTableElement(title = "Kontakt Brava")
    @XlsTableElement(title = "Kontakt Brava")
    @BooleanReplacer(trueValue = "Aktivirana", falseValue = "Deaktivirana")
    private Boolean contactLock;

    //Ovo je adresa gde je senzor poceo da radi
    @PdfTableElement(title = "Adresa")
    @XlsTableElement(title = "Adresa")
    private String address;


}
