package rs.oris.back.export.xml.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rs.oris.back.domain.reports.monthly_fuel.MonthlyFuelConsumptionReport;
import rs.oris.back.export.xml.XlsExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MontlyXlsExporter extends XlsExporter {
    private LocalDateTime from;
    private LocalDateTime to;

    public MontlyXlsExporter(LocalDateTime from, LocalDateTime to) {
        this.from=from;
        this.to=to;
    }



    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass,String title) {

        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //Konvertuj listu u mapu
            List<MonthlyFuelConsumptionReport> contentType= (List<MonthlyFuelConsumptionReport>) content;
            Map<String, List<MonthlyFuelConsumptionReport>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));

            //Kreiraj sheet1
            Sheet sheet = workbook.createSheet("Sheet1");
            String subtitle="Od: "+from.format(DateTimeFormatter.ISO_DATE)+"     Do: "+to.format(DateTimeFormatter.ISO_DATE);

            setTitle(sheet,title,subtitle);


            int row=2;
            Row headerRow = sheet.createRow(row++);
            int cellNum = 0;
            Long sumTotalTime=0L;
            Long totalDrivingTime=0L;
            Long totalIdleTime=0L;
            Long totalRpmInTime=0L;
            Integer counterFuelPer100Km=1;
            Double totalDistanceTraveled=0.0;
            Double totalFuelConsumption=0.0;
            Double totalFuelPer1HConsumption=0.0;
            Double totalFuelPer100KMConsumption=0.0;
            create(myclass,headerRow,workbook,cellNum++,null,null);

            //Popuni podatke
            for (Map.Entry<String, List<MonthlyFuelConsumptionReport>> entry : collected.entrySet()) {
                for (MonthlyFuelConsumptionReport monthlyFuelConsumptionReport : entry.getValue()) {
                    headerRow = sheet.createRow(row++);
                    cellNum = 0;
                    addRowToTable(monthlyFuelConsumptionReport, headerRow, cellNum);
                    sumTotalTime += monthlyFuelConsumptionReport.getTotalTime();
                    totalDrivingTime += monthlyFuelConsumptionReport.getDrivingTime();
                    totalIdleTime += monthlyFuelConsumptionReport.getIdleTime();
                    totalFuelConsumption += monthlyFuelConsumptionReport.getFuelSpent();
                    totalDistanceTraveled += monthlyFuelConsumptionReport.getDistanceTraveled();
                }
            }

            long totalDrivingAndIdleSeconds = totalDrivingTime + totalIdleTime;
            if (totalDrivingAndIdleSeconds > 0) {
                totalFuelPer1HConsumption = totalFuelConsumption / (totalDrivingAndIdleSeconds / 3600.0);
            }

            headerRow = sheet.createRow(row++);
            creteSumColumns(sheet, headerRow, sumTotalTime, totalDrivingTime, totalIdleTime, totalDistanceTraveled, totalFuelConsumption, totalFuelPer1HConsumption, totalFuelPer100KMConsumption);
            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    private void creteSumColumns(Sheet sheet, Row sumRow, Long sumTotalTime, Long totalDrivingTime, Long totalIdleTime, Double totalDistanceTraveled, Double totalFuelConsumption, Double totalFuelPer1HConsumption, Double totalFuelPer100KMConsumption) {
        int cellNum = 0;

        Workbook workbook = sheet.getWorkbook();
        CellStyle blueStyle = workbook.createCellStyle();
        blueStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        blueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        blueStyle.setFont(boldFont);

        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, "Ukupno");
        addEmptyCellToTableWithStyle(null, blueStyle, sumRow, cellNum++);
        addEmptyCellToTableWithStyle(null, blueStyle, sumRow, cellNum++);
        addEmptyCellToTableWithStyle(null, blueStyle, sumRow, cellNum++);
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, formatTime(sumTotalTime));
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, roundDecimalNumber(totalDistanceTraveled, 2));
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, formatTime(totalDrivingTime));
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, formatTime(totalIdleTime));
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, roundDecimalNumber(totalFuelConsumption, 2));
        if (totalDistanceTraveled != 0) {
            addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, roundDecimalNumber((totalFuelConsumption / totalDistanceTraveled) * 100, 2));
        } else {
            addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, "0");
        }
        addCellToTableWithStyle(null, blueStyle, sumRow, cellNum++, roundDecimalNumber(totalFuelPer1HConsumption, 2));
    }


}
