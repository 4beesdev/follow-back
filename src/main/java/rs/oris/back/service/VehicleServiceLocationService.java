package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.ServiceLocation;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.VehicleServiceLocation;
import rs.oris.back.repository.VehicleRepository;
import rs.oris.back.repository.VehicleServiceLocationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceLocationService {

    @Autowired
    private VehicleServiceLocationRepository vehicleServiceLocationRepository;
    @Autowired
    private rs.oris.back.repository.ServiceLocationRepository serviceLocationRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * vraca listu svih servisiranja vozila
     */
    public Response<List<VehicleServiceLocation>> getAllByVehicle(int vehicleId) {
        List<VehicleServiceLocation> vehicleServiceLocationList = vehicleServiceLocationRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(vehicleServiceLocationList);
    }

    /**
     * vraca listu svih servisiranja jednog servisa (majstora)
     */
    public Response<List<VehicleServiceLocation>> getAllBySL(int serviceLocationId) {
        List<VehicleServiceLocation> vehicleServiceLocationList = vehicleServiceLocationRepository.findByServiceLocationServiceLocationId(serviceLocationId);
        return new Response<>(vehicleServiceLocationList);
    }

    /**
     * vraca listu svih servisiranja u jednoj firmi
     */
    public Response<List<VehicleServiceLocation>> getAllByFirm(int firmId) {
        List<VehicleServiceLocation> vehicleServiceLocationList = vehicleServiceLocationRepository.findByVehicleFirmFirmId(firmId);
        return new Response<>(vehicleServiceLocationList);
    }

    /**
     * create/save
     */
    public Response<VehicleServiceLocation> createVehicleServiceLocation(VehicleServiceLocation vehicleServiceLocation, int slId, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }
        Optional<ServiceLocation> optionalServiceLocation = serviceLocationRepository.findById(slId);
        if (!optionalServiceLocation.isPresent()) {
            throw new Exception("Invalid service location id");
        }


        vehicleServiceLocation.setServiceLocation(optionalServiceLocation.get());
        vehicleServiceLocation.setVehicle(optionalVehicle.get());

        VehicleServiceLocation obj = vehicleServiceLocationRepository.save(vehicleServiceLocation);
        if (obj == null) {
            throw new Exception("Creation failed");
        }
        return new Response<>(obj);


    }
    /**
     * vraca jedno konkretno servisiranje po id-u
     */
    public Response<VehicleServiceLocation> getSingleVehicleServiceLocation(int vehicleServiceLocationId) throws Exception {
        Optional<VehicleServiceLocation> optionalVehicleServiceLocation = vehicleServiceLocationRepository.findById(vehicleServiceLocationId);
        if (!optionalVehicleServiceLocation.isPresent()) {
            throw new Exception("Invalid vehicleServiceLocation id " + vehicleServiceLocationId);
        }
        return new Response<>(optionalVehicleServiceLocation.get());
    }

    /**
     * update
     */
    public Response<VehicleServiceLocation> updateVehicleServiceLocation(int vehicleServiceLocationId, VehicleServiceLocation vehicleServiceLocation) throws Exception {
        Optional<VehicleServiceLocation> optionalVehicleServiceLocation = vehicleServiceLocationRepository.findById(vehicleServiceLocationId);
        if (!optionalVehicleServiceLocation.isPresent()) {
            throw new Exception("Invalid vehicleServiceLocation id " + vehicleServiceLocationId);
        }
        VehicleServiceLocation old = optionalVehicleServiceLocation.get();

        // Update fields
        old.setDate(vehicleServiceLocation.getDate());
        old.setExpenses(vehicleServiceLocation.getExpenses());
        old.setExplained(vehicleServiceLocation.getExplained());
        // End of update fields

        VehicleServiceLocation savedVehicleServiceLocation = vehicleServiceLocationRepository.save(old);

        if (savedVehicleServiceLocation == null) {
            throw new Exception("Failed to save vehicleServiceLocation with id " + vehicleServiceLocationId);
        }
        return new Response<>(savedVehicleServiceLocation);
    }
    /**
     * delete
     */
    public boolean deleteVehicleServiceLocation(int vehicleServiceLocationId) throws Exception {

        vehicleServiceLocationRepository.deleteById(vehicleServiceLocationId);

        return true;
    }


}
