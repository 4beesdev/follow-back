package rs.oris.back.export.pdf.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import rs.oris.back.domain.reports.daily_movement_consumption.DailyMovementConsumptionReport;
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

public class DailyMovementConsumptionPdfExporter extends PdfExporter {
    private LocalDateTime from;
    private LocalDateTime to;
    private Integer emptyingMargin;
    private Integer fuelMargin;

    public DailyMovementConsumptionPdfExporter(LocalDateTime from, LocalDateTime to, Integer emptyingMargin, Integer fuelMargin) {
        super(from,to);
        this.from=from;
        this.to=to;
        this.emptyingMargin=emptyingMargin;
        this.fuelMargin=fuelMargin;
    }

    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass, String title) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //Convert list to map
        List<DailyMovementConsumptionReport> contentType= (List<DailyMovementConsumptionReport>) content;
        Map<String, List<DailyMovementConsumptionReport>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));


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

            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd.MM.yyyy");

            //Dodaj vreme izvestaja
            Paragraph time = new Paragraph("Od: "+from.format(dateTimeFormatter)+"  Do: "+to.format(dateTimeFormatter));
            time.setAlignment(Element.ALIGN_CENTER);
            document.add(time);



            Paragraph emptyingFuelMarginParagraph = new Paragraph("Margina za utakanje/istakanje: " + emptyingMargin);
            emptyingFuelMarginParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(emptyingFuelMarginParagraph);


            //Filtriraj polja koja sadrze anotaciju PdfTableElement i broji ih
            long count = Arrays.stream(myclass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(PdfTableElement.class)).count();
            Font cellTitleFont = new Font(Font.TIMES_ROMAN, 8, Font.BOLD, Color.WHITE);
            Font cellContentSumFont = new Font(Font.TIMES_ROMAN, 10, Font.BOLD, Color.BLACK);



            //Popuni podatke
            for (Map.Entry<String, List<DailyMovementConsumptionReport>> entry : collected.entrySet()) {
                PdfPTable table = create(myclass, (int) count, cellTitleFont, WebColors.getRGBColor("#1867c0"));
                Long sumTotalTime=0L;
                Long sumTotalDrivingTime=0L;
                Long sumTotalIdleTime=0L;
                Long sumTotalStandingTime=0L;
                Double sumTotalKm=0.0;
                Double sumTotalFuelConsumptionDriving=0.0;
                Double sumTotalFuelConsumptionIdle=0.0;
                Double sumTotalFuelConsumption=0.0;
                for (DailyMovementConsumptionReport dailyMovementConsumptionReport : entry.getValue()) {
                    addRowToTable(table,dailyMovementConsumptionReport);
                    //Izracunaj sume
                    sumTotalTime+=dailyMovementConsumptionReport.getWorkingTime();
                    sumTotalDrivingTime+=dailyMovementConsumptionReport.getDrivingTime();
                    sumTotalIdleTime+=dailyMovementConsumptionReport.getIdleTime();
                    sumTotalStandingTime+=dailyMovementConsumptionReport.getStandingTime();
                    sumTotalKm+=dailyMovementConsumptionReport.getMileage();
                    sumTotalFuelConsumptionDriving+=dailyMovementConsumptionReport.getDrivingFuelConsumption();
                    sumTotalFuelConsumptionIdle+=dailyMovementConsumptionReport.getIdleFuelConsumption();
                    sumTotalFuelConsumption+=dailyMovementConsumptionReport.getTotalFuelConsumption();
                }
                //Dodaj space
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));

                document.add(table);
                PdfPTable tableSum = createSumColums(count, cellContentSumFont,sumTotalTime, sumTotalDrivingTime, sumTotalIdleTime, sumTotalStandingTime, sumTotalKm, sumTotalFuelConsumptionDriving, sumTotalFuelConsumptionIdle,sumTotalFuelConsumption);

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

    private PdfPTable createSumColums(long count, Font cellContentSumFont,Long sumTotalTime, Long sumTotalDrivingTime, Long sumTotalIdleTime, Long sumTotalStandingTime, Double sumTotalKm, Double sumTotalFuelConsumptionDriving, Double sumTotalFuelConsumptionIdle, Double sumTotalFuelConsumption) {

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

        //Sum total time
        addCellToTable(formatTime(sumTotalTime), cellContentSumFont, tableSum);

        //Sum driving time
        addCellToTable(formatTime(sumTotalDrivingTime), cellContentSumFont, tableSum);


        //Sum idle time
        addCellToTable(formatTime(sumTotalIdleTime), cellContentSumFont, tableSum);


        //Sum standing time
        addCellToTable(formatTime(sumTotalStandingTime), cellContentSumFont, tableSum);

        //Sum total km
        addCellToTable(roundDecimalNumber(sumTotalKm,2), cellContentSumFont, tableSum);

        //Sum total fuel consumption driving
        addCellToTable(roundDecimalNumber(sumTotalFuelConsumptionDriving,2), cellContentSumFont, tableSum);


        //Sum total fuel consumption idle
        addCellToTable(roundDecimalNumber(sumTotalFuelConsumptionIdle,2), cellContentSumFont, tableSum);

        //Sum total fuel consumption
        addCellToTable(roundDecimalNumber(sumTotalFuelConsumption,2), cellContentSumFont, tableSum);


        return tableSum;
    }

}
