package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.TechnicalInspection;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.TechnicalInspectionRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TechnicalInspectionService {

    @Autowired
    private TechnicalInspectionRepository technicalInspectionRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * vraca sve inspekcije vozila
     */
    public Response<List<TechnicalInspection>> getAll(int vehicleId) {
        List<TechnicalInspection> technicalInspectionList = technicalInspectionRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(technicalInspectionList);
    }
    public List<TechnicalInspection> getAllByStartDateAndEndDate(int vehicleId,Date startDate, Date endDate) {
        List<TechnicalInspection> technicalInspectionList = technicalInspectionRepository.findByVehicleVehicleId(vehicleId);
        technicalInspectionList=technicalInspectionList.stream().filter(technicalInspection -> technicalInspection.getDate().after(startDate) && technicalInspection.getDate().before(endDate)).collect(Collectors.toList());
        return technicalInspectionList;
    }
    /**
     *
     * vraca sve inspekcije firme
     */
    public Response<List<TechnicalInspection>> getAllFirm(int firmId) {
        List<TechnicalInspection> technicalInspectionList = technicalInspectionRepository.findByVehicleFirmFirmId(firmId);
        return new Response<>(technicalInspectionList);
    }
    /**
     *
     * cuva novu inspekciju
     */
	public Response<TechnicalInspection> createTechnicalInspection(TechnicalInspection technicalInspection, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }

        technicalInspection.setVehicle(optionalVehicle.get());
		TechnicalInspection obj = technicalInspectionRepository.save(technicalInspection);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     * vraca inspekciju po id-u
     */
    public Response<TechnicalInspection> getSingleTechnicalInspection(int technicalInspectionId) throws Exception {
        Optional<TechnicalInspection> optionalTechnicalInspection = technicalInspectionRepository.findById(technicalInspectionId);
        if (!optionalTechnicalInspection.isPresent()) {
            throw new Exception("Invalid technicalInspection id " + technicalInspectionId);
        }
        return new Response<>(optionalTechnicalInspection.get());
    }
    /**
     *
     * azurira inspekciju po id-u
     */
    public Response<TechnicalInspection> updateTechnicalInspection(int technicalInspectionId, TechnicalInspection technicalInspection) throws Exception {
        Optional<TechnicalInspection> optionalTechnicalInspection = technicalInspectionRepository.findById(technicalInspectionId);
        if (!optionalTechnicalInspection.isPresent()){
            throw new Exception("Invalid technicalInspection id " +technicalInspectionId);
        }

        TechnicalInspection old = optionalTechnicalInspection.get();

		// Update fields
        

        old.setDate(technicalInspection.getDate());
        old.setPeriod(technicalInspection.getPeriod());

		// End of update fields

        TechnicalInspection savedTechnicalInspection = technicalInspectionRepository.save(old);

        if (savedTechnicalInspection==null){
            throw new Exception("Failed to save technicalInspection with id "+technicalInspectionId);
        }
        return new Response<>(savedTechnicalInspection);
    }
    /**
     * brise inspekciju po id-u
     */
    public boolean deleteTechnicalInspection(int technicalInspectionId) throws Exception {
        Optional<TechnicalInspection> optionalTechnicalInspection = technicalInspectionRepository.findById(technicalInspectionId);

        if (!optionalTechnicalInspection.isPresent()){
            throw new Exception("Invalid technicalInspection id "+technicalInspectionId);
        }

        technicalInspectionRepository.deleteById(technicalInspectionId);

        return true;
    }



}
