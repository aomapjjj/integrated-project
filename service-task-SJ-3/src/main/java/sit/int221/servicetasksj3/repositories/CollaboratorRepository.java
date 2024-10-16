package sit.int221.servicetasksj3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.servicetasksj3.entities.AccessRight;
import sit.int221.servicetasksj3.entities.Collaborator;

import java.util.List;

public interface CollaboratorRepository extends JpaRepository<Collaborator, String> {
    List<Collaborator> findByBoardId(String boardId);
    Collaborator findByBoardIdAndCollaboratorId(String boardId, String collaboratorId);
    boolean existsByBoardIdAndCollaboratorId(String boardId, String collaboratorId);
    boolean existsByBoardIdAndCollaboratorEmail(String boardId, String collaboratorEmail);

    boolean existsByBoardIdAndCollaboratorIdAndAccessLevel(String boardId, String collaboratorId, AccessRight accessLevel);

}
