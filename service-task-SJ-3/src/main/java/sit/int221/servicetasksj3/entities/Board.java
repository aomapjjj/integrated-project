package sit.int221.servicetasksj3.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;


@Getter
@Setter
@Entity
@Table(name = "boards", schema = "kanbanIT")
public class Board {

    @Id
    @Column(name = "board_id", length = 10)
    private String boardId;

    @Column(name = "user_id", length = 36, nullable = false)
    private String user_id;


}
