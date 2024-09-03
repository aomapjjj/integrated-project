package sit.int221.servicetasksj3.dtos.boardsDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDTO {
    @NotBlank(message = "Board name cannot be null or empty")
    @Size(max = 100, message = "Board name cannot exceed 100 characters")
    private String name;
}
