package rs.oris.back.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import rs.oris.back.controller.ReportController;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.domain.dto.DTOUserReport;
import rs.oris.back.repository.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class UserReportService {

    @Autowired
    private UserReportRepository userReportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReportController reportController;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private NotificationAppRepository notificationAppRepository;
    @Autowired
    private GeozoneRepository geozoneRepository;
    @Autowired
    private NotificationVehicleRepository notificationVehicleRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private FirebaseMessaging fcm;
    @Autowired
    private AutomaticNotificationLoggerService automaticNotificationLoggerService;

    @Autowired
    private UserPushNotificationRepository userPushNotificationRepository;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Vraca sve UserReport-ove firme korisnika koji poziva
     *
     * @param user   korisnik
     * @param firmId firma
     */
    public Response<Map<String, Set<DTOUserReport>>> getAll(User user, int firmId) throws Exception {
//        Set<UserReport> list2 = userReportRepository.findAllByUserUserId(user.getUserId());
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<Route> routes = routeRepository.findAll();
        List<Geozone> geozoneList = geozoneRepository.findAll();

//        if ((user.getSuperAdmin() != null && user.getSuperAdmin() == true) || (user.getAdmin() != null && user.getAdmin() == true)) {
            Set<UserReport> list = userReportRepository.findByFirmFirmId(firmId);
//            list.addAll(list2);
            Set<DTOUserReport> list3 = new HashSet<>();
            for (UserReport ur : list) {
                DTOUserReport dto = new DTOUserReport(ur);

                for (Vehicle v : vehicles) {
                    for (String s : ur.getImei()) {
                        if (s.equalsIgnoreCase(v.getImei())) {
                            dto.getVehicleList().add(v);
                        }

                    }
                    if (ur.getRouteImei() != null && ur.getRouteImei().equals(v.getImei())) {
                        dto.getVehicleList().add(v);
                    }
                }
                for (Route r : routes) {
                    if (ur.getRouteIds() == null)
                        break;
                    for (Integer s : ur.getRouteIds()) {
                        if (s == r.getRouteId())
                            dto.getRouteIds().add(r);
                    }
                }
                for (Geozone r : geozoneList) {
                    if (ur.getGeozoneIds() == null)
                        break;
                    for (Integer s : ur.getGeozoneIds()) {
                        if (s == r.getGeozoneId())
                            dto.getGeozoneIds().add(r);
                    }
                }

                list3.add(dto);
            }

            Map<String, Set<DTOUserReport>> map = new HashMap<>();
            map.put("reports", list3);
            return new Response<>(map);
//        } else {
//            throw new Exception("Only admins can utilize this endpoint.");
//        }

    }

    /**
     * dodaje novi userReport
     */
    public Response<UserReport> add(User user, UserReport userReport, int firmId) throws Exception {
//        if ((user.getSuperAdmin() != null && user.getSuperAdmin() == true) || (user.getAdmin() != null && user.getAdmin() == true)) {

        if(userReport.getImei()==null || userReport.getImei().length==0){
            throw new Exception("Imei list cannot be empty");
        }

        userReport.setUser(user);
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id");
        }
        userReport.setFirm(optionalFirm.get());
        UserReport saved = userReportRepository.save(userReport);
        if (saved == null) {
            throw new Exception("Failed to save user report");
        }
        return new Response<>(saved);
//        } else {
//            throw new Exception("Not authorized for this operation");
//        }
    }

    /**
     * Brise userReport po id-u
     */
    public boolean delete(int id) {
        userReportRepository.deleteById(id);
        return true;
    }

    /**
     * azurira userReport
     */
    @Transactional
    public Response<UserReport> update(User user, UserReport userReport, int id) throws Exception {
        Optional<UserReport> optionalUserReport = userReportRepository.findById(id);
        if (!optionalUserReport.isPresent()) {
            throw new Exception("Invalid user report id " + id);
        }

        UserReport old = optionalUserReport.get();
        userReport.setFirm(old.getFirm());
        userReport.setUserReportId(old.getUserReportId());
        userReport.setUser(user);
        UserReport saved = userReportRepository.save(userReport);
        if (saved == null) {
            throw new Exception("Failed to update user report");
        }

        return new Response<>(saved);
    }

    /**
     * slanje maila, verujem radi testiranja
     */
    public boolean sendMail() throws Exception {
        byte[] file = null;
        file = reportController.izvestajORelacijamaVozilaExport("864547032670155", "2020-1-1", "2020-30-1", 1, 1, 22, 2, 0.0, 2);
        sendMail("petar.lakcevic@gmail.com", file);
        return true;
    }

    /**
     * slanje mejla, verujem radi testiranja
     */
    private void sendMail(String email, byte[] file) throws UnsupportedEncodingException, MessagingException {
        file = Base64.getDecoder().decode(file);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {

//                return new PasswordAuthentication("test4bees@gmail.com", "heqthaukzpesicxm");
                return new PasswordAuthentication("mail@galeb.com", "acvm mjfn rbkx scza");

            }
        });


        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("office@oris.com", "ORIS - noreply"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        //1 izvestaj o predjenom putu - ipp
        //2 izvestaj o predjenom putu mesecni - ippm
        //3 izvestaj o predjenom putu mesecni van i u radnom vremenu - ippmvrv
        //4 izvestaj o prekoracenju brzine
        //5 izvestaj o rutama
        String text = "Poštovani/a, u prilogu Vam dostavljamo zahtevani izveštaj.";
        msg.setSubject("Izvestaj");

        DataSource fds = new ByteArrayDataSource(file, "application/vnd.ms-excel");

        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText("Postovani, u prilogu vam saljemo izvestaj");

        MimeBodyPart mbp2 = new MimeBodyPart();
        mbp2.setDataHandler(new DataHandler(fds));

        mbp2.setFileName("izvestaj.pdf");

        Multipart mp = new MimeMultipart();
        mp.addBodyPart(mbp1);
        mp.addBodyPart(mbp2);
        msg.setContent(mp);
        msg.saveChanges();
        msg.setSentDate(new java.util.Date());

        Transport.send(msg);
    }

    /**
     * WORKBOOK -> PDF
     */
    public void go(Workbook workbook) throws Exception {

        FileInputStream input_document = new FileInputStream(new File("C:\\excel_to_pdf.xls"));
        // Read workbook into HSSFWorkbook
        HSSFWorkbook my_xls_workbook = new HSSFWorkbook(input_document);
        // Read worksheet into HSSFSheet
        HSSFSheet my_worksheet = my_xls_workbook.getSheetAt(0);
        // To iterate over the rows
        Iterator<Row> rowIterator = my_worksheet.iterator();
        //We will create output PDF document objects at this point
        Document iText_xls_2_pdf = new Document();
        PdfWriter.getInstance(iText_xls_2_pdf, new FileOutputStream("Excel2PDF_Output.pdf"));
        iText_xls_2_pdf.open();
        //we have two columns in the Excel sheet, so we create a PDF table with two columns
        //Note: There are ways to make this dynamic in nature, if you want to.
        PdfPTable my_table = new PdfPTable(2);
        //We will use the object below to dynamically add new data to the table
        PdfPCell table_cell;
        //Loop through rows.
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next(); //Fetch CELL
                switch (cell.getCellType()) { //Identify CELL type
                    //you need to add more code here based on
                    //your requirement / transformations
                    case Cell.CELL_TYPE_STRING:
                        //Push the data from Excel to PDF Cell
                        table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
                        //feel free to move the code below to suit to your needs
                        my_table.addCell(table_cell);
                        break;
                }
                //next line
            }

        }
        //Finally add the table to PDF document
        iText_xls_2_pdf.add(my_table);
        iText_xls_2_pdf.close();
        //we created our pdf file..
        input_document.close(); //close xls
    }

    /**
     * salje sms poruku preko mobile-gw api
     *
     * @param text  sadrzaj
     * @param phone broj telefona
     */
    public void sendSms(String text, String phone) throws Exception {
        String as = "galebele:5HdBRAT4";
        String encodedString = Base64.getEncoder().encodeToString(as.getBytes());
        String uri = "https://bulk.mobile-gw.com:9090/api/message";

        MediaType mediaType = MediaType.parseMediaType("application/json");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encodedString);
        headers.add("Content-Type", "application/json");
        headers.add("Cache-Control", "no-cache");

        Map<String, Object> map = new HashMap<>();
        map.put("sender", "Oris34");
        map.put("phoneNumber", phone);
        map.put("text", text);
        map.put("priority", "NORMAL");
        map.put("validity", 60);
        map.put("statusReport", 7);
        map.put("statusUrl", "https://host/dlr");
        map.put("trackingData", "MSG12345");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(uri, entity, String.class);
        if (response.getBody().contains("Unknown destination")) {
            throw new Exception("Invalid phone number");
        }

    }


    /**
     * salje jednu notifikaciju, po potrebi SMS i email,
     */
    public void snedNotification(SingleNotification singleNotification) throws Exception {
        log.info("Saljem notifikaciju za korisnika sa id-jem " + singleNotification.getUserId() +
                " za notifikaciju tipa " + singleNotification.getNotificationId() +
                " za vozilo sa id-jem " + singleNotification.getVehicleId());
        Optional<Notification> optionalNotification = notificationRepository.findById(singleNotification.getNotificationId());
        if (!optionalNotification.isPresent()) {
            return;
        }
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(singleNotification.getVehicleId());
        if (!optionalVehicle.isPresent()) {
            return;
        }

        Optional<User> optionalUser = userRepository.findByUserId(singleNotification.getUserId());
        if (!optionalNotification.isPresent()) {
            return;
        }
        Optional<NotificationVehicle> byId = notificationVehicleRepository.findById(singleNotification.getNmid());

        if (byId.isPresent()) {
            NotificationVehicle notificationVehicle = byId.get();
            notificationVehicle.setVehicleMileage(singleNotification.getVehicleMileage());
            notificationVehicle.setIsMileage(singleNotification.isMileage());
            notificationVehicle.setVehicleMileageReminder(singleNotification.getVehicleMileageReminder());
            notificationVehicle.setGpsMileage(singleNotification.getGpsMileage());
            notificationVehicle.setStartTimestamp(singleNotification.getStartTimestamp());
            notificationVehicle.setStartPointLat(singleNotification.getStartPointLat());
            notificationVehicle.setStartPointLng(singleNotification.getStartPointLng());
        }

        if (singleNotification.getNotificationId() == 12) {
            Vehicle vehicle = optionalVehicle.get();
            vehicle.setMillage(singleNotification.getKilometers());
            vehicleRepository.save(vehicle);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(singleNotification.getTimestamp());
        //calendar.add(Calendar.HOUR, 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        //todo: Ako planiraju da idu van srbije ovo treba mozda da se promeni
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));


        Notification notification = optionalNotification.get();
        Vehicle vehicle = optionalVehicle.get();
        String text = "<html><h2>Notifikacija" + "\n </h2>";
        if (singleNotification.getGreenType() != null && !singleNotification.getGreenType().equalsIgnoreCase("") && !singleNotification.getGreenType().equalsIgnoreCase(" ")) {
            text += "<p> <b>TIP:</b> " + singleNotification.getGreenType() + " </p> \n";
        } else {
            text += "<p> <b>TIP:</b> " + notification.getName() + " </p> \n";
        }
        //todo: dodaj vreme notifikacije prvo javljanje
        text += "<p> <b>VREME:</b> " + simpleDateFormat.format(calendar.getTime()) + "</p>\n";
        text += "<p> <b>REGISTRACIJA:</b> " + vehicle.getRegistration() + "</p>\n";
        text += "<p> <b>MODEL:</b> " + vehicle.getModel() + "</p>\n";
        if (singleNotification.getNotificationId() == 1) {
            text += "<p> <b>GEOZONA:</b> " + singleNotification.getGeozoneName() + "</p>\n";
        }
        if (singleNotification.getSpeed() > 0) {
            text += "<p> <b>BRZINA:</b> " + singleNotification.getSpeed() + "</p>\n";
        }
        if (singleNotification.getNotificationId() == 7) {
            text += "<p> <b>TIP PROMENE:</b> " + singleNotification.getFuelType() + "l</p>\n";
        }
        if (singleNotification.getNotificationId() == 8) {
            text += "<p> <b>SERVISNI INTERVAL:</b> " + singleNotification.getServiceIntervalNotif() + "</p>\n";

        }
        if (singleNotification.getNotificationId() == 9) {
            log.info("Slanje notifikacije o registraciji vozila za vozilo " + singleNotification.getRegistration());
            text += "<p> <b>REGISTRACIJA VOZILA :</b> " + singleNotification.getRegistration() + "</p>\n";
        }
        if (singleNotification.getNotificationId() == 10) {
            text += "<p> <b>AKTIVIRAN SENZOR ZA VOZILO  :</b> " + singleNotification.getRegistration() + "</p>\n";
        }
        if (singleNotification.getNotificationId() == 11) {
            text += "<p> <b>BOARD SERVISNI INTERVAL ZA VOZILO :</b> " + singleNotification.getRegistration() + "</p>\n";
            text += "<p> <b>INTERVAL  :</b> " + singleNotification.getServiceIntervalLengthBoard() + "</p>\n";
        }
        if (singleNotification.getNotificationId() == 12) {
            DecimalFormat df = new DecimalFormat("#.##");

            text += "<p> <b>GPS SERVISNI INTERVAL ZA VOZILO :</b> " + singleNotification.getRegistration() + "</p>\n";
            text += "<p> <b>INTERVAL  :</b> " + singleNotification.getServiceIntervalLengthGps() + "</p>\n";
            text += "<p> <b>TRENUTNA KILOMETRAŽA  :</b> " + Double.parseDouble(df.format(singleNotification.getKilometers())) + "</p>\n";

        }
        text += "<p> <b>PROIZVOĐAČ:</b> " + vehicle.getManufacturer() + "</p>\n </html>";


        NotificationApp notificationApp = new NotificationApp();
        notificationApp.setText(text);
        notificationApp.setSeen(false);
        notificationApp.setHeading(notification.getName());
        notificationApp.setTimestamp(singleNotification.getTimestamp());
        notificationApp.setUserId(optionalUser.get().getUserId());
        notificationAppRepository.save(notificationApp);

        if (singleNotification.isEmail()) {
            try {
                sendMail2(optionalUser.get().getEmail(), "Notifikacija - ORIS 34", text);
                log.info("Uspesan email za notifikaciju korisniku " + optionalUser.get().getName() + " sa emailom " +
                        optionalUser.get().getEmail());
                automaticNotificationLoggerService.saveSuccessNotificationLog(
                    singleNotification.getFirmId(), 
                    optionalUser.get().getName(), 
                    NotificationType.MAIL, 
                    optionalUser.get().getEmail(), 
                    notification.getName()
                );
            } catch (Exception e) {
                log.error("Neuspesan email za notifikaciju korisniku " + optionalUser.get().getName() + " sa emailom " +
                        optionalUser.get().getEmail() + " greska: " + e.getMessage());
                automaticNotificationLoggerService.saveFailedNotificationLog(
                    singleNotification.getUserId(), 
                    optionalUser.get().getName(), 
                    NotificationType.MAIL, 
                    optionalUser.get().getEmail(), 
                    notification.getName(), 
                    e.getMessage()
                );
                e.printStackTrace();
            }
            // sendMail2("petar.lakcevic@gmail.com", "Notifikacija - ORIS 34", text);
        }
        if (singleNotification.isSms()) {
            text = text.replace("<p>", "");
            text = text.replace("<b>", "");
            text = text.replace("</p>", "");
            text = text.replace("</b>", "");
            text = text.replace("<h2>", "");
            text = text.replace("</h2>", "");
            text = text.replace("<html>", "");
            text = text.replace("</html>", "");
            try {
                System.out.println("saljem sms na broj " + optionalUser.get().getPhone().trim() + " sa tekstom " + text);
                sendSms(text, optionalUser.get().getPhone().trim());
                automaticNotificationLoggerService.saveSuccessNotificationLog(singleNotification.getFirmId(), optionalUser.get().getName(), NotificationType.SMS, optionalUser.get().getPhone().trim(), notification.getName());
            } catch (Exception e) {
                automaticNotificationLoggerService.saveFailedNotificationLog(singleNotification.getUserId(), optionalUser.get().getName(), NotificationType.SMS, optionalUser.get().getPhone().trim(), notification.getName(), e.getMessage());
                System.out.println("neuspesan sms " + e.getMessage());

            }
            // sendSms(text, "+381643345522");
        }
        if (singleNotification.isPush()) {

            //Nema potrebe da proverimo da li postoji jer je to vec provereno gore
            User user = optionalUser.get();

            List<UserPushNotification> allByUser = userPushNotificationRepository.findAllByUser(user);
            for (UserPushNotification pushNotification : allByUser) {
                try {
                    fcm.send(com.google.firebase.messaging.Message.builder().setToken(pushNotification.getToken()).setNotification(com.google.firebase.messaging.Notification.builder().setTitle(getNotificationTitle(singleNotification, notification)).setBody("Registracioni broj " + singleNotification.getRegistration()).build()).build());
                    //Problem sa brisanjem oko DELETE endpointa koji bi bio public pa je ovo lakse resenje,situacija service interval board

                } catch (Exception e) {
                    //if it has error when fcm sends notif token is then invalid and it needs to be removed from db
                    userPushNotificationRepository.deleteById(pushNotification.getId());
                }
            }

        }
        if (singleNotification.getNotificationId() == 11) {
            notificationVehicleRepository.deleteById(singleNotification.getNmid());
        }
        if (singleNotification.getNotificationId() == 12) {
            notificationVehicleRepository.deleteById(singleNotification.getNmid());
        }
