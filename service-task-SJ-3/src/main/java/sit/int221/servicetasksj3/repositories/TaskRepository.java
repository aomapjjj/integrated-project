package sit.int221.servicetasksj3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.servicetasksj3.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
