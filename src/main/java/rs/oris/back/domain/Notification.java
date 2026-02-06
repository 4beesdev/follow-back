package rs.oris.back.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="notification")
public class Notification {
    @Id
    @GeneratedValue
    @Column(name = "notification_id")
    private int notificationId;
    private String name;
    @OneToMany(mappedBy = "notification")
    @JsonIgnore
    private Set<NotificationVehicle> notificationVehicleSet;

    public Notification() {
    }

    public Notification(int notificationId, String name, Set<NotificationVehicle> notificationVehicleSet) {
        this.notificationId = notificationId;
        this.name = name;
        this.notificationVehicleSet = notificationVehicleSet;
    }


    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<NotificationVehicle> getNotificationVehicleSet() {
        return notificationVehicleSet;
    }

    public void setNotificationVehicleSet(Set<NotificationVehicle> notificationVehicleSet) {
        this.notificationVehicleSet = notificationVehicleSet;
    }
}
