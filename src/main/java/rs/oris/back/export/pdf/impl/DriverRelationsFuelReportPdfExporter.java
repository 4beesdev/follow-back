package rs.oris.back.export.pdf.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import rs.oris.back.domain.reports.driver_relation_fuel.DriverRelationsFuelReport;
import rs.oris.back.domain.reports.effective_hours.EffectiveWorkingHoursReportData;
import rs.oris.back.export.Exporter;
import rs.oris.back.export.annotations.PdfTableElement;
import rs.oris.back.export.pdf.PdfExporter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DriverRelationsFuelReportPdfExporter extends PdfExporter {

    private LocalDateTime to;
    private LocalDateTime from;

    public DriverRelationsFuelReportPdfExporter(LocalDateTime to, LocalDateTime from) {
        super(to, from);
        this.to=to;
        this.from=from;
    }



    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass,String title) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //Convert list to map
        List<DriverRelationsFuelReport> contentType= (List<DriverRelationsFuelReport>) content;
        Map<String, List<DriverRelationsFuelReport>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));


        try {
            //Kreira dokument
            Document document = new Document(PageSize.A4.rotate());
            document.setMargins(20.0f,0.0f,50.0f,50.0f);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            document.open();
            //Dodaj naslov
            Paragraph titleP = new Paragraph(title);
            titleP.setAlignment(Element.ALIGN_CENTER);
            document.add(titleP);

            //Dodaj vreme izvestaja
            Paragraph time = new Paragraph("Od: "+from.format(DateTimeFormatter.ISO_DATE)+"     Do: "+to.format(DateTimeFormatter.ISO_DATE));
            time.setAlignment(Element.ALIGN_CENTER);
            document.add(time);


            //Filtriraj polja koja sadrze anotaciju PdfTableElement i broji ih
            long count = Arrays.stream(myclass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(PdfTableElement.class)).count();
            Font cellTitleFont = new Font(Font.TIMES_ROMAN, 8, Font.BOLD, Color.WHITE);
            Font cellContentSumFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, Color.BLACK);



            //Popuni podatke
            for (Map.Entry<String, List<DriverRelationsFuelReport>> entry : collected.entrySet()) {
                PdfPTable table = create(myclass, (int) count, cellTitleFont, WebColors.getRGBColor("#1867c0"));
                Long sumTotalTime=0L;
                Long totalDrivingTime=0L;
                Long totalIdleTime=0L;
                Double totalDistanceTraveled=0.0;
                Integer speedAvgCounter=1;
                Integer counterFuelPer1h=1;
                Integer counterFuelPer100Km=1;
                Double totalAvgSpeed=0.0;
                Double totalMaxSpeed=0.0;
                Double totalFuelConsumption=0.0;
                Double totalFuelPer1HConsumption=0.0;
                Double totalFuelPer100KMConsumption=0.0;
                for (DriverRelationsFuelReport driverRelationsFuelReport : entry.getValue()) {
                    addRowToTable(table,driverRelationsFuelReport);
                    //Izracunaj sume
                    sumTotalTime+=driverRelationsFuelReport.getTotalTime();
                    totalDrivingTime+=driverRelationsFuelReport.getMovementTime();
                    totalIdleTime+=driverRelationsFuelReport.getIdleTime();
                    totalDistanceTraveled+=driverRelationsFuelReport.getTraveledDistance();
                    totalMaxSpeed=driverRelationsFuelReport.getMaxSpeed()>totalMaxSpeed? driverRelationsFuelReport.getMaxSpeed() : totalMaxSpeed;
                    totalAvgSpeed=(totalAvgSpeed+driverRelationsFuelReport.getAvgSpeed())/speedAvgCounter;
                    speedAvgCounter++;

                    totalFuelConsumption+=driverRelationsFuelReport.getFuelConsumed();
                    if(driverRelationsFuelReport.getFuelConsumedInH()!=0){
                        totalFuelPer1HConsumption=(totalFuelPer1HConsumption+ driverRelationsFuelReport.getFuelConsumedInH())/counterFuelPer1h;
                        counterFuelPer1h++;
                    }

                }
                //Dodaj space
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));

                document.add(table);
                PdfPTable tableSum = createSumColums(count, cellContentSumFont,sumTotalTime,totalDrivingTime,totalIdleTime,totalDistanceTraveled,totalAvgSpeed,totalMaxSpeed,totalFuelConsumption,totalFuelPer1HConsumption,totalFuelPer100KMConsumption);

                document.add(tableSum);
            }

            //Dodaj space
            document.add(new Paragraph("\n"));



            document.close();

            writer.close();


            System.out.println("PDF with table created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    private PdfPTable createSumColums(long count, Font cellContentSumFont, Long sumTotalTime, Long totalDrivingTime, Long totalIdleTime, Double totalDistanceTraveled, Double totalAvgSpeed, Double totalMaxSpeed, Double totalFuelConsumption, Double totalFuelPer1HConsumption, Double totalFuelPer100KMConsumption) {

        PdfPTable tableSum = new PdfPTable((int) count);

        PdfPCell ukupno = new PdfPCell(new Phrase("Ukupno", cellContentSumFont));
        tableSum.addCell(ukupno);

        //Mora se dodati prazni prostor
        //PRAZNO
        addEmptyCellToTable(tableSum);

        //PRAZNO
        addEmptyCellToTable(tableSum);

        //PRAZNO
        addEmptyCellToTable(tableSum);

        //PRAZNO
        addEmptyCellToTable(tableSum);

        //PRAZNO
        addEmptyCellToTable(tableSum);


        //Sum driving distance
        addCellToTable(roundDecimalNumber(totalDistanceTraveled,2), cellContentSumFont, tableSum);

        //Sum total time
        addCellToTable(formatTime(sumTotalTime), cellContentSumFont, tableSum);

        //Sum driving time
        addCellToTable(formatTime(totalDrivingTime), cellContentSumFont, tableSum);

        //Sum idle time
        addCellToTable(formatTime(totalIdleTime), cellContentSumFont, tableSum);

        //Sum  avg speed
        addCellToTable(roundDecimalNumber(totalAvgSpeed,2), cellContentSumFont, tableSum);

        //Sum max speed
        addCellToTable(roundDecimalNumber(totalMaxSpeed,2), cellContentSumFont, tableSum);

        //PRAZNO
        addEmptyCellToTable(tableSum);

        //PRAZNO
        addEmptyCellToTable(tableSum);

        //PRAZNO
        addEmptyCellToTable(tableSum);

        //Sum fuel consumption
        addCellToTable(roundDecimalNumber(totalFuelConsumption,2), cellContentSumFont, tableSum);

        //Sum fuel per 100km consumption
        if(totalDistanceTraveled!=0)
            addCellToTable(roundDecimalNumber((totalFuelConsumption/totalDistanceTraveled)*100,2), cellContentSumFont, tableSum);
        else  addCellToTable("0", cellContentSumFont, tableSum);

        //Sum fuel per 1h consumption
        addCellToTable(roundDecimalNumber(totalFuelPer1HConsumption,2), cellContentSumFont, tableSum);



        return tableSum;
    }


}
