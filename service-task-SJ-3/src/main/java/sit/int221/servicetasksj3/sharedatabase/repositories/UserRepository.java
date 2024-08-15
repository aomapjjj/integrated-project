package sit.int221.servicetasksj3.sharedatabase.repositories;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import sit.int221.servicetasksj3.sharedatabase.entities.UsersEntity;

public interface UserRepository extends JpaRepository<UsersEntity, String> {

}
