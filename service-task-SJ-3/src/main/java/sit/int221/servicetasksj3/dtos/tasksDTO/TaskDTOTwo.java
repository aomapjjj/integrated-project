package sit.int221.servicetasksj3.dtos.tasksDTO;

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
