package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Driver;
import rs.oris.back.domain.DriverGroup;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.DriverGroupRepository;
import rs.oris.back.repository.DriverRepository;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private DriverGroupRepository driverGroupRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    /**
     * vraca sve vozace jedne firme
     * @param firmId firma
     */
    public Response<List<Driver>> getAll(int firmId) {
        List<Driver> driverList = driverRepository.findByFirmFirmId(firmId);
        return new Response<>(driverList);
    }
    /**
     * create/save novog vozaca i dodeljuje ga firmi
     */
    public Response<Driver> createDriver(Driver driver, int firmId) throws Exception {
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id");
        }
        if (driver.getVehicle() != null) {
            Optional<Vehicle> optionalVehicle = vehicleRepository.findById(driver.getVehicle().getVehicleId());
            if (optionalVehicle.isPresent()) {
                driver.setVehicle(optionalVehicle.get());
            }
        }
        driver.setFirm(optionalFirm.get());
        Driver obj = driverRepository.save(driver);
        if (obj == null) {
            throw new Exception("Creation failed");
        }
        return new Response<>(obj);
    }
    /**
     * vraca jednog vozaca po id-u
     */
    public Response<Driver> getSingleDriver(int driverId) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Invalid driver id " + driverId);
        }
        return new Response<>(optionalDriver.get());
    }
    /**
     * update
     */
    public Response<Driver> updateDriver(int driverId, Driver driver) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Invalid driver id " + driverId);
        }

        Driver old = optionalDriver.get();

        // Update fields
        old.setLicenceExpiry(driver.getLicenceExpiry());

        old.setMedicalExpiry(driver.getMedicalExpiry());

        old.setName(driver.getName());

        old.setPhone(driver.getPhone());

        old.setJmbg(driver.getJmbg());

        old.setIdNumber(driver.getIdNumber());
        old.setDateOfBirth(driver.getDateOfBirth());

        old.setActive(driver.isActive());

        old.setLicenceNumber(driver.getLicenceNumber());

        old.setCategory(driver.getCategory());

        old.setNumberOfPoints(driver.getNumberOfPoints());

        old.setIdentificationNumber(driver.getIdentificationNumber());

        old.setiButton(driver.getiButton());

        try {
            old.setVehicle(driver.getVehicle());
        } catch (Exception e) {
            throw new Exception("Failed to save Vehicle.");
        }
        // End of update fields

        Driver savedDriver = driverRepository.save(old);

        if (savedDriver == null) {
            throw new Exception("Failed to save driver with id " + driverId);
        }
        return new Response<>(savedDriver);
    }
    /**
     * delete
     */
    @Transactional
    public boolean deleteDriver(int driverId) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);

        if (!optionalDriver.isPresent()) {
            throw new Exception("Invalid driver id " + driverId);
        }

        if (optionalDriver.get().getVehicle() != null) {
            Vehicle v = optionalDriver.get().getVehicle();
            v.setDriver(null);
            vehicleRepository.save(v);

            Driver d = optionalDriver.get();
            d.setVehicle(null);
            driverRepository.save(d);
        }



        driverRepository.deleteById(driverId);
        return true;
    }

    /**
     * dodaje postojeceg vozaca grupi
     * @param firmId firma
     * @param driverId vozac
     * @param driverGroupId grupa
     * @return true/false
     */
    public boolean addDriverToGroup(int driverId, int driverGroupId) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Driver does not exists!");
        }

        Optional<DriverGroup> optionalDriverGroup = driverGroupRepository.findById(driverGroupId);
        if (!optionalDriverGroup.isPresent()) {
            throw new Exception("DriverGroup does not exists!");
        }


        Driver driver = optionalDriver.get();
        driver.setDriverGroup(optionalDriverGroup.get());

        Driver saved = driverRepository.save(driver);

        return true;

    }
}
