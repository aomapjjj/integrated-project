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
    private Integer taskId;
    @Column(name = "taskTitle", length = 100)
    private String taskTitle;
    @Column(name = "taskDescription", length = 500)
    private String taskDescription;
    @Column(name = "taskAssignees", length = 30)
    private Object taskAssignees;

    @Enumerated(EnumType.STRING)
    @Column(name = "taskStatus")
    private TaskStatus taskStatus;
    @Column(name = "createdOn", updatable = false)
    private ZonedDateTime createdOn;
    @Column(name = "updatedOn")
    private ZonedDateTime updatedOn;
}
