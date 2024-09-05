package sit.int221.servicetasksj3.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tasklimit")
public class TaskLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tasklimitId")
    private int id;
    @Column(name = "maximumTask")
    private Integer maximumTask;
    @Column(name = "isLimit", nullable = false, columnDefinition = "TINYINT", length = 1)
    private Boolean isLimit;
    @Column(name = "boardId", length = 10)
    private String boardId;

    public TaskLimit(Integer maximumTask, Boolean isLimit, String boardId) {
        this.maximumTask = maximumTask;
        this.isLimit = isLimit;
        this.boardId = boardId;
    }
}
