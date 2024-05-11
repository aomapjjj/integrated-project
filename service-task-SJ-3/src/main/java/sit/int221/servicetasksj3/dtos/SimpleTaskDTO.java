package sit.int221.servicetasksj3.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sit.int221.servicetasksj3.entities.TaskStatus;

import java.time.ZonedDateTime;

@Data
public class SimpleTaskDTO {
    private Integer id;
    private String title;
    private String  assignees;

    @JsonIgnore
    private TaskStatus statusTasks;

    public String getStatus(){
        return statusTasks.getName();
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime updatedOn;
}
