package rs.oris.back.export.xml.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.jdbc.Work;
import rs.oris.back.domain.reports.effective_hours.EffectiveWorkingHoursReportData;
import rs.oris.back.domain.reports.sensor_activation.SensorActivationReport;
import rs.oris.back.export.xml.XlsExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SensorsReportXlsExporter extends XlsExporter {
    private LocalDateTime from;
    private LocalDateTime to;

    public SensorsReportXlsExporter(LocalDateTime from, LocalDateTime to) {
        this.from=from;
        this.to=to;
    }



    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass,String title) {

        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //Konvertuj listu u mapu
            List<SensorActivationReport> contentType= (List<SensorActivationReport>) content;
            Map<String, List<SensorActivationReport>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));

            Sheet sheet = workbook.createSheet("Sheet1");

            //Postavi naslov
            String subtitle="Od: "+from.format(DateTimeFormatter.ISO_DATE)+"     Do: "+to.format(DateTimeFormatter.ISO_DATE);
            setTitle(sheet,title,subtitle);
            // Create a header row
            int row=2;
            Row headerRow = sheet.createRow(row++);
            int cellNum = 0;

            for (Map.Entry<String, List<SensorActivationReport>> entry : collected.entrySet()) {
                Long sumTotalTime=0L;

                create(myclass,headerRow,workbook,cellNum++,null,null);

                headerRow = sheet.createRow(row++);
                cellNum = 0;


                //Dodaj redove sa podacima
                for (SensorActivationReport sensorActivationReport : entry.getValue()) {
                    addRowToTable(sensorActivationReport,headerRow,cellNum);
                    headerRow = sheet.createRow(row++);
                    cellNum = 0;
                    sumTotalTime+=sensorActivationReport.getTotalTime();
                }

                creteSumColumns(sheet, headerRow, sumTotalTime);
                headerRow = sheet.createRow(row++);
                headerRow = sheet.createRow(row++);
                headerRow = sheet.createRow(row++);

            }



            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    private void creteSumColumns(Sheet sheet, Row sumRow, Long sumTotalTime) {
        int cellNum = 0;

        // Create blue style for the total row
        Workbook workbook = sheet.getWorkbook();
        CellStyle blueStyle = workbook.createCellStyle();
        blueStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        blueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        blueStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        blueStyle.setAlignment(HorizontalAlignment.CENTER);

        // Set bold font for "Ukupno"
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        blueStyle.setFont(boldFont);

        // First cell with "Ukupno"
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, "Ukupno");

        // Empty cells (apply the same blue style)
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, "");
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, "");
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, "");

        // Sum total time (also styled in blue)
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, formatTime(sumTotalTime));

        // Empty cells (apply blue style)
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, "");
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, "");
    }



}
