package sit.int221.servicetasksj3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.int221.servicetasksj3.dtos.boardsDTO.BoardResponseDTO;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.entities.TaskLimit;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {
    List<Board> findByOwnerId(String ownerId);

    Optional<Board> findById(String id);

    @Query("SELECT b FROM Board b LEFT JOIN Collaborator c ON b.id = c.boardId " +
            "WHERE b.ownerId = :userId OR c.collaboratorId = :userId")
    List<Board> findAllByUserIdOrCollaboratorId(@Param("userId") String userId);

}