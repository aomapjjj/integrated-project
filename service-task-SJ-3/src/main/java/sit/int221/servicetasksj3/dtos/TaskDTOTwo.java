package sit.int221.servicetasksj3.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import sit.int221.servicetasksj3.entities.TaskStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTOTwo {
        private Integer id;
        private String title;
        private String description;
        private String assignees;
        private TaskStatus status;

        public String getStatus(){
                return status.getName();
        }
}
