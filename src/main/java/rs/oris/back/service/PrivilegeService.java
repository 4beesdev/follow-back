package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.GroupPrivilege;
import rs.oris.back.domain.Privilege;
import rs.oris.back.domain.User;
import rs.oris.back.repository.GroupPrivilegeRepository;
import rs.oris.back.repository.PrivilegeRepository;
import rs.oris.back.repository.UserRepository;

import java.util.*;

@Service
public class PrivilegeService {

    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupPrivilegeRepository groupPrivilegeRepository;

    /**
     * vraca sve privilegije
     */
    public Response<Map<String, List<Privilege>>> getAllPrivilege() {
        List<Privilege> list = privilegeRepository.findAll();
        Map<String, List<Privilege>> map = new HashMap<>();
        map.put("privileges", list);
        return new Response<>(map);
    }
    /**
     * vraca sve privilegije grupe
     */
    public Response<Map<String, List<Privilege>>> getAllForGroup(int groupId) {

        List<GroupPrivilege> groupPrivilegeList = groupPrivilegeRepository.findByGroupGroupId(groupId);
        List<Privilege> list = new ArrayList<>();

        for (GroupPrivilege gp : groupPrivilegeList) {
            Optional<Privilege> optionalPrivilege = privilegeRepository.findById(gp.getPrivilege().getPrivilegeId());
            if (optionalPrivilege.isPresent()) {
                list.add(optionalPrivilege.get());
            }
        }


        Map<String, List<Privilege>> map = new HashMap<>();
        map.put("privileges", list);
        return new Response<>(map);
    }
    /**
     * vraca sve privilegije korisnika
     */
    public Response<Map<String, List<Privilege>>> getAllPrivilegeForAUser(int userId) throws Exception {

        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("User not found!");
        }
        User u = optionalUser.get();
        if (u.getGroup() == null) {
            return new Response<>(null);
        }

        List<GroupPrivilege> groupPrivilegeList = groupPrivilegeRepository.findByGroupGroupId(u.getGroup().getGroupId());
        List<Privilege> list = new ArrayList<>();

        for (GroupPrivilege gp : groupPrivilegeList) {
            Optional<Privilege> optionalPrivilege = privilegeRepository.findById(gp.getPrivilege().getPrivilegeId());
            if (optionalPrivilege.isPresent()) {
                list.add(optionalPrivilege.get());
            }
        }

        Map<String, List<Privilege>> map = new HashMap<>();
        map.put("privileges", list);
        return new Response<>(map);
    }
    /**
     * Funkcija se poziva  pri pokretanju aplikacije
     *
     */
    public void setUp() {
        long a = privilegeRepository.count();
        if (a < 2) {
            privilegeRepository.deleteAll();
            List<Privilege> list = new ArrayList<>();
            list.add(new Privilege(100, "Praćenje"));
            list.add(new Privilege(101, "Istorija kretanja"));
            list.add(new Privilege(102, "Prikaz markera, regiona, ruta"));
            list.add(new Privilege(103, "Prikaz izveštaja"));
            list.add(new Privilege(104, "Administracija"));

            list.add(new Privilege(200, "Izveštaj o pređenom putu"));
            list.add(new Privilege(201, "Izveštaj o pređenom putu - mesečni"));
            list.add(new Privilege(202, "Izveštaj o pređenom putu - mesečni - radno vrem"));
            list.add(new Privilege(203, "Izveštaj o pređenom putu - mesečni - van radnog vremena"));
            list.add(new Privilege(204, "Izveštaj o potrošnji goriva"));
            list.add(new Privilege(205, "Izveštaj o prekoračenju brzine"));
            list.add(new Privilege(206, "Izveštaj o prekoračenju brzine - grafik"));
            list.add(new Privilege(207, "Izveštaj o stanju vozila"));
            list.add(new Privilege(208, "Izveštaj o događajima vozila"));
            list.add(new Privilege(209, "Izveštaj posete markera"));
            list.add(new Privilege(210, "Izveštaj posete regiona"));
            list.add(new Privilege(211, "Izveštaj povrede rute"));
            list.add(new Privilege(212, "Izveštaj povrede rute markera"));
            list.add(new Privilege(213, "Izveštaj temperature - grafuj"));
            list.add(new Privilege(214, "Izveštaj o relacijama vozila"));
            list.add(new Privilege(215, "Izveštaj radnih sati"));

            list.add(new Privilege(300, "Korisnici"));
            list.add(new Privilege(301, "Kreiranje korisnika"));
            list.add(new Privilege(302, "Izmena korisnika"));
            list.add(new Privilege(303, "Brisanje korisnika"));
            list.add(new Privilege(304, "Grupe korisnika"));
            list.add(new Privilege(305, "Kreiranje grupe korisnika"));
            list.add(new Privilege(306, "Brisanje grupe korisnika"));
            list.add(new Privilege(307, "Izmena ovlašćenja"));

            list.add(new Privilege(308, "Vozila"));
            list.add(new Privilege(309, "Kreiranje vozila"));
            list.add(new Privilege(310, "Izmena vozila"));
            list.add(new Privilege(311, "Brisanje vozila"));
            list.add(new Privilege(312, "Grupe vozila"));
            list.add(new Privilege(313, "Kreiranje grupe vozila"));
            list.add(new Privilege(314, "Brisanje grupe vozila"));
            list.add(new Privilege(315, "Dodeljivanje grupi"));

            list.add(new Privilege(316, "Markeri"));
            list.add(new Privilege(317, "Kreiranje markera"));
            list.add(new Privilege(318, "Izmena markera"));
            list.add(new Privilege(319, "Brisanje markera"));
            list.add(new Privilege(320, "Grupe markera"));
            list.add(new Privilege(321, "Kreiranje grupe markera"));
            list.add(new Privilege(322, "Brisanje grupe markera"));
            list.add(new Privilege(323, "Dodeljivanje grupi markera"));

            list.add(new Privilege(324, "Regioni"));
            list.add(new Privilege(325, "Kreiranje regiona"));
            list.add(new Privilege(326, "Izmena regiona"));
            list.add(new Privilege(327, "Brisanje regiona"));
            list.add(new Privilege(328, "Grupe regiona"));
            list.add(new Privilege(329, "Kreiranje grupe regiona"));
            list.add(new Privilege(330, "Brisanje grupe regiona"));
            list.add(new Privilege(331, "Dodeljivanje grupi regiona"));

            list.add(new Privilege(332, "Rute"));
            list.add(new Privilege(333, "Kreiranje rute"));
            list.add(new Privilege(334, "Izmena rute"));
            list.add(new Privilege(335, "Brisanje rute"));
            list.add(new Privilege(336, "Dodela alarma vozilu"));
            list.add(new Privilege(337, "Dodela markera vozilu"));
            list.add(new Privilege(338, "Dodela regiona vozilu"));
            list.add(new Privilege(339, "Dodela rute vozilu"));

            privilegeRepository.saveAll(list);
        }


    }
}
