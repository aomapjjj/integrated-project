package sit.int221.servicetasksj3.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.int221.servicetasksj3.entities.Task;

import java.util.List;
import java.util.Optional;


public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.statusTasks.id = :newId WHERE t.statusTasks.id = :oldId")
    void updateStatusId(Integer oldId, Integer newId);

    boolean existsTaskByStatusTasksId(Integer id);

    List<Task> findByStatusTasksId(Integer id);
}
