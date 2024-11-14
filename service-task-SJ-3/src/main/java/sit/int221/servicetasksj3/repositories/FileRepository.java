package sit.int221.servicetasksj3.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int221.servicetasksj3.entities.TaskFile;

@Repository
public interface FileRepository extends JpaRepository<TaskFile, Integer> {
}