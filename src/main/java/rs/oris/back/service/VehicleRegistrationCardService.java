package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.VehicleRegistrationCard;
import rs.oris.back.repository.VehicleRegistrationCardRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleRegistrationCardService {

    @Autowired
    private VehicleRegistrationCardRepository vehicleRegistrationCardRepository;
    @Autowired
    private VehicleRepository vehicleRepository;


    /**
     *
     * vraca registracije vozila
     */
    public Response<List<VehicleRegistrationCard>> getByVehicle(int vehicleId) {
        List<VehicleRegistrationCard> vehicleRegistrationCardList = vehicleRegistrationCardRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(vehicleRegistrationCardList);
    }
    /**
     *
     * dodaje entitet //spaja vozilo s registrarskom karticom
     */
	public Response<VehicleRegistrationCard> createVehicleRegistrationCard(VehicleRegistrationCard vehicleRegistrationCard, int vehicleId) throws Exception {

        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }

		VehicleRegistrationCard obj = vehicleRegistrationCardRepository.save(vehicleRegistrationCard);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     * vraca jedan po id-u
     */
    public Response<VehicleRegistrationCard> getSingleVehicleRegistrationCard(int vehicleRegistrationCardId) throws Exception {
        Optional<VehicleRegistrationCard> optionalVehicleRegistrationCard = vehicleRegistrationCardRepository.findById(vehicleRegistrationCardId);
        if (!optionalVehicleRegistrationCard.isPresent()) {
            throw new Exception("Invalid vehicleRegistrationCard id " + vehicleRegistrationCardId);
        }
        return new Response<>(optionalVehicleRegistrationCard.get());
    }
    /**
     *
     * azurira entitet
     */
    public Response<VehicleRegistrationCard> updateVehicleRegistrationCard(int vehicleRegistrationCardId, VehicleRegistrationCard vehicleRegistrationCard) throws Exception {
        Optional<VehicleRegistrationCard> optionalVehicleRegistrationCard = vehicleRegistrationCardRepository.findById(vehicleRegistrationCardId);
        if (!optionalVehicleRegistrationCard.isPresent()){
            throw new Exception("Invalid vehicleRegistrationCard id " +vehicleRegistrationCardId);
        }

        VehicleRegistrationCard old = optionalVehicleRegistrationCard.get();

		// Update fields
        
        old.setExpieryDate(vehicleRegistrationCard.getExpieryDate());
        old.setExtendedDate(vehicleRegistrationCard.getExtendedDate());
        

		// End of update fields

        VehicleRegistrationCard savedVehicleRegistrationCard = vehicleRegistrationCardRepository.save(old);

        if (savedVehicleRegistrationCard==null){
            throw new Exception("Failed to save vehicleRegistrationCard with id "+vehicleRegistrationCardId);
        }
        return new Response<>(savedVehicleRegistrationCard);
    }
    /**
     *
     * brise entitet po id-u //ukida vezu
     */
    public boolean deleteVehicleRegistrationCard(int vehicleRegistrationCardId) throws Exception {
        Optional<VehicleRegistrationCard> optionalVehicleRegistrationCard = vehicleRegistrationCardRepository.findById(vehicleRegistrationCardId);

        if (!optionalVehicleRegistrationCard.isPresent()){
            throw new Exception("Invalid vehicleRegistrationCard id "+vehicleRegistrationCardId);
        }

        vehicleRegistrationCardRepository.deleteById(vehicleRegistrationCardId);

        return true;
    }


}
