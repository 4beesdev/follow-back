package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Driver;
import rs.oris.back.domain.PN;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.DriverRepository;
import rs.oris.back.repository.PNRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PNService {

    @Autowired
    private PNRepository pNRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     *
     * vraca sve putne naloge jednog vozaca
     */
    public Response<List<PN>> getAllForDrivur(int driverId) {
        List<PN> pNList = pNRepository.findByDriverDriverId(driverId);
        return new Response<>(pNList);
}
    /**
     *
     * vraca sve putne naloge jedne firme
     */
    public Response<List<PN>> getAllForFirm(int firmid) {
        List<PN> pNList = pNRepository.findByVehicleFirmFirmId(firmid);
        return new Response<>(pNList);
    }
    /**
     * vraca sve putne naloge vezane za prosledjeno vozilo
     */
    public Response<List<PN>> getAllForVehicle(int vehicleId) {
        List<PN> pNList = pNRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(pNList);
    }
    /**
     * create/save
     */
	public Response<PN> createPN(PN pN, int vehicleId, int driverId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }

        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Invalid driver id");
        }

        pN.setVehicle(optionalVehicle.get());
        pN.setDriver(optionalDriver.get());


		PN obj = pNRepository.save(pN);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     *
     * vraca jedan putni nalog preko id-a
     */
    public Response<PN> getSinglePN(int pNId) throws Exception {
        Optional<PN> optionalPN = pNRepository.findById(pNId);
        if (!optionalPN.isPresent()) {
            throw new Exception("Invalid pN id " + pNId);
        }
        return new Response<>(optionalPN.get());
    }
    /**
     * update
     */
    public Response<PN> updatePN(int pNId, PN pN) throws Exception {
        Optional<PN> optionalPN = pNRepository.findById(pNId);
        if (!optionalPN.isPresent()){
            throw new Exception("Invalid pN id " +pNId);
        }

        PN old = optionalPN.get();

		// Update fields
        
        old.setCompanyOwner(pN.getCompanyOwner());
        
        old.setTrailer(pN.isTrailer());
        
        old.setCompanyAddress(pN.getCompanyAddress());
        
        old.setPlaceOfIssue(pN.getPlaceOfIssue());
        
        old.setOthers(pN.getOthers());

        old.setNameOfUser(pN.getNameOfUser());
        
        old.setRegistrationField(pN.getRegistrationField());
        
        old.setTransportType(pN.getTransportType());
        
        old.setRoute(pN.getRoute());
        
        old.setTechnical(pN.getTechnical());
        
        old.setTrailerRegistration(pN.getTrailerRegistration());

        old.setTrailerMark(pN.getTrailerMark());
        
        old.setTrailerRegistrationField(pN.getTrailerRegistrationField());
        

		// End of update fields

        PN savedPN = pNRepository.save(old);

        if (savedPN==null){
            throw new Exception("Failed to save pN with id "+pNId);
        }
        return new Response<>(savedPN);
    }

    public boolean deletePN(int pNId) throws Exception {
        Optional<PN> optionalPN = pNRepository.findById(pNId);

        if (!optionalPN.isPresent()){
            throw new Exception("Invalid pN id "+pNId);
        }

        pNRepository.deleteById(pNId);

        return true;
    }



}
