package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.FuelCompany;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.FuelCompanyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FuelCompanyService {

    @Autowired
    private FuelCompanyRepository fuelCompanyRepository;
    @Autowired
    private FirmRepository firmRepository;

    /**
     * vraca sve lance benziskih pumpi vezanih za jednu firmu
     */
    public Response<List<FuelCompany>> getAllByFirmFirmId(int firmId) {
        List<FuelCompany> fuelCompanyList = fuelCompanyRepository.findByFirmFirmId(firmId);
        return new Response<>(fuelCompanyList);
    }
    /**
     * create/save
     */
	public Response<FuelCompany> createFuelCompany(FuelCompany fuelCompany, int firmId) throws Exception {
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id");
        }

        fuelCompany.setFirm(optionalFirm.get());

		FuelCompany obj = fuelCompanyRepository.save(fuelCompany);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     * vraca jedan laanac benz pump po id-u
     */
    public Response<FuelCompany> getSingleFuelCompany(int fuelCompanyId) throws Exception {
        Optional<FuelCompany> optionalFuelCompany = fuelCompanyRepository.findById(fuelCompanyId);
        if (!optionalFuelCompany.isPresent()) {
            throw new Exception("Invalid fuelCompany id " + fuelCompanyId);
        }
        return new Response<>(optionalFuelCompany.get());
    }
    /**
     * update
     */
    public boolean updateFuelCompany(int fuelCompanyId, FuelCompany fuelCompany) throws Exception {
        Optional<FuelCompany> optionalFuelCompany = fuelCompanyRepository.findById(fuelCompanyId);
        if (!optionalFuelCompany.isPresent()){
            throw new Exception("Invalid fuelCompany id " +fuelCompanyId);
        }

        FuelCompany old = optionalFuelCompany.get();

		// Update fields


        old.setActive(fuelCompany.isActive());

        old.setName(fuelCompany.getName());
        
        old.setPlace(fuelCompany.getPlace());
        
        old.setAddress(fuelCompany.getAddress());
        
        old.setPhone(fuelCompany.getPhone());
        
        old.setFax(fuelCompany.getFax());
        
        old.setEmail(fuelCompany.getEmail());
        
        old.setContactPerson(fuelCompany.getContactPerson());

		// End of update fields

        FuelCompany savedFuelCompany = fuelCompanyRepository.save(old);

        if (savedFuelCompany==null){
            throw new Exception("Failed to save fuelCompany with id "+fuelCompanyId);
        }
        return true;
    }
    /**
     * delete
     */
    public boolean deleteFuelCompany(int fuelCompanyId) throws Exception {
        Optional<FuelCompany> optionalFuelCompany = fuelCompanyRepository.findById(fuelCompanyId);

        if (!optionalFuelCompany.isPresent()){
            throw new Exception("Invalid fuelCompany id "+fuelCompanyId);
        }

        fuelCompanyRepository.deleteById(fuelCompanyId);

        return true;
    }


}
