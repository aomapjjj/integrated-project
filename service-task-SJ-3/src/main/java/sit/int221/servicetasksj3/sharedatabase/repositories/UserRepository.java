package sit.int221.servicetasksj3.sharedatabase.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.int221.servicetasksj3.sharedatabase.entities.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, String> {
    Users findByUsername(String username);


    Optional<Users> findByEmail(String email);
    @Query("SELECT u FROM Users u WHERE u.email = :identifier OR u.username = :identifier")
    Users findByEmailOrUsername(String identifier);


}
