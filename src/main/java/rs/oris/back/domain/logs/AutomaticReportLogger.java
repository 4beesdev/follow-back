package rs.oris.back.domain.logs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AutomaticReportLogger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Boolean success;

    private LocalDateTime time;

    private String email;

    private String imei;
    private String type;

    private String errorMessage;
    private String registration;

}
