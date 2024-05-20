package sit.int221.servicetasksj3.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sit.int221.servicetasksj3.entities.TaskStatus;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "title","description", "assignees", "status", "createdOn", "updatedOn"})
public class SimpleTaskDTO {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private TaskStatus status;

    public String getStatus(){
        return status.getName();
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime updatedOn;
}
