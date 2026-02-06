package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.repository.InterventionRepository;
import rs.oris.back.repository.ServiceLocationRepository;
import rs.oris.back.repository.VehicleRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InterventionService {

    @Autowired
    private InterventionRepository interventionRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private ServiceLocationRepository serviceLocationRepository;
    /**
     * vraca sve intervencije vozila
     */
    public Response<List<InterventionDTO>> getAllForVehicle(int vehicleId) {

        List<InterventionDTO> interventionList = interventionRepository.findByVehicleVehicleId(vehicleId).map(x->new InterventionDTO(x.getInterventionId(),x.getDoneDate(),x.getDoneTime(),x.getNeededDate(),x.getNeededTime(),x.getDescription(),x.getPrice(),x.getNote(),x.isDone(),x.isNeeded(),x.getVehicle(),x.getServiceLocation())).collect(Collectors.toCollection(ArrayList::new));
        return new Response<>(interventionList);
    }

    @Transactional
    public List<InterventionDTO> getAllForVehicleFilteredByStartAndEndDate(int vehicleId,Date startDoneDate,Date endDoneDate) {

        List<InterventionDTO> interventionList = interventionRepository.findByVehicleVehicleId(vehicleId).map(x->new InterventionDTO(x.getInterventionId(),x.getDoneDate(),x.getDoneTime(),x.getNeededDate(),x.getNeededTime(),x.getDescription(),x.getPrice(),x.getNote(),x.isDone(),x.isNeeded(),x.getVehicle(),x.getServiceLocation())).collect(Collectors.toCollection(ArrayList::new));
        interventionList= interventionList.stream().filter(element-> element.getDoneDate().before(endDoneDate) && element.getDoneDate().after(startDoneDate)).collect(Collectors.toList());
        return interventionList;
    }

    /**
     * vraca sve intervencije firme
     */
    public Response<ArrayList<InterventionDTO>> getIntFirm(int firmId) {
        ArrayList<InterventionDTO> interventionList = interventionRepository.findByVehicleFirmFirmId(firmId).map(x->new InterventionDTO(x.getInterventionId(),x.getDoneDate(),x.getDoneTime(),x.getNeededDate(),x.getNeededTime(),x.getDescription(),x.getPrice(),x.getNote(),x.isDone(),x.isNeeded(),x.getVehicle(),x.getServiceLocation())).collect(Collectors.toCollection(ArrayList::new));
        return new Response<>(interventionList);
    }
    /**
     * create/save
     */
    public Response<Intervention> createIntervention(Intervention intervention, int vehicleId, int slId) throws Exception {
        Optional<ServiceLocation> optionalServiceLocation = serviceLocationRepository.findById(slId);
        if (!optionalServiceLocation.isPresent()) {
            throw new Exception("Invalid service location id");
        }

        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }

        intervention.setServiceLocation(optionalServiceLocation.get());
        intervention.setVehicle(optionalVehicle.get());
		Intervention obj = interventionRepository.save(intervention);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     * vraca konkretnu intervenciju
     */
    public Response<Intervention> getSingleIntervention(int interventionId) throws Exception {
        Optional<Intervention> optionalIntervention = interventionRepository.findById(interventionId);
        if (!optionalIntervention.isPresent()) {
            throw new Exception("Invalid intervention id " + interventionId);
        }
        return new Response<>(optionalIntervention.get());
    }
    /**
     * update
     */
    public Response<Intervention> updateIntervention(int interventionId, Intervention intervention) throws Exception {
        Optional<Intervention> optionalIntervention = interventionRepository.findById(interventionId);
        if (!optionalIntervention.isPresent()){
            throw new Exception("Invalid intervention id " +interventionId);
        }

        Intervention old = optionalIntervention.get();

		// Update fields

        old.setDoneTime(intervention.getDoneTime());
        old.setNeededTime(intervention.getNeededTime());

        old.setNeededDate(intervention.getNeededDate());
        old.setDoneDate(intervention.getDoneDate());

        old.setDescription(intervention.getDescription());
        
        old.setPrice(intervention.getPrice());
        
        old.setNote(intervention.getNote());

		// End of update fields

        Intervention savedIntervention = interventionRepository.save(old);

        if (savedIntervention==null){
            throw new Exception("Failed to save intervention with id "+interventionId);
        }
        return new Response<>(savedIntervention);
    }
    /**
     * delete
     */
    public boolean deleteIntervention(int interventionId) throws Exception {
        Optional<Intervention> optionalIntervention = interventionRepository.findById(interventionId);

        if (!optionalIntervention.isPresent()){
            throw new Exception("Invalid intervention id "+interventionId);
        }

        interventionRepository.deleteById(interventionId);

        return true;
    }


    public Response<Intervention> addFilesToIntervention(int interventionId, List<MultipartFile> files) throws Exception {
        Optional<Intervention> optionalIntervention = interventionRepository.findById(interventionId);
        if (!optionalIntervention.isPresent()){
            throw new Exception("Invalid intervention id " +interventionId);
        }

        Intervention old = optionalIntervention.get();
        List<InterventionFiles> collect = files.stream().map(interventionFile -> {
            InterventionFiles interventionFiles = new InterventionFiles();
            try {
                interventionFiles.setFileContent(interventionFile.getBytes());
                interventionFiles.setFileName(interventionFile.getOriginalFilename());
                interventionFiles.setFileType(interventionFile.getContentType());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return interventionFiles;
        }).collect(Collectors.toList());

        old.getInterventionFiles().addAll(collect);
        Intervention savedIntervention = interventionRepository.save(old);
        if (savedIntervention==null){
            throw new Exception("Failed to save intervention with id "+interventionId);
        }
        return new Response<>(savedIntervention);
    }

    public Response<Intervention> removeFilesToIntervention(int interventionId, List<Long> ids) throws Exception {
        Optional<Intervention> optionalIntervention = interventionRepository.findById(interventionId);
        if (!optionalIntervention.isPresent()){
            throw new Exception("Invalid intervention id " +interventionId);
        }

        Intervention old = optionalIntervention.get();
        List<InterventionFiles> collect = old.getInterventionFiles().stream().filter(interventionFiles -> ids.contains(interventionFiles.getId())).collect(Collectors.toList());
        old.getInterventionFiles().removeAll(collect);

        Intervention savedIntervention = interventionRepository.save(old);
        if (savedIntervention==null){
            throw new Exception("Failed to save intervention with id "+interventionId);
        }
        return new Response<>(savedIntervention);
    }


}
