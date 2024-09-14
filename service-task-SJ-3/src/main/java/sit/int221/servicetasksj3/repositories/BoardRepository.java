package sit.int221.servicetasksj3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int221.servicetasksj3.entities.Board;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {
    List<Board> findByOwnerId(String ownerId);

    // Method to check if a user already owns a board
    boolean existsByOwnerId(String ownerId);
}