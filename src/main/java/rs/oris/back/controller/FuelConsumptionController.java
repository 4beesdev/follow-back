package rs.oris.back.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.FuelConsumption;
import rs.oris.back.domain.User;
import rs.oris.back.domain.dto.FmsFuelConsumptionHolderDto;
import rs.oris.back.domain.dto.fuel_consumtion.FuelConsumptionDTO;
import rs.oris.back.service.FuelConsumptionService;
import rs.oris.back.service.UserService;

import java.util.List;

@RestController
@CrossOrigin
public class FuelConsumptionController {

    @Autowired
    private FuelConsumptionService fuelConsumptionService;

    @Autowired
    private UserService userService;
    /**
     * sve potrosnje jednog vozaca
     */
    @GetMapping("api/firm/{firm_id}/driver/{driver_id}/fuel-consumption")
    public Response<List<FuelConsumption>> getAllDriver(@PathVariable("driver_id") int driverId) throws Exception {
        return fuelConsumptionService.getAllByDriver(driverId);
    }
    /**
     * sve potrosnje na jednog pumpi
     */
    @GetMapping("api/firm/{firm_id}/fuel-station/{fuel_station_id}/fuel-consumption")
    public Response<List<FuelConsumption>> getAllFuelStation(@PathVariable("fuel_station_id") int fstId) throws Exception {
        return fuelConsumptionService.getAllByFST(fstId);
    }
    /**
     * istoriju potrosnje jednog vozila
     */
    @GetMapping("api/firm/{firm_id}/vehicle/{vehicle_id}/fuel-consumption")
    public Response<List<FuelConsumption>> getAllVehicle(@PathVariable("vehicle_id") int vehicleId) throws Exception {
        return fuelConsumptionService.getAllByVehicle(vehicleId);
    }

    /**
     * vraca jednu potrosnju po id-u
     */
    @GetMapping("api/firm/{firm_id}/fuel-consumption/{id}")
    public Response<FuelConsumption> getFuelConsumptionById(@PathVariable("id") int fuelConsumptionId) throws Exception {
        return fuelConsumptionService.getSingleFuelConsumption(fuelConsumptionId);
    }
    /**
     * findAll
     */
    @GetMapping("api/firm/{firm_id}/fuel-consumption")
    public Response<FuelConsumptionDTO> getFuelConsumptionForAll(@PathVariable("firm_id") int firmId,@RequestParam("size") int size,@RequestParam("page") int page,@RequestParam("sort-field") String sortField,@RequestParam("order") String order ) throws Exception{
        page--;
        return fuelConsumptionService.getFuelConsumptionForAll(firmId,page,size,sortField,order);
    }

    /**
     * delete
     */
    @Transactional
    @DeleteMapping("/api/firm/{firm_id}/fuel-consumption/{fuelConsumption_id}")
    public boolean deleteFuelConsumption(@PathVariable("fuelConsumption_id") int fuelConsumptionId) throws Exception {
        return fuelConsumptionService.deleteFuelConsumption(fuelConsumptionId);
    }
    /**
     * update
     */
    @PutMapping("/api/firm/{firm_id}/fuel-consumption/{fuelConsumption_id}")
    public Response<FuelConsumption> updateFuelConsumption(@PathVariable("fuelConsumption_id") int fuelConsumptionId, @RequestBody FuelConsumption fuelConsumption) throws Exception {
        return fuelConsumptionService.updateFuelConsumption(fuelConsumptionId, fuelConsumption);
    }
    /**
     * create/save
     * @param vehicleId vozilo
     * @param fstId pumpa
     * @param driverId vozac
     */
    @PostMapping("/api/firm/{firm_id}/driver/{driver_id}/fuel-company/{fuel_company_id}/fuel-station/{fuel_station_id}/vehicle/{vehicle_id}/fuel-consumption")
    public Response<FuelConsumption> createFuelConsumption(@RequestBody FuelConsumption fuelConsumption,@PathVariable("fuel_company_id") int fuelCompany, @PathVariable("vehicle_id") int vehicleId, @PathVariable("fuel_station_id") int fstId, @PathVariable("driver_id") int driverId) throws Exception {
        return fuelConsumptionService.createFuelConsumption(fuelConsumption,vehicleId,fstId,driverId,fuelCompany);
    }
//Load excel file for fuel consumption
    @PostMapping("/api/firm/{firm_id}/fuel/import")
    public ResponseEntity<FmsFuelConsumptionHolderDto> importFuelConsumption(@RequestHeader("Authorization") String jwt, @RequestPart("file") MultipartFile multipartFile, @PathVariable("firm_id") int firmId ) throws Exception {
        String payload = jwt.substring(jwt.indexOf(".") + 1, jwt.lastIndexOf("."));
        byte[] byteArray = Base64.decodeBase64(payload.getBytes());
        String decodedJson = new String(byteArray);
        String username = decodedJson.substring(decodedJson.indexOf(":") + 2, decodedJson.indexOf(",") - 1);
        User user=userService.findByUsername(username);
        return fuelConsumptionService.importFuelConsumption(multipartFile,firmId,user.getEmail());
    }

}
