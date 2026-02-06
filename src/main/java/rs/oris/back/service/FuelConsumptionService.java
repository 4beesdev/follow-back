package rs.oris.back.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.domain.dto.FmsFuelConsumptionDTO;
import rs.oris.back.domain.dto.FmsFuelConsumptionHolderDto;
import rs.oris.back.domain.dto.fuel_consumtion.FuelConsumptionDTO;
import rs.oris.back.repository.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FuelConsumptionService {

    @Autowired
    private FuelConsumptionRepository fuelConsumptionRepository;
    @Autowired
    private XlsImportLoggerService xlsImportLoggerService;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private FuelStationRepository fuelStationRepository;

    @Autowired
    private FuelCompanyRepository fuelCompanyRepository;
    @Autowired
    private FirmRepository firmRepository;
    /**
     * sve potrosnje jednog vozaca
     */
    public Response<List<FuelConsumption>> getAllByDriver(int driverId) {
        List<FuelConsumption> fuelConsumptionList = fuelConsumptionRepository.findByDriverDriverId(driverId);
        return new Response<>(fuelConsumptionList);
    }
    /**
     * istoriju potrosnje jednog vozila
     */
    public Response<List<FuelConsumption>> getAllByVehicle(int vehicleId) {
        List<FuelConsumption> fuelConsumptionList = fuelConsumptionRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(fuelConsumptionList);
    }

    public List<FuelConsumption> getAllByVehicleByDate(int vehicleId, Date dateFrom, Date dateTo) {
        List<FuelConsumption> fuelConsumptionList = fuelConsumptionRepository.findByVehicleVehicleId(vehicleId);
        fuelConsumptionList=fuelConsumptionList.stream().filter(fuelConsumption -> fuelConsumption.getDate().after(dateFrom) && fuelConsumption.getDate().before(dateTo)).collect(Collectors.toList());
        return fuelConsumptionList;
    }


    public Response<List<FuelConsumption>> getAllByFST(int fstId) {
        List<FuelConsumption> fuelConsumptionList = fuelConsumptionRepository.findByFuelStationFuelStationId(fstId);
        return new Response<>(fuelConsumptionList);
    }

    /**
     * create/save
     * @param vehicleId vozilo
     * @param fstId pumpa
     * @param driverId vozac
     */
    public Response<FuelConsumption> createFuelConsumption(FuelConsumption fuelConsumption, int vehicleId, int fstId, int driverId, int fuelCompany) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Invalid driver id");
        }
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }
        Optional<FuelStation> optionalFuelStation = fuelStationRepository.findById(fstId);
        if (!optionalFuelStation.isPresent()) {
            throw new Exception("invalid fuel station id");
        }

        Optional<FuelCompany> optionalFuelCompany = fuelCompanyRepository.findById(fuelCompany);
        if (!optionalFuelCompany.isPresent()) {
            throw new Exception("invalid fuel company id");
        }



        fuelConsumption.setDriver(optionalDriver.get());
        fuelConsumption.setVehicle(optionalVehicle.get());
        fuelConsumption.setFuelStation(optionalFuelStation.get());
        fuelConsumption.setFuelCompany(optionalFuelCompany.get());
        FuelConsumption obj = fuelConsumptionRepository.save(fuelConsumption);
        if (obj == null) {
            throw new Exception("Creation failed");
        }
        return new Response<>(obj);
    }
    /**
     * vraca jednu potrosnju po id-u
     */
    public Response<FuelConsumption> getSingleFuelConsumption(int fuelConsumptionId) throws Exception {
        Optional<FuelConsumption> optionalFuelConsumption = fuelConsumptionRepository.findById(fuelConsumptionId);
        if (!optionalFuelConsumption.isPresent()) {
            throw new Exception("Invalid fuelConsumption id " + fuelConsumptionId);
        }
        return new Response<>(optionalFuelConsumption.get());
    }
    /**
     * update
     */
    public Response<FuelConsumption> updateFuelConsumption(int fuelConsumptionId, FuelConsumption fuelConsumption) throws Exception {
        Optional<FuelConsumption> optionalFuelConsumption = fuelConsumptionRepository.findById(fuelConsumptionId);
        if (!optionalFuelConsumption.isPresent()) {
            throw new Exception("Invalid fuelConsumption id " + fuelConsumptionId);
        }

        FuelConsumption old = optionalFuelConsumption.get();
        // Update fields

        fuelConsumption.setFuelConsumptionId(fuelConsumptionId);

        old.setValue(fuelConsumption.getValue());

        old.setAmount(fuelConsumption.getAmount());

        old.setFuel(fuelConsumption.getFuel());

        old.setKm(fuelConsumption.getKm());

        old.setAccount(fuelConsumption.getAccount());


        // End of update fields

        FuelConsumption savedFuelConsumption = fuelConsumptionRepository.save(old);

        if (savedFuelConsumption == null) {
            throw new Exception("Failed to save fuelConsumption with id " + fuelConsumptionId);
        }
        return new Response<>(savedFuelConsumption);
    }
    /**
     * delete
     */
    public boolean deleteFuelConsumption(int fuelConsumptionId) throws Exception {
        Optional<FuelConsumption> optionalFuelConsumption = fuelConsumptionRepository.findById(fuelConsumptionId);

        if (!optionalFuelConsumption.isPresent()) {
            throw new Exception("Invalid fuelConsumption id " + fuelConsumptionId);
        }

        fuelConsumptionRepository.deleteById(fuelConsumptionId);

        return true;
    }

    public Response<FuelConsumptionDTO> getFuelConsumptionForAll(int firmId, int page, int size, String sortField, String order) {
        Firm firm = firmRepository.findById(firmId)
                .orElseThrow(() -> new IllegalArgumentException("Firm with id " + firmId + " does not exist"));
        Sort.Direction direction= Objects.equals(order, "asc") ? Sort.Direction.ASC:Sort.Direction.DESC;
        Sort sort=Sort.by(direction,sortField);
        Pageable pageable = PageRequest.of(page, size,sort);
        Page<FuelConsumption> fuelConsumptionsList = fuelConsumptionRepository.findAllByVehicle_Firm(firm,pageable);
        return new Response<>(new FuelConsumptionDTO(fuelConsumptionsList.getContent(),fuelConsumptionsList.getTotalElements()));
    }

    private Date parseMultipleDateFormats(String date) throws Exception {

        List<SimpleDateFormat> dateFormats=new LinkedList<SimpleDateFormat>(){{
           add(new SimpleDateFormat("dd/MM/yyyy"));
              add(new SimpleDateFormat("dd.MM.yyyy."));
            add(new SimpleDateFormat("d.M.yyyy"));

        }};
        boolean parsingSuccessful = false;
        Date parsedDate = null;

        for (SimpleDateFormat dateFormat : dateFormats) {
            try {


                parsedDate =dateFormat.parse(date);
                System.out.println("Parsed successfully with format: " + dateFormat.toPattern());
                parsingSuccessful = true;
                break; // Exit loop if successfully parsed
            } catch (Exception e) {
                System.out.println("Failed to parse with format: " +  dateFormat.toPattern());
            }
        }
        if (!parsingSuccessful) {
            throw new DateTimeParseException("Failed to parse the date with any of the provided formats.", dateFormats.toString(), 0);
        } else {
            return parsedDate;
        }
    }

    public ResponseEntity<FmsFuelConsumptionHolderDto> importFuelConsumption(MultipartFile multipartFile, int firmId, String email) {
        if (multipartFile.isEmpty()) {
            // Handle empty file error
            return ResponseEntity.badRequest().body(new FmsFuelConsumptionHolderDto("No file uploaded", null, null));
        }

        List<FmsFuelConsumptionDTO> successList = new LinkedList<>();
        List<FmsFuelConsumptionDTO> failList = new LinkedList<>();
        Map<String, FuelConsumption> fuelConsumptionList = new HashMap<>();
        Boolean isError = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Optional<Firm> firmOptional = firmRepository.findById(firmId);
        if (!firmOptional.isPresent()) {
            xlsImportLoggerService.saveFailLog("Failed to import fuel consumption data. Firm " + firmId + " does not exist", email);
            return ResponseEntity.badRequest().body(new FmsFuelConsumptionHolderDto("Firm with id " + firmId + " does not exist", null, null));
        }

        // Load Excel file
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Workbook workbook = null;
            String filename = multipartFile.getOriginalFilename();

            if (filename != null && filename.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (filename != null && filename.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                return ResponseEntity.badRequest().body(new FmsFuelConsumptionHolderDto("Invalid file format. Only .xls and .xlsx are supported", null, null));
            }

            int rowNum = 0;
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            Iterator<Row> rowIterator = sheet.iterator();
            // Skip the header row
            Row row = rowIterator.next();
            rowNum++;

            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                rowNum++;
                Date date;

                try {
                    String vehicleRegistration = row.getCell(0).getStringCellValue();
                    try {
                        String e=row.getCell(1).getStringCellValue().trim();
//

                         date = dateFormat.parse(e);
                    } catch (Exception e) {
                        try{

                            date = DateUtil.getJavaDate(row.getCell(1).getNumericCellValue());
                        }
                        catch (Exception e2){
                            xlsImportLoggerService.saveFailLog("Date is not in correct format " + row.getCell(1).getStringCellValue() + " it must be in DD/MM/YYYY or DD.MM.YYYY. in row: " + row.getRowNum(), email);
                            failList.add(new FmsFuelConsumptionDTO("Date is not in correct format " + row.getCell(1).getStringCellValue() + " it must be in DD/MM/YYYY or DD.MM.YYYY. in row: " + row.getRowNum(), false, rowNum));
                            isError = true;
                            continue;
                        }

                    }

                    String gorivnaKompanija = row.getCell(2).getStringCellValue();
                    String gorivnaStanica = row.getCell(3).getStringCellValue();
                    String fuelType = row.getCell(4).getStringCellValue();
                    Double amount = 0.0;
                    try{
                        amount =row.getCell(5).getNumericCellValue();
                    }catch (Exception e){
                        amount=Double.valueOf(row.getCell(5).getStringCellValue());
                    }
                    Double quantity =0.0;
                    try{
                        quantity= row.getCell(6).getNumericCellValue();
                    }
                    catch (Exception e){
                        quantity=Double.valueOf(row.getCell(6).getStringCellValue());
                    }
                    Double kilometers = 0.0;
                    try {
                        kilometers = row.getCell(7).getNumericCellValue();
                    } catch (Exception e) {
                        // kilometers are optional
                    }

                    DataFormatter formatter = new DataFormatter();
                    String invoiceNumber = formatter.formatCellValue(row.getCell(8));
                    String driverName = row.getCell(9).getStringCellValue();

                    FuelConsumption fuelConsumption = new FuelConsumption();
                    fuelConsumption.setDate(date);
                    fuelConsumption.setFuel(fuelType);
                    fuelConsumption.setAmount(amount);
                    fuelConsumption.setKm(kilometers);
                    fuelConsumption.setAccount(invoiceNumber);
                    fuelConsumption.setValue(quantity);

                    // Handle vehicle
                    List<Vehicle> optionalVehicle = vehicleRepository.findByRegistrationIgnoreCaseAndTrim(vehicleRegistration);
                    if (optionalVehicle.isEmpty()) {
                        xlsImportLoggerService.saveFailLog("Vehicle with registration " + vehicleRegistration + " does not exist in row: " + row.getRowNum(), email);
                        failList.add(new FmsFuelConsumptionDTO("Vehicle with registration " + vehicleRegistration + " does not exist in row: " + row.getRowNum(), false, rowNum));
                        isError = true;
                        continue;
                    }
                    fuelConsumption.setVehicle(optionalVehicle.get(0));

                    // Handle fuel company
                    List<FuelCompany> optionalFuelCompany = fuelCompanyRepository.findByName(gorivnaKompanija);
                    if (optionalFuelCompany.isEmpty()) {
                        xlsImportLoggerService.saveFailLog("Fuel company with name " + gorivnaKompanija + " does not exist in row: " + row.getRowNum(), email);
                        failList.add(new FmsFuelConsumptionDTO("Fuel company with name " + gorivnaKompanija + " does not exist in row: " + row.getRowNum(), false, rowNum));
                        isError = true;
                        continue;
                    }
                    fuelConsumption.setFuelCompany(optionalFuelCompany.get(0));

                    // Handle fuel station
                    List<FuelStation> optionalFuelStation = fuelStationRepository.findByName(gorivnaStanica);
                    if (optionalFuelStation.isEmpty()) {
                        xlsImportLoggerService.saveFailLog("Fuel station with name " + gorivnaStanica + " does not exist in row: " + row.getRowNum(), email);
                        failList.add(new FmsFuelConsumptionDTO("Fuel station with name " + gorivnaStanica + " does not exist in row: " + row.getRowNum(), false, rowNum));
                        isError = true;
                        continue;
                    }
                    fuelConsumption.setFuelStation(optionalFuelStation.get(0));

                    // Handle driver
                    Optional<Driver> driverOptional = driverRepository.findByNameAndFirm(driverName, firmOptional.get());
                    if (!driverOptional.isPresent()) {
                        xlsImportLoggerService.saveFailLog("Driver with name " + driverName + " does not exist in row: " + row.getRowNum(), email);
                        failList.add(new FmsFuelConsumptionDTO("Driver with name " + driverName + " does not exist in row: " + row.getRowNum(), false, rowNum));
                        isError = true;
                        continue;
                    }
                    fuelConsumption.setDriver(driverOptional.get());

                    successList.add(new FmsFuelConsumptionDTO(null, true, rowNum));
                    fuelConsumptionList.put(String.valueOf(UUID.randomUUID()), fuelConsumption);

                } catch (Exception e) {
                    xlsImportLoggerService.saveFailLog("Failed to import fuel consumption data. Please check if the data is in the correct format in row: " + row.getRowNum(), email);
                    isError = true;
                    failList.add(new FmsFuelConsumptionDTO("Row " + rowNum + " does not have all required elements", false, rowNum));
                }
            }

            if (!isError && failList.isEmpty()) {
                fuelConsumptionList.forEach((uuid, fuelConsumption) -> {
                    fuelConsumptionRepository.save(fuelConsumption);
                    xlsImportLoggerService.saveSuccessLog(fuelConsumption.getVehicle().getRegistration(), email);
                });
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exception
            xlsImportLoggerService.saveFailLog("Failed to import fuel consumption data", email);
            return ResponseEntity.badRequest().body(new FmsFuelConsumptionHolderDto("Failed to import fuel consumption data", null, null));
        }

        return ResponseEntity.ok(new FmsFuelConsumptionHolderDto("Fuel consumption data imported successfully", successList, failList));
    }

}
