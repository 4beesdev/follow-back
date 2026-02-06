package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.ServiceLocation;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.ServiceLocationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceLocationService {

    @Autowired
    private ServiceLocationRepository serviceLocationRepository;
    @Autowired
    private FirmRepository vehicleRepository;
    /**
     * sve servise jedne firme
     */
    public Response<List<ServiceLocation>> getAllForFirm(int firmId) {
        List<ServiceLocation> serviceLocationList = serviceLocationRepository.findByFirmFirmId(firmId);
        return new Response<>(serviceLocationList);
    }

    /**
     *
     * create/save
     */
    public Response<ServiceLocation> createServiceLocation(ServiceLocation serviceLocation, int firmId) throws Exception {
        Optional<Firm> optionalFirm = vehicleRepository.findById(firmId);
        if (!optionalFirm.isPresent()){
            throw new Exception("Invalid firm id");
        }

        serviceLocation.setFirm(optionalFirm.get());

        ServiceLocation obj = serviceLocationRepository.save(serviceLocation);
        if (obj == null) {
            throw new Exception("Creation failed");
        }
        return new Response<>(obj);
    }
    /**
     *
     * vraca jedan servis po id-u
     */
    public Response<ServiceLocation> getSingleServiceLocation(int serviceLocationId) throws Exception {
        Optional<ServiceLocation> optionalServiceLocation = serviceLocationRepository.findById(serviceLocationId);
        if (!optionalServiceLocation.isPresent()) {
            throw new Exception("Invalid serviceLocation id " + serviceLocationId);
        }
        return new Response<>(optionalServiceLocation.get());
    }
    /**
     *
     * update
     */
    public Response<ServiceLocation> updateServiceLocation(int serviceLocationId, ServiceLocation serviceLocation) throws Exception {
        Optional<ServiceLocation> optionalServiceLocation = serviceLocationRepository.findById(serviceLocationId);
        if (!optionalServiceLocation.isPresent()){
            throw new Exception("Invalid serviceLocation id " +serviceLocationId);
        }

        ServiceLocation old = optionalServiceLocation.get();

        // Update fields

        old.setAddress(serviceLocation.getAddress());
        old.setName(serviceLocation.getName());
        old.setType(serviceLocation.getType());
        old.setPhone(serviceLocation.getPhone());
        old.setMobilePhone(serviceLocation.getMobilePhone());
        old.setEmail(serviceLocation.getEmail());
        old.setContactPerson(serviceLocation.getContactPerson());

        // End of update fields

        ServiceLocation savedServiceLocation = serviceLocationRepository.save(old);

        if (savedServiceLocation==null){
            throw new Exception("Failed to save serviceLocation with id "+serviceLocationId);
        }
        return new Response<>(savedServiceLocation);
    }
    /**
     *
     * delete
     */
    public boolean deleteServiceLocation(int serviceLocationId) throws Exception {
        Optional<ServiceLocation> optionalServiceLocation = serviceLocationRepository.findById(serviceLocationId);

        if (!optionalServiceLocation.isPresent()){
            throw new Exception("Invalid serviceLocation id "+serviceLocationId);
        }

        serviceLocationRepository.deleteById(serviceLocationId);

        return true;
    }


}
