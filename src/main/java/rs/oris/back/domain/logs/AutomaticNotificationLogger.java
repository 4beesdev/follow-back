package rs.oris.back.domain.logs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rs.oris.back.domain.NotificationType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AutomaticNotificationLogger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Boolean success;

    private LocalDateTime time;

    private String phoneNumber;

    private String subject;

    private String userName;
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private int provider;
}
