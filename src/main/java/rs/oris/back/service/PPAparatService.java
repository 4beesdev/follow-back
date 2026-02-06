package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.AparatType;
import rs.oris.back.domain.PPAparat;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.AparatTypeRepository;
import rs.oris.back.repository.PPAparatRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PPAparatService {

    @Autowired
    private PPAparatRepository pPAparatRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private AparatTypeRepository aparatTypeRepository;

    /**
     * vraca sve aparate prosledjenog vozila
     */
    public Response<List<PPAparat>> getAllByVehicle(int vehicleId) {
        List<PPAparat> pPAparatList = pPAparatRepository.findByVehicleVehicleId(vehicleId);
        return new Response<>(pPAparatList);
    }

    /**
     * create/save
     * @param pPAparat aparat za cuvanje
     * @param vehicleId vozilo u kome ce biti aparat
     */
    public Response<PPAparat> createPPAparat(PPAparat pPAparat, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Invalid vehicle id");
        }

        pPAparat.setVehicle(optionalVehicle.get());

        PPAparat obj = pPAparatRepository.save(pPAparat);
        if (obj == null) {
            throw new Exception("Creation failed");
        }
        return new Response<>(obj);
    }
    /**
     * vraca jedan aparat po id-u
     */
    public Response<PPAparat> getSinglePPAparat(int pPAparatId) throws Exception {
        Optional<PPAparat> optionalPPAparat = pPAparatRepository.findById(pPAparatId);
        if (!optionalPPAparat.isPresent()) {
            throw new Exception("Invalid pPAparat id " + pPAparatId);
        }
        return new Response<>(optionalPPAparat.get());
    }

    /**
     * update
     */
    public Response<PPAparat> updatePPAparat(int pPAparatId, PPAparat pPAparat) throws Exception {
        Optional<PPAparat> optionalPPAparat = pPAparatRepository.findById(pPAparatId);
        if (!optionalPPAparat.isPresent()) {
            throw new Exception("Invalid pPAparat id " + pPAparatId);
        }



        PPAparat old = optionalPPAparat.get();

        // Update fields
        if (pPAparat.getAparatType() != null) {
            Optional<AparatType> optionalAparatType = aparatTypeRepository.findById(pPAparat.getAparatType().getAparatTypeId());

            if (!optionalAparatType.isPresent()) {
            }else{
                old.setAparatType(optionalAparatType.get());
            }
        }



        old.setAmount(pPAparat.getAmount());

        old.setNumber(pPAparat.getNumber());

        old.setServiced(pPAparat.getServiced());

        old.setServicedto(pPAparat.getServicedto());

        // End of update fields

        PPAparat savedPPAparat = pPAparatRepository.save(old);

        if (savedPPAparat == null) {
            throw new Exception("Failed to save pPAparat with id " + pPAparatId);
        }
        return new Response<>(savedPPAparat);
    }
    /**
     * delete
     */
    public boolean deletePPAparat(int pPAparatId) throws Exception {

        pPAparatRepository.deleteById(pPAparatId);

        return true;
    }


}
