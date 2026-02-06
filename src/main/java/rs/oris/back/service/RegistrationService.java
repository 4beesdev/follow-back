package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Registration;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.RegistrationRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    /**
     *
     * vraca istoriju registracija jednog vozila
     */
    public Response<List<Registration>> getAllByVehicle(int vehicleId) {
        List<Registration> registrationList = registrationRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(registrationList);
    }
    /**
     *
     * vraca sve registracije jedne firme
     */
    public Response<List<Registration>> getAllByFirm(int firmId) {
        List<Registration> registrationList = registrationRepository.findByVehicleFirmFirmId(firmId);
        return new Response<>(registrationList);
    }

    public List<Registration> getAllByFirmAndStartDateAndEndDate(int firmId, Date startDate,Date endDate) {
        List<Registration> registrationList = registrationRepository.findByVehicleFirmFirmId(firmId);
        registrationList=registrationList.stream().filter(registration -> registration.getRegistrationDate().after(startDate) && registration.getRegistrationDate().before(endDate)).collect(Collectors.toList());
        return registrationList;
    }
    /**
     * create/save
     */
	public Response<Registration> createRegistration(Registration registration, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }
        registration.setVehicle(optionalVehicle.get());
		Registration obj = registrationRepository.save(registration);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     *
     * vraca registraciju po id-u
     */
    public Response<Registration> getSingleRegistration(int registrationId) throws Exception {
        Optional<Registration> optionalRegistration = registrationRepository.findById(registrationId);
        if (!optionalRegistration.isPresent()) {
            throw new Exception("Invalid registration id " + registrationId);
        }
        return new Response<>(optionalRegistration.get());
    }
    /**
     *
     * update
     */
    public Response<Registration> updateRegistration(int registrationId, Registration registration) throws Exception {
        Optional<Registration> optionalRegistration = registrationRepository.findById(registrationId);
        if (!optionalRegistration.isPresent()){
            throw new Exception("Invalid registration id " +registrationId);
        }

        Registration old = optionalRegistration.get();

		// Update fields

        old.setResponsible(registration.getResponsible());
        
        old.setNote(registration.getNote());
        
        old.setAmount(registration.getAmount());

		// End of update fields

        Registration savedRegistration = registrationRepository.save(old);

        if (savedRegistration==null){
            throw new Exception("Failed to save registration with id "+registrationId);
        }
        return new Response<>(savedRegistration);
    }
    /**
     *
     * delete
     */
    public boolean deleteRegistration(int registrationId) throws Exception {
        Optional<Registration> optionalRegistration = registrationRepository.findById(registrationId);

        if (!optionalRegistration.isPresent()){
            throw new Exception("Invalid registration id "+registrationId);
        }

        registrationRepository.deleteById(registrationId);

        return true;
    }



}
