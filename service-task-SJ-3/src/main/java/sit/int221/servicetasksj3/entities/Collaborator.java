package sit.int221.servicetasksj3.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@Entity
@Table(name = "collaborators", schema = "kanbanIT", catalog = "")
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
    @Enumerated(EnumType.STRING) // บันทึกเป็น String ใน database
    private AccessRight accessLevel;
    @Column(name = "addedOn")
    private Timestamp addedOn;
}
