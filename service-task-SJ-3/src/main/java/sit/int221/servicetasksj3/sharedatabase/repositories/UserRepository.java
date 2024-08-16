package sit.int221.servicetasksj3.sharedatabase.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;

public interface UserRepository extends JpaRepository<Users, String> {

}
