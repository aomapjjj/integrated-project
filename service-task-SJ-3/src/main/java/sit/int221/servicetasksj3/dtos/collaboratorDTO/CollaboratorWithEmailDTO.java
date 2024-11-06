package sit.int221.servicetasksj3.dtos.collaboratorDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sit.int221.servicetasksj3.dtos.emailDTO.EmailRequestDTO;

@Getter
@Setter
public class CollaboratorWithEmailDTO {
    @NotNull
    private CollaboratorDTO collaborator;
    @NotNull
    private EmailRequestDTO email;
}