package rs.oris.back.export.pdf.impl;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import rs.oris.back.domain.reports.monthly_fuel.MonthlyFuelConsumptionReport;
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

public class MonthlyReportPdfExporter extends PdfExporter {
    private LocalDateTime to;
    private LocalDateTime from;

    public MonthlyReportPdfExporter( LocalDateTime from,LocalDateTime to) {
        super(to, from);
        this.to=to;
       this.from=from;
    }



    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass,String title) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //Konvertuj listu u mapu
        List<MonthlyFuelConsumptionReport> contentType= (List<MonthlyFuelConsumptionReport>) content;
        Map<String, List<MonthlyFuelConsumptionReport>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));


        try {
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


            long count = Arrays.stream(myclass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(PdfTableElement.class)).count();
            Font cellTitleFont = new Font(Font.TIMES_ROMAN, 8, Font.BOLD, Color.WHITE);
            Font cellContentSumFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD, Color.BLACK);


            Long sumTotalTime=0L;
            Long totalDrivingTime=0L;
            Long totalIdleTime=0L;
            Long totalRpmInTime=0L;
            Integer counterFuelPer100Km=1;
            Double totalDistanceTraveled=0.0;
            Integer counterFuelPer1h=1;
            Double totalFuelConsumption=0.0;
            Double totalFuelPer1HConsumption=0.0;
            Double totalFuelPer100KMConsumption=0.0;
            PdfPTable table = create(myclass, (int) count, cellTitleFont,WebColors.getRGBColor("#1867c0"));

            for (Map.Entry<String, List<MonthlyFuelConsumptionReport>> entry : collected.entrySet()) {

                for (MonthlyFuelConsumptionReport monthlyFuelConsumptionReport : entry.getValue()) {
                    addRowToTable(table,monthlyFuelConsumptionReport);
                    //Suma totalnih vrednosti
                    sumTotalTime+=monthlyFuelConsumptionReport.getTotalTime();
                    totalDrivingTime+=monthlyFuelConsumptionReport.getDrivingTime();
                    totalIdleTime+=monthlyFuelConsumptionReport.getIdleTime();
                    totalFuelConsumption+=monthlyFuelConsumptionReport.getFuelSpent();
                    totalDistanceTraveled+=monthlyFuelConsumptionReport.getDistanceTraveled();
                    if(monthlyFuelConsumptionReport.getGetAverageFuelSpentPer1h()!=0){
                        totalFuelPer1HConsumption=(totalFuelPer1HConsumption+ monthlyFuelConsumptionReport.getGetAverageFuelSpentPer1h())/counterFuelPer1h;
                        counterFuelPer1h++;

                    }

                }




            }

            //Dodaj prazno polje
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));

            document.add(table);

            document.add(new Paragraph("\n"));
            PdfPTable tableSum = createSumColums(count, cellContentSumFont,sumTotalTime,totalDrivingTime,totalIdleTime,totalDistanceTraveled,totalFuelConsumption,totalFuelPer1HConsumption,totalFuelPer100KMConsumption);

            document.add(tableSum);

            //Dodaj prazno polje
            document.add(new Paragraph("\n"));

            document.close();

            writer.close();

            System.out.println("PDF with table created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    private  PdfPTable createSumColums(long count, Font cellTitleFont, Long sumTotalTime, Long totalDrivingTime, Long totalIdleTime, Double totalDistanceTraveled, Double totalFuelConsumption, Double totalFuelPer1HConsumption, Double totalFuelPer100KMConsumption) {
        PdfPTable tableSum = new PdfPTable((int) count);

        PdfPCell ukupno = new PdfPCell(new Phrase("Ukupno", cellTitleFont));
        tableSum.addCell(ukupno);


        //PRAZNO
        addEmptyCellToTable(tableSum);


        //PRAZNO
        addEmptyCellToTable(tableSum);

        //PRAZNO
        addEmptyCellToTable(tableSum);

        //Sum total time
        addCellToTable(formatTime(sumTotalTime), cellTitleFont, tableSum);


        //Sum driving distance
        addCellToTable(roundDecimalNumber(totalDistanceTraveled,2), cellTitleFont, tableSum);


        //Sum driving time
        addCellToTable(formatTime(totalDrivingTime), cellTitleFont, tableSum);

        //Sum idle time
        addCellToTable(formatTime(totalIdleTime), cellTitleFont, tableSum);

        //Sum fuel consumption
        addCellToTable(roundDecimalNumber(totalFuelConsumption,2), cellTitleFont, tableSum);

        //Sum fuel per 100km consumption
        if(totalDistanceTraveled!=0)
            addCellToTable(roundDecimalNumber((totalFuelConsumption/totalDistanceTraveled)*100,2), cellTitleFont, tableSum);
        else addCellToTable("0", cellTitleFont, tableSum);

        //Sum fuel per 1h consumption
        addCellToTable(roundDecimalNumber(totalFuelPer1HConsumption,2), cellTitleFont, tableSum);



        return tableSum;
    }

}
