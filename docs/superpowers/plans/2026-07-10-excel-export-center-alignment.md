# Excel Export Center Alignment Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Sve ćelije tabela u Excel/PDF export izveštajima dobijaju centrirano horizontalno poravnanje, da se vrednosti susednih kolona (npr. „Pređeni put" 20.43 i „Vreme vožnje" 0:33:29) više ne spajaju vizuelno.

**Architecture:** Jedan novi privatni helper `centerDataCells(XSSFSheet, int fromRow)` u `ReportService` koji od reda sa nazivima kolona do kraja sheet-a zamenjuje stil svake ćelije centriranim klonom (klonovi keširani po indeksu izvornog stila — deljeni POI stilovi se ne mutiraju, pa header blok iznad tabele ostaje netaknut). Svaka od 13 aktivnih export metoda dobija: (1) hvatanje indeksa reda gde tabela počinje u `int tableStartRow`, (2) jedan poziv helpera pre upisa/PDF konverzije. `tempExport` se preskače (vraća `null`, nema workbook). PDF automatski nasleđuje centriranje jer `getPdf` konvertuje isti workbook.

**Tech Stack:** Java 8, Spring Boot, Apache POI (efektivno 3.16 — vidi Global Constraints), JUnit 4 (preko spring-boot-starter-test), Maven.

**Spec:** `docs/superpowers/specs/2026-07-10-excel-export-alignment-design.md`

## Global Constraints

- Repo: `c:\Users\User\Documents\GitHub\oris-backend2\oris-backend`, branch `refresh`. Svi fajl-putevi ispod su relativni na ovaj koren.
- Java 8 (`<java.version>1.8</java.version>`). Bez lambda-streamova gde postojeći kod koristi klasične petlje — prati stil okolnog koda.
- POI: pom deklariše i poi 5.0.0 i poi 3.16, ali poi-ooxml je 3.16 i postojeći kod koristi POI 3.x API (`Cell.CELL_TYPE_STRING`), pa je efektivna verzija 3.16. U NOVOM kodu koristi samo API koji postoji u 3.16: `setAlignment(HorizontalAlignment)`, `cloneStyleFrom(...)`, `getAlignmentEnum()` (u testu). NE koristi `getAlignment()` za čitanje enum-a (u 3.16 vraća `short`).
- Komentari u kodu na srpskom, kao ostatak fajla. Ne reformatiraj postojeći kod — samo umetni linije.
- `ReportService.java` ima ~4400 linija; brojevi linija u ovom planu su orijentacioni (pre izmena). Edit-uj po navedenim anchor snippetima, ne po brojevima linija.
- Svaki commit se završava sa: `Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>`
- Maven build okruženje nije potvrđeno na ovoj mašini. Prvi zadatak proverava `mvn -v`; ako `mvn test` padne zbog okruženja (npr. Java 17 vs 1.8 toolchain problem, poznat iz sestrinskog repo-a), fallback je opisan u Task 8.

---

### Task 1: Helper `centerDataCells` + test + ippExport (izveštaj sa slike)

**Files:**
- Modify: `src/main/java/rs/oris/back/service/ReportService.java` (helper pre `getPdf` metode ~linija 2572; dva umetanja u `ippExport` ~linije 2330 i 2422)
- Test: `src/test/java/rs/oris/back/service/ReportServiceCenterAlignmentTest.java` (novi fajl; `src/test` direktorijum još ne postoji — kreiraće ga Write)

**Interfaces:**
- Consumes: postojeći `addExcelReportHeader(...)` (vraća indeks sledećeg slobodnog reda = red gde tabela počinje), postojeći `ippExport(ArrayList<Ipp>, int, Timestamp, Timestamp, String)`.
- Produces: `private void centerDataCells(XSSFSheet sheet, int fromRow)` — SVI kasniji taskovi zovu tačno ovaj potpis. Semantika: centrira horizontalno sve ćelije od `fromRow` (uključivo) do `sheet.getLastRowNum()`; redovi `null` se preskaču; stilovi se NE mutiraju nego klonovi keširani po `style.getIndex()`.

