package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.AparatType;

import java.util.List;
import java.util.Optional;

@Repository
public interface AparatTypeRepository extends JpaRepository<AparatType, Integer> {

    Optional<AparatType> findByAparatTypeId(int aparatTypeId);

    List<AparatType> findByFirmFirmId(int firmId);
}
