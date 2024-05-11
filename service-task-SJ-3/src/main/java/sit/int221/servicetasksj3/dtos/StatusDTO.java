package sit.int221.servicetasksj3.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sit.int221.servicetasksj3.entities.TaskStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StatusDTO {
    private Integer id;
    private String name;
    private String description;
}
