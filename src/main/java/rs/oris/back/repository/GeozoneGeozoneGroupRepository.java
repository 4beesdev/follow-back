package rs.oris.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import rs.oris.back.domain.GeozoneGeozoneGroup;
import rs.oris.back.domain.GeozoneGroup;

import java.util.List;

@Repository
public interface GeozoneGeozoneGroupRepository extends JpaRepository<GeozoneGeozoneGroup, Integer> {

    void deleteByGeozoneGeozoneIdAndGeozoneGroupGeozoneGroupId(int geozoneId, int groupId);

    List<GeozoneGeozoneGroup> findByGeozoneGeozoneGroupId(int geozoneGroupId);

    @Modifying
    //delete all GeozoneGeozoneGroup where GeozoneGroup has id
    void deleteByGeozoneGroupGeozoneGroupId(int groupId);
    @Modifying
    //delete all GeozoneGeozoneGroup where GeozoneGroup has id
    void deleteAllByGeozoneGroup_GeozoneGroupId(int groupId);


    void deleteByGeozoneGeozoneId(int id);


    void deleteAllByGeozoneGroup(GeozoneGroup geozoneGroup);
}
