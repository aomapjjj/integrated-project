package sit.int221.servicetasksj3.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "microsoft_users")
public class MicrosoftDetail {

    @Id
    @Column(length = 36)
    private String oid;

    @Column(length = 255, nullable = false)
    private String email;

    @Column(length = 255)
    private String displayName;

    @Column(nullable = false)
    private LocalDateTime lastLogin;

}
