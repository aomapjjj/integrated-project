package sit.int221.servicetasksj3.dtos.collaboratorDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.int221.servicetasksj3.entities.AccessRight;
import sit.int221.servicetasksj3.entities.CollabStatus;

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
    @NotNull
    private CollabStatus status;

    public CollaboratorDTO(String id, String name, String email, AccessRight accessRight, Timestamp addedOn, CollabStatus status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.accessRight = accessRight;
        this.addedOn = addedOn;
        this.status = status;
    }
}
