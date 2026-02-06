package rs.oris.back.export.pdf.impl;

import com.itextpdf.text.BaseColor;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.DateUtil;
import rs.oris.back.domain.reports.effective_hours.EffectiveWorkingHoursReportData;
import rs.oris.back.export.annotations.PdfTableElement;
import rs.oris.back.export.pdf.PdfExporter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EffectiveWokringHoursPdfExporter extends PdfExporter {
    private LocalDateTime to;
    private LocalDateTime from;

    public EffectiveWokringHoursPdfExporter(LocalDateTime to, LocalDateTime from) {
        super(to, from);
        this.to=to;
       this.from=from;
    }



    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass,String title ) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //Konvertuj listu u mapu
        List<EffectiveWorkingHoursReportData> contentType= (List<EffectiveWorkingHoursReportData>) content;
        Map<String, List<EffectiveWorkingHoursReportData>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));


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

            //Filtriraj polja koja imaju anotaciju PdfTableElement i prebroj ih
            long count = Arrays.stream(myclass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(PdfTableElement.class)).count();
            Font cellTitleFont = new Font(Font.TIMES_ROMAN, 8, Font.BOLD, Color.WHITE);
            Font cellContentSumFont = new Font(Font.TIMES_ROMAN, 12, Font.BOLD, Color.BLACK);



            for (Map.Entry<String, List<EffectiveWorkingHoursReportData>> entry : collected.entrySet()) {
                PdfPTable table = create(myclass, (int) count, cellTitleFont,WebColors.getRGBColor("#1867c0"));
                Long sumTotalTime=0L;
                Long totalDrivingTime=0L;
                Long totalIdleTime=0L;
                Long totalRpmInTime=0L;
                Integer counterFuelPer1h=1;

                Long totalRpmOutTime=0L;
                Double totalFuelConsumption=0.0;
                Double totalFuelPer1HConsumption=0.0;
                for (EffectiveWorkingHoursReportData effectiveWorkingHoursReportData : entry.getValue()) {
                    addRowToTable(table,effectiveWorkingHoursReportData);
                    //Izracunaj sum vrednosti
                    sumTotalTime+=effectiveWorkingHoursReportData.getTotalTime();
                    totalDrivingTime+=effectiveWorkingHoursReportData.getDrivingTime();
                    totalIdleTime+=effectiveWorkingHoursReportData.getIdleTime();
                    totalRpmInTime+=effectiveWorkingHoursReportData.getRpmInTime();
                    totalRpmOutTime+=effectiveWorkingHoursReportData.getRpmOutTime();
                    totalFuelConsumption+=effectiveWorkingHoursReportData.getFuelSpent();
                    if(effectiveWorkingHoursReportData.getGetAverageFuelSpentPer1h()!=0){
                        totalFuelPer1HConsumption=(totalFuelPer1HConsumption+ effectiveWorkingHoursReportData.getGetAverageFuelSpentPer1h())/counterFuelPer1h;
                        counterFuelPer1h++;
                    }

                }
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));

                document.add(table);
                PdfPTable tableSum = createSumColums(count, cellContentSumFont,sumTotalTime,totalDrivingTime,totalIdleTime,totalRpmInTime,totalRpmOutTime,totalFuelConsumption,totalFuelPer1HConsumption);


                document.add(tableSum);
            }

            //Dodaj prazno
            document.add(new Paragraph("\n"));



            document.close();

            writer.close();


            System.out.println("PDF with table created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    private  PdfPTable createSumColums(long count, Font cellTitleFont, Long sumTotalTime, Long totalDrivingTime, Long totalIdleTime, Long totalRpmInTime, Long totalRpmOutTime, Double totalFuelConsumption, Double totalFuelPer1HConsumption) {
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

        //Sum driving time
        addCellToTable(formatTime(totalDrivingTime), cellTitleFont, tableSum);


        //Sum idle time
        addCellToTable(formatTime(totalIdleTime), cellTitleFont, tableSum);


        //Sum rpm in time
        addCellToTable(formatTime(totalRpmInTime), cellTitleFont, tableSum);


        //Sum rpm out time
        addCellToTable(formatTime(totalRpmOutTime), cellTitleFont, tableSum);


        //Sum fuel consumption
        addCellToTable(roundDecimalNumber(totalFuelConsumption,2), cellTitleFont, tableSum);


        //Sum fuel per 1h consumption
        addCellToTable(roundDecimalNumber(totalFuelPer1HConsumption,2), cellTitleFont, tableSum);



        return tableSum;
    }

}
