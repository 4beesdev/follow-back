package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.AddedInsurance;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.AddedInsuranceRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;
/**
 * CRUD za AddedInsurance
 */
@Service
public class AddedInsuranceService {

    @Autowired
    private AddedInsuranceRepository addedInsuranceRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * vraca sva osiguranja vozila
     * @param vehicleId vozilo
     */
    public Response<List<AddedInsurance>> getAllForVehicle(int vehicleId) {
        List<AddedInsurance> addedInsuranceList = addedInsuranceRepository.findAllByVehicleVehicleId(vehicleId);
        return new Response<>(addedInsuranceList);
    }

    /**
     * create/save
     */
	public Response<AddedInsurance> createAddedInsurance(AddedInsurance addedInsurance, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Vehicle not present");
        }
        addedInsurance.setVehicle(optionalVehicle.get());

		AddedInsurance obj = addedInsuranceRepository.save(addedInsurance);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     * vraca konkretno osiguranje preko id-a
     * @param addedInsuranceId id
     */
    public Response<AddedInsurance> getSingleAddedInsurance(int addedInsuranceId) throws Exception {
        Optional<AddedInsurance> optionalAddedInsurance = addedInsuranceRepository.findById(addedInsuranceId);
        if (!optionalAddedInsurance.isPresent()) {
            throw new Exception("Invalid addedInsurance id " + addedInsuranceId);
        }
        return new Response<>(optionalAddedInsurance.get());
    }
    /**
     * update
     */
    public Response<AddedInsurance> updateAddedInsurance(int addedInsuranceId, AddedInsurance addedInsurance) throws Exception {
        Optional<AddedInsurance> optionalAddedInsurance = addedInsuranceRepository.findById(addedInsuranceId);
        if (!optionalAddedInsurance.isPresent()){
            throw new Exception("Invalid addedInsurance id " +addedInsuranceId);
        }

        AddedInsurance old = optionalAddedInsurance.get();

		// Update fields

        old.setType(addedInsurance.getType());
        old.setName(addedInsurance.getName());
        old.setDateFrom(addedInsurance.getDateFrom());
        old.setDateTo(addedInsurance.getDateTo());
        old.setExpenses(addedInsurance.getExpenses());

		// End of update fields
        AddedInsurance savedAddedInsurance = addedInsuranceRepository.save(old);
        if (savedAddedInsurance==null){
            throw new Exception("Failed to save addedInsurance with id "+addedInsuranceId);
        }
        return new Response<>(savedAddedInsurance);
    }
    /**
     * delete
     */
    public boolean deleteAddedInsurance(int addedInsuranceId) throws Exception {
        Optional<AddedInsurance> optionalAddedInsurance = addedInsuranceRepository.findById(addedInsuranceId);
        if (!optionalAddedInsurance.isPresent()){
            throw new Exception("Invalid addedInsurance id "+addedInsuranceId);
        }
        addedInsuranceRepository.deleteById(addedInsuranceId);
        return true;
    }
}
