package rs.oris.back.export.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import rs.oris.back.export.annotations.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static rs.oris.back.util.DateUtil.toSerbianTimeZone;

public class PdfExporter {

    protected LocalDateTime to;
    protected LocalDateTime from;

    public PdfExporter(LocalDateTime to, LocalDateTime from) {
        this.to=to;
        this.from=from;
    }

    public <T>  byte[] export(List<T> content, Class<T> myclass,String title){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        try {
            //Kreiraj dokument
            Document document = new Document(PageSize.A4.rotate());
            document.setMargins(20.0f,0.0f,50.0f,0.0f);

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
            //Dodaj prazan red
            document.add(new Paragraph("\n"));


            // Kreiraj tabelu
            long count = Arrays.stream(myclass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(PdfTableElement.class)).count();
            PdfPTable table = new PdfPTable((int) count);
            Font cellTitleFont = new Font(Font.TIMES_ROMAN, 8, Font.BOLD,Color.WHITE);

            //Dodaj header
            for (Field field : myclass.getDeclaredFields()) {
                if (field.isAnnotationPresent(PdfTableElement.class)) {
                    PdfTableElement annotation = field.getAnnotation(PdfTableElement.class);
                    table.addCell(createCell(annotation.title(),cellTitleFont, WebColors.getRGBColor("#1867c0")));
                    field.setAccessible(true);
                }
            }


            //Dodaj redove
            for (Object exporable : content) {
                addRowToTable(table,exporable);
            }


            document.add(table);

            document.close();

            writer.close();


            System.out.println("PDF with table created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();

    }

    //Dodaj red u tabelu
    protected   void addRowToTable(PdfPTable table, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Font cellDataFont = new Font(Font.TIMES_ROMAN, 8, Font.NORMAL);

        for (Field field : obj.getClass().getDeclaredFields()) {
            //Ako je polje anotirano sa PdfTableElement
            if (field.isAnnotationPresent(PdfTableElement.class)) {
                field.setAccessible(true);
                Object value = field.get(obj);
                PdfPCell cell;
                if(value==null) cell=new PdfPCell(new Phrase("",cellDataFont));
                else
                //Ako je polje tipa Long i ima anotaciju TimestampFormat
                if (field.isAnnotationPresent(TimestampFormat.class)) {
                    String formattedTime = formatTime((Long) value);
                    cell = new PdfPCell(new Phrase(formattedTime, cellDataFont));

                } else
//                Ako je polje tipa LocalDateTime
                    if (field.getType() == LocalDateTime.class) {
                    String formatedTime = formatLocalDateTime((LocalDateTime) value);
                    cell = new PdfPCell(new Phrase(formatedTime, cellDataFont));
                } else
                    //Ako je polje tipa Double i ima anotaciju Round
                    if (field.getType() == Double.class && field.isAnnotationPresent(Round.class)) {
                    String roundedNumber = roundDecimalNumber(field, value);

                    cell = new PdfPCell(new Phrase(roundedNumber, cellDataFont));
                } else
                    //Ako je polje tipa Double i ima anotaciju Round
                    if(field.isAnnotationPresent(ListNames.class)){

                    List<String> names = (List<String>) value;
                    String namesString = String.join(", ", names);
                    cell = new PdfPCell(new Phrase(namesString, cellDataFont));
                }
                    else if(field.getType()==Boolean.class && field.isAnnotationPresent(BooleanReplacer.class)){
                    BooleanReplacer annotation = field.getAnnotation(BooleanReplacer.class);
                    Boolean valueBoolean=(Boolean)  value;
                    String booleanValue = valueBoolean ? annotation.trueValue() : annotation.falseValue();
                    cell = new PdfPCell(new Phrase(booleanValue, cellDataFont));
                    }
                else {
                    cell = new PdfPCell(new Phrase(value != null ? value.toString() : "", cellDataFont));

                }
                table.addCell(cell);
            }
        }
    }

    //Zaokruzi decimalni broj
    protected  String roundDecimalNumber(Field field, Object value) {
        Round annotation = field.getAnnotation(Round.class);
        String formatString = "%." + annotation.roundTo() + "f";
        String roundedNumber = String.format(formatString, value);
        return roundedNumber;
    }

    //Formatiraj LocalDateTime
    protected  String roundDecimalNumber(Object value , int roundTo) {
        String formatString = "%." +roundTo + "f";
        String roundedNumber = String.format(formatString, value);
        return roundedNumber;
    }

    //Formatiraj LocalDateTime
    protected String formatLocalDateTime(LocalDateTime value) {
        return toSerbianTimeZone(value);

    }

    //Formatiraj vreme
    protected String formatTime(Long totalTime) {
        Duration duration = Duration.ofSeconds(totalTime);
        long hours = duration.toHours();
        //+1 za srbiju jer jer utc
        long minutes = duration.minusHours(hours).toMinutes();
        long seconds = duration.minusHours(hours).minusMinutes(minutes).getSeconds();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    //Kreiraj celiju
    protected PdfPCell createCell(String content, Font font,Color color) {
        PdfPCell cell = new PdfPCell(new Paragraph(content,font));
        cell.setBackgroundColor(color); // RGB values for blue
        cell.setPadding(8);

        return cell;
    }

    //Dodaj celiju u tabelu
    protected  void addCellToTable(String string, Font cellTitleFont, PdfPTable tableSum) {

        PdfPCell cell = new PdfPCell(new Phrase(string, cellTitleFont));
        tableSum.addCell(cell);
    }

    //Dodaj praznu celiju u tabelu
    protected  void addEmptyCellToTable(  PdfPTable tableSum) {

        PdfPCell cell = new PdfPCell(new Phrase(""));
        tableSum.addCell(cell);
    }

    //Kreiraj tabelu
    protected  <T> PdfPTable create(Class<T> myclass, int count, Font cellTitleFont, Color bcgColor) {
        PdfPTable table = new PdfPTable(count);

        for (Field field : myclass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PdfTableElement.class)) {
                PdfTableElement annotation = field.getAnnotation(PdfTableElement.class);
                table.addCell(createCell(annotation.title(), cellTitleFont, bcgColor));
                field.setAccessible(true);
            }
        }
        return table;
    }
}
