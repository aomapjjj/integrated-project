package sit.int221.servicetasksj3.dtos.boardsDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sit.int221.servicetasksj3.entities.AccessRight;

import java.sql.Timestamp;

@Getter
@Setter
public class CollaboratorDTO {
    private String id;
    private String name;
    private String email;
    @NotNull
    private AccessRight accessRight;
    private Timestamp addedOn;

    public CollaboratorDTO(String id, String name, String email, AccessRight accessRight, Timestamp addedOn) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.accessRight = accessRight;
        this.addedOn = addedOn;
    }
}
