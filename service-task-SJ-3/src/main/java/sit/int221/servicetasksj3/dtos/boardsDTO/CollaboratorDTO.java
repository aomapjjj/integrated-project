package sit.int221.servicetasksj3.dtos.boardsDTO;

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
