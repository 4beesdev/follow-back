package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.FuelCompany;
import rs.oris.back.domain.FuelStation;
import rs.oris.back.repository.FuelCompanyRepository;
import rs.oris.back.repository.FuelStationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FuelStationService {

    @Autowired
    private FuelStationRepository fuelStationRepository;
    @Autowired
    private FuelCompanyRepository fuelCompanyRepository;

    /**
     * vraca sve pumpe jedne firme
     */
    public Response<List<FuelStation>> getAllByFirm(int firmId) {
        List<FuelStation> fuelStationList = fuelStationRepository.findByFuelCompanyFirmFirmId(firmId);
        return new Response<>(fuelStationList);
    }
    /**
     * create/save
     */
	public Response<FuelStation> createFuelStation(FuelStation fuelStation, int fuelCompanyId) throws Exception {
        Optional<FuelCompany> optionalFuelCompany = fuelCompanyRepository.findById(fuelCompanyId);
        if(!optionalFuelCompany.isPresent()){
            throw new Exception("Invalid fuel company id");
        }
        fuelStation.setFuelCompany(optionalFuelCompany.get());

		FuelStation obj = fuelStationRepository.save(fuelStation);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     * vraca jednu pumpu po id-u
     */
    public Response<FuelStation> getSingleFuelStation(int fuelStationId) throws Exception {
        Optional<FuelStation> optionalFuelStation = fuelStationRepository.findById(fuelStationId);
        if (!optionalFuelStation.isPresent()) {
            throw new Exception("Invalid fuelStation id " + fuelStationId);
        }
        return new Response<>(optionalFuelStation.get());
    }
    /**
     * update
     */
    public Response<FuelStation> updateFuelStation(int fuelStationId, FuelStation fuelStation) throws Exception {
        Optional<FuelStation> optionalFuelStation = fuelStationRepository.findById(fuelStationId);
        if (!optionalFuelStation.isPresent()){
            throw new Exception("Invalid fuelStation id " +fuelStationId);
        }

        FuelStation old = optionalFuelStation.get();

		// Update fields

        old.setName(fuelStation.getName());
        
        old.setPlace(fuelStation.getPlace());
        
        old.setAddress(fuelStation.getAddress());
        
        old.setPhone(fuelStation.getPhone());
        
        old.setFax(fuelStation.getFax());
        
        old.setEmail(fuelStation.getEmail());
        
        old.setContactPerson(fuelStation.getContactPerson());
        
        old.setActive(fuelStation.isActive());

		// End of update fields

        FuelStation savedFuelStation = fuelStationRepository.save(old);

        if (savedFuelStation==null){
            throw new Exception("Failed to save fuelStation with id "+fuelStationId);
        }
        return new Response<>(savedFuelStation);
    }
    /**
     * delete
     */
    public boolean deleteFuelStation(int fuelStationId) throws Exception {
        Optional<FuelStation> optionalFuelStation = fuelStationRepository.findById(fuelStationId);

        if (!optionalFuelStation.isPresent()){
            throw new Exception("Invalid fuelStation id "+fuelStationId);
        }

        fuelStationRepository.deleteById(fuelStationId);

        return true;
    }



}
