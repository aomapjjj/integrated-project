package sit.int221.servicetasksj3.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sit.int221.servicetasksj3.entities.TaskStatus;

@Data
@Getter
@Setter
public class StatusDTO {
    private Integer id;
    private String name;
    private String description;

    @JsonIgnore
    private TaskStatus statusTasks;

    @JsonIgnore
    public String getStatusName() {
        if (statusTasks != null) {
            return statusTasks.getName();
        }
        return null;
    }

}
