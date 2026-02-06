package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Tachograph;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.repository.TachographRepository;
import rs.oris.back.repository.VehicleRepository;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Service
public class TachographService {

    @Autowired
    private TachographRepository tachographRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    /**
     * vrraca sve tachograph-e vezane za vozilo
     */
    public Response<List<Tachograph>> getAll(int vehicleId) {
        List<Tachograph> tachographList = tachographRepository.findByVehicleVehicleId(vehicleId);
       DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (Tachograph tachograph : tachographList) {
            tachograph.getVehicle().setMillage(Double.parseDouble(decimalFormat.format(tachograph.getVehicle().getMillage())));
        }
        return new Response<>(tachographList);
    }
    /**
     * create/save
     */
	public Response<Tachograph> createTachograph(Tachograph tachograph, int vehicleId) throws Exception {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if(!optionalVehicle.isPresent()){
            throw new Exception("Invalid vehicle id");
        }

        tachograph.setVehicle(optionalVehicle.get());
		Tachograph obj = tachographRepository.save(tachograph);
		if (obj == null) {
			throw new Exception("Creation failed");
		}
		return new Response<>(obj);
	}
    /**
     * vraca 1 tachograph po id-u
     */
    public Response<Tachograph> getSingleTachograph(int tachographId) throws Exception {
        Optional<Tachograph> optionalTachograph = tachographRepository.findById(tachographId);
        if (!optionalTachograph.isPresent()) {
            throw new Exception("Invalid tachograph id " + tachographId);
        }
        return new Response<>(optionalTachograph.get());
    }
    /**
     * update
     */
    public Response<Tachograph> updateTachograph(int tachographId, Tachograph tachograph) throws Exception {
        Optional<Tachograph> optionalTachograph = tachographRepository.findById(tachographId);
        if (!optionalTachograph.isPresent()){
            throw new Exception("Invalid tachograph id " +tachographId);
        }

        Tachograph old = optionalTachograph.get();
		// Update fields
        
        old.setDateFrom(tachograph.getDateFrom());
        old.setDateTo(tachograph.getDateTo());
        old.setDigital(tachograph.isDigital());

		// End of update fields

        Tachograph savedTachograph = tachographRepository.save(old);

        if (savedTachograph==null){
            throw new Exception("Failed to save tachograph with id "+tachographId);
        }
        return new Response<>(savedTachograph);
    }
    /**
     * delete
     */
    public boolean deleteTachograph(int tachographId) throws Exception {
        Optional<Tachograph> optionalTachograph = tachographRepository.findById(tachographId);

        if (!optionalTachograph.isPresent()){
            throw new Exception("Invalid tachograph id "+tachographId);
        }

        tachographRepository.deleteById(tachographId);

        return true;
    }


}
