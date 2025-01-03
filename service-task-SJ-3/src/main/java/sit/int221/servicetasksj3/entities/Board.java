package sit.int221.servicetasksj3.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;


@Getter
@Setter
@Entity
@Table(name = "boards")
public class Board {
    @Id
    @Column(name = "boardId", length = 10)
    private String id;

    @Column(name = "boardname", length = 120, nullable = false)
    private String name;

    @JsonIgnore
    @Column(name = "userId", length = 36, nullable = false)
    private String ownerId;

    @Column(name = "createdOn", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime createdOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private Visibility visibility = Visibility.PRIVATE;

    public void setUsername(String username) {
    }

    public void setEmail(String email) {
    }
}
