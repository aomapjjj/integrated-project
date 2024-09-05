package sit.int221.servicetasksj3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.servicetasksj3.entities.TaskStatus;

public interface StatusRepository extends JpaRepository<TaskStatus, Integer> {
    TaskStatus findByName(String name);

    TaskStatus findByNameAndBoardId(String name, String boardId);
}
