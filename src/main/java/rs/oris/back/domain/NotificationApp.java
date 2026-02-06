package rs.oris.back.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "notification_app")
public class NotificationApp {
    @Id
    @GeneratedValue
    @Column(name = "notification_app_id")
    private int notificationAppId;
    private String heading;
    private boolean seen;
    private Timestamp timestamp;
    @Column(length = 5000)
    private String text;
    private int userId;

    public NotificationApp() {
    }

    public NotificationApp(int notificationAppId, String heading, boolean seen, Timestamp timestamp, String text, int userId) {
        this.notificationAppId = notificationAppId;
        this.heading = heading;
        this.seen = seen;
        this.timestamp = timestamp;
        this.text = text;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public int getNotificationAppId() {
        return notificationAppId;
    }

    public void setNotificationAppId(int notificationAppId) {
        this.notificationAppId = notificationAppId;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
