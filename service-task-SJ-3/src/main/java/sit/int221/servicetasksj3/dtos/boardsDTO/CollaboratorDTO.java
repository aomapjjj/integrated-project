package sit.int221.servicetasksj3.dtos.boardsDTO;

import lombok.Getter;
import lombok.Setter;
import sit.int221.servicetasksj3.entities.AccessRight;

import java.sql.Timestamp;

@Getter
@Setter
public class CollaboratorDTO {
    private String oid;
    private String name;
    private String email;
    private AccessRight accessRight; // ใช้ Enum ที่นี่
    private Timestamp addedOn;

    // Constructors
    public CollaboratorDTO() {}

    public CollaboratorDTO(String oid, String name, String email, AccessRight accessRight, Timestamp addedOn) {
        this.oid = oid;
        this.name = name;
        this.email = email;
        this.accessRight = accessRight;
        this.addedOn = addedOn;
    }
}
