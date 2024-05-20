package sit.int221.servicetasksj3.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StatusLimitTaskDTO {
    private Integer id;
    private String name;
    private String description;
    private boolean limitMaximumTask;
}
