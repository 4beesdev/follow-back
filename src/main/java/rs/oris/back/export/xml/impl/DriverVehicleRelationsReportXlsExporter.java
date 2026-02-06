package rs.oris.back.export.xml.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rs.oris.back.domain.reports.driver_relation.DriverVehicleRelationReportData;
import rs.oris.back.domain.reports.sensor_activation.SensorActivationReport;
import rs.oris.back.export.xml.XlsExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DriverVehicleRelationsReportXlsExporter extends XlsExporter {
    private LocalDateTime from;
    private LocalDateTime to;

    public DriverVehicleRelationsReportXlsExporter(LocalDateTime from, LocalDateTime to) {
        this.from=from;
        this.to=to;
    }



    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass,String title) {

        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //Konvertuj listu u mapu
            List<DriverVehicleRelationReportData> contentType= (List<DriverVehicleRelationReportData>) content;
            Map<String, List<DriverVehicleRelationReportData>> collected = contentType.stream().collect(Collectors.groupingBy(x -> x.getRegistration()));

            //Kreira sheet1
            Sheet sheet = workbook.createSheet("Sheet1");


            String subtitle="Od: "+from.format(DateTimeFormatter.ISO_DATE)+"     Do: "+to.format(DateTimeFormatter.ISO_DATE);
            setTitle(sheet,title,subtitle);
            // Create a header row
            int row=2;
            Row headerRow = sheet.createRow(row++);
            int cellNum = 0;

            //Insert data
            for (Map.Entry<String, List<DriverVehicleRelationReportData>> entry : collected.entrySet()) {

                create(myclass,headerRow,workbook,cellNum++,null,null);

                headerRow = sheet.createRow(row++);
                cellNum = 0;
                Long sumTotalTime=0L;
                Long totalDrivingTime=0L;
                Long totalIdleTime=0L;
                Double totalDistanceTraveled=0.0;
                Integer speedAvgCounter=1;

                Double totalAvgSpeed=0.0;
                Double totalMaxSpeed=0.0;

                for (DriverVehicleRelationReportData driverVehicleRelationReportData : entry.getValue()) {
                    addRowToTable(driverVehicleRelationReportData,headerRow,cellNum);
                    //Izracunaj sume
                    headerRow = sheet.createRow(row++);
                    cellNum = 0;
                    sumTotalTime+=driverVehicleRelationReportData.getTotalTime();
                    totalDrivingTime+=driverVehicleRelationReportData.getMovementTime();
                    totalIdleTime+=driverVehicleRelationReportData.getIdleTime();
                    totalDistanceTraveled+=driverVehicleRelationReportData.getTraveledDistance();
                    totalMaxSpeed=driverVehicleRelationReportData.getMaxSpeed()>totalMaxSpeed? driverVehicleRelationReportData.getMaxSpeed() : totalMaxSpeed;

                    totalAvgSpeed=(totalAvgSpeed+driverVehicleRelationReportData.getAvgSpeed())/speedAvgCounter;
                    speedAvgCounter++;


                }
                creteSumColumns(sheet, headerRow, sumTotalTime,totalDrivingTime,totalIdleTime,totalDistanceTraveled,totalAvgSpeed,totalMaxSpeed);
                headerRow = sheet.createRow(row++);
                headerRow = sheet.createRow(row++);
                headerRow = sheet.createRow(row++);

            }



            // Write the workbook to a file
            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    private void creteSumColumns(Sheet sheet, Row sumRow, Long sumTotalTime, Long totalDrivingTime, Long totalIdleTime, Double totalDistanceTraveled, Double totalAvgSpeed, Double totalMaxSpeed) {


        int cellNum = 0;

        Cell cell = sumRow.createCell(cellNum++);
        cell.setCellValue("Ukupno");


        //Mora se dodati prazni prostor
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
        //PRAZNO
        addEmptyCellToTable(cell,sumRow,cellNum++);

        //Sum total time
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalDistanceTraveled,2));
        //Sum total time
        addCellToTable(cell,sumRow,cellNum++,formatTime(sumTotalTime));
        //Sum total time
        addCellToTable(cell,sumRow,cellNum++,formatTime(totalDrivingTime));
        //Sum total time
        addCellToTable(cell,sumRow,cellNum++,formatTime(totalIdleTime));
        //Sum total time
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalAvgSpeed,2));
        addCellToTable(cell,sumRow,cellNum++,roundDecimalNumber(totalMaxSpeed,2));




    }


}
