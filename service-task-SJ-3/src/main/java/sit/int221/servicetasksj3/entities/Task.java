package sit.int221.servicetasksj3.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "tasks")
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

    @ManyToOne
    @JoinColumn(name = "statusId")
    private TaskStatus status;

    @Column(name = "createdOn", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime createdOn;
    @Column(name = "updatedOn", updatable = false, insertable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime updatedOn;

    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            this.title = null;
        } else {
            this.title = title.trim();
        }
    }

    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            this.description = null;
        } else {
            this.description = description.trim();
        }
    }

    public void setAssignees(String assignees) {
        if (assignees == null || assignees.isEmpty()) {
            this.assignees = null;
        } else {
            this.assignees = assignees.trim();
        }
    }

    public void setStatus(TaskStatus status) {
        if (status == null){
            status = new TaskStatus();
        }
        this.status = status;
    }
}
