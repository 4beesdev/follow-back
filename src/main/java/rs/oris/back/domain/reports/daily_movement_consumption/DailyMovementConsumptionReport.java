package rs.oris.back.domain.reports.daily_movement_consumption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.export.annotations.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyMovementConsumptionReport {


    //Broj Registracije
    @PdfTableElement(title = "Registracija")
    @XlsTableElement(title = "Registracija")
    private String registration;



    //Datum
    @PdfTableElement(title = "Datum")
    @XlsTableElement(title = "Datum")
    private String datum;



    //Vreme početka,
    @PdfTableElement(title = "Vreme početka")
    @XlsTableElement(title = "Vreme početka")
    private String startTime;



    //Vreme kraja,
    @PdfTableElement(title = "Vreme kraja")
    @XlsTableElement(title = "Vreme kraja")
    private String endTime;


    //Radno vreme
    @PdfTableElement(title = "Radno vreme")
    @XlsTableElement(title = "Radno vreme ")
    @TimestampFormat
    //Razlika izmedju startTime i endTime
    private Long workingTime;

    //Vreme voznje
    @PdfTableElement(title = "Vreme vožnje")
    @XlsTableElement(title = "Vreme vožnje")
    @TimestampFormat
    private Long drivingTime;

    //Vreme miroavanja
    @PdfTableElement(title = "Vreme mirovanja")
    @XlsTableElement(title = "Vreme mirovanja")
    @TimestampFormat
    private Long idleTime;

    //Vreme stajanja
    @PdfTableElement(title = "Vreme stajanja")
    @XlsTableElement(title = "Vreme stajanja")
    @TimestampFormat
    private Long standingTime;

    //Kilometraza
    @PdfTableElement(title = "Pređeni put")
    @XlsTableElement(title = "Pređeni put")
    @Round(roundTo = 2)
    private Double mileage;

    //Potroseno gorivo u voznji
    @PdfTableElement(title = "Potrošeno gorivo u vožnji")
    @XlsTableElement(title = "Potrošeno gorivo u vožnji")
    @Round(roundTo = 2)
    private Double drivingFuelConsumption;

    //Potroseno gorivo u mirovanju
    @PdfTableElement(title = "Potrošeno gorivo u mirovanju")
    @XlsTableElement(title = "Potrošeno gorivo u mirovanju")
    @Round(roundTo = 2)
    private Double idleFuelConsumption;

    //Potroseno gorivo ukupno
    @PdfTableElement(title = "Ukupno potrošeno gorivo")
    @XlsTableElement(title = "Ukupno potrošeno gorivo")
    @Round(roundTo = 2)
    private Double totalFuelConsumption;

}
