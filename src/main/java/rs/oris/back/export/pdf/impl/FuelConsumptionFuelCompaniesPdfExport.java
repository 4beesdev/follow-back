package rs.oris.back.export.pdf.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import rs.oris.back.export.annotations.PdfTableElement;
import rs.oris.back.export.pdf.PdfExporter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class FuelConsumptionFuelCompaniesPdfExport extends PdfExporter {
    private LocalDateTime to;
    private LocalDateTime from;

    private Double maxSpeed;
    private Double maxWheelSpin;
    public FuelConsumptionFuelCompaniesPdfExport(LocalDateTime to, LocalDateTime from, Double maxSpeed, Double maxWheelSpin) {
        super(to, from);
        this.to=to;
        this.from=from;
        this.maxSpeed=maxSpeed;
        this.maxWheelSpin=maxWheelSpin;
    }

    @Override
    public <T> byte[] export(List<T> content, Class<T> myclass, String title) {
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
            Paragraph time = new Paragraph("Od: "+from.format(formatter)+"    Do: "+to.format(formatter));
            time.setAlignment(Element.ALIGN_CENTER);
            document.add(time);
            //Dodaj prazan red
            document.add(new Paragraph("\n"));

            //Dodaj limits
            Paragraph limits = new Paragraph("Granična brzina: "+roundDecimalNumber(maxSpeed,0)+"  -  Granični broj obrtaja: "+roundDecimalNumber(maxWheelSpin,0));
            limits.setAlignment(Element.ALIGN_CENTER);
            document.add(limits);
            document.add(new Paragraph("\n"));

            // Kreiraj tabelu
            long count = Arrays.stream(myclass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(PdfTableElement.class)).count();
            PdfPTable table = new PdfPTable((int) count);
            Font cellTitleFont = new Font(Font.TIMES_ROMAN, 8, Font.BOLD, Color.WHITE);

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
}
