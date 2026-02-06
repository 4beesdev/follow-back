package rs.oris.back.export.xml.impl;

import org.apache.poi.ss.usermodel.Cell;
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
            Integer counterFuelPer1h=1;
            Double totalFuelConsumption=0.0;
            Double totalFuelPer1HConsumption=0.0;
            Double totalFuelPer100KMConsumption=0.0;
            create(myclass,headerRow,workbook,cellNum++,null,null);

            //Popuni podatke
            for (Map.Entry<String, List<MonthlyFuelConsumptionReport>> entry : collected.entrySet()) {


                headerRow = sheet.createRow(row++);
                cellNum = 0;


                for (MonthlyFuelConsumptionReport monthlyFuelConsumptionReport : entry.getValue()) {
                    addRowToTable(monthlyFuelConsumptionReport,headerRow,cellNum);
                    headerRow = sheet.createRow(row++);
                    //Izracunaj sum vrednosti
                    cellNum = 0;
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

            headerRow = sheet.createRow(row++);

            creteSumColumns(sheet, headerRow, sumTotalTime, totalDrivingTime, totalIdleTime, totalDistanceTraveled, totalFuelConsumption, totalFuelPer1HConsumption, totalFuelPer100KMConsumption);
            headerRow = sheet.createRow(row++);
            headerRow = sheet.createRow(row++);
            headerRow = sheet.createRow(row++);
            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    private void creteSumColumns(Sheet sheet, Row sumRow, Long sumTotalTime, Long totalDrivingTime, Long totalIdleTime, Double totalDistanceTraveled, Double totalFuelConsumption, Double totalFuelPer1HConsumption, Double totalFuelPer100KMConsumption) {


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


        //Sum driving distance
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalDistanceTraveled,2));

        //Sum driving time
        addCellToTable(cell,sumRow,cellNum++,formatTime(totalDrivingTime));


        //Sum idle time
        addCellToTable(cell,sumRow,cellNum++,formatTime(totalIdleTime));

        //Sum fuel consumption
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalFuelConsumption,2));


        //Sum fuel per 100km consumption
        if(totalDistanceTraveled!=0)
            addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber((totalFuelConsumption/totalDistanceTraveled)*100,2));
        else         addCellToTable(cell,sumRow,cellNum++,"0");


        //Sum fuel per 1h consumption
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalFuelPer1HConsumption,2));

    }


}
