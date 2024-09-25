package sit.int221.servicetasksj3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<TaskStatus, Integer> {
    TaskStatus findByName(String name);

    TaskStatus findByNameAndBoardId(String name, String boardId);


    List<TaskStatus> findByBoardId(String boardId);

    Optional<TaskStatus> findByBoardIdAndId(String boardId, Integer id);

}
