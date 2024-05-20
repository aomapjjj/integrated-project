package sit.int221.servicetasksj3.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StatusDTOTwo {
    private Integer id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "UTC")
    private ZonedDateTime updatedOn;
}
