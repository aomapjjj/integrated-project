package sit.int221.servicetasksj3.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import sit.int221.servicetasksj3.entities.TaskStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Integer id;
    private String title;
    private String assignees;
    @JsonIgnore
    private TaskStatus statusTasks;

    public String getStatus(){
        return statusTasks.getName();
    }
}