- [ ] **Step 1: Proveri build okruženje**

Run: `mvn -v` (u korenu repo-a)
Expected: ispis Maven + Java verzije. Zabeleži Java verziju za Task 8. Ako `mvn` ne postoji, stani i javi korisniku.

- [ ] **Step 2: Napiši failing test**

Kreiraj `src/test/java/rs/oris/back/service/ReportServiceCenterAlignmentTest.java`:

```java
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
```

- [ ] **Step 3: Pokreni test — mora da PADNE**

Run: `mvn -q test -Dtest=ReportServiceCenterAlignmentTest`
Expected: FAIL na assertu „Celija (r,c) nije centrirana" (npr. „Vreme vožnje" string ćelija ima GENERAL/LEFT poravnanje).
Ako umesto toga padne zbog okruženja (compiler/toolchain greška, ne assert), vidi fallback u Task 8 Step 2 i koristi ga za SVE test-korake u planu.

- [ ] **Step 4: Dodaj helper u ReportService**

U `src/main/java/rs/oris/back/service/ReportService.java` nađi ovaj anchor (početak `getPdf` metode):

```java
    private byte[] getPdf(XSSFWorkbook workbook, boolean orientation) throws Exception {
```

i umetni NEPOSREDNO IZNAD njega:

```java
    /**
     * Centrira horizontalno sve celije tabele izvestaja, od reda sa nazivima
     * kolona (fromRow) do poslednjeg reda. POI stilovi su deljeni izmedju
     * celija pa se ne smeju mutirati — za svaki zateceni stil pravi se
     * centrirani klon i kesira po indeksu stila, da se ne bi promenio header
     * blok iznad tabele i da se ne bi napravilo previse stilova.
     */
    private void centerDataCells(XSSFSheet sheet, int fromRow) {
        XSSFWorkbook wb = sheet.getWorkbook();
        Map<Short, XSSFCellStyle> centeredStyles = new HashMap<>();
        for (int r = fromRow; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            for (Cell cell : row) {
                XSSFCellStyle source = (XSSFCellStyle) cell.getCellStyle();
                XSSFCellStyle centered = centeredStyles.get(source.getIndex());
                if (centered == null) {
                    centered = wb.createCellStyle();
                    centered.cloneStyleFrom(source);
                    centered.setAlignment(HorizontalAlignment.CENTER);
                    centeredStyles.put(source.getIndex(), centered);
                }
                cell.setCellStyle(centered);
            }
        }
    }

```

Napomena: `Map`/`HashMap` su već pokriveni postojećim `import java.util.*;` — ne treba novi import. `Cell`, `Row`, `HorizontalAlignment` dolaze iz postojećeg `import org.apache.poi.ss.usermodel.*;`.

- [ ] **Step 5: Poveži ippExport**

