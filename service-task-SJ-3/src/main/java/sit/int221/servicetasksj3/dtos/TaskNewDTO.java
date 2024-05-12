package sit.int221.servicetasksj3.dtos;

import lombok.Data;

@Data
public class TaskNewDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private String status = "NO_STATUS";

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

    public void setStatus(String status) {
        if (status == null || status.isEmpty()) {
            this.status = "NO_STATUS";
        } else {
            this.status = status;
        }
    }
}
