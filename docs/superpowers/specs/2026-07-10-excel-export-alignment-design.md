# Ujednačeno poravnanje kolona u exportovanim izveštajima (Excel/PDF)

**Datum:** 10.07.2026.
**Status:** odobren dizajn, čeka implementacioni plan

## Problem

U exportovanim Excel izveštajima kolone sa podacima imaju različito horizontalno
poravnanje. Primer iz „Izveštaja o pređenom putu": kolona „Pređeni put" ima stil sa
`HorizontalAlignment.RIGHT`, dok je „Vreme vožnje" string bez dodeljenog stila
(default: levo). Desno poravnata vrednost `20.43` i levo poravnata `0:33:29` stoje
jedna uz drugu i vizuelno se spajaju u `20.43 0:33:29`.

Isti copy-paste šablon (brojevi desno, stringovi bez stila levo) postoji u svim
export metodama u `ReportService`.

## Odluke (potvrdio korisnik)

1. **Poravnanje:** sve ćelije sa podacima se centriraju (`HorizontalAlignment.CENTER`),
   isto kao zaglavlja kolona.
2. **Obim:** sve export metode u `ReportService`.

## Rešenje

### Novi helper u `ReportService`

```java
private void centerDataCells(XSSFSheet sheet, int fromRow)
```

- Prolazi kroz sve redove od `fromRow` (red sa nazivima kolona tabele) do
  `sheet.getLastRowNum()` i svakoj ćeliji postavlja centrirano poravnanje.
- POI stilovi su deljeni objekti, pa se ne sme mutirati postojeći stil ćelije —
  helper za svaki zatečeni stil pravi centrirani klon i kešira ga
  (`Map<XSSFCellStyle, XSSFCellStyle>`), da centriranje ne bi „procurelo" u
  header blok izveštaja i da se ne bi generisale hiljade stilova (Excel limit).
- Ćelije bez dodeljenog stila (vraćaju default stil workbooka) dobijaju keširani
  centrirani klon default stila.
- Stilovi koji su već centrirani se preskaču.
- `null` redovi se preskaču.

### Pozivi helpera

Svaka export metoda u `ReportService` koja gradi Excel workbook dobija jedan
poziv `centerDataCells(sheet, <početak tabele>)` neposredno pre upisa workbooka
u izlazni stream:

`routeExport`, `routeExport2`, `speedExport`, `speedExport2`, `ippExport`,
`ippmExport`, `tempExport`, `interventionExport`, `fuelForVehiclesExport`,
`registrationsExport`, `exportRouteIskiakanje`, `geozoneExcport`,
`standingExport`, `greenExport`.

- `<početak tabele>` je indeks reda u kome su nazivi kolona; svaka metoda taj
  indeks već ima (vrednost `rowCount` posle `addExcelReportHeader`, odnosno
  ekvivalent u metodama koje header grade ručno). Tačne vrednosti po metodi
  utvrđuje implementacioni plan.
- Izveštaji koji prave više sheet-ova dobijaju poziv po sheet-u.

### Šta se NE menja

- Header blok izveštaja (logo, naslov, Kompanija/Generisano/Period, warning
  poruka) — ostaje levo poravnat.
- Brojevni formati (`0.00`), boje, fontovi, borderi — helper menja isključivo
  horizontalno poravnanje.

### PDF

PDF varijanta se generiše konverzijom istog workbooka (Gotenberg,
`getPdf(...)`), pa centriranje automatski važi i za PDF. Nema posebne izmene.

## Van obima (poznat follow-up)

Druga familija exportera u `rs.oris.back.export.xml` (`XlsExporter` + 8
naslednika: sensors, monthly, fuel companies, daily movement, effective working
hours, driver relations...) piše skoro sve vrednosti kao stringove, pa je
poravnanje tamo pretežno ujednačeno (levo). Ako se i tamo poželi centriranje,
isti helper pristup se može primeniti naknadno.

## Rizici / ivice

- Izveštaji sa merged ćelijama i „Ukupno" redovima: centriranje se primenjuje i
  na njih (deo su tabele) — vizuelno konzistentno, bez posebnih slučajeva.
- Broj stilova: keš garantuje najviše po jedan novi stil po zatečenom stilu.

## Verifikacija

1. `mvn compile` prolazi.
2. Programska provera: generisati `ippExport` (export=1) sa test podacima,
   otvoriti dobijeni workbook kroz POI i potvrditi da sve ćelije od reda sa
   nazivima kolona nadole imaju `HorizontalAlignment.CENTER`, a da ćelije
   header bloka iznad tabele nisu promenjene.
3. Ručna kontrola izgleda bar jednog XLSX fajla (ipp) u Excelu/LibreOffice.
