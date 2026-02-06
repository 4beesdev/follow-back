package rs.oris.back.domain.reports.fms_fuel_company_consumption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.export.annotations.PdfTableElement;
import rs.oris.back.export.annotations.Round;
import rs.oris.back.export.annotations.XlsTableElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FmsFuelCompanyFuelConsumption {

    @PdfTableElement(title = "Registarski broj vozila")
    @XlsTableElement(title = "Registarski broj vozila")
    private String vehicleRegistration;

    @PdfTableElement(title = "Proizvođač")
    @XlsTableElement(title = "Proizvođač")
    private String manufacturer;

    @PdfTableElement(title = "Model")
    @XlsTableElement(title = "Model")
    private String model;

    @PdfTableElement(title = "Broj pređenih kilometara")
    @XlsTableElement(title = "Broj pređenih kilometara")
    @Round(roundTo=2)
    private Double mileage;

    @PdfTableElement(title = "Sipano goriva")
    @XlsTableElement(title = "Sipano goriva")
    @Round(roundTo=2)
    private Double fuelFilled;

    @PdfTableElement(title = "Prosečna potrošnja goriva")
    @XlsTableElement(title = "Prosečna potrošnja goriva")
    @Round(roundTo=2)
    private Double averageFuelConsumption;

    @PdfTableElement(title = "Broj prekoračenja definisane brzine")
    @XlsTableElement(title = "Broj prekoračenja definisane brzine")
    private Long numberOfSpeedLimitViolations;

    @PdfTableElement(title = "Kritičan broj obrtaja motora")
    @XlsTableElement(title = "Kritičan broj obrtaja motora")
    @Round(roundTo = 0)
    private Double criticalEngineRevolutions;


}
