package rs.oris.back.domain.reports.effective_hours;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.export.annotations.PdfTableElement;
import rs.oris.back.export.annotations.Round;
import rs.oris.back.export.annotations.TimestampFormat;
import rs.oris.back.export.annotations.XlsTableElement;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EffectiveWorkingHoursReportData {

    //Broj registracije
    @PdfTableElement(title = "Registracija")
    @XlsTableElement(title = "Registracija")
    private String registration;

    //Proizvodjac/Model
    @PdfTableElement(title = "Proizvodjac/Model")
    @XlsTableElement(title = "Proizvodjac/Model")
    private String model;

    //Pocetno vreme
    @PdfTableElement(title = "Vreme pocetka")
    @XlsTableElement(title = "Vreme pocetka")
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

    //Ukupno vreme voznje
    @PdfTableElement(title = "Vreme kretanja")
    @XlsTableElement(title = "Vreme kretanja")
    @TimestampFormat
    private Long  drivingTime;

    //Vreme mirovanja
    @PdfTableElement(title = "Vreme mirovanja")
    @XlsTableElement(title = "Vreme mirovanja")
    @TimestampFormat
    private Long idleTime;

    //Rpm zadovoljen,
    @PdfTableElement(title = "Radni mod")
    @XlsTableElement(title = "Radni mod")
    @TimestampFormat
    private Long rpmInTime;

    //Rpm nije zadovoljen,
    @PdfTableElement(title = "Na leru")
    @XlsTableElement(title = "Na leru")
    @TimestampFormat
    private Long rpmOutTime;

    //Ukupno potroseno(l)
    @PdfTableElement(title = "Potroseno gorivo")
    @XlsTableElement(title = "Potroseno gorivo")
    @Round(roundTo=2)
    private Double fuelSpent;

    //Prosečna potrošnja (l/1h).
    @PdfTableElement(title = "Prosecna potrosnja goriva na sat")
    @XlsTableElement(title = "Prosecna potrosnja goriva na sat")
    @Round(roundTo=2)
    private Double getAverageFuelSpentPer1h;
}
