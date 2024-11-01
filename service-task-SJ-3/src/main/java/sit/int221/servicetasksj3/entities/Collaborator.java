package sit.int221.servicetasksj3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@Entity
@Table(name = "collaborators", schema = "kanbanIT")
public class Collaborator {
    @Id
    @Column(name = "collabEntryId")
    private int collabEntryId;

    @Column(name = "collabsId")
    private String collaboratorId;

    @Column(name = "collabsName")
    private String collaboratorName;

    @Column(name = "collabsEmail", length = 255)
    private String collaboratorEmail;

    @Column(name = "boardId")
    private String boardId;
    
    @Column(name = "ownerId")
    private String ownerId;

    @Column(name = "accessLevel")
    @Enumerated(EnumType.STRING)
    private AccessRight accessLevel = AccessRight.READ;

    @Column(name = "addedOn")
    private Timestamp addedOn;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CollabStatus status = CollabStatus.PENDING;
}
