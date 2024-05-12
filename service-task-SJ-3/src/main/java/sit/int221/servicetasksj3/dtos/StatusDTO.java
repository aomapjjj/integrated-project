package sit.int221.servicetasksj3.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StatusDTO {
    private Integer id;
    private String name;
    private String description;
}