Edit 1 — capture. Anchor (unikatan, naslov „Izveštaj o pređenom putu"):

```java
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o pređenom putu", firmName, period, warningMessage);
```

zameni sa:

```java
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o pređenom putu", firmName, period, warningMessage);
        int tableStartRow = rowCount;
```

Edit 2 — poziv. Anchor (autoSize petlja `< 12` postoji samo u ippExport):

```java
        for (int i = 0; i < 12; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception e) {
            }
        }
```

zameni sa:

```java
        for (int i = 0; i < 12; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception e) {
            }
        }

        centerDataCells(sheet, tableStartRow);
```

(Poziv ide pre `ByteArrayOutputStream bos`/`getPdf` — u ippExport se bos upisuje PRE `if (export == 2)` grane, pa ovim pokrivamo i XLSX i PDF putanju.)

- [ ] **Step 6: Pokreni test — mora da PROĐE**

Run: `mvn -q test -Dtest=ReportServiceCenterAlignmentTest`
Expected: PASS (BUILD SUCCESS, Tests run: 1, Failures: 0).

- [ ] **Step 7: Commit**

```bash
git add src/main/java/rs/oris/back/service/ReportService.java src/test/java/rs/oris/back/service/ReportServiceCenterAlignmentTest.java
git commit -m "Fix: centrirano poravnanje svih kolona u ipp exportu + centerDataCells helper

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 2: routeExport + routeExport2 (Izveštaj o relacijama vozila)

**Files:**
- Modify: `src/main/java/rs/oris/back/service/ReportService.java` (routeExport ~672 i ~1052; routeExport2 ~1140 i ~1489)

**Interfaces:**
- Consumes: `centerDataCells(XSSFSheet sheet, int fromRow)` iz Task 1.
- Produces: ništa novo.

- [ ] **Step 1: Capture u routeExport**

Anchor (BEZ `warningMessage` parametra — po tome se razlikuje od routeExport2):

```java
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o relacijama vozila", firmName, period);
```

zameni sa:

```java
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o relacijama vozila", firmName, period);
        int tableStartRow = rowCount;
```

- [ ] **Step 2: Capture u routeExport2**

Anchor (SA `warningMessage`):

```java
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o relacijama vozila", firmName, period, warningMessage);
```

zameni sa:

```java
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o relacijama vozila", firmName, period, warningMessage);
        int tableStartRow = rowCount;
```

- [ ] **Step 3: Poziv u OBE metode (replace_all)**

Prvo proveri broj pojavljivanja — Grep pattern `Ako je export 2 onda se radi pdf export` u `ReportService.java` mora da vrati TAČNO 2 linije (u routeExport i routeExport2). Ako vrati više, uradi dva pojedinačna Edit-a sa širim kontekstom umesto replace_all.

Zatim Edit sa `replace_all: true`. Anchor:

```java
        //Ako je export 2 onda se radi pdf export
        if (export == 2) {
            return getPdf(workbook, true);
        }
```

zameni sa:

```java
        centerDataCells(sheet, tableStartRow);

        //Ako je export 2 onda se radi pdf export
        if (export == 2) {
            return getPdf(workbook, true);
        }
```

- [ ] **Step 4: Kompajliraj**

Run: `mvn -q -DskipTests compile`
Expected: BUILD SUCCESS (bez grešaka — obe metode imaju `sheet` i `tableStartRow` u scope-u).

- [ ] **Step 5: Commit**

```bash
git add src/main/java/rs/oris/back/service/ReportService.java
git commit -m "Fix: centrirano poravnanje kolona u route exportima (relacije vozila)

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 3: speedExport + speedExport2 (Izveštaj o prekoračenju brzine)

**Files:**
- Modify: `src/main/java/rs/oris/back/service/ReportService.java` (speedExport ~1815 i ~1919; speedExport2 ~2161 i ~2244)

**Interfaces:**
- Consumes: `centerDataCells(XSSFSheet sheet, int fromRow)` iz Task 1. NAPOMENA: u ovim metodama sheet promenljiva se zove `sh`, workbook `wb`.
- Produces: ništa novo.

- [ ] **Step 1: Capture u speedExport**

Anchor (samo speedExport zove addExcelReportHeader sa ovim naslovom):

```java
        int rIdx = addExcelReportHeader(wb, sh, "Izveštaj o prekoračenju brzine", firmName, period);
```

zameni sa:

```java
        int rIdx = addExcelReportHeader(wb, sh, "Izveštaj o prekoračenju brzine", firmName, period);
        int tableStartRow = rIdx;
```

- [ ] **Step 2: Poziv u speedExport**

Anchor (komentar `// Izvoz` postoji samo u speedExport):

```java
        // Izvoz
        if (export == 2) {
            return getPdf(wb, true);
        }
```

zameni sa:

```java
        centerDataCells(sh, tableStartRow);

        // Izvoz
        if (export == 2) {
            return getPdf(wb, true);
        }
```

- [ ] **Step 3: Capture u speedExport2**

speedExport2 gradi header ručno (logo + naslov + meta redovi), pa se tableStartRow hvata neposredno pre kreiranja reda sa nazivima kolona. Anchor:

```java
        int columnCount = 8;
        Row h = sh.createRow(rIdx++);
```

zameni sa:

```java
        int columnCount = 8;
        int tableStartRow = rIdx;
        Row h = sh.createRow(rIdx++);
```

- [ ] **Step 4: Poziv u speedExport2**

Anchor (komentar `===== IZVOZ =====` postoji samo u speedExport2):

```java
        // ===== IZVOZ =====
        if (export == 2) {
            return getPdf(wb, true); // tvoja postojeća PDF rutina
        }
```

zameni sa:

```java
        centerDataCells(sh, tableStartRow);

        // ===== IZVOZ =====
        if (export == 2) {
            return getPdf(wb, true); // tvoja postojeća PDF rutina
        }
```

- [ ] **Step 5: Kompajliraj i commituj**

Run: `mvn -q -DskipTests compile` → BUILD SUCCESS

```bash
git add src/main/java/rs/oris/back/service/ReportService.java
git commit -m "Fix: centrirano poravnanje kolona u speed exportima (prekoracenje brzine)

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 4: ippmExport (Mesečni izveštaj o pređenom putu)

**Files:**
- Modify: `src/main/java/rs/oris/back/service/ReportService.java` (~2812 i ~2920)

**Interfaces:**
- Consumes: `centerDataCells(XSSFSheet sheet, int fromRow)` iz Task 1.
- Produces: ništa novo.

VAŽNO: u ippmExport se tableStartRow NE hvata na `addExcelReportHeader` povratnoj vrednosti — između header-a i tabele su meta redovi „Tip izveštaja:"/„RADNO VREME:" koji MORAJU ostati levo poravnati. Capture ide neposredno pre reda „Reg." + brojevi dana.

- [ ] **Step 1: Capture**

Anchor (`row = sheet.createRow(++rowCount);` sa PRE-inkrementom i bez `Row` deklaracije — jedinstveno u kombinaciji sa "Reg." ispod):

```java
        cellCount = 0;
        row = sheet.createRow(++rowCount);
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Reg.");
```

zameni sa:

```java
        cellCount = 0;
        int tableStartRow = rowCount + 1;
        row = sheet.createRow(++rowCount);
        row.createCell(cellCount);
        row.getCell(cellCount).setCellStyle(upperStyle);
        row.getCell(cellCount++).setCellValue("Reg.");
```

(`++rowCount` pre-inkrementira, pa je red tabele na `rowCount + 1` u odnosu na vrednost PRE te linije.)

- [ ] **Step 2: Poziv**

Anchor (kraj zakomentarisanog bloka odmah uz `if (export == 2)` BEZ praznog reda i BEZ komentara između — po tome se razlikuje od route metoda):

```java
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        if (export == 2) {
            return getPdf(workbook, true);
        }
```

zameni sa:

```java
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        centerDataCells(sheet, tableStartRow);

        if (export == 2) {
            return getPdf(workbook, true);
        }
```

- [ ] **Step 3: Kompajliraj i commituj**

Run: `mvn -q -DskipTests compile` → BUILD SUCCESS

```bash
git add src/main/java/rs/oris/back/service/ReportService.java
git commit -m "Fix: centrirano poravnanje kolona u ippm exportu (mesecni predjeni put)

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 5: interventionExport + fuelForVehiclesExport + registrationsExport

**Files:**
- Modify: `src/main/java/rs/oris/back/service/ReportService.java` (intervention ~3088/~3145; fuel ~3260/~3297; registrations ~3415/~3463)

**Interfaces:**
- Consumes: `centerDataCells(XSSFSheet sheet, int fromRow)` iz Task 1.
- Produces: ništa novo.

Ove tri metode grade sopstveni mali header (naslov + OD/DO ili period red) — capture ide neposredno pre reda sa nazivima kolona.

- [ ] **Step 1: interventionExport — capture**

Anchor:

```java
        // Header kolone
        row = sheet.createRow(++rowCount);
        String[] headers = {"Registarski broj", "Proizvodjac/Model", "Servisna lokacija", "Datum", "Opis", "Cena", "Napomena"};
```

zameni sa:

```java
        // Header kolone
        int tableStartRow = rowCount + 1;
        row = sheet.createRow(++rowCount);
        String[] headers = {"Registarski broj", "Proizvodjac/Model", "Servisna lokacija", "Datum", "Opis", "Cena", "Napomena"};
```

- [ ] **Step 2: interventionExport — poziv**

Anchor (zakomentarisani for-blok pa prazan red pa `if` — jedinstveno za intervention):

```java
//        }

        if (export == 2) {
            return getPdf(workbook, true);
        }
```

zameni sa:

```java
//        }

        centerDataCells(sheet, tableStartRow);

        if (export == 2) {
            return getPdf(workbook, true);
        }
```

- [ ] **Step 3: fuelForVehiclesExport — capture**

Anchor:

```java
        Row headerRow = sheet.createRow(rowCount++);
        String[] headers = {"Vozilo", "Gorivo", "Količina (l)", "Gorivna kompanija", "Iznos", "Gorivna stanica", "Datum", "Vozač", "Broj računa", "Kilometraža"};
```

zameni sa:

```java
        int tableStartRow = rowCount;
        Row headerRow = sheet.createRow(rowCount++);
        String[] headers = {"Vozilo", "Gorivo", "Količina (l)", "Gorivna kompanija", "Iznos", "Gorivna stanica", "Datum", "Vozač", "Broj računa", "Kilometraža"};
```

- [ ] **Step 4: fuelForVehiclesExport — poziv**

Anchor (autoSize petlja sa `headers.length` i `catch (Exception ignored) {}` u više linija — jedinstveno za fuel):

```java
        for (int i = 0; i < headers.length; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception ignored) {}
        }
```

zameni sa:

```java
        for (int i = 0; i < headers.length; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception ignored) {}
        }

        centerDataCells(sheet, tableStartRow);
```

(Poziv je pre `ByteArrayOutputStream bos`/try bloka koji sadrži i getPdf granu — pokriva obe putanje.)

- [ ] **Step 5: registrationsExport — capture**

Anchor:

```java
        // Header kolone
        row = sheet.createRow(++rowCount);
        String[] headers = {"Vozilo", "Datum registracije", "Datum isteka", "Odgovorna osoba", "Iznos registracije", "Napomena"};
```

zameni sa:

```java
        // Header kolone
        int tableStartRow = rowCount + 1;
        row = sheet.createRow(++rowCount);
        String[] headers = {"Vozilo", "Datum registracije", "Datum isteka", "Odgovorna osoba", "Iznos registracije", "Napomena"};
```

- [ ] **Step 6: registrationsExport — poziv**

Anchor (komentar `// Ako je PDF eksport` postoji samo u registrations):

```java
        // Ako je PDF eksport
        if (export == 2) {
            return getPdf(workbook, true);
        }
```

zameni sa:

```java
        centerDataCells(sheet, tableStartRow);

        // Ako je PDF eksport
        if (export == 2) {
            return getPdf(workbook, true);
        }
```

- [ ] **Step 7: Kompajliraj i commituj**

Run: `mvn -q -DskipTests compile` → BUILD SUCCESS

```bash
git add src/main/java/rs/oris/back/service/ReportService.java
git commit -m "Fix: centrirano poravnanje kolona u intervention/fuel/registrations exportima

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 6: exportRouteIskiakanje + geozoneExcport

**Files:**
- Modify: `src/main/java/rs/oris/back/service/ReportService.java` (iskakanje ~3581/~3695; geozone ~3899/~3971)

**Interfaces:**
- Consumes: `centerDataCells(XSSFSheet sheet, int fromRow)` iz Task 1. Geozone koristi promenljive `wb`/`sh`.
- Produces: ništa novo.

Napomena za iskakanje: tabela sadrži i merged naslovne redove po vozilu („ŠA 180-RD - Renault / Express") — i oni će biti centrirani, što je vizuelno u redu (naslov preko cele širine tabele).

- [ ] **Step 1: exportRouteIskiakanje — capture**

Anchor:

```java
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o povredama ruta", firmName, period);
```

zameni sa:

```java
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o povredama ruta", firmName, period);
        int tableStartRow = rowCount;
```

- [ ] **Step 2: exportRouteIskiakanje — poziv**

Anchor (`eid == 2` + `getPdf(workbook, ...)` + odmah `ByteArrayOutputStream` bez praznog reda — jedinstveno za iskakanje):

```java
        if (eid == 2) {
            return getPdf(workbook, true);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
```

zameni sa:

```java
        centerDataCells(sheet, tableStartRow);

        if (eid == 2) {
            return getPdf(workbook, true);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
```

- [ ] **Step 3: geozoneExcport — capture**

Anchor:

```java
        Row hr = sh.createRow(rIdx++);
        hr.setHeightInPoints(30);
```

zameni sa:

```java
        int tableStartRow = rIdx;
        Row hr = sh.createRow(rIdx++);
        hr.setHeightInPoints(30);
```

- [ ] **Step 4: geozoneExcport — poziv**

Anchor (`eid == 2` + `getPdf(wb, ...)` — geozone je jedina eid-metoda sa `wb`):

```java
        if (eid == 2) {
            return getPdf(wb, true);
        }
```

zameni sa:

```java
        centerDataCells(sh, tableStartRow);

        if (eid == 2) {
            return getPdf(wb, true);
        }
```

- [ ] **Step 5: Kompajliraj i commituj**

Run: `mvn -q -DskipTests compile` → BUILD SUCCESS

```bash
git add src/main/java/rs/oris/back/service/ReportService.java
git commit -m "Fix: centrirano poravnanje kolona u route-iskakanje i geozone exportima

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 7: standingExport + greenExport

**Files:**
- Modify: `src/main/java/rs/oris/back/service/ReportService.java` (standing ~4093/~4245; green ~4304/~4357)

**Interfaces:**
- Consumes: `centerDataCells(XSSFSheet sheet, int fromRow)` iz Task 1.
- Produces: ništa novo.

Napomena za standing: meta redovi „Minimalno stajanje/mirovanje" idu POSLE addExcelReportHeader i moraju ostati levi — capture ide neposredno pre reda sa nazivima kolona. Kod je uvučen 12 mesta (unutar try bloka).

- [ ] **Step 1: standingExport — capture**

Anchor (obrati pažnju na uvlačenje od 12 mesta):

```java
            sheet.setColumnWidth(0, 5000);
            Row row = sheet.createRow(rowCount++);
```

zameni sa:

```java
            sheet.setColumnWidth(0, 5000);
            int tableStartRow = rowCount;
            Row row = sheet.createRow(rowCount++);
```

- [ ] **Step 2: standingExport — poziv**

Anchor (komentar `//Ako je eid 2 pretvori u pdf` postoji samo u standing):

```java
            if (eid == 2) {
                //Ako je eid 2 pretvori u pdf
                return getPdf(workbook, true);
            }
```

zameni sa:

```java
            centerDataCells(sheet, tableStartRow);

            if (eid == 2) {
                //Ako je eid 2 pretvori u pdf
                return getPdf(workbook, true);
            }
```

- [ ] **Step 3: greenExport — capture**

Anchor:

```java
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o sigurnoj vožnji", firmName, period);
```

zameni sa:

```java
        int rowCount = addExcelReportHeader(workbook, sheet, "Izveštaj o sigurnoj vožnji", firmName, period);
        int tableStartRow = rowCount;
```

- [ ] **Step 4: greenExport — poziv**

Anchor (aktivna, NEzakomentarisana autoSize petlja `< 7` — u standing metodi ista petlja postoji ali zakomentarisana sa `//` prefiksom, pa je ovaj anchor jedinstven):

```java
        for (int i = 0; i < 7; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception e) {
            }
        }
```

zameni sa:

```java
        for (int i = 0; i < 7; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception e) {
            }
        }

        centerDataCells(sheet, tableStartRow);
```

- [ ] **Step 5: Kompajliraj i commituj**

Run: `mvn -q -DskipTests compile` → BUILD SUCCESS

```bash
git add src/main/java/rs/oris/back/service/ReportService.java
git commit -m "Fix: centrirano poravnanje kolona u standing i green exportima

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 8: Finalna verifikacija

**Files:**
- Read-only provere; nema izmena koda (osim ako verifikacija otkrije propust).

**Interfaces:**
- Consumes: sve iz prethodnih taskova.
- Produces: potvrda da je spec ispunjen.

- [ ] **Step 1: Prebroj call site-ove**

Run (Grep u `ReportService.java`): pattern `centerDataCells\(`
Expected: TAČNO 14 pogodaka — 1 deklaracija helpera + 13 poziva (ippExport, routeExport, routeExport2, speedExport, speedExport2, ippmExport, interventionExport, fuelForVehiclesExport, registrationsExport, exportRouteIskiakanje, geozoneExcport, standingExport, greenExport). `tempExport` NEMA poziv (vraća null, nema workbook) — to je očekivano i dokumentovano u spec-u.

- [ ] **Step 2: Pun build + test**

Run: `mvn -q -DskipTests compile` → BUILD SUCCESS
Run: `mvn -q test -Dtest=ReportServiceCenterAlignmentTest` → Tests run: 1, Failures: 0

Fallback ako `mvn test` ne radi zbog okruženja (surefire/toolchain problem — poznat obrazac iz sestrinskog repo-a): kompajliraj test ručno i pusti JUnitCore direktno:

```powershell
mvn -q -DskipTests compile
mvn -q dependency:build-classpath "-Dmdep.outputFile=cp.txt"
javac -encoding UTF-8 -cp "target/classes;$(Get-Content cp.txt)" -d target/test-classes src/test/java/rs/oris/back/service/ReportServiceCenterAlignmentTest.java
java -cp "target/classes;target/test-classes;$(Get-Content cp.txt)" org.junit.runner.JUnitCore rs.oris.back.service.ReportServiceCenterAlignmentTest
```

Expected: `OK (1 test)`. Obriši `cp.txt` posle (ne commituj ga).

- [ ] **Step 3: Vizuelni smoke check (opciono ali preporučeno)**

Napiši mali privremeni runner u scratchpad-u (van repo-a) koji pozove `ippExport` sa test podacima i snimi dekodovan XLSX u scratchpad, pa reci korisniku da ga otvori u Excel-u i uporedi sa screenshotom problema (kolone „Pređeni put" i „Vreme vožnje" više se ne spajaju). Alternativno korisnik generiše pravi izveštaj kroz aplikaciju.

- [ ] **Step 4: Završni pregled diff-a**

Run: `git log --oneline` — očekuje se 7 commit-ova iz ovog plana (+ spec/plan commit-ovi).
Run: `git diff <bazni-commit> --stat` — izmenjeni fajlovi su SAMO `ReportService.java`, novi test fajl i docs.
