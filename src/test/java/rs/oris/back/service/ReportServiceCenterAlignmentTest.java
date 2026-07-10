package rs.oris.back.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import rs.oris.back.domain.Ipp;

/**
 * Proverava da su u exportovanom izvestaju o predjenom putu sve celije tabele
 * (od reda sa nazivima kolona do kraja) centrirane, a da header blok iznad
 * tabele (Kompanija/Generisano/Period) NIJE centriran.
 */
public class ReportServiceCenterAlignmentTest {

    @Test
    public void ippExportCentersAllTableCells() throws Exception {
        ReportService service = new ReportService();
        ArrayList<Ipp> list = new ArrayList<>();
        list.add(new Ipp("SA 180-RD", "Express", "Teretno", "Renault", 20.43, 84086, 303, 2009, 100, 19.95));
        list.add(new Ipp("SA 181-GN", "Fiorino", "Teretno", "Fiat", 49.22, 77247, 2566, 6571, 100, 16.18));

        byte[] encoded = service.ippExport(list, 1,
                new Timestamp(1752098400000L), new Timestamp(1752184800000L), "Test Firma");
        assertNotNull(encoded);

        byte[] xlsx = Base64.getDecoder().decode(encoded);
        XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(xlsx));
        try {
            XSSFSheet sheet = wb.getSheetAt(0);

            // nadji red sa nazivima kolona ("Reg." u koloni 0)
            int tableHeaderRow = -1;
            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row != null && row.getCell(0) != null
                        && "Reg.".equals(row.getCell(0).toString())) {
                    tableHeaderRow = r;
                    break;
                }
            }
            assertTrue("Red sa nazivima kolona nije pronadjen", tableHeaderRow >= 0);

            // sve celije tabele (nazivi kolona + podaci) su centrirane
            for (int r = tableHeaderRow; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                for (Cell cell : row) {
                    assertEquals("Celija (" + r + "," + cell.getColumnIndex() + ") nije centrirana",
                            HorizontalAlignment.CENTER,
                            cell.getCellStyle().getAlignmentEnum());
                }
            }

            // header blok iznad tabele nije centriran ("Kompanija:" ostaje levo)
            boolean foundKompanija = false;
            for (int r = 0; r < tableHeaderRow; r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                for (Cell cell : row) {
                    if (cell.toString().startsWith("Kompanija")) {
                        foundKompanija = true;
                        assertNotEquals("Header blok ne sme biti centriran",
                                HorizontalAlignment.CENTER,
                                cell.getCellStyle().getAlignmentEnum());
                    }
                }
            }
            assertTrue("Header blok (Kompanija:) nije pronadjen", foundKompanija);
        } finally {
            wb.close();
        }
    }
}
