package sit.int221.servicetasksj3.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
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
}
