package sit.int221.servicetasksj3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.servicetasksj3.entities.Task;
import sit.int221.servicetasksj3.entities.TaskLimit;

import java.util.Optional;

public interface LimitRepository extends JpaRepository<TaskLimit, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE TaskLimit tl SET tl.maximumTask = :newLimit, tl.isLimit = :isLimit WHERE tl.id = :id")
    void updateLimit(Integer id, Integer newLimit, Boolean isLimit);

//    Optional<TaskLimit> findByBoard_IdAndId(String boardId, Integer id);
}
