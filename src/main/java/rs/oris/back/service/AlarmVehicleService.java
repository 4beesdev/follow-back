package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.*;
import rs.oris.back.repository.AlarmRepository;
import rs.oris.back.repository.AlarmVehicleRepository;
import rs.oris.back.repository.UserRepository;
import rs.oris.back.repository.VehicleRepository;

import java.util.*;

@Service
public class AlarmVehicleService {

    @Autowired
    private AlarmVehicleRepository alarmVehicleRepository;
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     *
     * brise alarm
     */
    public boolean delete(int alarmVehicleId) {
        alarmVehicleRepository.deleteById(alarmVehicleId);
        return true;
    }
    /**
     * dodeljuje postojeci alarm postojecem vozilu
     */
    public Response<AlarmVehicle> add(User user, int alarmId, int vehicleId, int userId, AlarmBE alarmBE) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new Exception("User with id " + userId + " not found!");
        }

        Optional<Alarm> optionalAlarm = alarmRepository.findById(alarmId);
        if (!optionalAlarm.isPresent()) {
            throw new Exception("Alarm with id " + alarmId + " not found!");
        }

        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);
        if (!optionalVehicle.isPresent()) {
            throw new Exception("Vehicle with id " + vehicleId + " not found!");
        }

        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            AlarmVehicle alarmVehicle = new AlarmVehicle();
            alarmVehicle.setAlarm(optionalAlarm.get());
            alarmVehicle.setVehicle(optionalVehicle.get());
            alarmVehicle.setUser(optionalUser.get());

            alarmVehicle.setContact(alarmBE.isContact());
            alarmVehicle.setAuthorized(alarmBE.isAuthorized());
            alarmVehicle.setMaxSpeed(alarmBE.getMaxSpeed());
            alarmVehicle.setSpeed(alarmBE.isSpeed());

            AlarmVehicle saved = alarmVehicleRepository.save(alarmVehicle);
            if (saved == null) {
                throw new Exception("Failed to save alarm vehicle");
            }

            return new Response<>(saved);
        } else {
            if (optionalVehicle.get().getFirm().getFirmId() != user.getFirm().getFirmId()) {
                throw new Exception("You dont have permission for this operation");
            }
            AlarmVehicle alarmVehicle = new AlarmVehicle();
            alarmVehicle.setAlarm(optionalAlarm.get());
            alarmVehicle.setVehicle(optionalVehicle.get());
            alarmVehicle.setUser(optionalUser.get());


            alarmVehicle.setContact(alarmBE.isContact());
            alarmVehicle.setAuthorized(alarmBE.isAuthorized());
            alarmVehicle.setMaxSpeed(alarmBE.getMaxSpeed());
            alarmVehicle.setSpeed(alarmBE.isSpeed());


            AlarmVehicle saved = alarmVehicleRepository.save(alarmVehicle);
            if (saved == null) {
                throw new Exception("Failed to save alarm vehicle");
            }
            return new Response<>(saved);
        }
    }
    /**
     * vraca sve alarme u vozilima jedne firme / korisnika
     */
    public Response<Map<String, List<AlarmVehicle>>> getAll(User user) throws Exception {
        if (user.getSuperAdmin() != null && user.getSuperAdmin()) {
            List<AlarmVehicle> list = alarmVehicleRepository.findAll();
            Map<String, List<AlarmVehicle>> map = new HashMap<>();
            map.put("alarms", list);
            return new Response<>(map);
        } else {
            if (user.getFirm() == null) {
                throw new Exception("Firm not set");
            } else {
                if(user.getAdmin()!=null && user.getAdmin()){
                    List<AlarmVehicle> list = alarmVehicleRepository.findByUserUserId(user.getFirm().getFirmId());
                    Map<String, List<AlarmVehicle>> map = new HashMap<>();
                    map.put("alarms", list);
                    return new Response<>(map);
                }else{
                    List<AlarmVehicle> list = alarmVehicleRepository.findByUserFirmFirmId(user.getUserId());
                    Map<String, List<AlarmVehicle>> map = new HashMap<>();
                    map.put("alarms", list);
                    return new Response<>(map);
                }
            }
        }
    }

    /**
     * vraca sve alarmVehicle i convertuje ih u alarmBE
     */
    public List<AlarmBE> getForBe() {
        List<AlarmVehicle> list = alarmVehicleRepository.findAll();
        List<AlarmBE> listBE = new ArrayList<>();
        for (AlarmVehicle av : list) {
            AlarmBE alarmBE = new AlarmBE();
            alarmBE.setImei(av.getVehicle().getImei());
            alarmBE.setUserId(av.getUser().getUserId());
            alarmBE.setContact(av.isContact());
            alarmBE.setAuthorized(av.isAuthorized());
            alarmBE.setSpeed(av.isSpeed());
            alarmBE.setMaxSpeed(av.getMaxSpeed());
        }
        return listBE;
    }
    /**
     * @param user korisnik
     * @return sve alarme korisnika
     */
    public Response<Map<String, List<AlarmVehicle>>> getMine(User user) {
        List<AlarmVehicle> list = alarmVehicleRepository.findByUserUserId(user.getUserId());
        Map<String, List<AlarmVehicle>> map = new HashMap<>();
        map.put("alarms", list);
        return new Response<>(map);
    }
}
