package sit.int221.servicetasksj3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.servicetasksj3.entities.TaskStatus;

import java.util.List;

public interface StatusRepository extends JpaRepository<TaskStatus, Integer> {
    List<TaskStatus> findByName(String name);
}
