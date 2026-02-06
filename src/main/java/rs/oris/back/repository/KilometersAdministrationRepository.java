package rs.oris.back.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.KilometersAdministration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KilometersAdministrationRepository extends PagingAndSortingRepository<KilometersAdministration,Long> {



    Page<KilometersAdministration> findAllByDateBetweenAndVehicle_Firm_FirmId(LocalDate fromDate, LocalDate toDate,int firmId, Pageable pageable);

    List<KilometersAdministration> findAllByDateBetweenAndVehicle_Registration(LocalDate fromDate, LocalDate toDate,String registration);
    Optional<KilometersAdministration> findByDateBetweenAndVehicle_Registration(LocalDate fromDate, LocalDate toDate, String registration);


    Page<KilometersAdministration> findAllByDateBetweenAndVehicle_RegistrationContainingAndVehicle_Firm_FirmId(
            LocalDate fromDate,
            LocalDate toDate,
            String registration,
            int firmId,
            Pageable pageable
    );}
