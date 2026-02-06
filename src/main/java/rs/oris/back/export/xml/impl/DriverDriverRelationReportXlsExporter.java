package rs.oris.back.export.xml.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rs.oris.back.export.annotations.ListNames;
import rs.oris.back.export.annotations.Round;
import rs.oris.back.export.annotations.TimestampFormat;
import rs.oris.back.export.annotations.XlsTableElement;
import rs.oris.back.export.xml.XlsExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DriverDriverRelationReportXlsExporter extends XlsExporter {

    private LocalDateTime from;
    private LocalDateTime to;

    public DriverDriverRelationReportXlsExporter(LocalDateTime from, LocalDateTime to) {
        this.from=from;
        this.to=to;
    }

    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass, String title) {


            try (Workbook workbook = new XSSFWorkbook()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                //Kreira sheet1
                Sheet sheet = workbook.createSheet("Sheet1");
                String subtitle="Od: "+from.format(DateTimeFormatter.ISO_DATE)+"     Do: "+to.format(DateTimeFormatter.ISO_DATE);
                setTitle(sheet,title,subtitle);
                // Create a header row
                Row headerRow = sheet.createRow(2);
                int cellNum = 0;

                for (Field field : myclass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(XlsTableElement.class)) {
                        XlsTableElement annotation = field.getAnnotation(XlsTableElement.class);
                        Cell cell = headerRow.createCell(cellNum++);
                        cell.setCellValue(annotation.title());
                        cell.setCellStyle(getStyleForCellHeader(workbook));
                    }
                }

                // Populate data rows
                int rowNum = 3;
                for (T obj : content) {
                    Row row = sheet.createRow(rowNum++);
                    cellNum = 0;

                    for (Field field : obj.getClass().getDeclaredFields()) {
                        if (field.isAnnotationPresent(XlsTableElement.class)) {
                            field.setAccessible(true);
                            Object value = field.get(obj);
                            Cell cell = row.createCell(cellNum++);

                            if (field.isAnnotationPresent(TimestampFormat.class) && value instanceof Long) {
                                String formattedTime = formatTime((Long) value);
                                cell.setCellValue(formattedTime);
                            } else if (field.getType() == LocalDateTime.class && value instanceof LocalDateTime) {
                                String formattedTime = formatLocalDateTime((LocalDateTime) value);
                                cell.setCellValue(formattedTime);
                            }else if(field.getType()==Double.class && field.isAnnotationPresent(Round.class)){
                                Round annotation = field.getAnnotation(Round.class);
                                String formatString = "%." + annotation.roundTo() + "f";
                                String roundedNumber = String.format(formatString, value);

                                cell.setCellValue(roundedNumber);
                            }
                            else  if(field.isAnnotationPresent(ListNames.class)){

                                List<String> names = (List<String>) value;
                                if(names!=null){
                                    String namesString = String.join(", ", names);
                                    cell.setCellValue(namesString);
                                }

                            }
                            else {
                                cell.setCellValue(value != null ? value.toString() : "");
                            }
                        }
                    }
                }

                // Write the workbook to a file
                workbook.write(outputStream);

                return outputStream.toByteArray();
            } catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return new byte[0];
           }
}
