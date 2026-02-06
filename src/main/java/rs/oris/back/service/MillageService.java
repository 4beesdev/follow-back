package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Millage;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.MillageRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MillageService {

    @Autowired
    private MillageRepository millageRepository;

    @Autowired
    private VehicleRepository vehicleRepository;
    /**
     * Vraca istoriju kilometrazi jednog vozila
     * @param vehicleId vozilo
     */
    public Response<List<Millage>> getAllForVehicle(int vehicleId) {
        List<Millage> millageList = millageRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(millageList);
    }

    public List<Millage> getAllForVehicleByStartAndEndDate(int vehicleId, Date startDate, Date endDate) {
        List<Millage> millageList = millageRepository.findByVehicleVehicleId(vehicleId);
        millageList=millageList.stream().filter(millage -> millage.getDate().after(startDate) && millage.getDate().before(endDate)).collect(Collectors.toList());
        return millageList;
    }

    /**
     * create/save
     */
    public Response<Millage> createMillage(Millage millage, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }

        millage.setVehicle(optionalVehicle.get());
        Millage obj = millageRepository.save(millage);
        if (obj == null) {
            throw new Exception("Creation failed");
        }
        return new Response<>(obj);
    }
    /**
     * vraca jedan objekat kilometraze prema id-u
     * @param millageId id
     */
    public Response<Millage> getSingleMillage(int millageId) throws Exception {
        Optional<Millage> optionalMillage = millageRepository.findById(millageId);
        if (!optionalMillage.isPresent()) {
            throw new Exception("Invalid millage id " + millageId);
        }
        return new Response<>(optionalMillage.get());
    }
    /**
     * update
     */
    public Response<Millage> updateMillage(int millageId, Millage millage) throws Exception {
        Optional<Millage> optionalMillage = millageRepository.findById(millageId);
        if (!optionalMillage.isPresent()){
            throw new Exception("Invalid millage id " +millageId);
        }

        Millage old = optionalMillage.get();

        // Update fields


        old.setAmount(millage.getAmount());
        old.setDate(millage.getDate());

        // End of update fields

        Millage savedMillage = millageRepository.save(old);

        if (savedMillage==null){
            throw new Exception("Failed to save millage with id "+millageId);
        }
        return new Response<>(millage);
    }
    /**
     * delete
     */
    public boolean deleteMillage(int millageId) throws Exception {
        Optional<Millage> optionalMillage = millageRepository.findById(millageId);

        if (!optionalMillage.isPresent()){
            throw new Exception("Invalid millage id "+millageId);
        }

        millageRepository.deleteById(millageId);

        return true;
    }
    
}
