package rs.oris.back.export.xml;


import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import rs.oris.back.export.annotations.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static rs.oris.back.util.DateUtil.toSerbianTimeZone;
import static rs.oris.back.util.DateUtil.toSerbianTimeZoneLocalDate;

public class XlsExporter {
    public <T> byte[] export(List<T> content, Class<T> myclass, String title) {

        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //Kreira sheet1
            Sheet sheet = workbook.createSheet("Sheet1");

            // Kreira naslov
            Row headerRow = sheet.createRow(0);
            int cellNum = 0;

            for (Field field : myclass.getDeclaredFields()) {
                if (field.isAnnotationPresent(XlsTableElement.class)) {
                    XlsTableElement annotation = field.getAnnotation(XlsTableElement.class);
                    Cell cell = headerRow.createCell(cellNum++);
                    cell.setCellValue(annotation.title());
                    cell.setCellStyle(getStyleForCellHeader(workbook));
                }
            }

            // Popuni redove sa podacima
            int rowNum = 1;
            for (T obj : content) {
                Row row = sheet.createRow(rowNum++);
                cellNum = 0;

                for (Field field : obj.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(XlsTableElement.class)) {

                        field.setAccessible(true);
                        Object value = field.get(obj);
                        Cell cell = row.createCell(cellNum++);

                        //Ako je polje tipa Long i ima anotaciju TimestampFormat
                        if (field.isAnnotationPresent(TimestampFormat.class) && value instanceof Long) {
                            String formattedTime = formatTime((Long) value);
                            cell.setCellValue(formattedTime);
                        } else
                            //Ako je polje tipa LocalDateTime
                            if (field.getType() == LocalDateTime.class && value instanceof LocalDateTime) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
                                LocalDate formattedTime = toSerbianTimeZoneLocalDate((LocalDateTime) value);
                                cell.setCellValue(formattedTime.format(formatter));
                        } else
                            //Ako je polje tipa Double i ima anotaciju Round
                            if (field.getType() == Double.class && field.isAnnotationPresent(Round.class)) {
                            Round annotation = field.getAnnotation(Round.class);
                            String formatString = "%." + annotation.roundTo() + "f";
                            String roundedNumber = String.format(formatString, value);

                            cell.setCellValue(roundedNumber);
                        } else
                            //Ako je polje tipa List<String>
                            if (field.isAnnotationPresent(ListNames.class)) {

                            List<String> names = (List<String>) value;
                            if (names != null) {
                                String namesString = String.join(", ", names);
                                cell.setCellValue(namesString);
                            }
                        }

                            else if(field.getType()==Boolean.class && field.isAnnotationPresent(BooleanReplacer.class)){
                                BooleanReplacer annotation = field.getAnnotation(BooleanReplacer.class);
                                Boolean valueBoolean=(Boolean)  value;
                                String booleanValue = valueBoolean ? annotation.trueValue() : annotation.falseValue();
                                cell.setCellValue(booleanValue);
                            }
                            else {
                            cell.setCellValue(value != null ? value.toString() : "");
                        }
                    }
                }
            }


            workbook.write(outputStream);

            return outputStream.toByteArray();
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    protected String formatLocalDateTime(LocalDateTime value) {

       return toSerbianTimeZone(value);
    }

    protected void setTitle(Sheet sheet, String title, String subtitle) {

        //Kreiraj red za naslov
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < 10; i++) {
            Font font = sheet.getWorkbook().createFont();
            font.setBold(true);

            // Create a cell style and apply the font
            CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
            cellStyle.setFont(font);

            // Create the first cell and set the title
            Cell cell = titleRow.createCell(0);
            cell.setCellValue(title);
            cell.setCellStyle(cellStyle); // Apply the bold style
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

        //Kreiraj red za podnaslov
        Row subtitleRow = sheet.createRow(1);
        for (int i = 0; i < 5; i++) {
            Cell cell = subtitleRow.createCell(i);
            cell.setCellValue(subtitle);
        }
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
    }

    //Formatiraj vreme
    protected String formatTime(Long totalTime) {
        Duration duration = Duration.ofSeconds(totalTime);
        duration.plusHours(1);
        long hours = duration.toHours();
        //+1 za srbiju jer je utc

        long minutes = duration.minusHours(hours).toMinutes();
        long seconds = duration.minusHours(hours).minusMinutes(minutes).getSeconds();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }



    protected <T> void create(Class<T> myclass, Row headerRow, Workbook workbook, Integer cellNum, com.lowagie.text.Font cellTitleFont, Color bcgColor) {
        for (Field field : myclass.getDeclaredFields()) {
            if (field.isAnnotationPresent(XlsTableElement.class)) {
                XlsTableElement annotation = field.getAnnotation(XlsTableElement.class);
                Cell cell = headerRow.createCell(cellNum++);
                cell.setCellValue(annotation.title());
                cell.setCellStyle(getStyleForCellHeader(workbook));
            }
        }
    }


    //Kreiraj prazan red
    protected void createSpace(int i, Sheet sheet) {
        sheet.createRow(i);
    }


    //Dodaj celiju u tabelu
    protected void addCellToTable(Cell cell, Row sumRow, Integer cellNum, Double value) {

        cell = sumRow.createCell(cellNum++);
        cell.setCellValue(value);
    }


    //Dodaj celiju u tabelu
    protected void addCellToTable(Cell cell, Row sumRow, Integer cellNum, String value) {

        cell = sumRow.createCell(cellNum++);
        cell.setCellValue(value);
    }

    protected void addCellToTableWithStyle(Cell cell, CellStyle cellStyle ,Row sumRow, Integer cellNum, String value) {

        cell = sumRow.createCell(cellNum++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
    }

    //Dodaj praznu celiju u tabelu
    protected void addEmptyCellToTable(Cell cell,  Row sumRow, Integer cellNum) {

        cell = sumRow.createCell(cellNum++);


        // Create the first cell and set the title

        cell.setCellValue("");
    }
    protected void addEmptyCellToTableWithStyle(Cell cell,  CellStyle cellStyle, Row sumRow, Integer cellNum) {

        cell = sumRow.createCell(cellNum++);


        // Create the first cell and set the title
        cell.setCellStyle(cellStyle);
        cell.setCellValue("");
    }

    //Dodaj red u tabelu
    protected void addRowToTable(Object obj, Row row, int cellNum) throws IllegalAccessException {


        for (Field field : obj.getClass().getDeclaredFields()) {

            if (field.isAnnotationPresent(XlsTableElement.class)) {
                field.setAccessible(true);
                Object value = field.get(obj);
                Cell cell = row.createCell(cellNum++);

                //Ako je polje tipa Long i ima anotaciju TimestampFormat
                if (field.isAnnotationPresent(TimestampFormat.class) && value instanceof Long) {
                    String formattedTime = formatTime((Long) value);

                    cell.setCellValue(formattedTime);
                } else
                    //Ako je polje tipa LocalDateTime
                    if (field.getType() == LocalDateTime.class && value instanceof LocalDateTime) {
                    String formattedTime = formatLocalDateTime((LocalDateTime) value);
                    cell.setCellValue(formattedTime);
                } else
                    //Ako je polje tipa Double i ima anotaciju Round
                    if (field.getType() == Double.class && field.isAnnotationPresent(Round.class)) {
                    Round annotation = field.getAnnotation(Round.class);
                    String formatString = "%." + annotation.roundTo() + "f";
                    String roundedNumber = String.format(formatString, value);

                    cell.setCellValue(roundedNumber);
                } else
                    //Ako je polje tipa List<String>
                    if (field.isAnnotationPresent(ListNames.class)) {
                    List<String> names = (List<String>) value;
                    if (names != null) {
                        String namesString = String.join(", ", names);
                        cell.setCellValue(namesString);
                    }

                }     else if(field.getType()==Boolean.class && field.isAnnotationPresent(BooleanReplacer.class)){
                        BooleanReplacer annotation = field.getAnnotation(BooleanReplacer.class);
                        Boolean valueBoolean=(Boolean)  value;
                        String booleanValue = valueBoolean ? annotation.trueValue() : annotation.falseValue();
                        cell.setCellValue(booleanValue);
                    }else {
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }
        }
    }

    //Kreiraj stil za zaglavlje
    protected CellStyle getStyleForCellHeader(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();

        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        headerStyle.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.getIndex());

        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }


    //Zaokruzi decimalni broj
    protected String roundDecimalNumber(Object value, int roundTo) {
        String formatString = "%." + roundTo + "f";
        String roundedNumber = String.format(formatString, value);
        return roundedNumber;
    }
}
