package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.User;
import rs.oris.back.repository.FirmRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FirmService {

    @Autowired
    private FirmRepository firmRepository;

    /**
     * vraca sve aktivne firme ako je korisnik superadmin
     */
    public Response<Map<String, List<Firm>>> getAllFirm(User userCurrent) {
        if (userCurrent.getSuperAdmin()) {
            List<Firm> list = firmRepository.findByActive(true);
            Map<String, List<Firm>> map = new HashMap<>();
            map.put("firms", list);
            return new Response<>(map);
        } else {
            return null;
        }
    }

    /**
     * create/save
     */
    public Response<Firm> addFirm(Firm firm) throws Exception {
        List<Firm> firmList = firmRepository.findAll();

        for (Firm firm2 : firmList) {
            if (firm2.getName().trim().equalsIgnoreCase(firm.getName().trim())) {
                throw new Exception("Two frims can't have the same name");
            }
        }

        firm.setActive(true);
        Firm saved = firmRepository.save(firm);
        if (saved == null) {
            throw new Exception("Failed to add firm");
        }
        return new Response<>(saved);
    }
    /**
     * update
     */
    public Response<Firm> updateFirm(int firmId, Firm firm) throws Exception {
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id " + firmId);
        }
        Firm old = optionalFirm.get();
        old.setName(firm.getName());
        old.setEmail(firm.getEmail());
        old.setPhone(firm.getPhone());
        old.setNewReports(firm.getNewReports());
        old.setFms(firm.getFms());
        old.setTracking(firm.getTracking());
        Firm saved = firmRepository.save(old);
        if (saved == null) {
            throw new Exception("Failed to update firm");
        }
        return new Response<>(saved);
    }
    /**
     * delete
     */
    public boolean deleteFirm(int firmId, User userCurrent) throws Exception {
        if (!userCurrent.getSuperAdmin()) {
            throw new Exception("Only a superadmin can delete a firm");
        } else {
            Optional<Firm> optionalFirm = firmRepository.findById(firmId);
            if (!optionalFirm.isPresent()) {
                throw new Exception("Invalid firm id " + firmId);
            }

            Firm f = optionalFirm.get();
            f.setActive(false);
            firmRepository.save(f);
            return true;
        }
    }
}
