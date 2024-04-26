package sit.int221.servicetasksj3.dtos;

import lombok.*;
import java.time.ZonedDateTime;

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
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
}
