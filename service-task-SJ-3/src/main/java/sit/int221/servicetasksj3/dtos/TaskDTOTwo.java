package sit.int221.servicetasksj3.dtos;

import lombok.*;
import java.sql.Timestamp;

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
    private Timestamp createdOn;
    private Timestamp updatedOn;
}
