package sit.int221.servicetasksj3.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTOTwo {
        private Integer id;
        private String title;
        private String description;
        private String assignees;
        private String status;
 }
