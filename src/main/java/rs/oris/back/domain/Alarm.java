package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="alarm")
public class Alarm {
    @Id
    @GeneratedValue
    @Column(name = "alarm_id")
    private int alarmId;
    private String name;
    @OneToMany(mappedBy = "alarm")
    @JsonIgnore
    private Set<AlarmVehicle> alarmVehicleSet;



    public Alarm() {
    }

    public Alarm(int alarmId, String name, Set<AlarmVehicle> alarmVehicleSet) {
        this.alarmId = alarmId;
        this.name = name;
        this.alarmVehicleSet = alarmVehicleSet;
    }


    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AlarmVehicle> getAlarmVehicleSet() {
        return alarmVehicleSet;
    }

    public void setAlarmVehicleSet(Set<AlarmVehicle> alarmVehicleSet) {
        this.alarmVehicleSet = alarmVehicleSet;
    }
}
