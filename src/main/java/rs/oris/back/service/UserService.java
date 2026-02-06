package rs.oris.back.service;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.domain.dto.PaginationDTO;
import rs.oris.back.repository.*;


import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private FirmRepository firmRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private NotificationAppRepository notificationAppRepository;
    @Autowired
    private VehicleGroupRepository vehicleGroupRepository;
    @Autowired
    private UserVehicleGroupRepository userVehicleGroupRepository;

    @Autowired
    private PushNotificationRepository pushNotificationRepository;

    @Autowired
    private NotificationVehicleRepository  notificationVehicleRepository;

    @Autowired
    private NotificationVehicleService notificationVehicleService;

    /**
     *
     * vraca sve korisnike firme ako atribut firm korisnika nije null, vraca sve korisnike u suprotnom
     */
    public Response<Map<String, List<User>>> getAllUsers(User user) {
        if (user.getFirm() != null) {
            List<User> userList = userRepository.findByFirmFirmId(user.getFirm().getFirmId());
            for (User u : userList) {
                u.setPassword(null);
            }
            Map<String, List<User>> userMap = new HashMap<>();
            userMap.put("users", userList);
            return new Response<>(userMap);


        } else {
            List<User> userList = userRepository.findAll();
            for (User u : userList) {
                u.setPassword(null);
            }
            Map<String, List<User>> userMap = new HashMap<>();
            userMap.put("users", userList);
            return new Response<>(userMap);
        }
    }
    /**
     * Cuva novog korisnika
     *
     * @param signedUpUser novi korisnik
     * @return
     * @throws Exception ako korisnk vec postoji ili ne moze da se sacuva
     */
    public boolean signUp(User signedUpUser) throws Exception {
        String username = signedUpUser.getUsername();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            throw new Exception("User " + username + " already exists.");
        }
        signedUpUser.setPassword(bCryptPasswordEncoder.encode(signedUpUser.getPassword()));
        User savedUser = userRepository.save(signedUpUser);
        if (savedUser == null) {
            throw new Exception("User " + username + " not saved.");
        }
        return true;
    }

    /**
     * Trazi korisnika po username-u
     * @throws Exception ako ne postoji
     */
    public User findByUsername(String username) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception("User " + username + " doesn't exists.");
        }
        return user;
    }
    /**
     * vraca sve korisnike jedne firme
     */
    public Response<Map<String, List<User>>> getAllUsersForFirm(int firmId) {
        List<User> userList = userRepository.findByFirmFirmId(firmId);
        Map<String, List<User>> userMap = new HashMap<>();
        userMap.put("users", userList);
        return new Response<>(userMap);
    }
    /**
     *
     * vraca sve admine firme
     */
    public Response<Map<String, List<User>>> getAdminsForFirm(int firmId) {
        List<User> userList = userRepository.findByFirmFirmIdAndAdmin(firmId, true);
        for (User u : userList) {
            u.setPassword(null);
        }
        Map<String, List<User>> userMap = new HashMap<>();
        userMap.put("admins", userList);
        return new Response<>(userMap);
    }

    /**
     *
     * vraca sve korisnike iz jedne grupe
     */
    public Response<Map<String, List<User>>> getAllUsersForGroup(int groupId) {
        List<User> userList = userRepository.findByGroupGroupId(groupId);
        Map<String, List<User>> userMap = new HashMap<>();
        userMap.put("users", userList);
        return new Response<>(userMap);
    }

    /**
     *
     * azurira korisnika
     */
    public boolean updateUser(int userId, User user) throws Exception {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("Invalid user id " + userId);
        }
        User old = optionalUser.get();
        if (user.getPassword() != null) {
            old.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }

        if (user.getGroup() != null) {
            Optional<Group> optionalGroup = groupRepository.findById(user.getGroup().getGroupId());
            if (!optionalGroup.isPresent()) {
                throw new Exception("Invalid group id " + user.getGroup().getGroupId());
            }
            old.setGroup(optionalGroup.get());
        }


        //Proveriti da samo super admin moze da dodaje admine.
        old.setName(user.getName());
        old.setUsername(user.getUsername());
        old.setAdmin(user.getAdmin());
        old.setPhone(user.getPhone());
        old.setEmail(user.getEmail());

        User savedUser = userRepository.save(old);

        if (savedUser == null) {
            throw new Exception("Failed to save user with id " + userId);
        }
        return true;
    }
    /**
     *
     * registruje novog korisnika (NE KORISTI SE, KORISTI SE SIGNUP FUNKCIJA INSTEAD)
     */
    public Response<User> addUser(User signedUpUser) throws Exception {
        String username = signedUpUser.getUsername();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            throw new Exception("User " + username + " already exists.");
        }
        if (signedUpUser.getPassword() == null) {
            throw new Exception("Password is null");
        }
        if (signedUpUser.getFirm() == null) {
            throw new Exception("Firm not set");
        }
        Optional<Firm> optionalFirm = firmRepository.findById(signedUpUser.getFirm().getFirmId());
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id " + signedUpUser.getFirm().getFirmId());
        }
        signedUpUser.setFirm(optionalFirm.get());

        signedUpUser.setPassword(bCryptPasswordEncoder.encode(signedUpUser.getPassword()));

        User savedUser = userRepository.save(signedUpUser);
        if (savedUser == null) {
            throw new Exception("User " + username + " not saved.");
        }
        savedUser.setPassword(null);
        return new Response<>(savedUser);
    }

    /**
     *
     * funkcija dodaje korisnika firmi ali se ne koristi
     */
    public Response<User> addUserFormFirm(int firmId, User signedUpUser) throws Exception {
        String username = signedUpUser.getUsername();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            throw new Exception("User " + username + " already exists.");
        }
        if (signedUpUser.getPassword() == null) {
            throw new Exception("Password is null");
        }
        Optional<Firm> optionalFirm = firmRepository.findById(firmId);
        if (!optionalFirm.isPresent()) {
            throw new Exception("Invalid firm id " + firmId);
        }
        if (signedUpUser.getGroup() != null) {
            Optional<Group> optionalGroup = groupRepository.findById(signedUpUser.getGroup().getGroupId());
            if (optionalGroup.isPresent()) {
                signedUpUser.setGroup(optionalGroup.get());
            }
        }
        signedUpUser.setFirm(optionalFirm.get());
        signedUpUser.setPassword(bCryptPasswordEncoder.encode(signedUpUser.getPassword()));
        User savedUser = userRepository.save(signedUpUser);
        if (savedUser == null) {
            throw new Exception("User " + username + " not saved.");
        }
        return new Response<>(savedUser);

    }
    /**
     *
     * dodaje postojeceg korisnika firmi
     */
    public Response<User> addUserToFirm(int userId, int groupId) throws Exception {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("Invalid user id " + userId);
        }
        Optional<Group> optionalGroup = groupRepository.findByGroupId(groupId);
        if (!optionalGroup.isPresent()) {
            throw new Exception("Invalid group id " + groupId);
        }

        User u = optionalUser.get();
        u.setGroup(optionalGroup.get());
        User saved = userRepository.save(u);
        if (u == null) {
            throw new Exception("Failed to save user!");
        }
        return new Response<>(saved);
    }

    /**
     *
     * uklanja korisnika iz grupe
     */
    public boolean deleteGroup(int userId) throws Exception {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("Invalid user id " + userId);
        }
        User u = optionalUser.get();
        u.setGroup(null);
        User saved = userRepository.save(u);
        if (u == null) {
            throw new Exception("Failed to save user!");
        }
        return true;


    }
    /**
     * vraca sve admine
     */
    public Response<Map<String, List<User>>> getAllAdmins() {
        List<User> userList = userRepository.findByAdmin(true);
        for (User u : userList) {
            u.setPassword(null);
        }
        Map<String, List<User>> userMap = new HashMap<>();
        userMap.put("users", userList);
        return new Response<>(userMap);
    }
    /**
     *
     * registruje novog superadmina
     */
    public Response<User> addSuperadmin(User user) throws Exception {
        if (user.getUsername().length() < 2) {
            throw new Exception("Username must be longer");
        }
        User optionalUser = userRepository.findByUsername(user.getUsername());

        if (optionalUser != null) {
            throw new Exception("Username already taken");
        }

        if (user.getPassword().length() < 2) {
            throw new Exception("Password must be longer");
        }


        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setAdmin(true);
        user.setSuperAdmin(true);

        User saved = userRepository.save(user);
        return new Response<>(saved);


    }
    /**
     *
     * postavlja korisnika u firmu
     */
    public Response<User> addNewUser(User user, User user1, int firmId) throws Exception {
        if (user == null)
            throw new Exception("Bad toki");
        if (user.getUsername().length() < 2) {
            throw new Exception("Username must be longer");
        }
        if (user.getPassword().length() < 2) {
            throw new Exception("Password must be longer");
        }
        User optionalUser = userRepository.findByUsername(user.getUsername());
        if (optionalUser != null) {
            throw new Exception("Username already taken");
        }

        if (user1.getSuperAdmin() != null && user1.getSuperAdmin()) {
            Optional<Firm> optionalFirm = firmRepository.findById(firmId);
            if (!optionalFirm.isPresent()) {
                throw new Exception("Invalid firm id " + firmId);
            }
            user.setFirm(optionalFirm.get());
        } else {
            Optional<Firm> optionalFirm = firmRepository.findById(firmId);
            if (!optionalFirm.isPresent()) {
                throw new Exception("Firm not set!");
            }
            user.setFirm(optionalFirm.get());
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return new Response<>(saved);
    }


    ///////////////////////////////

    /**
     *
     * vraca korisnika po id-u
     */
    public Response<User> findById(int userId) throws Exception {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("Invalid user " + userId);
        }
        User u = optionalUser.get();
        u.setPassword(null);
        return new Response<>(u);
    }
    /**
     *
     * pravi novog superadmina
     */
    public User findUserById(int userId) throws Exception{
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("Invalid user " + userId);
        }
        User u = optionalUser.get();
        return u;
    }


    public Boolean doesUserExist(int userId){
        Optional<User> optionalUser = userRepository.findByUserId(userId);
       return optionalUser.isPresent();
    }
    /**
     *
     * pravi novog superadmina
     */
    public Response<User> addNewSuperadmin(User user, User user1) throws Exception {
        if (!user1.getSuperAdmin()) {
            throw new Exception("Only a superadmin can add superadmins");
        }
        user.setSuperAdmin(true);
        if (user.getPassword().length() < 2) {
            throw new Exception("Password must be longer");
        }
        if (user.getUsername().length() < 2) {
            throw new Exception("Username must be longer");
        }
        user.setFirm(null);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        if (saved == null) {
            throw new Exception("Failed to save superadmin");
        }
        return new Response<>(saved);
    }
    /**
     * brise superadmina
     */
    public boolean deleteSuperAdmin(int userId, User user1) throws Exception {
        if (!user1.getSuperAdmin()) {
            throw new Exception("Only a superadmin can delete superadmins");
        }
        userRepository.deleteById(userId);
        return true;
    }
    /**
     * vraca sve superadmine
     */
    public Response<Map<String, List<User>>> getAllSA(User user1) throws Exception {
        if (!user1.getSuperAdmin()) {
            throw new Exception("Only a superadmin can add superadmins");
        }
        List<User> userList = userRepository.findBySuperAdmin(true);
        for (User u : userList) {
            u.setPassword(null);
        }
        Map<String, List<User>> userMap = new HashMap<>();
        userMap.put("superAdmins", userList);
        return new Response<>(userMap);
    }
    /**
     * azurira superadmina
     */
    public Response<User> updateSuperadmin(int userId, User usercurrent, User usernew) throws Exception {
        if (!usercurrent.getSuperAdmin()) {
            throw new Exception("Only a superadmin can update superadmins");
        }
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("Invalid user id " + userId);
        }
        User old = optionalUser.get();
        if (old.getPassword() != null) {
            old.setPassword(bCryptPasswordEncoder.encode(usernew.getPassword()));
        }
        old.setUsername(usernew.getUsername());
        old.setName(usernew.getName());
        old.setPhone(usernew.getPhone());
        old.setEmail(usernew.getEmail());

        User saved = userRepository.save(old);
        if (saved == null) {
            throw new Exception("Failed to save user");
        }
        return new Response<>(saved);
    }
    /**
     *
     * brise korisnika iz baze ako je korisnik koji poziva operaciju ili superadmin
     * ili admin iz firme
     */

    @Transactional
    public boolean deleteUser(int userId, User userCurrent) throws Exception {
        Optional<User> optionalUser = userRepository.findByUserId(userId);


        if (!optionalUser.isPresent()) {
            throw new Exception("Invalid user " + userId);
        }

        if (userCurrent.getSuperAdmin()==true) {
            deleteUserPerif(userId);
            return true;
        } else {
            if (userCurrent.getFirm().getFirmId() != optionalUser.get().getFirm().getFirmId()) {
                throw new Exception("Not the same firm!");
            }
            if (userCurrent.getAdmin()) {
                deleteUserPerif(userId);
                return true;
            } else {
                throw new Exception("Only admin can delete other users");
            }
        }
    }

    private void deleteUserPerif(int userId) {
        List<NotificationVehicle> notificationVehicles = notificationVehicleRepository.findByUserUserId(userId);

        for (NotificationVehicle nv : notificationVehicles) {
          notificationVehicleService.delete(nv.getNotificationVehicleId());
        }

        userVehicleGroupRepository.deleteByUserUserId(userId);
        pushNotificationRepository.deleteByUserUserId(userId);
        userRepository.deleteById(userId);
    }

    /**
     *
     * uklanja korisnika iz grupe
     */
    public Response<User> removeGroup(int userId) throws Exception {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("Invalid user " + userId);
        }
        User u = optionalUser.get();
        u.setGroup(null);

        User saved = userRepository.save(u);
        if (saved == null) {
            throw new Exception("Failed to remove from group!");
        }
        return new Response<>(saved);

    }

    /**
     *
     * vraca sve notifikacije korisnika koje nije jos pregledao, sortirane obrnuto hronoloski
     */

    public Response<Boolean> getNotifsSeen(User usercurrent) {
        List<Integer> idList = new ArrayList<>();
        List<NotificationApp> list = notificationAppRepository.findByUserIdAndSeenOrderByTimestampDesc(usercurrent.getUserId(), false);
        for (NotificationApp na : list) {
            idList.add(na.getNotificationAppId());
        }
        //on ovde uzim NotificationAppId pa bi trebamo da ga setuje ovde
        if(idList.size()>0)
            notificationAppRepository.updateAllNotifsFromUser(idList);
        return new Response<>(true);
    }
    /**
     *
     * vraca sve notifikacije korisnika, sortirane obrnuto hronoloski
     */
    public Response<List<NotificationApp>> getNotifsHistory(User usercurrent) {
        List<NotificationApp> list = notificationAppRepository.findByUserIdOrderByTimestampDesc(usercurrent.getUserId());
        return new Response<>(list);
    }
    /**
     *
     * vraca sve notifikacije korisnika, sortirane obrnuto hronoloski
     */
    public Response<List<NotificationApp>> getNotifsHistoryTopN(User usercurrent,Integer topNumber) {
        PageRequest of = PageRequest.of(0, topNumber);
        List<NotificationApp> list = notificationAppRepository.findByUserIdOrderByTimestampDesc(usercurrent.getUserId(), of);
        return new Response<>(list);
    }
    /**
     *
     * dodeljuje vehicle group korisniku
     */
    public Response<Boolean> setVehicleGroupToUser(User usercurrent, int userId, int vehicleGroupId) throws Exception {
        if (usercurrent.getSuperAdmin() == null)
            usercurrent.setSuperAdmin(false);
        if (usercurrent.getAdmin() == null)
            usercurrent.setAdmin(false);
        if (usercurrent.getSuperAdmin() == false && usercurrent.getAdmin() == false) {
            throw new Exception("Only admin or superadmin can do this");
        }


        Optional<User> optionalUser = userRepository.findByUserId(userId);
        Optional<VehicleGroup> optionalVehicleGroup = vehicleGroupRepository.findById(vehicleGroupId);

        if (!optionalUser.isPresent()) {
            throw new Exception("User id invalid.");
        }
        if (!optionalVehicleGroup.isPresent()) {
            throw new Exception("Vehicle group id invalid");
        }

        if (optionalUser.get().getFirm().getFirmId() != optionalVehicleGroup.get().getFirm().getFirmId()) {
            throw new Exception("User and vehicle group do not belong to the same firm");
        }

        Optional<UserVehicleGroup> optionalUserVehicleGroup = userVehicleGroupRepository.findByUserUserIdAndVehicleGroupVehicleGroupId(userId, vehicleGroupId);
        if (optionalUserVehicleGroup.isPresent()) {
            throw new Exception("Already exists");
        }

        UserVehicleGroup userVehicleGroup = new UserVehicleGroup();
        userVehicleGroup.setUser(optionalUser.get());
        userVehicleGroup.setVehicleGroup(optionalVehicleGroup.get());

        userVehicleGroupRepository.save(userVehicleGroup);

        return new Response<>(true);
    }
    /**
     *
     * brisanje korisnika iz grupe vozila
     */
    @Transactional
    public Response<Boolean> deleteUVG(User usercurrent, int userId) throws Exception {
        if (usercurrent.getSuperAdmin() == null)
            usercurrent.setSuperAdmin(false);
        if (usercurrent.getAdmin() == null)
            usercurrent.setAdmin(false);
        if (usercurrent.getSuperAdmin() == false && usercurrent.getAdmin() == false) {
            throw new Exception("Only admin or superadmin can do this");
        }

        if (usercurrent.getSuperAdmin() == false) {
            Optional<User> optionalUser = userRepository.findByUserId(userId);
            if (!optionalUser.isPresent()) {
                throw new Exception("Invalid user id");
            }
            if (optionalUser.get().getFirm().getFirmId() != usercurrent.getFirm().getFirmId()) {
                throw new Exception("Firms do not match");
            }

        }

        userVehicleGroupRepository.deleteByUserUserId(userId);

        return new Response<>(true);
    }
    /**
     *
     * vraca vehicle groupe od korisnika
     */
    public Response<Set<VehicleGroup>> getVehGroupForUser(User usercurrent, int userId) throws Exception {
        List<UserVehicleGroup> userVehicleGroupList = userVehicleGroupRepository.findByUserUserId(userId);
        Set<VehicleGroup> vgrList = new HashSet<>();

        for (UserVehicleGroup uvg : userVehicleGroupList) {
            vgrList.add(uvg.getVehicleGroup());
        }

        return new Response<>(vgrList);
    }
    /**
     *
     * vraca poslednjih 50 nepregledanih notifikacija korisnika
     */
    public Response<List<NotificationApp>> getNotifs(User usercurrent) {
        List<NotificationApp> list = notificationAppRepository.findTop50ByUserIdAndSeenOrderByTimestampDesc(usercurrent.getUserId(), false);
        return new Response<>(list);
    }
    /**
     *
     * brise sve notifikacije korisnika
     */

    @Transactional
    public boolean clearNotifs(User usercurrent) {
        notificationAppRepository.deleteByUserId(usercurrent.getUserId());
        return true;
    }

    //Vraca istoriju notifikacija kao paginaciju
    public Response<PaginationDTO> getNotifsHistoryPagination(User usercurrent, Integer page, Integer size) {
        PageRequest of = PageRequest.of(page,size);
        List<NotificationApp> list = notificationAppRepository.findByUserIdOrderByTimestampDesc(usercurrent.getUserId(), of);
        Long count= notificationAppRepository.countAllByUserId(usercurrent.getUserId());
        return new Response<>(new PaginationDTO(list,count));
    }
}
