package rs.oris.back.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Data
public class UserPushNotification  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;



    @Column(columnDefinition = "TEXT")
    private String token;

    public UserPushNotification(User idUser, String token) {
        this.user = idUser;
        this.token = token;
    }


}
