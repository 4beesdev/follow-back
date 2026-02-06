package rs.oris.back.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "user_vehicle_group")
public class UserVehicleGroup {
    @Id
    @GeneratedValue
    @Column(name = "user_vehicle_group_id")
    private int userVehicleGroupId;
    @ManyToOne
    @JoinColumn(name = "vehicle_group_id")
    @JsonIgnore
    private VehicleGroup vehicleGroup;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public UserVehicleGroup() {
    }

    public UserVehicleGroup(int userVehicleGroupId, VehicleGroup vehicleGroup, User user) {
        this.userVehicleGroupId = userVehicleGroupId;
        this.vehicleGroup = vehicleGroup;
        this.user = user;
    }

    public int getUserVehicleGroupId() {
        return userVehicleGroupId;
    }

    public void setUserVehicleGroupId(int userVehicleGroupId) {
        this.userVehicleGroupId = userVehicleGroupId;
    }

    public VehicleGroup getVehicleGroup() {
        return vehicleGroup;
    }

    public void setVehicleGroup(VehicleGroup vehicleGroup) {
        this.vehicleGroup = vehicleGroup;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
