package rs.oris.back.export.xml.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rs.oris.back.domain.reports.driver_relation_fuel.DriverRelationsFuelReport;
import rs.oris.back.export.xml.XlsExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DriverRelationsFuelRpoertXLSExporter extends XlsExporter {

    private LocalDateTime from;
    private LocalDateTime to;

    public DriverRelationsFuelRpoertXLSExporter(LocalDateTime from, LocalDateTime to) {
        this.from=from;
        this.to=to;
    }



    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass,String title) {

        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //Konvertuj listu u mapu
            List<DriverRelationsFuelReport> contentType= (List<DriverRelationsFuelReport>) content;
            Map<String, List<DriverRelationsFuelReport>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));

            //Kreira sheet1
            Sheet sheet = workbook.createSheet("Sheet1");

            //Set title and subtitle
            String subtitle="Od: "+from.format(DateTimeFormatter.ISO_DATE)+"     Do: "+to.format(DateTimeFormatter.ISO_DATE);
            setTitle(sheet,title,subtitle);
            int row=2;
            Row headerRow = sheet.createRow(row++);
            int cellNum = 0;


            //Popuni podatke
            for (Map.Entry<String, List<DriverRelationsFuelReport>> entry : collected.entrySet()) {
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
                create(myclass,headerRow,workbook,cellNum++,null,null);

                headerRow = sheet.createRow(row++);
                cellNum = 0;


                for (DriverRelationsFuelReport driverRelationsFuelReport : entry.getValue()) {
                    addRowToTable(driverRelationsFuelReport,headerRow,cellNum);

                    //Izracunaj sum vrednosti
                    headerRow = sheet.createRow(row++);
                    cellNum = 0;
                    sumTotalTime+=driverRelationsFuelReport.getTotalTime();
                    totalDrivingTime+=driverRelationsFuelReport.getMovementTime();
                    totalIdleTime+=driverRelationsFuelReport.getIdleTime();
                    totalDistanceTraveled+=driverRelationsFuelReport.getTraveledDistance();
                    totalMaxSpeed=driverRelationsFuelReport.getMaxSpeed()>totalMaxSpeed? driverRelationsFuelReport.getMaxSpeed() : totalMaxSpeed;
                    totalAvgSpeed=(totalAvgSpeed+driverRelationsFuelReport.getAvgSpeed())/speedAvgCounter;
                    totalFuelConsumption+=driverRelationsFuelReport.getFuelConsumed();
                    if(driverRelationsFuelReport.getFuelConsumedInH()!=0){
                        totalFuelPer1HConsumption=(totalFuelPer1HConsumption+ driverRelationsFuelReport.getFuelConsumedInH())/counterFuelPer1h;
                        counterFuelPer1h++;
                    }

                    speedAvgCounter++;

                }
                creteSumColumns(sheet, headerRow,sumTotalTime,totalDrivingTime,totalIdleTime,totalDistanceTraveled,totalAvgSpeed,totalMaxSpeed,totalFuelConsumption,totalFuelPer1HConsumption,totalFuelPer100KMConsumption);
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



    private void creteSumColumns(Sheet sheet, Row sumRow,  Long sumTotalTime, Long totalDrivingTime, Long totalIdleTime, Double totalDistanceTraveled, Double totalAvgSpeed, Double totalMaxSpeed, Double totalFuelConsumption, Double totalFuelPer1HConsumption, Double totalFuelPer100KMConsumption) {


        int cellNum = 0;

        Cell cell = sumRow.createCell(cellNum++);
        cell.setCellValue("Ukupno");

        //Dodaj prazno polje radi razmaka
        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);

        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);

        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);

        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);

        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);

        //Sum driving distance
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalDistanceTraveled,2));

        //Sum total time
        addCellToTable(cell,sumRow,cellNum++,formatTime(sumTotalTime));

        //Sum driving time
        addCellToTable(cell,sumRow,cellNum++,formatTime(totalDrivingTime));

        //Sum idle time
        addCellToTable(cell,sumRow,cellNum++,formatTime(totalIdleTime));

        //Sum  avg speed
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalAvgSpeed,2));

        //Sum max speed
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalMaxSpeed,2));


        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);


        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);


        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);

        //Sum fuel consumption
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalFuelConsumption,2));

        //Sum fuel per 100km consumption
        if(totalDistanceTraveled!=0)
            addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber((totalFuelConsumption/totalDistanceTraveled)*100,2));
        else             addCellToTable(cell,sumRow,cellNum++,"0");

        //Sum fuel per 1h consumption
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalFuelPer1HConsumption,2));



    }
}
