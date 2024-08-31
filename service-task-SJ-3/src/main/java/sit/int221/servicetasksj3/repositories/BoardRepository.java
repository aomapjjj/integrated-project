package sit.int221.servicetasksj3.repositories;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int221.servicetasksj3.entities.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {

}