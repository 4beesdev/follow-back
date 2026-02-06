package rs.oris.back.schedule;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import rs.oris.back.controller.ReportController;
import rs.oris.back.controller.ReportsController;
import rs.oris.back.domain.UserReport;
import rs.oris.back.domain.logs.LoggerFileUtil;
import rs.oris.back.domain.reports.monthly_fuel.MonthlyFuelConsumptionReport;
import rs.oris.back.export.pdf.PdfExporter;
import rs.oris.back.export.pdf.impl.MonthlyReportPdfExporter;
import rs.oris.back.export.xml.XlsExporter;
import rs.oris.back.export.xml.impl.MontlyXlsExporter;
import rs.oris.back.repository.UserReportRepository;
import rs.oris.back.service.AutomaticReportLoggerService;
import rs.oris.back.service.ReportService;
import rs.oris.back.util.DateUtil;

@Component
@Slf4j
public class ScheduledTask {
    @Autowired
    private UserReportRepository userReportRepository;
    //Modul 5
    @Autowired
    private ReportsController reportsController;
    @Autowired
    private ReportController reportController;

    @Autowired
    private AutomaticReportLoggerService automaticReportLoggerService;

    @Autowired
    private ReportService reportService;

    /**
     * automatski se poziva funkcija preko cron vremena prosledjenog u parametru
     * ovo spring sam poziva
     */
//    @Scheduled(cron = "0 0 * ? * *")
//    public void sendReports() {
//        List<UserReport> userReportList = userReportRepository.findAll();
//        Date d = new Date();
//        d.setTime(d.getTime() - 60 * 60 * 1000);
//        d = DateUtil.toSerbianTimeZone(d);
//        Calendar c = DateUtil.toSerbianTimeZone(Calendar.getInstance());
//        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
//
////        for (UserReport userReport : userReportList) {
////
////            if ((hourOfDay) != userReport.getSendingHour()) {
////                if (userReport.getIsSentToday() == null || userReport.getIsSentToday())
////                    updateSentTodayOnUserReport(false, userReport);
////                continue;
////            }
////            if (!userReport.getEmail().contains("@")) {
////                if (userReport.getIsSentToday() == null || userReport.getIsSentToday())
////                    updateSentTodayOnUserReport(false, userReport);
////                continue;
////            }
////
////            long diffMillis = d.getTime() - userReport.getDateFrom().getTime();
////
////            if (diffMillis < 0) {
////                continue;
////            }
////
////            long dayss = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
////            int daysdiff = (int) dayss;
////
////            if ((daysdiff == 0 || daysdiff % userReport.getPeriod() == 0) && userReport.getIsSentToday() != null && !userReport.getIsSentToday()) {
////                sendReport(userReport);
////                updateSentTodayOnUserReport(true, userReport);
////            }
////        }
//
//
//        if (hourOfDay == 0) {
//            for (UserReport ur : userReportList) {
//                if (ur.getIsSentToday() == null || ur.getIsSentToday()) {
//                    updateSentTodayOnUserReport(false, ur);
//                }
//            }
//            return; //novo
//        }
//
////        for (UserReport userReport : userReportList) {
////
////            if (hourOfDay != userReport.getSendingHour()) {
////                continue;
////            }
////
////            if (userReport.getEmail() == null || !userReport.getEmail().contains("@")) {
////                continue;
////            }
////
////            long diffMillis = d.getTime() - userReport.getDateFrom().getTime();
////
////            if (diffMillis < 0) {
////                continue;
////            }
////
////            long dayss = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
////            int daysdiff = (int) dayss;
////
////            boolean alreadySentToday = Boolean.TRUE.equals(userReport.getIsSentToday());
////
////            if ((daysdiff == 0 || daysdiff % userReport.getPeriod() == 0) && !alreadySentToday) {
////                sendReport(userReport);
////                updateSentTodayOnUserReport(true, userReport);
////            }
////        }
//
//        for (UserReport userReport : userReportList) {
//
//            if (hourOfDay != userReport.getSendingHour()) {
//                continue;
//            }
//
//            if (userReport.getEmail() == null || !userReport.getEmail().contains("@")) {
//                continue;
//            }
//
//            long diffMillis = d.getTime() - userReport.getDateFrom().getTime();
//            if (diffMillis < 0) continue;
//
//            long dayss = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
//            int daysdiff = (int) dayss;
//
//            boolean alreadySentToday = Boolean.TRUE.equals(userReport.getIsSentToday());
//
//            if ((daysdiff == 0 || daysdiff % userReport.getPeriod() == 0) && !alreadySentToday) {
//                sendReport(userReport);
//                updateSentTodayOnUserReport(true, userReport);
//            }
//        }
//    }


