package rs.oris.back.export.xml.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rs.oris.back.domain.reports.effective_hours.EffectiveWorkingHoursReportData;
import rs.oris.back.export.xml.XlsExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EffectiveWorkingHoursXlsExporter extends XlsExporter {
    private LocalDateTime from;
    private LocalDateTime to;

    public EffectiveWorkingHoursXlsExporter(LocalDateTime from, LocalDateTime to) {
        this.from=from;
        this.to=to;
    }



    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass,String title) {

        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //Konvertuj listu u mapu
            List<EffectiveWorkingHoursReportData> contentType= (List<EffectiveWorkingHoursReportData>) content;
            Map<String, List<EffectiveWorkingHoursReportData>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));


            //Kreiraj sheet1
            Sheet sheet = workbook.createSheet("Sheet1");

            //Postavi vreme
            String subtitle="Od: "+from.format(DateTimeFormatter.ISO_DATE)+"     Do: "+to.format(DateTimeFormatter.ISO_DATE);
            setTitle(sheet,title,subtitle);
            // Create a header row
            int row=2;
            Row headerRow = sheet.createRow(row++);
            int cellNum = 0;

            for (Map.Entry<String, List<EffectiveWorkingHoursReportData>> entry : collected.entrySet()) {
                Long sumTotalTime=0L;
                Long totalDrivingTime=0L;
                Long totalIdleTime=0L;
                Long totalRpmInTime=0L;
                Long totalRpmOutTime=0L;
                Double totalFuelConsumption=0.0;
                Integer counterFuelPer1h=1;
                Double totalFuelPer1HConsumption=0.0;
                create(myclass,headerRow,workbook,cellNum++,null,null);

                headerRow = sheet.createRow(row++);
                cellNum = 0;


                for (EffectiveWorkingHoursReportData effectiveWorkingHoursReportData : entry.getValue()) {
                    addRowToTable(effectiveWorkingHoursReportData,headerRow,cellNum);
                    headerRow = sheet.createRow(row++);
                    cellNum = 0;

                    //Izracunaj sume
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
                creteSumColumns(sheet, headerRow, sumTotalTime, totalDrivingTime, totalIdleTime, totalRpmInTime, totalRpmOutTime, totalFuelConsumption, totalFuelPer1HConsumption);
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

    private void creteSumColumns(Sheet sheet, Row sumRow, Long sumTotalTime, Long totalDrivingTime, Long totalIdleTime, Long totalRpmInTime, Long totalRpmOutTime, Double totalFuelConsumption, Double totalFuelPer1HConsumption) {


        int cellNum = 0;

        Cell cell = sumRow.createCell(cellNum++);
        cell.setCellValue("Ukupno");

        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);

        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);

        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);

        //Sum total time
        addCellToTable(cell,sumRow,cellNum++,formatTime(sumTotalTime));

        //Sum driving time
        addCellToTable(cell,sumRow,cellNum++,formatTime(totalDrivingTime));


        //Sum idle time
        addCellToTable(cell,sumRow,cellNum++,formatTime(totalIdleTime));


        //Sum rpm in time
        addCellToTable(cell,sumRow,cellNum++,formatTime(totalRpmInTime));


        //Sum rpm out time
        addCellToTable(cell,sumRow,cellNum++,formatTime(totalRpmOutTime));

        //Sum fuel consumption
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalFuelConsumption,2));


        //Sum fuel per 1h consumption
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalFuelPer1HConsumption,2));

    }


}
