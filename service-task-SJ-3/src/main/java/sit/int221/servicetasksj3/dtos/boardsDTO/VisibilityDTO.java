package sit.int221.servicetasksj3.dtos.boardsDTO;

import jakarta.validation.constraints.*;
import lombok.*;;
import sit.int221.servicetasksj3.entities.Visibility;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisibilityDTO {
    @NotNull
    private Visibility visibility;
}
