package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.AparatType;
import rs.oris.back.domain.Firm;
import rs.oris.back.domain.Izvestaj;
import rs.oris.back.repository.AparatTypeRepository;
import rs.oris.back.repository.FirmRepository;
import rs.oris.back.repository.IzvestajRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AparatTypeService {

    @Autowired
    private AparatTypeRepository aparatTypeRepository;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private IzvestajRepository izvestajRepository;

    /**
     * vraca sve tipove protivpozarnih aparata jedne firme
     */
    public Response<List<AparatType>> getAll(int firmId) {

        List<AparatType> aparatTypeList = aparatTypeRepository.findByFirmFirmId(firmId);
        return new Response<>(aparatTypeList);
    }
    /**
     * create/save i dodeljuje ga firmi
     */
    public Response<AparatType> createAparatType(AparatType aparatType, int firmId) throws Exception {
        // TODO: Check
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id");
        }

        aparatType.setFirm(optionalFirm.get());
        AparatType obj = aparatTypeRepository.save(aparatType);
        if (obj == null) {
            throw new Exception("Creation failed");
        }
        return new Response<>(obj);
    }
    /**
     * vraca jedan tip pp aparata po id-u
     */
    public Response<AparatType> getSingleAparatType(int aparatTypeId) throws Exception {
        Optional<AparatType> optionalAparatType = aparatTypeRepository.findById(aparatTypeId);
        if (!optionalAparatType.isPresent()) {
            throw new Exception("Invalid aparatType id " + aparatTypeId);
        }
        return new Response<>(optionalAparatType.get());
    }

    /**
     * delete
     */
    public boolean delete(int aparatTypeId) throws Exception {
        aparatTypeRepository.deleteById(aparatTypeId);
        return true;
    }
    /**
     * testiranje
     */
    public Response<String> izvestaj(int broj) {
        int a = (int) (Math.random() * 1000);
        Izvestaj izvestaj = new Izvestaj();
        izvestaj.setBrojNaplativihSati(a);
        izvestaj.setBrojSati(broj);
        java.util.Date date = new java.util.Date();
        izvestaj.setDate(new Date(date.getTime()));
        izvestaj.setPotvrdjeno(true);
        izvestajRepository.save(izvestaj);

        return new Response<>("uspesnoSacuvano"+izvestaj.getBrojNaplativihSati()+" "+izvestaj.getBrojSati());

    }
}
