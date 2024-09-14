package sit.int221.servicetasksj3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int221.servicetasksj3.dtos.boardsDTO.BoardResponseDTO;
import sit.int221.servicetasksj3.entities.Board;
import sit.int221.servicetasksj3.entities.TaskLimit;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {
    List<Board> findByOwnerId(String ownerId);
 
    boolean existsByOwnerId(String ownerId);

    Optional<Board> findById(String id);
}