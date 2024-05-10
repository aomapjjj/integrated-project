package sit.int221.servicetasksj3.dtos;

import lombok.*;

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