//        if(singleNotification.getNotificationId()==0){
//            notificationVehicleRepository.deleteById(singleNotification.getNmid());
//        }

    }

    private String getNotificationTitle(SingleNotification singleNotification, Notification notification) {
        if (singleNotification.getGreenType() != null && !singleNotification.getGreenType().equalsIgnoreCase("") && !singleNotification.getGreenType().equalsIgnoreCase(" ")) {
            return singleNotification.getGreenType();
        }

        return notification.getName();

    }

    /**
     * slanje maila s prosledjenim primaocem, naslovom i textom
     */
    public void sendMail2(String receiver, String heading, String text) throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

//        Session session = Session.getInstance(props, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("test4bees@gmail.com", "heqthaukzpesicxm");
//
//            }
//        });

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mail@galeb.com", "acvm mjfn rbkx scza");

            }
        });

        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress("office@galeb.com", "Oris 34 - noreply"));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
        msg.setSubject(heading);
        msg.setContent(text, "text/html; charset=UTF-8");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(text, "text/html; charset=UTF-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        msg.setContent(multipart);
        Transport.send(msg);
    }

    public NotificationModel updateNotificationModel(NotificationModel notificationModel) {
        System.out.println("Stigao update");
        System.out.println(notificationModel);
        Optional<NotificationVehicle> byId = notificationVehicleRepository.findById(notificationModel.getNmid());
        System.out.println("Updating");
        if (byId.isPresent()) {
            NotificationVehicle notificationVehicle = byId.get();
            notificationVehicle.setVehicleMileage(notificationModel.getVehicleMileage());
            notificationVehicle.setStartPointLat(notificationModel.getStartPointLat());
            notificationVehicle.setStartPointLng(notificationModel.getStartPointLng());
            notificationVehicle.setIsMileage(notificationModel.getMileage());
            notificationVehicle.setStartTimestamp(notificationModel.getStartTimestamp());
            notificationVehicleRepository.save(notificationVehicle);
        }
        return notificationModel;
    }
}
