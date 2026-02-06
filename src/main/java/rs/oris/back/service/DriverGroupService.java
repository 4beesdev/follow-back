package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.DriverGroup;
import rs.oris.back.domain.Firm;
import rs.oris.back.repository.DriverGroupRepository;
import rs.oris.back.repository.FirmRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DriverGroupService {

    @Autowired
    private DriverGroupRepository driverGroupRepository;
    @Autowired
    private FirmRepository firmRepository;
    /**
     * vraca sve grupe vozaca jedne firme
     */
    public Response<List<DriverGroup>> getAllByFirmId(int firmId) {
        List<DriverGroup> driverGroupList = driverGroupRepository.findByFirmFirmId(firmId);
        return new Response<>(driverGroupList);
    }
    /**
     * create/save
     */
    public Response<DriverGroup> createDriverGroup(DriverGroup driverGroup, int firmId) throws Exception {
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id");
        }
        driverGroup.setFirm(optionalFirm.get());

        DriverGroup obj = driverGroupRepository.save(driverGroup);
        if (obj == null) {
            throw new Exception("Creation failed");
        }
        return new Response<>(obj);
    }
    /**
     * vraca jednu grupu vozaca po id-u
     * @param driverGroupId id
     */
    public Response<DriverGroup> getSingleDriverGroup(int driverGroupId) throws Exception {
        Optional<DriverGroup> optionalDriverGroup = driverGroupRepository.findById(driverGroupId);
        if (!optionalDriverGroup.isPresent()) {
            throw new Exception("Invalid driverGroup id " + driverGroupId);
        }
        return new Response<>(optionalDriverGroup.get());
    }
    /**
     * update
     */
    public Response<DriverGroup> updateDriverGroup(int driverGroupId, DriverGroup driverGroup) throws Exception {
        Optional<DriverGroup> optionalDriverGroup = driverGroupRepository.findById(driverGroupId);
        if (!optionalDriverGroup.isPresent()) {
            throw new Exception("Invalid driverGroup id " + driverGroupId);
        }

        DriverGroup old = optionalDriverGroup.get();

        // Update fields

        old.setName(driverGroup.getName());


        // End of update fields

        DriverGroup savedDriverGroup = driverGroupRepository.save(old);

        if (savedDriverGroup == null) {
            throw new Exception("Failed to save driverGroup with id " + driverGroupId);
        }
        return new Response<>(savedDriverGroup);
    }
    /**
     * delete
     */
    public boolean deleteDriverGroup(int driverGroupId) throws Exception {
        Optional<DriverGroup> optionalDriverGroup = driverGroupRepository.findById(driverGroupId);

        if (!optionalDriverGroup.isPresent()) {
            throw new Exception("Invalid driverGroup id " + driverGroupId);
        }

        driverGroupRepository.deleteById(driverGroupId);

        return true;
    }


}
