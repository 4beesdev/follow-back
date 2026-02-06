package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "user_oris")
public class User implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private int userId;
    @Column(name = "username")
    private String username; //username je email zapravo
    private String password;
    private String name;
    private String email;
    private String phone;
    private Boolean active = true;
    private Boolean admin = false;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Group group;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firm_id")
    private Firm firm;
    @Column(name = "super_admin")
    private Boolean superAdmin = false;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private transient Set<UserReport> userReportSet;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<AlarmVehicle> alarmVehicleSet;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<NotificationVehicle> notificationVehicleSet;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<UserVehicleGroup> userVehicleGroupSet;


    public User() {
    }


    public User(int userId, String username, String password, String name, String email, String phone, Boolean active, Boolean admin, Group group, Firm firm, Boolean superAdmin, Set<UserReport> userReportSet, Set<AlarmVehicle> alarmVehicleSet, Set<NotificationVehicle> notificationVehicleSet, Set<UserVehicleGroup> userVehicleGroupSet) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.active = active;
        this.admin = admin;
        this.group = group;
        this.firm = firm;
        this.superAdmin = superAdmin;
        this.userReportSet = userReportSet;
        this.alarmVehicleSet = alarmVehicleSet;
        this.notificationVehicleSet = notificationVehicleSet;
        this.userVehicleGroupSet = userVehicleGroupSet;
    }

    public Set<UserVehicleGroup> getUserVehicleGroupSet() {
        return userVehicleGroupSet;
    }

    public void setUserVehicleGroupSet(Set<UserVehicleGroup> userVehicleGroupSet) {
        this.userVehicleGroupSet = userVehicleGroupSet;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<NotificationVehicle> getNotificationVehicleSet() {
        return notificationVehicleSet;
    }

    public void setNotificationVehicleSet(Set<NotificationVehicle> notificationVehicleSet) {
        this.notificationVehicleSet = notificationVehicleSet;
    }

    public Set<AlarmVehicle> getAlarmVehicleSet() {
        return alarmVehicleSet;
    }

    public void setAlarmVehicleSet(Set<AlarmVehicle> alarmVehicleSet) {
        this.alarmVehicleSet = alarmVehicleSet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<UserReport> getUserReportSet() {
        return userReportSet;
    }

    public void setUserReportSet(Set<UserReport> userReportSet) {
        this.userReportSet = userReportSet;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public Boolean getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(Boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    @JsonProperty(value = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
