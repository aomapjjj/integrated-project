package sit.int221.servicetasksj3.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String assignees;

    @Enumerated(EnumType.STRING)
    @Column(name = "taskStatus")
    private TaskStatus status = TaskStatus.NO_STATUS;

    @Column(name = "createdOn", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime createdOn;

    @Column(name = "updatedOn", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime updatedOn;

    public void setTitle(String title) {
        if (title != null) {
            title = title.trim();
        }
        this.title = title;
    }

    public void setDescription(String description) {
        if (description != null) {
            description = description.trim();
        }
        this.description = description;
    }

    public void setAssignees(String assignees) {
        if (assignees != null) {
            assignees = assignees.trim();
        }
        this.assignees = assignees;
    }

    public void setStatus(TaskStatus status) {
        if (status == null){
            status = TaskStatus.NO_STATUS;
        }
        this.status = status;
    }
}
