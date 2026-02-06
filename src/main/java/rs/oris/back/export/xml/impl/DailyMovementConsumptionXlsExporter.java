package rs.oris.back.export.xml.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rs.oris.back.domain.reports.daily_movement_consumption.DailyMovementConsumptionReport;
import rs.oris.back.export.xml.XlsExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DailyMovementConsumptionXlsExporter extends XlsExporter {
    private LocalDateTime from;
    private LocalDateTime to;
    private Integer emptyingMargin;
    private Integer fuelMargin;

    public DailyMovementConsumptionXlsExporter(LocalDateTime from, LocalDateTime to, Integer emptyingMargin, Integer fuelMargin) {
        this.from = from;
        this.to = to;
        this.emptyingMargin = emptyingMargin;
        this.fuelMargin = fuelMargin;
    }

    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass, String title) {
        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //Konvertuj listu u mapu
            List<DailyMovementConsumptionReport> contentType = (List<DailyMovementConsumptionReport>) content;
            Map<String, List<DailyMovementConsumptionReport>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));


            //Kreiraj sheet1
            Sheet sheet = workbook.createSheet("Sheet1");
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd.MM.yyyy");

            //Postavi vreme
            String subtitle = "Od: " + from.format(dateTimeFormatter) + "  Do: " + to.format(dateTimeFormatter);
            setTitle(sheet, title, subtitle);
            // Create a new row for "Fuel Margin" and "Emptying Fuel Margin"
            Row marginRow = sheet.createRow(2);  // Create a row at index 2 (third row)
            int marginCellNum = 0;

            Cell emptyingFuelMarginCell = marginRow.createCell(marginCellNum);
            emptyingFuelMarginCell.setCellValue("Margina za utakanje/istakanje: " + emptyingMargin);

// Merge the next 5 cells with the first cell to make the width of 6 cells
            sheet.addMergedRegion(new CellRangeAddress(marginRow.getRowNum(), marginRow.getRowNum(), marginCellNum, marginCellNum + 3));

// Apply styles to the merged cell if needed (optional)
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.setAlignment(HorizontalAlignment.LEFT);  // Center the text horizontally
            emptyingFuelMarginCell.setCellStyle(style);

