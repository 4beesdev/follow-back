package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.FirstAid;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.FirstAidRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FirstAidService {

    @Autowired
    private FirstAidRepository firstAidRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    /**
     * vraca sve jedinice prve pomoci jedne firme
     */
    public Response<List<FirstAid>> getAllForFirm(int firmId) {
        List<FirstAid> firstAidList = firstAidRepository.findByVehicleFirmFirmId(firmId);
        return new Response<>(firstAidList);
    }
    /**
     * vraca sve jedinice prve pomoci u vozilu
     */
    public Response<List<FirstAid>> getAllForVeh(int vehicleId) {
        List<FirstAid> firstAidList = firstAidRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(firstAidList);
    }
    /**
     * create/save
     */
	public Response<FirstAid> createFirstAid(FirstAid firstAid, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()){
            throw new Exception("Invalid vehicle id");
        }

        firstAid.setVehicle(optionalVehicle.get());
		FirstAid obj = firstAidRepository.save(firstAid);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     * vraca jednu jedinicu prve pomoci po id-u
     */
    public Response<FirstAid> getSingleFirstAid(int firstAidId) throws Exception {
        Optional<FirstAid> optionalFirstAid = firstAidRepository.findById(firstAidId);
        if (!optionalFirstAid.isPresent()) {
            throw new Exception("Invalid firstAid id " + firstAidId);
        }
        return new Response<>(optionalFirstAid.get());
    }
    /**
     * update
     */
    public Response<FirstAid> updateFirstAid(int firstAidId, FirstAid firstAid) throws Exception {
        Optional<FirstAid> optionalFirstAid = firstAidRepository.findById(firstAidId);
        if (!optionalFirstAid.isPresent()){
            throw new Exception("Invalid firstAid id " +firstAidId);
        }

        FirstAid old = optionalFirstAid.get();

		// Update fields
        
        old.setExpiryDate(firstAid.getExpiryDate());

		// End of update fields

        FirstAid savedFirstAid = firstAidRepository.save(old);

        if (savedFirstAid==null){
            throw new Exception("Failed to save firstAid with id "+firstAidId);
        }
        return new Response<>(savedFirstAid);
    }
    /**
     * delete
     */
    public boolean deleteFirstAid(int firstAidId) throws Exception {
        Optional<FirstAid> optionalFirstAid = firstAidRepository.findById(firstAidId);

        if (!optionalFirstAid.isPresent()){
            throw new Exception("Invalid firstAid id "+firstAidId);
        }

        firstAidRepository.deleteById(firstAidId);

        return true;
    }

}