    @Scheduled(cron = "0 0 * ? * *")
    public void sendReports() {
        log.info("########################################################################################################################################");
        log.info(LocalDateTime.now() + " Scheduled task sendReports started.");
        List<UserReport> userReportList = userReportRepository.findAll();
        Date d = new Date();
        d.setTime(d.getTime() - 60 * 60 * 1000);
        d = DateUtil.toSerbianTimeZone(d);
        Calendar c = DateUtil.toSerbianTimeZone(Calendar.getInstance());
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay == 0) {
            for (UserReport ur : userReportList) {
                if (ur.getIsSentToday() == null || ur.getIsSentToday()) {
                    log.info("########################################################################################################################################");
                    log.info(LocalDateTime.now() + " Resetting isSentToday for UserReport ID: " + ur.getUserReportId());
                    updateSentTodayOnUserReport(false, ur);
                }
            }
            return; //novo
        }

        for (UserReport userReport : userReportList) {

            // Dodato detaljno logovanje za debug
            log.info("########################################################################################################################################");
            log.info(LocalDateTime.now() + " Processing UserReport ID: " + userReport.getUserReportId() + 
                    " - hourOfDay: " + hourOfDay + ", sendingHour from DB: " + userReport.getSendingHour());

            if (hourOfDay != userReport.getSendingHour()) {
                log.info("########################################################################################################################################");
                log.info(LocalDateTime.now() + " Skipping UserReport ID: " + userReport.getUserReportId() + 
                        " due to sending hour mismatch. hourOfDay=" + hourOfDay + ", sendingHour=" + userReport.getSendingHour());
                continue;
            }

            if (userReport.getEmail() == null || !userReport.getEmail().contains("@")) {
                log.info("########################################################################################################################################");
                log.info(LocalDateTime.now() + " Skipping UserReport ID: " + userReport.getUserReportId() + " due to invalid email.");
                continue;
            }

            long diffMillis = d.getTime() - userReport.getDateFrom().getTime();
            if (diffMillis < 0) continue;

            long dayss = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
            int daysdiff = (int) dayss;

            boolean alreadySentToday = Boolean.TRUE.equals(userReport.getIsSentToday());

            log.info("########################################################################################################################################");
            log.info(LocalDateTime.now() + " UserReport ID: " + userReport.getUserReportId() + 
                    " - Checking conditions. daysdiff=" + daysdiff + ", period=" + userReport.getPeriod() + 
                    ", alreadySentToday=" + alreadySentToday + ", condition=" + (daysdiff == 0 || daysdiff % userReport.getPeriod() == 0));

            if ((daysdiff == 0 || daysdiff % userReport.getPeriod() == 0) && !alreadySentToday) {
                log.info("####################################");
                log.info(LocalDateTime.now() + " ===== PRE SLANJA IZVESTAJA - DETALJNI PODACI =====");
                log.info(LocalDateTime.now() + " UserReport ID: " + userReport.getUserReportId());
                log.info(LocalDateTime.now() + " Report Type (report): " + userReport.getReport());
                log.info(LocalDateTime.now() + " Email: " + userReport.getEmail());
                log.info(LocalDateTime.now() + " IMEI array: " + java.util.Arrays.toString(userReport.getImei()));
                log.info(LocalDateTime.now() + " Route IMEI: " + userReport.getRouteImei());
                log.info(LocalDateTime.now() + " Sending Hour: " + userReport.getSendingHour());
                log.info(LocalDateTime.now() + " Period: " + userReport.getPeriod());
                log.info(LocalDateTime.now() + " Days diff: " + daysdiff);
                log.info(LocalDateTime.now() + " Already sent today: " + alreadySentToday);
                log.info(LocalDateTime.now() + " =================================================");
                log.info("####################################");
                log.info(LocalDateTime.now() + " Sending report for UserReport ID: " + userReport.getUserReportId());
                try {
                    sendReport(userReport);
                    updateSentTodayOnUserReport(true, userReport);
                    log.info("####################################");
                    log.info(LocalDateTime.now() + " Successfully sent report for UserReport ID: " + userReport.getUserReportId());
                } catch (Exception e) {
                    log.error("####################################");
                    log.error(LocalDateTime.now() + " Failed to send report for UserReport ID: " + userReport.getUserReportId() + ", error: " + e.getMessage());
                    e.printStackTrace();
                    // Ne postavljaj isSentToday na true ako je greška
                }
            } else {
                log.info("########################################################################################################################################");
                log.info(LocalDateTime.now() + " Skipping UserReport ID: " + userReport.getUserReportId() + 
                        " - Period condition not met or already sent today. daysdiff=" + daysdiff + 
                        ", period=" + userReport.getPeriod() + ", alreadySentToday=" + alreadySentToday);
            }
        }
    }

    //TEST SCHEDULER
    //    @Scheduled(cron = "0 * * ? * *") // Executes every minute
    //    public void sendReportsForTesting() {
    //        List<UserReport> userReportList = userReportRepository.findAll();
    //        Date d = new Date();
    //        d.setTime(d.getTime() - 60 * 60 * 1000); // 1 hour ago
    //        d = DateUtil.toSerbianTimeZone(d);
    //        Calendar c = DateUtil.toSerbianTimeZone(Calendar.getInstance());
    //        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
    //
    //
    //
    //        for (UserReport userReport : userReportList) {
    //            // Filter for the specific email
    //            System.out.println("-----------------------");
    //            System.out.println(userReport.getEmail());
    //            //if all imei in user report are null
    //            for (String s : userReport.getImei()) {
    //                System.out.println("IMEI: "+s);
    //            }
    //
    //
    //            if (!"jstrahinja@gmail.com".equalsIgnoreCase(userReport.getEmail())) {
    //
    //                continue;
    //            }
    //
    //            long diff = Math.abs(d.getTime() - userReport.getDateFrom().getTime());
    //            long dayss = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    //
    //            int daysdiff = (int) dayss;
    //            if ((daysdiff == 0 || daysdiff % userReport.getPeriod() == 0)) {
    //                sendReport(userReport);
    //                // No update to sentToday for testing
    //            }
    //        }
    //    }

    public void updateSentTodayOnUserReport(Boolean isSentToday, UserReport userReport) {
        log.info("####################################");
        log.info(LocalDateTime.now() + " Updating isSentToday to " + isSentToday + " for UserReport ID: " + userReport.getUserReportId());
        userReport.setIsSentToday(isSentToday);
        userReportRepository.save(userReport);
    }

    //1 izvestaj o predjenom putu - ipp
    //2 izvestaj o predjenom putu mesecni - ippm
    //3 izvestaj o predjenom putu mesecni van i u radnom vremenu - ippmvrv
    //4 izvestaj o prekoracenju brzine -ipb
    //5 izvestaj o rutama -ir
    //6 izvestaj o geo zonama -igz
    //7 izvestaj o povredama rute -ipr
    //8 izvestaj o sigurnoj voznji -isv1
    //9 izvestaj o stanju vozila -isv2
    //10 Driver Relations Report -drfr
    //11 Monthly Fuel Reports -mfr
    //12 Effective Working Hours -ewh
    //13 Sensors Activation Report -sar
    //14 Driver Vehicle Relation Report -dvrr
    //15 Driver Driver Relation Report -ddrr

    private void sendReport(UserReport userReport) throws Exception {

        log.info("####################################");
        log.info(LocalDateTime.now() + " ===== U sendReport METODI - NA POCETKU =====");
        log.info(LocalDateTime.now() + " UserReport ID: " + userReport.getUserReportId());
        log.info(LocalDateTime.now() + " Report Type (report): " + userReport.getReport() );
        log.info(LocalDateTime.now() + " Email: " + userReport.getEmail());
        log.info(LocalDateTime.now() + " IMEI: " + java.util.Arrays.toString(userReport.getImei()));
        log.info(LocalDateTime.now() + " Route IMEI: " + userReport.getRouteImei());
        log.info(LocalDateTime.now() + " =================================================");

        //        userReport.setHto(22);
        //        userReport.setMto(59);

        log.info("####################################");
        log.info(LocalDateTime.now() + " Generating report type " + userReport.getReport() + " for UserReport ID: " + userReport.getUserReportId());

        byte[] file = null;
        switch (userReport.getReport()) {
        /**
         * izradjuje izvestaj u zavisnosti od tipa izvestaja
         */
        case 1:
            try {
                LocalDate to = LocalDate.now().minusDays(1);
                LocalDate from = to.minusDays(userReport.getPeriod() - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String toString = formatter.format(to);
                String fromString = formatter.format(from);
                log.info("####################################");
                log.info(LocalDateTime.now() + " Calling izvestajOPredjenomPutuExport for UserReport ID: " + userReport.getUserReportId());
                file = reportController.izvestajOPredjenomPutuExport(userReport.getImei()[0], fromString, toString, userReport.getHfrom(),
                        userReport.getMfrom(), 22, 59, userReport.getXlsxpdf(), userReport.getImei());
                log.info("####################################");
                log.info(LocalDateTime.now() + " Sending email for UserReport ID: " + userReport.getUserReportId());
                sendMail(userReport, file, true);
                automaticReportLoggerService.saveSuccessLog("ipp", userReport.getEmail(), userReport.getImei());
                log.info("####################################");
                log.info(LocalDateTime.now() + " Report generated successfully for UserReport ID: " + userReport.getUserReportId());
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - izvestajOPredjenomPutu");
            } catch (Exception e) {
                log.info("####################################");
                log.info(LocalDateTime.now() + " Failed to generate report for UserReport ID: " + userReport.getUserReportId());
                automaticReportLoggerService.saveFailLog("ipp", userReport.getEmail(), userReport.getImei(), e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - izvestajOPredjenomPutu");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - izvestajOPredjenomPutu");
                e.printStackTrace();
            }
            break;
        case 2:
            try {
                Calendar aCalendar = Calendar.getInstance();
                aCalendar.add(Calendar.MONTH, -1);
                aCalendar.set(Calendar.DATE, 1);
                Date firstDateOfPreviousMonth = aCalendar.getTime();
                aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date lastDateOfPreviousMonth = aCalendar.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String to = formatter.format(lastDateOfPreviousMonth);
                String from = formatter.format(firstDateOfPreviousMonth);
                file = reportController.izvestajOPredjenomPutuMesecniExport(userReport.getImei()[0], from, to, userReport.getXlsxpdf(), userReport.getImei());
                sendMail(userReport, file, true);
                automaticReportLoggerService.saveSuccessLog("ippm", userReport.getEmail(), userReport.getImei());
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - izvestajOPredjenomPutuMesecni");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("ippm", userReport.getEmail(), userReport.getImei(), e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - izvestajOPredjenomPutuMesecni");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - izvestajOPredjenomPutuMesecni");
                e.printStackTrace();
            }
            break;
        case 3:
            try {
                Calendar aCalendar = Calendar.getInstance();
                aCalendar.add(Calendar.MONTH, -1);
                aCalendar.set(Calendar.DATE, 1);
                Date firstDateOfPreviousMonth = aCalendar.getTime();
                aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date lastDateOfPreviousMonth = aCalendar.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String to = formatter.format(lastDateOfPreviousMonth);
                String from = formatter.format(firstDateOfPreviousMonth);

                file = reportController.izvestajOPredjenomPutuTelMesecniVRVExport(userReport.getImei()[0], from, to, userReport.getHfrom(),
                        userReport.getMfrom(), userReport.getHto(), userReport.getMto(), userReport.getHfromsa(), userReport.getMfromsa(),
                        userReport.getHtosa(), userReport.getMtosa(), userReport.getHfromsu(), userReport.getMfromsu(), userReport.getHtosu(),
                        userReport.getMtosu(), userReport.getWorking(), userReport.getXlsxpdf(), userReport.getImei());
                sendMail(userReport, file, true);
                automaticReportLoggerService.saveSuccessLog("ippmvrv", userReport.getEmail(), userReport.getImei());
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - izvestajOPredjenomPutuMesecniVRV");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("ippmvrv", userReport.getEmail(), userReport.getImei(), e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - izvestajOPredjenomPutuMesecniVRV");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - izvestajOPredjenomPutuMesecniVRV");
                e.printStackTrace();
            }
            break;
        case 4:
            log.info("####################################");
            log.info(LocalDateTime.now() + " ===== CASE 4 - PREKORACENJE BRZINE =====");
            log.info(LocalDateTime.now() + " UserReport ID: " + userReport.getUserReportId());
            log.info(LocalDateTime.now() + " Email: " + userReport.getEmail());
            log.info(LocalDateTime.now() + " IMEI: " + java.util.Arrays.toString(userReport.getImei()));
            log.info(LocalDateTime.now() + " =========================================");
            try {
                LocalDate to = LocalDate.now().minusDays(1);
                LocalDate from = to.minusDays(userReport.getPeriod() - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String toString = formatter.format(to);
                String fromString = formatter.format(from);
                file = reportController.izvestajOPrekoracenjuBrzineExport2(userReport.getImei(), fromString, toString, userReport.getHfrom(),
                        userReport.getMfrom(), 22, 59, userReport.getMaxSpeed(), userReport.getXlsxpdf(), false);
                sendMail(userReport, file, true);
                automaticReportLoggerService.saveSuccessLog("ipb", userReport.getEmail(), new String[] { userReport.getRouteImei() });
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - izvestajOPrekoracenjuBrzine");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("ipb", userReport.getEmail(), new String[] { userReport.getRouteImei() }, e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - izvestajOPrekoracenjuBrzine");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - izvestajOPrekoracenjuBrzine");
                e.printStackTrace();
            }
            break;
        case 5:

            try {
                LocalDate to = LocalDate.now().minusDays(1);
                LocalDate from = to.minusDays(userReport.getPeriod() - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String toString = formatter.format(to);
                String fromString = formatter.format(from);
                file = reportController.izvestajORelacijamaVozilaExportAutomatski(userReport.getImei(), fromString, toString, userReport.getHfrom(), userReport.getMfrom(), 22,
                        59, userReport.getMinDistance(), userReport.getXlsxpdf());
                sendMail(userReport, file, true);

                automaticReportLoggerService.saveSuccessLog("ir", userReport.getEmail(), new String[] { userReport.getRouteImei() });
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - izvestajORelacijamaVozila");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("ir", userReport.getEmail(), new String[] { userReport.getRouteImei() }, e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - izvestajORelacijamaVozila");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - izvestajORelacijamaVozila");
                e.printStackTrace();
            }
            break;
        case 6:
            try {
                LocalDate to = LocalDate.now().minusDays(1);
                LocalDate from = to.minusDays(userReport.getPeriod() - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String toString = formatter.format(to);
                String fromString = formatter.format(from);
                List<Integer> listGeozonesId = java.util.Arrays.stream(userReport.getGeozoneIds()).boxed().collect(java.util.stream.Collectors.toList());

                //                   reportController.izvestajORutamaExport(userReport.getRouteImei(), fromString, toString, userReport.getHfrom(), userReport.getMfrom(), 22,59, userReport.getXlsxpdf());
                for (Integer integer : listGeozonesId) {
                    file = reportController.getGeozoneReportExprot(1, listGeozonesId, userReport.getRouteImei(),
                            from.atStartOfDay().toEpochSecond(ZoneOffset.UTC), to.atStartOfDay().toEpochSecond(ZoneOffset.UTC));
                    sendMail(userReport, file, true);

                }
                automaticReportLoggerService.saveSuccessLog("ir", userReport.getEmail(), new String[] { userReport.getRouteImei() });
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - getGeozoneReport");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("ir", userReport.getEmail(), new String[] { userReport.getRouteImei() }, e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - getGeozoneReport");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - getGeozoneReport");
                e.printStackTrace();
            }
            break;
        case 7:
            try {
                LocalDate to = LocalDate.now().minusDays(1);
                LocalDate from = to.minusDays(userReport.getPeriod() - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String toString = formatter.format(to);
                String fromString = formatter.format(from);
                //izvestajOZelenojVoznjiExport?
                //                   reportController.izvestajORutamaExport(userReport.getRouteImei(), fromString, toString, userReport.getHfrom(), userReport.getMfrom(), 22,59, userReport.getXlsxpdf());
                //file = reportController.getGeozoneReportExprot(userReport.getRouteImei(), fromString, toString, userReport.getHfrom(), userReport.getMfrom(), 22,59, userReport.getXlsxpdf());

                //                    sendMail(userReport, file);
                //                    loggerService.saveSuccessLog("ir",userReport.getEmail(),userReport.getImei());

            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("ir", userReport.getEmail(), userReport.getImei(), e.getMessage());

                e.printStackTrace();
            }
            break;
        case 8:
            try {
                LocalDate to = LocalDate.now().minusDays(1);
                LocalDate from = to.minusDays(userReport.getPeriod() - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String toString = formatter.format(to);
                String fromString = formatter.format(from);
                file = reportController.izvestajOZelenojVoznjiExport(0, userReport.getRouteImei(),
                        String.valueOf(from.atStartOfDay().toEpochSecond(ZoneOffset.UTC)), String.valueOf(to.atStartOfDay().toEpochSecond(ZoneOffset.UTC)),
                        userReport.getHfrom(), userReport.getMfrom(), 22, 59);

                sendMail(userReport, file, true);
                automaticReportLoggerService.saveSuccessLog("ir", userReport.getEmail(), new String[] { userReport.getRouteImei() });
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - izvestajOZelenojVoznji");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("ir", userReport.getEmail(), new String[] { userReport.getRouteImei() }, e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - izvestajOZelenojVoznji");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - izvestajOZelenojVoznji");
                e.printStackTrace();
            }
            break;
        case 9:
            try {

                LocalDate to = LocalDate.now().minusDays(1);
                LocalDate from = to.minusDays(userReport.getPeriod() - 1);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String toString = formatter.format(to);
                String fromString = formatter.format(from);
                System.out.println("OVDE 1");

                //
                //ne znam sta je ovo mi odakle je ali treba za metodu
                file = reportController.izvestajOStajanjuExport(userReport.getXlsxpdf(), fromString, toString, userReport.getHfrom(), userReport.getMfrom(), 22,
                        59, userReport.getMinIdle(), userReport.getMin(), userReport.getImei(), userReport.getIsIdle());

                sendMail(userReport, file, true);
                automaticReportLoggerService.saveSuccessLog("ir", userReport.getEmail(), userReport.getImei());
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - izvestajOStajanju");

            } catch (Exception e) {
                e.printStackTrace();
                automaticReportLoggerService.saveFailLog("ir", userReport.getEmail(), userReport.getImei(), e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - izvestajOStajanju");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - izvestajOStajanju");

            }
            break;
        case 10:
            try {
                LocalDateTime to = LocalDateTime.now().minusDays(1);
                LocalDateTime from = to.minusDays(userReport.getPeriod() - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                List<String> imeis = Arrays.asList(userReport.getImei());
                if (userReport.getXlsxpdf() == 2)
                    //todo: add fuel margine
                    file = reportsController.exportDriverRelationsFuelReportInPdf(from, to, userReport.getFuelMargin(), userReport.getEmptyingMargin(), imeis,
                            userReport.getMinDistance()).getBody().getByteArray();
                else
                    file = reportsController.exportDriverRelationsFuelReportInXLS(from, to, userReport.getFuelMargin(), userReport.getEmptyingMargin(), imeis,
                            userReport.getMinDistance()).getBody().getByteArray();

                sendMail(userReport, file, true);
                automaticReportLoggerService.saveSuccessLog("drfr", userReport.getEmail(), userReport.getImei());
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - DriverRelationsFuelReport");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("drfr", userReport.getEmail(), userReport.getImei(), e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - DriverRelationsFuelReport");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - DriverRelationsFuelReport");
                e.printStackTrace();
            }
            break;
        case 11:
            try {
                LocalDateTime to = LocalDateTime.now().minusDays(1);
                LocalDateTime from = to.minusDays(userReport.getPeriod() - 1);
                List<String> imeis = Arrays.asList(userReport.getImei());
                List<MonthlyFuelConsumptionReport> monthFuelReport = reportService.getMonthFuelReport(from, to, imeis, userReport.getFuelMargin(),
                        userReport.getEmptyingMargin());

                if (userReport.getXlsxpdf() == 2) {
                    PdfExporter pdfExporter = new MonthlyReportPdfExporter(from, to);
                    file = pdfExporter.export(monthFuelReport, MonthlyFuelConsumptionReport.class, "Mesecni izvestaj potrosnje goriva");
                } else {
                    XlsExporter xlsExporter = new MontlyXlsExporter(from, to);
                    file = xlsExporter.export(monthFuelReport, MonthlyFuelConsumptionReport.class, "Mesecni izvestaj  potrosnje goriva");

                }

                sendMail(userReport, file, false);
                automaticReportLoggerService.saveSuccessLog("mfr", userReport.getEmail(), userReport.getImei());
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - MonthlyFuelReport");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("mfr", userReport.getEmail(), userReport.getImei(), e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - MonthlyFuelReport");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - MonthlyFuelReport");
                e.printStackTrace();
            }
            break;
        case 12:
            try {
                LocalDateTime to = LocalDateTime.now().minusDays(1);
                LocalDateTime from = to.minusDays(userReport.getPeriod() - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                List<String> imeis = Arrays.asList(userReport.getImei());
                int rpm = (userReport.getRpm() == null) ? 0 : userReport.getRpm();
                if (userReport.getXlsxpdf() == 2)
                    file = reportsController.exportEffectiveWorkingHoursInPdf(from, to, imeis, userReport.getEmptyingMargin(), rpm, userReport.getFuelMargin())
                            .getBody().getByteArray();
                else
                    file = reportsController.exportEffectiveWorkingHoursInXls(from, to, imeis, userReport.getEmptyingMargin(), rpm, userReport.getFuelMargin())
                            .getBody();

                sendMail(userReport, file, false);
                automaticReportLoggerService.saveSuccessLog("ewh", userReport.getEmail(), userReport.getImei());
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - EffectiveWorkingHours");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("ewh", userReport.getEmail(), userReport.getImei(), e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - EffectiveWorkingHours");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - EffectiveWorkingHours");
                e.printStackTrace();
            }
            break;
        case 13:
            log.info("####################################");
            log.info(LocalDateTime.now() + " ===== CASE 13 - SENSORS ACTIVATION REPORT =====");
            log.info(LocalDateTime.now() + " UserReport ID: " + userReport.getUserReportId());
            log.info(LocalDateTime.now() + " Email: " + userReport.getEmail());
            log.info(LocalDateTime.now() + " IMEI: " + java.util.Arrays.toString(userReport.getImei()));
            log.info(LocalDateTime.now() + " =================================================");
            try {
                // LocalDateTime to = LocalDateTime.now().minusDays(1);
                // LocalDateTime from = to.minusDays(userReport.getPeriod() - 1);
                LocalDateTime to = LocalDateTime.now().minusDays(1).toLocalDate().atTime(23, 59, 59);
                LocalDateTime from = to.minusDays(userReport.getPeriod() - 1).toLocalDate().atStartOfDay();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                List<String> imeis = Arrays.asList(userReport.getImei());
                log.info("####################################");
                log.info(LocalDateTime.now() + " Calling SensorActivationRportExport for UserReport ID: " + userReport.getUserReportId());
                log.info(LocalDateTime.now() + " Date range - from: " + from + " to: " + to);
                if (userReport.getXlsxpdf() == 2)
                    file = reportsController.getSensorActivationRportExportPdf(from, to, imeis).getBody().getByteArray();
                else
                    file = reportsController.getSensorActivationRportExportXls(from, to, imeis).getBody().getByteArray();

                log.info("####################################");
                log.info(LocalDateTime.now() + " Sending email for UserReport ID: " + userReport.getUserReportId());

                sendMail(userReport, file, false);
                automaticReportLoggerService.saveSuccessLog("sar", userReport.getEmail(), userReport.getImei());
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - SensorActivationReport");
            } catch (Exception e) {
                log.info("####################################");
                log.info(LocalDateTime.now() + " Failed to generate report for UserReport ID: " + userReport.getUserReportId());
                automaticReportLoggerService.saveFailLog("sar", userReport.getEmail(), userReport.getImei(), e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - SensorActivationReport");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - SensorActivationReport");
                e.printStackTrace();
                // Uklonjen throw e; jer metoda sendReport ne deklariše da baca Exception
                // Greška će biti uhvaćena u try-catch bloku oko poziva sendReport u sendReports metodi
            }
            break;
        case 14:
            try {
                LocalDateTime to = LocalDateTime.now().minusDays(1);
                LocalDateTime from = to.minusDays(userReport.getPeriod() - 1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                List<String> imeis = Arrays.asList(userReport.getImei());

                if (userReport.getXlsxpdf() == 2)
                    file = reportsController.exportDriverVehicleRelationsReportInPdf(from, to, imeis, userReport.getMinDistance()).getBody().getByteArray();
                    //exportDriverVehicleRelationsReportInPdf
                else
                    file = reportsController.exportDriverVehicleRelationsReportInXls(from, to, imeis, userReport.getMinDistance()).getBody().getByteArray();

                sendMail(userReport, file, false);
                automaticReportLoggerService.saveSuccessLog("dvrr", userReport.getEmail(), userReport.getImei());
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - DriverDriverRelationReport");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("dvrr", userReport.getEmail(), userReport.getImei(), e.getMessage());
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - DriverVehicleRelationsReport");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - DriverDriverRelationReport");
                e.printStackTrace();
            }
            break;

        case 16:
            try {
                //ovde treba izmena da bude date kada je on hteo
                LocalDateTime to = LocalDateTime.now().minusDays(1);
                LocalDateTime from = to.minusDays(userReport.getPeriod() - 1);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                List<String> imeis = Arrays.asList(userReport.getImei());
                if (userReport.getXlsxpdf() == 2) {
                    file = reportsController.exportDailyMovementConsumptionReportPDF(from, to, imeis, userReport.getEmptyingMargin(),
                            userReport.getFuelMargin()).getBody().getByteArray();
                } else {
                    file = reportsController.exportDailyMovementConsumptionReportXLS(from, to, imeis, userReport.getEmptyingMargin(),
                            userReport.getFuelMargin()).getBody().getByteArray();
                }

                sendMail(userReport, file, false);
                automaticReportLoggerService.saveSuccessLog("dimp", userReport.getEmail(), userReport.getImei());
                LoggerFileUtil.logToFile("Uspešno kreiran izveštaj - DailyMovementConsumptionReport");
            } catch (Exception e) {
                automaticReportLoggerService.saveFailLog("dimp", userReport.getEmail(), userReport.getImei(), e.getMessage());
                e.printStackTrace();
                System.out.println("NEUSPESNO SLANJE IZVESTAJA - DailyMovementConsumptionReport");
                LoggerFileUtil.logToFile("Neuspešno kreiran izveštaj - DailyMovementConsumptionReport");
            }
            break;
        default:
            break;
        }
    }

    /**
     * salje userreport na mail
     */
    private void sendMail(UserReport userReport, byte[] file, boolean toDecode) throws UnsupportedEncodingException, MessagingException {
        log.info("####################################");
        log.info(LocalDateTime.now() + " Preparing to send email to " + userReport.getEmail() + " for UserReport ID: " + userReport.getUserReportId());
        if (!userReport.getEmail().contains("@")) {
            log.info("####################################");
            log.info(LocalDateTime.now() + " Invalid email address for UserReport ID: " + userReport.getUserReportId());
            return;
        }
        if (file == null) {
            log.info("####################################");
            log.info(LocalDateTime.now() + " No file to send for UserReport ID: " + userReport.getUserReportId());
            return;
        }
        if (toDecode)
            file = Base64.getDecoder().decode(file);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        log.info("####################################");
        log.info(LocalDateTime.now() + " Setting up email session for UserReport ID: " + userReport.getUserReportId());

//        Session session = Session.getInstance(props, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("test4bees@gmail.com", "heqthaukzpesicxm");
//            }
//        });

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mail@galeb.com", "acvm mjfn rbkx scza");
            }
        });

        log.info("####################################");
        log.info(LocalDateTime.now() + " Creating email message for UserReport ID: " + userReport.getUserReportId());

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("office@oris.com", "ORIS - noreply"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userReport.getEmail()));
        //1 izvestaj o predjenom putu - ipp
        //2 izvestaj o predjenom putu mesecni - ippm
        //3 izvestaj o predjenom putu mesecni van i u radnom vremenu - ippmvrv
        //4 izvestaj o prekoracenju brzine
        //5 izvestaj o rutama
        String text = "Poštovani/a, u prilogu Vam dostavljamo zahtevani izveštaj.";

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = formatter.format(date);

        DataSource fds = new ByteArrayDataSource(file, "application/vnd.ms-excel");

        log.info("####################################");
        log.info(LocalDateTime.now() + " Attaching report to email for UserReport ID: " + userReport.getUserReportId());

        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText(text);

        MimeBodyPart mbp2 = new MimeBodyPart();
        mbp2.setDataHandler(new DataHandler(fds));
        log.info("####################################");
        log.info(LocalDateTime.now() + " Setting email subject and filename for UserReport ID: " + userReport.getUserReportId());
        switch (userReport.getReport()) {
        case 1:
            log.info("####################################");
            log.info(LocalDateTime.now() + " Setting email subject and filename for report type 1 for UserReport ID: " + userReport.getUserReportId());
            msg.setSubject("Izveštaj o pređenom putu - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                log.info("####################################");
                log.info(LocalDateTime.now() + " Setting filename for PDF for UserReport ID: " + userReport.getUserReportId());
                mbp2.setFileName("PredjeniPut" + dateStr + ".pdf");
            } else {
                log.info("####################################");
                log.info(LocalDateTime.now() + " Setting filename for XLSX for UserReport ID: " + userReport.getUserReportId());
                mbp2.setFileName("PredjeniPut" + dateStr + ".xlsx");
            }
            log.info("####################################");
            log.info(LocalDateTime.now() + " Finished setting email subject and filename for UserReport ID: " + userReport.getUserReportId());
            break;
        case 2:
            msg.setSubject("Mesešni izveštaj o pređenom putu - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("PredjeniPutMesecni" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("PredjeniPutMesecni" + dateStr + ".xlsx");
            }
            break;
        case 3:
            msg.setSubject("Mesešni izveštaj o pređenom putu (u i van radnog vremena) - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("PredjeniPutRadnoVreme" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("PredjeniPutRadnoVreme" + dateStr + ".xlsx");
            }
            break;
        case 4:
            msg.setSubject("Izveštaj o prekoračenju brzine - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("PrekoracenjeBrzine" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("PrekoracenjeBrzine" + dateStr + ".xlsx");
            }
            break;
        case 5:
            msg.setSubject("Izveštaj o relacijama - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Relacije" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Relacije" + dateStr + ".xlsx");
            }
            break;
        case 6:
            msg.setSubject("Izveštaj o geozoni - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Geozona" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Geozona" + dateStr + ".xlsx");
            }
            break;
        case 7:
            msg.setSubject("Izveštaj o geozoni - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Izveštaj o geozoni" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Izveštaj o geozoni" + dateStr + ".xlsx");
            }
            break;
        case 8:
            msg.setSubject("Izveštaj o zelenoj voznji - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Zelena voznja" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Zelena voznja" + dateStr + ".xlsx");
            }
            break;
        case 9:
            msg.setSubject("Izveštaj o stajanju  - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Izveštaj o stajanju" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Izveštaj o stajanju" + dateStr + ".xlsx");
            }
            break;
        case 10:
            msg.setSubject("Izveštaj o relacijama vozila - gorivo - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Relacija vozila - gorivo" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Relacija vozila - gorivo" + dateStr + ".xls");
            }
            break;
        case 11:
            msg.setSubject("Izveštaj o mesecnoj potrosnji goriva - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Mesecna potrosnja goriva" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Mesecna potrosnja goriva" + dateStr + ".xls");
            }
            break;
        case 12:
            msg.setSubject("Izveštaj o efektivnim radnim satima masine - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Efektivni radni sati masine" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Efektivni radni sati masine" + dateStr + ".xls");
            }
            break;
        case 13:
            msg.setSubject("Izveštaj o senzorima - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Senzori" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Senzori" + dateStr + ".xls");
            }

            log.info("####################################");
            log.info(LocalDateTime.now() + " Finished setting email subject and filename for UserReport ID: " + userReport.getUserReportId());
            break;
        case 14:
            msg.setSubject("Izveštaj o relacijama vozila po vozacu - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Relacija vozila po vozacu" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Relacija vozila po vozacu" + dateStr + ".xls");
            }
            break;
        case 15:
            msg.setSubject("Izveštaj o relacijama vozila po vozilu - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Relacija vozila po vozilu" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Relacija vozila po vozilu" + dateStr + ".xls");
            }
            break;
        case 16:
            msg.setSubject("Dnevni izvestaj o kretanju i potrosnji goriva - ORIS");
            if (userReport.getXlsxpdf() == 2) {
                mbp2.setFileName("Dnevni izvestaj o kretanju i potrosnji goriva" + dateStr + ".pdf");
            } else {
                mbp2.setFileName("Dnevni izvestaj o kretanju i potrosnji goriva" + dateStr + ".xls");
            }
            break;
        default:
            msg.setSubject("Izveštaj - ORIS");
        }

        Multipart mp = new MimeMultipart();
        mp.addBodyPart(mbp1);
        mp.addBodyPart(mbp2);
        msg.setContent(mp);
        msg.saveChanges();
        msg.setSentDate(new java.util.Date());

        Transport.send(msg);
    }

    //    public void sendMailWithAttachment(UserReport userReport, byte[] file, String to, String subject, String body, String fileToAttach)
    //    {
    //        MimeMessagePreparator preparator = new MimeMessagePreparator()
    //        {
    //            public void prepare(MimeMessage mimeMessage) throws Exception
    //            {
    //                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
    //                mimeMessage.setFrom(new InternetAddress("admin@gmail.com"));
    //                String text = "Poštovani/a, u prilogu Vam dostavljamo zahtevani izveštaj.";
    //                mimeMessage.setText(text);
    //
    //                switch (userReport.getReport()) {
    //                    case 1:
    //                        mimeMessage.setSubject("Izveštaj o pređenom putu - ORIS");
    //                        break;
    //                    case 2:
    //                        mimeMessage.setSubject("Mesešni izveštaj o pređenom putu - ORIS");
    //                        break;
    //                    case 3:
    //                        mimeMessage.setSubject("Mesešni izveštaj o pređenom putu (u i van radnog vremena) - ORIS");
    //                        break;
    //                    case 4:
    //                        mimeMessage.setSubject("Izveštaj o prekoračenju brzine - ORIS");
    //                        break;
    //                    case 5:
    //                        mimeMessage.setSubject("Izveštaj o relacijama - ORIS");
    //                        break;
    //                    default:
    //                        mimeMessage.setSubject("Izveštaj - ORIS");
    //                }
    //
    //
    //                FileSystemResource file = new FileSystemResource(new File(fileToAttach));
    //                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
    //                helper.addAttachment("logo.jpg", file);
    //            }
    //        };
    //
    //        try {
    //            mailSender.send(preparator);
    //        }
    //        catch (MailException ex) {
    //            // simply log it and go on...
    //            System.err.println(ex.getMessage());
    //        }
    //    }

}
