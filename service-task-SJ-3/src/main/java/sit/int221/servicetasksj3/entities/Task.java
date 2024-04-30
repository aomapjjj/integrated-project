package sit.int221.servicetasksj3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "TASKS")
public class Task {
    @Id
    @Column(name = "taskId", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "taskTitle", length = 100)
    private String title;
    @Column(name = "taskDescription", length = 500)
    private String description;
    @Column(name = "taskAssignees", length = 30)
    private Object assignees;

    @Enumerated(EnumType.STRING)
    @Column(name = "taskStatus")
    private TaskStatus status;
    @Column(name = "createdOn", updatable = false, insertable = false)
    private ZonedDateTime createdOn;
    @Column(name = "updatedOn", updatable = false, insertable = false)
    private ZonedDateTime updatedOn;
}