// Increment the cell number to skip over the merged cells
            marginCellNum += 4;
            // Add "Emptying Fuel Margin" and a random number
            Cell empty = marginRow.createCell(marginCellNum++);
            empty.setCellValue("");
            Cell emptyCellValue = marginRow.createCell(marginCellNum++);
            emptyCellValue.setCellValue("");  // Replace with a more suitable value if needed


            // Create a header row
            int row = 4;  // Start from the next row after the margin row
            Row headerRow = sheet.createRow(row++);
            int cellNum = 0;

            for (Map.Entry<String, List<DailyMovementConsumptionReport>> entry : collected.entrySet()) {
                Long sumTotalTime = 0L;
                Long sumTotalDrivingTime = 0L;
                Long sumTotalIdleTime = 0L;
                Long sumTotalStandingTime = 0L;
                Double sumTotalKm = 0.0;
                Double sumTotalFuelConsumptionDriving = 0.0;
                Double sumTotalFuelConsumptionIdle = 0.0;
                Double sumTotalFuelConsumption = 0.0;

                create(myclass, headerRow, workbook, cellNum++, null, null);

                headerRow = sheet.createRow(row++);
                cellNum = 0;


                for (DailyMovementConsumptionReport dailyMovementConsumptionReport : entry.getValue()) {
                    addRowToTable(dailyMovementConsumptionReport, headerRow, cellNum);
                    headerRow = sheet.createRow(row++);
                    cellNum = 0;

                    //Izracunaj sume
                    sumTotalTime += dailyMovementConsumptionReport.getWorkingTime();
                    sumTotalDrivingTime += dailyMovementConsumptionReport.getDrivingTime();
                    sumTotalIdleTime += dailyMovementConsumptionReport.getIdleTime();
                    sumTotalStandingTime += dailyMovementConsumptionReport.getStandingTime();
                    sumTotalKm += dailyMovementConsumptionReport.getMileage();
                    sumTotalFuelConsumptionDriving += dailyMovementConsumptionReport.getDrivingFuelConsumption();
                    sumTotalFuelConsumptionIdle += dailyMovementConsumptionReport.getIdleFuelConsumption();
                    sumTotalFuelConsumption += dailyMovementConsumptionReport.getTotalFuelConsumption();

                }
                creteSumColumns(sheet, headerRow, sumTotalTime, sumTotalDrivingTime, sumTotalIdleTime, sumTotalStandingTime, sumTotalKm, sumTotalFuelConsumptionDriving, sumTotalFuelConsumptionIdle, sumTotalFuelConsumption);
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

    private void creteSumColumns(Sheet sheet, Row sumRow, Long sumTotalTime, Long sumTotalDrivingTime, Long sumTotalIdleTime, Long sumTotalStandingTime, Double sumTotalKm, Double sumTotalFuelConsumptionDriving, Double sumTotalFuelConsumptionIdle, Double sumTotalFuelConsumption) {

        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        int cellNum = 0;



        Cell cell = sumRow.createCell(cellNum++);

// Create a new cell style and set the font
        XSSFWorkbook workbook = (XSSFWorkbook) sheet.getWorkbook();
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);

// Create a custom XSSFColor using the hex color value
        XSSFColor color = new XSSFColor(java.awt.Color.decode("#89CFF0"));

// Apply the custom color to the cell's background
        cellStyle.setFillForegroundColor(color);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

// Apply the style to the cell
        cell.setCellStyle(cellStyle);

        cell.setCellValue("Ukupno");
        sheet.setColumnWidth(0, 5000);

        //PRAZNO
        addEmptyCellToTableWithStyle(cell, cellStyle,sumRow, cellNum++);
        sheet.setColumnWidth(cellNum - 1, 5000);

        //PRAZNO
        addEmptyCellToTableWithStyle(cell, cellStyle,sumRow, cellNum++);
        sheet.setColumnWidth(cellNum - 1, 5000);

        //PRAZNO
        addEmptyCellToTableWithStyle(cell,cellStyle, sumRow, cellNum++);
        sheet.setColumnWidth(cellNum - 1, 5000);

        //Sum total time
        addCellToTableWithStyle(cell,cellStyle, sumRow, cellNum++, formatTime(sumTotalTime));
        sheet.setColumnWidth(cellNum - 1, 5000);

        //Sum driving time
        addCellToTableWithStyle(cell, cellStyle,sumRow, cellNum++, formatTime(sumTotalDrivingTime));
        sheet.setColumnWidth(cellNum - 1, 5000);


        //Sum idle time
        addCellToTableWithStyle(cell,cellStyle, sumRow, cellNum++, formatTime(sumTotalIdleTime));
        sheet.setColumnWidth(cellNum - 1, 5000);


        //Sum standing time
        addCellToTableWithStyle(cell,cellStyle, sumRow, cellNum++, formatTime(sumTotalStandingTime));
        sheet.setColumnWidth(cellNum - 1, 5000);

        //Sum total km
        addCellToTableWithStyle(cell, cellStyle,sumRow, cellNum++, roundDecimalNumber(sumTotalKm, 2));
        sheet.setColumnWidth(cellNum - 1, 5000);

        //Sum total fuel consumption driving
        addCellToTableWithStyle(cell,cellStyle, sumRow, cellNum++, roundDecimalNumber(sumTotalFuelConsumptionDriving, 2));
        sheet.setColumnWidth(cellNum - 1, 5000);


        //Sum total fuel consumption idle
        addCellToTableWithStyle(cell,cellStyle, sumRow, cellNum++, roundDecimalNumber(sumTotalFuelConsumptionIdle, 2));
        sheet.setColumnWidth(cellNum - 1, 5000);

        //Sum total fuel consumption
        addCellToTableWithStyle(cell,cellStyle, sumRow, cellNum++, roundDecimalNumber(sumTotalFuelConsumption, 2));
        sheet.setColumnWidth(cellNum - 1, 5000);

    }
}
