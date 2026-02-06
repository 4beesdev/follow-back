package rs.oris.back.export.pdf.impl;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import rs.oris.back.domain.reports.driver_relation.DriverVehicleRelationReportData;
import rs.oris.back.domain.reports.driver_relation_fuel.DriverRelationsFuelReport;
import rs.oris.back.export.annotations.PdfTableElement;
import rs.oris.back.export.pdf.PdfExporter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DriverVehicleRelationsReportPdfExporter extends PdfExporter {

    private LocalDateTime to;
    private LocalDateTime from;

    public DriverVehicleRelationsReportPdfExporter( LocalDateTime from,LocalDateTime to) {
        super(to, from);
        this.to=to;
        this.from=from;
    }



    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass,String title) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //Konvertuj listu u mapu
        List<DriverVehicleRelationReportData> contentType= (List<DriverVehicleRelationReportData>) content;
        Map<String, List<DriverVehicleRelationReportData>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));


        try {
            //Kreiraj dokument
            Document document = new Document(PageSize.A4.rotate());
            //Postavi margine
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


            //Filtrira polja koja imaju anotaciju PdfTableElement i broji ih
            long count = Arrays.stream(myclass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(PdfTableElement.class)).count();
            //Kreiraj fontove
            Font cellTitleFont = new Font(Font.TIMES_ROMAN, 8, Font.BOLD, Color.WHITE);
            Font cellContentSumFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, Color.BLACK);



            //Dodaj podatke u tabelu
            for (Map.Entry<String, List<DriverVehicleRelationReportData>> entry : collected.entrySet()) {
                PdfPTable table = create(myclass, (int) count, cellTitleFont, WebColors.getRGBColor("#1867c0"));
                Long sumTotalTime=0L;
                Long totalDrivingTime=0L;
                Long totalIdleTime=0L;
                Double totalDistanceTraveled=0.0;
                Integer speedAvgCounter=1;

                Double totalAvgSpeed=0.0;
                Double totalMaxSpeed=0.0;

                for (DriverVehicleRelationReportData driverRelationsFuelReport : entry.getValue()) {
                    addRowToTable(table,driverRelationsFuelReport);
                    //Racunica za sum polja
                    sumTotalTime+=driverRelationsFuelReport.getTotalTime();
                    totalDrivingTime+=driverRelationsFuelReport.getMovementTime();
                    totalIdleTime+=driverRelationsFuelReport.getIdleTime();
                    totalDistanceTraveled+=driverRelationsFuelReport.getTraveledDistance();

                    //Racunica za max speed
                    totalMaxSpeed=driverRelationsFuelReport.getMaxSpeed()>totalMaxSpeed? driverRelationsFuelReport.getMaxSpeed() : totalMaxSpeed;


                    totalAvgSpeed=(totalAvgSpeed+driverRelationsFuelReport.getAvgSpeed())/speedAvgCounter;
                    speedAvgCounter++;
                }
                document.add(new Paragraph("\n")); // Add one empty line
                document.add(new Paragraph("\n")); // Add one empty line
                document.add(new Paragraph("\n")); // Add one empty line

                document.add(table);
                //Kreiraj tebelu
                PdfPTable tableSum = createSumColums(count, cellContentSumFont,sumTotalTime,totalDrivingTime,totalIdleTime,totalDistanceTraveled,totalAvgSpeed,totalMaxSpeed);

                document.add(tableSum);
            }

            //Add space
            document.add(new Paragraph("\n")); // Add one empty line


            document.close();
            writer.close();


            System.out.println("PDF with table created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    //Dodaj red u tabelu
    private PdfPTable createSumColums(long count, Font cellContentSumFont, Long sumTotalTime, Long totalDrivingTime, Long totalIdleTime, Double totalDistanceTraveled, Double totalAvgSpeed, Double totalMaxSpeed) {

        PdfPTable tableSum = new PdfPTable((int) count);

        PdfPCell ukupno = new PdfPCell(new Phrase("Ukupno", cellContentSumFont));
        tableSum.addCell(ukupno);


        //Dodaj prazno polje jer treba da popuni prazan prostor
        //PRAZNo
        addEmptyCellToTable(tableSum);

        //PRAZNo
        addEmptyCellToTable(tableSum);

        //PRAZNo
        addEmptyCellToTable(tableSum);
        //PRAZNo
        addEmptyCellToTable(tableSum);
        //PRAZNo
        addEmptyCellToTable(tableSum);
        //PRAZNo
        addEmptyCellToTable(tableSum);
        //PRAZNo
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




        return tableSum;
    }


}
