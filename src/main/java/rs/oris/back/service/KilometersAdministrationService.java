package rs.oris.back.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import rs.oris.back.domain.KilometersAdministration;
import rs.oris.back.domain.Vehicle;
import rs.oris.back.domain.dto.KilometersUpdateDTO;
import rs.oris.back.domain.dto.NewKilometersRecordDTO;
import rs.oris.back.exceptions.KilometersDoesNotExistException;
import rs.oris.back.repository.KilometersAdministrationRepository;
import rs.oris.back.repository.VehicleRepository;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class KilometersAdministrationService {


    private final KilometersAdministrationRepository kilometersAdministrationRepository;

    private final VehicleRepository vehicleRepository;
    public Page<KilometersAdministration> getKilometersAdministration(int page, int size, LocalDate fromDate, LocalDate toDate, String registration, int firmId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        if(registration!=null)
        {
            return  kilometersAdministrationRepository.findAllByDateBetweenAndVehicle_RegistrationContainingAndVehicle_Firm_FirmId(fromDate, toDate, registration, firmId,pageRequest);

        }
       return kilometersAdministrationRepository.findAllByDateBetweenAndVehicle_Firm_FirmId(fromDate,toDate,firmId,pageRequest);
    }

    public void deleteKilometerAdministration(Long id) {

        kilometersAdministrationRepository.deleteById(id);
    }

    public KilometersAdministration addNewKilometerRecord(NewKilometersRecordDTO newKilometersRecordDTO) {

        Vehicle vehicle = vehicleRepository.findById(newKilometersRecordDTO.getVehicleId()).orElseThrow(() -> new KilometersDoesNotExistException("Vehicle with id: " + newKilometersRecordDTO.getVehicleId() + " does not exist."));


        KilometersAdministration kilometersAdministration = new KilometersAdministration();
        kilometersAdministration.setDate(newKilometersRecordDTO.getDate());
        kilometersAdministration.setMileage(newKilometersRecordDTO.getMileage());
        kilometersAdministration.setVehicle(vehicle);
       return  kilometersAdministrationRepository.save(kilometersAdministration);

    }

    public KilometersAdministration updateKilometeres(KilometersUpdateDTO kilometersUpdateDTO,Long id) {

        Vehicle vehicle = vehicleRepository.findById(kilometersUpdateDTO.getVehicleId()).orElseThrow(() -> new KilometersDoesNotExistException("Vehicle with id: " + kilometersUpdateDTO.getVehicleId() + " does not exist."));



        KilometersAdministration kilometersAdministration = kilometersAdministrationRepository.findById(id).orElseThrow(()-> new KilometersDoesNotExistException("Kilometers with id: "+id+" does not exist."));
        kilometersAdministration.setDate(kilometersUpdateDTO.getDate());
        kilometersAdministration.setMileage(kilometersUpdateDTO.getMileage());
        kilometersAdministration.setVehicle(vehicle);
        return kilometersAdministrationRepository.save(kilometersAdministration);
    }
}
